package rest.general.operations;

import java.util.List;

import rest.general.AbstractAssumptionNode;
import rest.general.AbstractSubAssumptionAnalyzer;
import rest.general.LogicalOperation;

/**
 * The OROperation class implements the LogicalOperation interface and
 * represents the logical OR operation for evaluating complex dependencies.
 */
class OROperation implements LogicalOperation {
	private final AbstractSubAssumptionAnalyzer subAssumptionAnalyzer;

	/**
	 * Constructs an OROperation with the specified sub-assumption analyzer.
	 *
	 * @param subAssumptionAnalyzer The analyzer used for evaluating sub-assumptions
	 *                              if their logical operation is null.
	 */
	public OROperation(AbstractSubAssumptionAnalyzer subAssumptionAnalyzer) {
		this.subAssumptionAnalyzer = subAssumptionAnalyzer;
	}

	/**
	 * Evaluates the OR operation for the given list of sub-assumptions.
	 *
	 * @param subAssumptions The list of sub-assumptions to be evaluated.
	 * @return true if at least one sub-assumption evaluates to true, false
	 *         otherwise.
	 */
	@Override
	public boolean evaluate(List<AbstractAssumptionNode> subAssumptions) {
		return subAssumptions.stream()
				.anyMatch(subAssumption -> subAssumption.getLogicalOperation() == null
						? !(subAssumptionAnalyzer.evaluateAssumptions(List.of(subAssumption.getAssumption()), false))
						: subAssumption.getLogicalOperation().evaluate(subAssumption.getSubAssumptions()));
	}
}
