package rest;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.servlet.MultipartConfigElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import rest.general.RestConnector;
import rest.general.SecurityCheckAdapter;
import spark.Spark;

/**
 * The `DataFlowAnalysisConnector` class extends `RestConnector` and provides
 * RESTful endpoints for initiating and managing data flow analysis. It utilizes
 * Spark for handling HTTP requests and responses.
 *
 */
public class DataFlowAnalysisConnector extends RestConnector {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataFlowAnalysisConnector.class);
	private static final String SERVICE_PATH = "/dataFlowAnalysis";
	private static final String TESTMODELS_PATH = "org.palladiosimulator.dataflow.confidentiality.analysis.testmodels";
	private static final String CASESTUDIES_DIR_CONTAINER = "DataFlowAnalysisDependencies" + File.separator
			+ "DataFlowAnalysis" + File.separator + "tests" + File.separator + TESTMODELS_PATH + File.separator
			+ "casestudies";
	private static final int SPARK_PORT = 2406;
	private final File casestudiesDirectory;
	private final ObjectMapper objectMapper;
	private final SecurityCheckAdapter dataFlowAnalysisAdapter;

	/**
	 * The main method to initialize the `DataFlowAnalysisConnector` and its
	 * endpoints.
	 *
	 * @param args Command-line arguments currently not used.
	 */
	public static void main(String[] args) {
		DataFlowAnalysisConnector dataFlowAnalysisConnector = new DataFlowAnalysisConnector();
		dataFlowAnalysisConnector.initEndpoints();
	}

	/**
	 * Constructs a new `DataFlowAnalysisConnector`. Initializes Spark,
	 * configuration parameters, and directories.
	 */
	public DataFlowAnalysisConnector() {
		Spark.port(SPARK_PORT);

		this.objectMapper = new ObjectMapper();
		this.dataFlowAnalysisAdapter = new DataFlowAnalysisAdapter();

		File caseStudiesDirectory = null;
		File userDirFile = new File(System.getProperty("user.dir"));

		String parentDir = userDirFile.getParent();
		File[] testModelsDirFile = parentDir == null ? null
				: userDirFile.getParentFile().listFiles((File dir, String name) -> name.equals(TESTMODELS_PATH));

		if (testModelsDirFile != null && testModelsDirFile.length > 0) {
			File[] casestudiesDirFile = testModelsDirFile[0]
					.listFiles((File dir, String name) -> name.equals("casestudies"));

			if (casestudiesDirFile != null && casestudiesDirFile.length == 1) {
				caseStudiesDirectory = casestudiesDirFile[0];
			}
		}

		this.casestudiesDirectory = (caseStudiesDirectory != null && caseStudiesDirectory.exists())
				? caseStudiesDirectory
				: new File(DataFlowAnalysisConnector.CASESTUDIES_DIR_CONTAINER);
	}

	/**
	 * Initializes the connection test endpoint.
	 */
	@Override
	protected void initConnectionTestEndpoint() {
		Spark.get(SERVICE_PATH + "/test", (req, res) -> {
			LOGGER.info("Received connection test from host '" + req.host() + "'.");

			res.status(200);
			res.type("text/plain");

			return "Connection test successful!";
		});

	}

	/**
	 * Initializes the model transfer endpoint for receiving models for analysis.
	 */
	@Override
	protected void initModelTransferEndpoint() {
		Spark.post(SERVICE_PATH + "/set/model/:modelName", (req, res) -> {
			LOGGER.info("Received model for analysis from '" + req.host() + "'.");

			if (this.casestudiesDirectory == null || !this.casestudiesDirectory.exists()) {
				res.status(500);
				return "Analysis cannot locate 'casestudies' directory.";
			}

			if (req.raw().getAttribute("org.eclipse.jetty.multipartConfig") == null) {
				MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
						System.getProperty("java.io.tmpdir"));
				req.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);
			}

			String modelName = req.params(":modelName");
			if (modelName == null || modelName.isEmpty()) {
				res.status(400);
				res.body("No model name provided.");
			}

			// Create new Base Folder.
			File modelFolder = new File(this.casestudiesDirectory.getAbsolutePath() + File.separator + "CaseStudy-"
					+ modelName + File.separator + modelName);
			if (!modelFolder.exists()) {
				modelFolder.mkdirs();
			}

			// Copy model files.
			var parts = req.raw().getParts();
			for (var part : parts) {
				var fileName = part.getName();
				var targetFilePath = Paths.get(modelFolder.getAbsolutePath() + File.separator + fileName);

				Files.copy(part.getInputStream(), targetFilePath, StandardCopyOption.REPLACE_EXISTING);
			}

			res.status(200);

			return "Sucess!";
		});

	}

	/**
	 * Initializes the analysis execution endpoint.
	 */
	@Override
	protected void initAnalysisExecutionEndpoint() {
		// Analysis execution endpoint.
		Spark.post(SERVICE_PATH + "/run", (req, res) -> {
			LOGGER.info("Received analysis execution command from '" + req.host() + "'.");

			AnalysisParameter parameter = this.objectMapper.readValue(req.body(), AnalysisParameter.class);

			// Configure DataFlowAnalysisAdapter.
			try {
				this.dataFlowAnalysisAdapter.initForAnalysis(parameter);
			} catch (IllegalArgumentException e) {
				LOGGER.error(e.getMessage());
				res.status(400);
				return e.getMessage();
			}

			AnalysisOutput anaylsisOutput = this.dataFlowAnalysisAdapter.executeAnalysis();
			LOGGER.info("Analysis was successfully performed.");

			res.status(200);
			res.type("application/json");
			return this.objectMapper.writeValueAsString(anaylsisOutput);
		});
	}
}
