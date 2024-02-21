package rest.general.operations;

import java.util.List;

import rest.general.AbstractAssumptionNode;
import rest.general.AbstractSubAssumptionAnalyzer;
import rest.general.LogicalOperation;

/**
 * The XOROperation class implements the LogicalOperation interface and
 * represents the logical XOR (exclusive OR) operation for evaluating complex
 * dependencies.
 */
class XOROperation implements LogicalOperation {
	private final AbstractSubAssumptionAnalyzer subAssumptionAnalyzer;

	/**
	 * Constructs an XOROperation with the specified sub-assumption analyzer.
	 *
	 * @param subAssumptionAnalyzer The analyzer used for evaluating sub-assumptions
	 *                              if their logical operation is null.
	 */
	public XOROperation(AbstractSubAssumptionAnalyzer subAssumptionAnalyzer) {
		this.subAssumptionAnalyzer = subAssumptionAnalyzer;
	}

	/**
	 * Evaluates the XOR operation for the given list of sub-assumptions.
	 *
	 * @param subAssumptions The list of sub-assumptions to be evaluated.
	 * @return true if an odd number of sub-assumptions evaluate to true, false
	 *         otherwise.
	 */
	@Override
	public boolean evaluate(List<AbstractAssumptionNode> subAssumptions) {
		long trueCount = subAssumptions.stream()
				.filter(subAssumption -> subAssumption.getLogicalOperation() == null
						? subAssumptionAnalyzer.evaluateAssumptions(List.of(subAssumption.getAssumption()), false)
						: subAssumption.getLogicalOperation().evaluate(subAssumption.getSubAssumptions()))
				.count();
		return trueCount % 2 == 1;
	}
}
