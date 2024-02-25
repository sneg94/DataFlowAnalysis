package rest.general.operations;

import java.util.List;
import rest.general.LogicalOperation;

/**
 * The XOROperation class implements the LogicalOperation interface and
 * represents the logical XOR operation for evaluating complex dependencies.
 */
public class XOROperation implements LogicalOperation {

	/**
	 * Evaluates the XOR operation for the given list of sub-assumptions.
	 *
	 * @param assumptionResults The list of boolean assumption results to be
	 *                          evaluated.
	 * @return true if exactly one sub-assumption evaluates to true, false
	 *         otherwise.
	 */
	@Override
	public boolean evaluate(List<Boolean> assumptionResults) {
		long countTrue = assumptionResults.stream().filter(result -> result).count();
		return countTrue == 1;
	}
}
