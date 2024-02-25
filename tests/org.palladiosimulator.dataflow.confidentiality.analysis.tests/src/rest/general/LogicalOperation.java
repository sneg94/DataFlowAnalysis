package rest.general;

import java.util.List;

/**
 * This interface represents a logical operation that can be evaluated based on
 * a list of boolean assumptions.
 */
public interface LogicalOperation {

	/**
	 * Evaluates the logical operation based on the provided list of boolean
	 * assumption results.
	 *
	 * @param assumptionResults The list of boolean assumption results to be used in
	 *                          the evaluation.
	 * @return The result of the logical operation evaluation as a boolean value.
	 */
	public boolean evaluate(List<Boolean> assumptionResults);
}
