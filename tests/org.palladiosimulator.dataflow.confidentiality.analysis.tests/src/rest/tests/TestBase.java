package rest.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.nio.file.Paths;
import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.palladiosimulator.dataflow.confidentiality.analysis.DataFlowConfidentialityAnalysis;
import org.palladiosimulator.dataflow.confidentiality.analysis.builder.DataFlowAnalysisBuilder;
import org.palladiosimulator.dataflow.confidentiality.analysis.builder.pcm.PCMDataFlowConfidentialityAnalysisBuilder;
import org.palladiosimulator.dataflow.confidentiality.analysis.core.AbstractStandalonePCMDataFlowConfidentialityAnalysis;
import org.palladiosimulator.dataflow.confidentiality.analysis.testmodels.Activator;
import rest.DataFlowAnalysisAdapter;
import rest.entities.GraphAssumption;

/**
 * Base class for tests related to data flow analysis.
 */
public abstract class TestBase {
	private static final String TEST_MODEL_PROJECT_NAME = "org.palladiosimulator.dataflow.confidentiality.analysis.testmodels";
	protected DataFlowConfidentialityAnalysis analysis = null;
	protected DataFlowAnalysisAdapter dataFlowAnalysisAdapter = null;

	protected abstract String getFolderName();

	protected String getFilesName() {
		return "default";
	};

	protected String getBaseFolderName() {
		return "casestudies/CaseStudy-" + getFolderName();
	};

	/**
	 * Setup method to initialize data flow analysis.
	 */
	@BeforeEach
	public void setup() {
		initializeDataFlowAnalysis();
		this.dataFlowAnalysisAdapter = new DataFlowAnalysisAdapter();
		this.dataFlowAnalysisAdapter.setDataFlowAnalysis(this.analysis);
	}

	/**
	 * Initializes the data flow analysis by configuring paths for usage model,
	 * allocation, and node characteristics. It then constructs and initializes an
	 * instance of AbstractStandalonePCMDataFlowConfidentialityAnalysis.
	 */
	private void initializeDataFlowAnalysis() {
		final String usageModelPath = Paths.get(getBaseFolderName(), getFolderName(), getFilesName() + ".usagemodel")
				.toString();
		final String allocationPath = Paths.get(getBaseFolderName(), getFolderName(), getFilesName() + ".allocation")
				.toString();
		final String nodeCharacteristicsPath = Paths
				.get(getBaseFolderName(), getFolderName(), getFilesName() + ".nodecharacteristics").toString();

		AbstractStandalonePCMDataFlowConfidentialityAnalysis analysis = new DataFlowAnalysisBuilder().standalone()
				.modelProjectName(TEST_MODEL_PROJECT_NAME).useBuilder(new PCMDataFlowConfidentialityAnalysisBuilder())
				.usePluginActivator(Activator.class).useUsageModel(usageModelPath).useAllocationModel(allocationPath)
				.useNodeCharacteristicsModel(nodeCharacteristicsPath).build();

		analysis.initializeAnalysis();
		this.analysis = analysis;
	}

	/**
	 * Method to assert sub-assumption violations.
	 *
	 * @param isViolated       Boolean indicating if there is any violation.
	 * @param graphAssumptions Collection of graph assumptions to evaluate.
	 */
	protected void assertSubAssumptionViolations(boolean isViolated, Collection<GraphAssumption> graphAssumptions) {
		dataFlowAnalysisAdapter.evaluateAssumptions(graphAssumptions, false);
		assertEquals(isViolated, dataFlowAnalysisAdapter.getIsAnyViolation());
	}

	/**
	 * Method to assert probability violation is set after the analysis is done.
	 *
	 * @param probability      The expected probability.
	 * @param assumptionName   The name of the assumption.
	 * @param graphAssumptions Collection of graph assumptions to evaluate.
	 */
	protected void assertPobabilityViolationIsSet(double probability, String assumptionName,
			Collection<GraphAssumption> graphAssumptions) {
		dataFlowAnalysisAdapter.evaluateAssumptions(graphAssumptions, false);
		for (GraphAssumption graphAssumption : graphAssumptions) {
			if (graphAssumption.getName().equals(assumptionName)) {

				assertEquals(probability, graphAssumption.getProbabilityOfViolation());
			}
		}
	}

	/**
	 * Method to assert that 'analyzed' flag is set after the analysis is done.
	 *
	 * @param isAnalyzedSet    Boolean indicating if analyzed is set.
	 * @param graphAssumptions Collection of graph assumptions to evaluate.
	 */
	protected void assertAnalyzedIsSet(boolean isAnalyzedSet, Collection<GraphAssumption> graphAssumptions) {
		dataFlowAnalysisAdapter.evaluateAssumptions(graphAssumptions, false);
		for (GraphAssumption graphAssumption : graphAssumptions) {
			assertEquals(isAnalyzedSet, graphAssumption.isAnalyzed());
		}
	}
}
