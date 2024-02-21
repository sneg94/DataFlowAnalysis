package rest;

import rest.general.AbstractSubAssumptionAnalyzer;
import rest.general.SecurityCheckAdapter;
import rest.general.RestConnector.AnalysisOutput;
import rest.general.RestConnector.AnalysisParameter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import org.palladiosimulator.dataflow.confidentiality.analysis.DataFlowConfidentialityAnalysis;
import org.palladiosimulator.dataflow.confidentiality.analysis.builder.DataFlowAnalysisBuilder;
import org.palladiosimulator.dataflow.confidentiality.analysis.builder.pcm.PCMDataFlowConfidentialityAnalysisBuilder;
import org.palladiosimulator.dataflow.confidentiality.analysis.core.AbstractStandalonePCMDataFlowConfidentialityAnalysis;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.pcm.PCMActionSequence;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.sequence.AbstractActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.entity.sequence.ActionSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.palladiosimulator.dataflow.confidentiality.analysis.testmodels.Activator;
import rest.entities.GraphAssumption;

/**
 * The `DataFlowAnalysisAdapter` class extends the
 * `AbstractSubAssumptionAnalyzer` class, implements the SecurityCheckAdapter
 * interface and serves as an adapter for performing data flow analysis. It
 * utilizes the Palladio Simulator's data flow analysis framework.
 */
