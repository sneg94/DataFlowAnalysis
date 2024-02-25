package rest.general;

/**
 * The AbstractAssumptionTree class represents an abstract tree structure of
 * assumptions and sub-assumptions.
 */
public abstract class AbstractAssumptionTree implements TreeSubAssumptionEvaluator {
	private AbstractAssumptionNode root;
	private TreeSubAssumptionEvaluator treeSubAssumptionEvaluator;

	/**
	 * Constructs an AbstractAssumptionTree with the specified root node.
	 *
	 * @param root                       The root node of the tree.
	 * @param treeSubAssumptionEvaluator The evaluator for sub-assumptions in the
	 *                                   tree.
	 */
	public AbstractAssumptionTree(AbstractAssumptionNode root, TreeSubAssumptionEvaluator treeSubAssumptionEvaluator) {
		this.root = root;
		this.treeSubAssumptionEvaluator = treeSubAssumptionEvaluator;
	}

	/**
	 * Gets the root node of the tree.
	 *
	 * @return The root node.
	 */
	public AbstractAssumptionNode getRoot() {
		return root;
	}

	/**
	 * Evaluates the tree structure to determine if assumptions and sub-assumptions
	 * are satisfied based on their dependencies and logical operations.
	 *
	 * @return True if the entire tree is satisfied; false otherwise.
	 */
	protected abstract boolean evaluateTree();
}
