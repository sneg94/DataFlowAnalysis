package rest.general;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import rest.entities.GraphAssumption;

/**
 * The AbstractAssumptionNode class represents an abstract node in an assumption
 * tree. It encapsulates an assumption, a list of sub-assumptions, and a logical
 * operation for evaluating complex dependencies.
 */
public abstract class AbstractAssumptionNode {

	private GraphAssumption assumption;
	private List<AbstractAssumptionNode> subAssumptions;
	private LogicalOperation logicalOperation;
	private List<Boolean> subAssumptionResults;

	/**
	 * Constructs an AbstractAssumptionNode with the specified assumption,
	 * sub-assumptions, and logical operation.
	 *
	 * @param assumption       The assumption associated with this node.
	 * @param subAssumptions   The sub-assumptions of this node.
	 * @param logicalOperation The logical operation associated with this node.
	 */
	public AbstractAssumptionNode(GraphAssumption assumption, List<AbstractAssumptionNode> subAssumptions,
			LogicalOperation logicalOperation) {
		this.assumption = assumption;
		this.subAssumptions = subAssumptions;
		this.logicalOperation = logicalOperation;
		this.subAssumptionResults = new ArrayList<>();
	}

	/**
	 * Gets the assumption associated with this node.
	 *
	 * @return The assumption.
	 */
	public GraphAssumption getAssumption() {
		return assumption;
	}

	/**
	 * Returns an unmodifiable view of the sub-assumptions list.
	 *
	 * @return An unmodifiable view of the sub-assumptions list.
	 */
	public List<AbstractAssumptionNode> getSubAssumptions() {
		return Collections.unmodifiableList(this.subAssumptions);
	}

	/**
	 * Gets the logical operation associated with this node.
	 *
	 * @return The logical operation.
	 */
	public LogicalOperation getLogicalOperation() {
		return logicalOperation;
	}

	/**
	 * Adds a sub-assumption result to the list of sub-assumption results.
	 *
	 * @param result The result of a sub-assumption evaluation.
	 */
	public void addSubAssumptionResult(Boolean result) {
		this.subAssumptionResults.add(result);
	}

	/**
	 * Evaluates the assumption associated with this node based on the logical
	 * operation and the list of sub-assumptions.
	 *
	 * @return true if the assumption holds, false otherwise.
	 */
	public boolean evaluate() {
		return logicalOperation.evaluate(this.subAssumptionResults);
	}
}