public class DataFlowAnalysisAdapter extends AbstractSubAssumptionAnalyzer implements SecurityCheckAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataFlowAnalysisAdapter.class);
	public static final String MODEL_PROJECT_NAME = "org.palladiosimulator.dataflow.confidentiality.analysis.testmodels";
	private DataFlowConfidentialityAnalysis analysis = null;
	private String baseFolderName;
	private String folderName;
	private String filesName;
	private String scenarioName;
	private boolean anyAssumptionViolation = false;
	private List<GraphAssumption> violatedSubAssumptions;

	/**
	 * Initializes the adapter for analysis based on the provided
	 * `AnalysisParameter`.
	 *
	 * @param analysisParameter The parameters for the analysis.
	 * @throws IllegalArgumentException if the model path is invalid.
	 */
	@Override
	public void initForAnalysis(AnalysisParameter analysisParameter) {
		// Extract model name.
		String modelPath = analysisParameter.modelPath();
		int lastSeparatorIndex = modelPath.lastIndexOf(File.separator);

		if (lastSeparatorIndex == -1) {
			throw new IllegalArgumentException("Model path is invalid.");
		}
		String modelName = modelPath.substring(lastSeparatorIndex + 1);

		this.assumptions = analysisParameter.assumptions();
		this.violatedSubAssumptions = new ArrayList<GraphAssumption>();
		this.baseFolderName = "casestudies/CaseStudy-" + modelName;
		this.folderName = modelName;
		this.filesName = "default";
		this.scenarioName = "Analysis of model '" + modelName + "' on "
				+ new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss").format(new Date());

	}

	private void setup() {
		LOGGER.info("Performing set-up for the analysis.");

		final String usageModelPath = Paths.get(this.baseFolderName, this.folderName, this.filesName + ".usagemodel")
				.toString();
		final String allocationPath = Paths.get(this.baseFolderName, this.folderName, this.filesName + ".allocation")
				.toString();
		final String nodeCharacteristicsPath = Paths
				.get(this.baseFolderName, this.folderName, this.filesName + ".nodecharacteristics").toString();

		AbstractStandalonePCMDataFlowConfidentialityAnalysis analysis = new DataFlowAnalysisBuilder().standalone()
				.modelProjectName(MODEL_PROJECT_NAME).useBuilder(new PCMDataFlowConfidentialityAnalysisBuilder())
				.usePluginActivator(Activator.class).useUsageModel(usageModelPath).useAllocationModel(allocationPath)
				.useNodeCharacteristicsModel(nodeCharacteristicsPath).build();

		analysis.initializeAnalysis();
		this.analysis = analysis;
		LOGGER.info("Set-Up complete.");
	}

	/**
	 * Executes the data flow analysis and returns the results as an
	 * `AnalysisOutput`.
	 *
	 * @return The results of the analysis.
	 */
	@Override
	public AnalysisOutput executeAnalysis() {
		LOGGER.info("Initiating execution of analysis.");
		StringBuilder outputStringBuilder = new StringBuilder(
				"#################### Analysis Output ####################\n");

		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				PrintStream printStream = new PrintStream(outputStream)) {
			PrintStream oldPrintStream = System.out;
			System.setOut(printStream);

			this.setup();

			this.performScenarioEvaluation();
			resetEvaluationState();
			System.setOut(oldPrintStream);

			outputStringBuilder.append(outputStream.toString());
			LOGGER.info("Execution of analysis successfully completed.");
		} catch (Exception e) {
			LOGGER.error("Error occured during analysis execution.", e);
			outputStringBuilder.append("Analysis execution encountered a fatal error. Details are shown below:\n");

			StringWriter stringWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(stringWriter));

			outputStringBuilder.append(stringWriter.toString());
		}

		outputStringBuilder.append("\n#########################################################");

		return new AnalysisOutput(outputStringBuilder.toString(), this.assumptions);
	}

	/**
	 * Performs the evaluation of the given scenario by analyzing data flows against
	 * assumptions. Prints information about violations, if any, to the console.
	 * This method considers sub-assumptions in the given scenario.
	 */
	@Override
	protected void performScenarioEvaluation() {
		LOGGER.info("Evaluate given scenario.");
		evaluateAssumptions(this.assumptions, false);
		if (!this.anyAssumptionViolation) {
			System.out.println("\n\n No violations found!");
		}
		LOGGER.info("Finished evaluating the scenario.");
	}

	/**
	 * Evaluates assumptions and their dependent sub-assumptions for a given
	 * collection.
	 *
	 * @param currAssumptions A collection of assumptions to be evaluated along with
	 *                        their dependent sub-assumptions.
	 * @param isParent        Indicates whether the assumptions are top-level or
	 *                        sub-assumptions.
	 * @return true if any violation is found, {@code false} otherwise.
	 */
	public boolean evaluateAssumptions(Collection<GraphAssumption> currAssumptions, boolean isParent) {
		List<ActionSequence> actionSequences = analysis.findAllSequences();
		List<ActionSequence> propagationResult = analysis.evaluateDataFlows(actionSequences);
		boolean isAnyViolation = false;

		for (GraphAssumption assumption : currAssumptions) {
			List<GraphAssumption> subAssumptions = getSubAssumptions(assumption);
			if (subAssumptions.isEmpty() && !isParent && isAssumptionInDependencies(assumption)) {
				continue;
			}
			BiPredicate<List<String>, List<String>> constraint = getConstraint(assumption);
			int sequenceIndex = 0;

			isAnyViolation |= evaluateAssumptionWithSequences(assumption, subAssumptions, constraint, propagationResult,
					isParent, sequenceIndex);
			if (isAnyViolation) {
				assumption.setProbabilityOfViolation(1.0);
			}
		}
		return isAnyViolation;
	}

	/**
	 * Evaluates a single assumption with its dependent sub-assumptions and prints
	 * violations if any.
	 *
	 * @param assumption        The assumption to be evaluated.
	 * @param subAssumptions    The dependent sub-assumptions.
	 * @param constraint        The constraint for evaluating the assumption.
	 * @param propagationResult The result of data flow analysis.
	 * @param isParent          Indicates whether the assumption is a top-level or
	 *                          sub-assumption.
	 * @param sequenceIndex     The index used for tracking the order of data flow
	 *                          sequences.
	 * @return true if the assumption or any of its sub-assumptions is violated,
	 *         false otherwise.
	 */
	private boolean evaluateAssumptionWithSequences(GraphAssumption assumption, List<GraphAssumption> subAssumptions,
			BiPredicate<List<String>, List<String>> constraint, List<ActionSequence> propagationResult,
			boolean isParent, int sequenceIndex) {

		boolean isParentViolated = false;

		for (ActionSequence actionSequence : propagationResult) {
			List<AbstractActionSequenceElement<?>> violations = analysis.queryDataFlow(actionSequence, it -> {
				List<String> dataLiterals = getDataLiterals(it);
				List<String> nodeLiterals = getNodeLiterals(it);

				return constraint.test(dataLiterals, nodeLiterals);
			});

			if (!violations.isEmpty()) {
				isParentViolated = true;
				this.anyAssumptionViolation = true;
				assumption.setProbabilityOfViolation(1.0);
				printViolationInformation(assumption, isParent, sequenceIndex, violations);
			}
			sequenceIndex++;
		}

		checkSubAssumptionViolation(assumption, subAssumptions, isParentViolated);
		assumption.setAnalyzed(true);

		return isParentViolated;
	}

	/**
	 * Checks if the sub-assumptions of a given assumption have violations and
	 * updates the assumption accordingly.
	 *
	 * @param assumption       The assumption whose sub-assumptions are to be
	 *                         checked.
	 * @param subAssumptions   A list of sub-assumptions.
	 * @param isParentViolated A boolean value indicating whether the parent
	 *                         assumption is violated.
	 */
	private void checkSubAssumptionViolation(GraphAssumption assumption, List<GraphAssumption> subAssumptions,
			boolean isParentViolated) {

		if (!subAssumptions.isEmpty() && !isParentViolated) {
			System.out.println("\n" + assumption.getName() + " has the following sub-assumption violations: \n");
			boolean isSubAssumptionViolated = performSubAssumptionEvaluation(subAssumptions, true);

			if (isSubAssumptionViolated) {
				subAssumptions.add(assumption);
				assumption.setProbabilityOfViolation(1.0);
			}
		}
	}

	/**
	 * Performs the evaluation of sub-assumptions scenarios for a given set of
	 * assumptions. Prints information about violations, if any, to the console.
	 *
	 * @param subAssumptions A list of sub-assumptions to be evaluated.
	 * @param parentIndex    The index of the parent assumption.
	 */
	@Override
	protected boolean performSubAssumptionEvaluation(List<GraphAssumption> subAssumptions, boolean isParent) {
		LOGGER.info("Evaluate given sub-assumptions scenarios.");
		boolean isSubAssumptionViolated = evaluateAssumptions(subAssumptions, true);
		LOGGER.info("Finished evaluating the sub-assumption scenario.");
		return isSubAssumptionViolated;
	}

	/**
	 * Generates a constraint predicate for a given assumption, considering data and
	 * node constraints.
	 *
	 * @param assumption The assumption for which the constraint predicate is
	 *                   generated.
	 * @return The constraint predicate.
	 */
	private BiPredicate<List<String>, List<String>> getConstraint(GraphAssumption assumption) {
		var dataConstraints = new HashSet<String>();
		var nodeConstraints = new HashSet<String>();

		// Extract constraints from the individual assumption descriptions.
		String assumptionDescription = assumption.getDescription();
		String[] descriptionLines = assumptionDescription.split(System.lineSeparator());

		for (String descriptionLine : descriptionLines) {
			String lineWithoutWhitespace = descriptionLine.replaceAll("\\s", "");
			String[] lineComponents = lineWithoutWhitespace.split(":");

			if (lineComponents.length == 2) {
				String constraintType = lineComponents[0].toLowerCase();
				if (constraintType.equals("dataconstraints") || constraintType.equals("nodeconstraints")) {
					HashSet<String> target = constraintType.equals("dataconstraints") ? dataConstraints
							: nodeConstraints;
					target.addAll(Arrays.asList(lineComponents[1].split(",")));
				}
			}
		}

		return (List<String> dataLiterals,
				List<String> nodeLiterals) -> !dataConstraints.isEmpty()
						&& !Collections.disjoint(dataLiterals, dataConstraints)
						|| !nodeConstraints.isEmpty() && !Collections.disjoint(nodeLiterals, nodeConstraints);
	}

	/**
	 * Resets the evaluation state, setting the flag indicating whether any
	 * assumption violation exists to false. Also clears the output buffer.
	 */
	private void resetEvaluationState() {
		anyAssumptionViolation = false;
		System.out.flush();
	}

	public int getSubAssumptionViolationCount() {
		return violatedSubAssumptions.size();
	}

	/**
	 * Prints information about violations for a specific assumption.
	 *
	 * @param assumption    The assumption being evaluated.
	 * @param isParent      Indicates whether the assumption is a top-level
	 *                      assumption or a sub-assumption.
	 * @param sequenceIndex The index of the action sequence being evaluated.
	 * @param violations    The list of violations found for the assumption.
	 */
	private void printViolationInformation(GraphAssumption assumption, boolean isParent, int sequenceIndex,
			List<AbstractActionSequenceElement<?>> violations) {
		String print = !isParent ? "\n" + assumption.getName() + " has the following violations: "
				: "\nViolated sub-assumption: " + assumption.getName() + "\n";
		System.out.println(print);

		String fullIndex = String.valueOf(sequenceIndex);
		System.out.println(DataFlowAnalysisAdapter.formatDataFlow(fullIndex, new PCMActionSequence(violations), true));
	}

	/**
	 * Formats a data flow sequence with a specified index, sequence, and new line
	 * after each entry option.
	 *
	 * @param index                 The index of the data flow sequence.
	 * @param sequence              The action sequence to be formatted.
	 * @param newLineAfterEachEntry A flag indicating whether a new line should be
	 *                              added after each entry.
	 * @return A formatted string representing the data flow sequence.
	 */
	private static String formatDataFlow(String index, ActionSequence sequence, boolean newLineAfterEachEntry) {
		return String.format("%s: %s", index, sequence.getElements().stream().map(it -> it.toString())
				.collect(Collectors.joining(newLineAfterEachEntry ? "\n" : ", ")));
	}

	/**
	 * Retrieves data literals from an abstract action sequence element.
	 *
	 * @param actionSequenceE The abstract action sequence element from which data
	 *                        literals are retrieved.
	 * @return A list of data literals.
	 */
	private List<String> getDataLiterals(AbstractActionSequenceElement<?> actionSequenceE) {
		return actionSequenceE.getAllDataFlowVariables().stream().map(e -> e.getAllCharacteristics())
				.flatMap(List::stream).map(e -> e.characteristicLiteral().getName()).toList();
	}

	/**
	 * Retrieves node literals from an abstract action sequence element.
	 *
	 * @param actionSequenceE The abstract action sequence element from which node
	 *                        literals are retrieved.
	 * @return A list of node literals.
	 */
	private List<String> getNodeLiterals(AbstractActionSequenceElement<?> actionSequenceE) {
		return actionSequenceE.getAllNodeCharacteristics().stream().map(e -> e.characteristicLiteral().getName())
				.toList();
	}
}
