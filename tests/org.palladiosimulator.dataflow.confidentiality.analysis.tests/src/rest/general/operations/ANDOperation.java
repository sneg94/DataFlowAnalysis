package rest.general.operations;

import java.util.List;
import rest.general.LogicalOperation;

/**
 * The ANDOperation class implements the LogicalOperation interface and
 * represents the logical AND operation for evaluating complex dependencies.
 */
public class ANDOperation implements LogicalOperation {

	/**
	 * Evaluates the logical AND operation for the given list of sub-assumptions.
	 *
	 * @param assumptionResults The list of boolean assumption results to be
	 *                          evaluated.
	 * @return true if all sub-assumptions evaluate to true, false otherwise.
	 */
	@Override
	public boolean evaluate(List<Boolean> assumptionResults) {
		return assumptionResults.stream().allMatch(result -> result);
	}
}
