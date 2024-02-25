package rest.general;

import java.util.List;

public interface TreeSubAssumptionEvaluator {
	/**
	 * Evaluates the sub-assumptions associated with the given assumption.
	 *
	 * @param assumption The assumption whose sub-assumptions need to be evaluated.
	 * @return The list of results of the evaluated sub-assumptions.
	 */
	public List<Boolean> evaluateSubAssumptions(AbstractAssumptionNode assumption);
}