package rest.general;

/**
 * The AbstractAssumptionTree class represents an abstract tree structure of
 * assumptions and sub-assumptions.
 */
public abstract class AbstractAssumptionTree {

	private AbstractAssumptionNode root;

	/**
	 * Constructs an AbstractAssumptionTree with the specified root node.
	 *
	 * @param root The root node of the tree.
	 */
	public AbstractAssumptionTree(AbstractAssumptionNode root) {
		this.root = root;
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
