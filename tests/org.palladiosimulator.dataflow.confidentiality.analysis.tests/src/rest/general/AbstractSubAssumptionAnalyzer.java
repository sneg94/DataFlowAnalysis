package rest.general;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import rest.entities.GraphAssumption;

/**
 * The AbstractSubAssumptionAnalyzer class is an abstract base class for
 * performing analysis of assumptions, including sub-assumptions. Subclasses
 * must provide specific implementations for the analysis logic.
 */
public abstract class AbstractSubAssumptionAnalyzer {

	protected Collection<GraphAssumption> assumptions;

	/**
	 * Performs the evaluation of the given scenario by analyzing data flows against
	 * assumptions. This method considers sub-assumptions in the given scenario.
	 */
	protected void performScenarioEvaluation() {
		evaluateAssumptions(this.assumptions, false);
	};

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
	public abstract boolean evaluateAssumptions(Collection<GraphAssumption> currAssumptions, boolean isParent);

	/**
	 * Performs the evaluation of sub-assumptions scenarios for a given set of
	 * assumptions.
	 *
	 * @param subAssumptions A list of sub-assumptions to be evaluated.
	 * @param parentIndex    The index of the parent assumption.
	 */
	protected boolean performSubAssumptionEvaluation(List<GraphAssumption> subAssumptions, boolean isParent) {
		return evaluateAssumptions(subAssumptions, isParent);
	}

	/**
	 * Retrieves a list of sub-assumptions for a given assumption based on
	 * dependencies.
	 *
	 * @param assumption The main assumption for which sub-assumptions are
	 *                   retrieved.
	 * @return A list of sub-assumptions.
	 */
	protected List<GraphAssumption> getSubAssumptions(GraphAssumption assumption) {
		return this.assumptions.stream()
				.filter(currAssumption -> assumption.getDependencies().contains(currAssumption.getId()))
				.collect(Collectors.toList());
	}

	/**
	 * Checks whether the given assumption is present in the dependencies of any of
	 * the existing assumptions.
	 *
	 * @param assumption The assumption to be checked.
	 * @return {@code true} if the assumption is present in the dependencies of any
	 *         existing assumption, otherwise {@code false}.
	 */
	protected boolean isAssumptionInDependencies(GraphAssumption assumption) {
		return this.assumptions.stream()
				.anyMatch(currAssumption -> currAssumption.getDependencies().contains(assumption.getId()));
	}

	/**
	 * Checks if a given assumption has already been evaluated to prevent redundant
	 * analysis.
	 *
	 * @param assumption               The assumption to check for a match.
	 * @param alreadyPassedAssumptions A list of assumptions that have already been
	 *                                 evaluated.
	 * @return True if the assumption has been evaluated; false otherwise.
	 */
	protected boolean hasMatch(GraphAssumption assumption, List<GraphAssumption> alreadyPassedAssumptions) {
		for (GraphAssumption curr : alreadyPassedAssumptions) {
			if (curr.getId().equals(assumption.getId())) {
				return true;
			}
		}
		return false;
	}
}
