package rest.general.operations;

import java.util.List;
import rest.general.LogicalOperation;

/**
 * The OROperation class implements the LogicalOperation interface and
 * represents the logical OR operation for evaluating complex dependencies.
 */
public class OROperation implements LogicalOperation {

	/**
	 * Evaluates the logical OR operation for the given list of sub-assumptions.
	 *
	 * @param assumptionResults The list of boolean assumption results to be
	 *                          evaluated.
	 * @return true if at least one sub-assumption evaluates to true, false
	 *         otherwise.
	 */
	@Override
	public boolean evaluate(List<Boolean> assumptionResults) {
		return assumptionResults.stream().anyMatch(result -> result);
	}
}
