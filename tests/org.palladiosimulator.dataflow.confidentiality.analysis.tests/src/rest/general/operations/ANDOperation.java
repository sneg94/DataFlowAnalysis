package rest.general.operations;

import java.util.List;

import rest.general.AbstractAssumptionNode;
import rest.general.AbstractSubAssumptionAnalyzer;
import rest.general.LogicalOperation;

/**
 * The ANDOperation class implements the LogicalOperation interface and
 * represents the logical AND operation for evaluating complex dependencies.
 */
class ANDOperation implements LogicalOperation {
	private final AbstractSubAssumptionAnalyzer subAssumptionAnalyzer;

	/**
	 * Constructs an ANDOperation with the specified sub-assumption analyzer.
	 *
	 * @param subAssumptionAnalyzer The analyzer used for evaluating sub-assumptions
	 *                              if their logical operation is null.
	 */
	public ANDOperation(AbstractSubAssumptionAnalyzer subAssumptionAnalyzer) {
		this.subAssumptionAnalyzer = subAssumptionAnalyzer;
	}

	/**
	 * Evaluates the AND operation for the given list of sub-assumptions.
	 *
	 * @param subAssumptions The list of sub-assumptions to be evaluated.
	 * @return true if all sub-assumptions evaluate to true, false otherwise.
	 */
	@Override
	public boolean evaluate(List<AbstractAssumptionNode> subAssumptions) {
		return subAssumptions.stream()
				.allMatch(subAssumption -> subAssumption.getLogicalOperation() == null
						? subAssumptionAnalyzer.evaluateAssumptions(List.of(subAssumption.getAssumption()), false)
						: subAssumption.getLogicalOperation().evaluate(subAssumption.getSubAssumptions()));
	}
}
