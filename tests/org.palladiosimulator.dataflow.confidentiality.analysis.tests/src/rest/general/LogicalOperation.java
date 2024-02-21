package rest.general;

import java.util.List;

/**
 * The LogicalOperation interface represents a logical operation with methods
 * for evaluation.
 */
public interface LogicalOperation {
	boolean evaluate(List<AbstractAssumptionNode> subAssumption);
}