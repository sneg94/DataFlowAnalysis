package rest.entities;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * An extended {@link SecurityCheckAssumption} adding properties tailored
 * towards a more high-level analysis.
 *
 */
public class GraphAssumption extends SecurityCheckAssumption implements Cloneable {
	/**
	 * Custom {@link Comparator} that compares {@link GraphAssumption}s based on
	 * their unique IDs.
	 */
	public static class AssumptionComparator implements Comparator<GraphAssumption> {
		@Override
		public int compare(GraphAssumption a1, GraphAssumption a2) {
			return a1.getId().compareTo(a2.getId());
		}
	}

	/**
	 * The user-specified name of the {@link GraphAssumption}.
	 */
	private String name;
	/**
	 * A flag indicating whether the user marked the {@link GraphAssumption} as
	 * manually analyzed.
	 */
	private boolean manuallyAnalyzed;
	/**
	 * A {@link Set} containing the unique <code>id</code>s of all
	 * {@link GraphAssumption}s on which this {@link GraphAssumption} depends.
	 */
	private Set<UUID> dependencies;
	/**
	 * The risk associated with the {@link GraphAssumption}.
	 */
	private Double risk; // -

	/**
	 * Default constructor that only initializes the <code>id</code> of the
	 * {@link GraphAssumption} with a randomly generated {@link UUID}.
	 */
	public GraphAssumption() {
		super();
		this.dependencies = new HashSet<>();
		this.manuallyAnalyzed = false;
	}

	/**
	 * Gets the name of the {@link GraphAssumption}.
	 *
	 * @return The name of the assumption.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the {@link GraphAssumption}.
	 *
	 * @param name The name that should be set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the <code>manuallyAnalyzed</code> property of the
	 * {@link GraphAssumption}.
	 *
	 * @return The <code>manuallyAnalyzed</code> property.
	 */
	public boolean getManuallyAnalyzed() {
		return this.manuallyAnalyzed;
	}

	/**
	 * Sets the <code>manuallyAnalyzed</code> property of the
	 * {@link GraphAssumption}.
	 *
	 * @param manuallyAnalyzed The state that should be set.
	 */
	public void setManuallyAnalyzed(boolean manuallyAnalyzed) {
		this.manuallyAnalyzed = manuallyAnalyzed;
	}

	/**
	 * Gets the numerical risk associated with the {@link GraphAssumption}.
	 *
	 * @return The risk that associated with the assumption.
	 */
	public Double getRisk() {
		return this.risk;
	}

	/**
	 * Sets the numerical risk associated with the {@link GraphAssumption}.
	 *
	 * @param risk The risk that should be set.
	 */
	public void setRisk(Double risk) {
		this.risk = risk;
	}

	/**
	 * Gets the {@link Collection} containing the {@link UUID}s of the
	 * {@link GraphAssumption}s on which this {@link GraphAssumption} is depending.
	 *
	 * @return The {@link Collection} containing the {@link UUID}s.
	 */
	public Collection<UUID> getDependencies() {
		return this.dependencies;
	}

	/**
	 * Checks whether all mandatory fields of the {@link GraphAssumption} (i.e.,
	 * <code>type</code>, <code>name</code>, and <code>description</code> have been
	 * sufficiently specified).
	 *
	 * @return <code>true</code> if all the mandatory fields are sufficiently
	 *         specified and <code>false</code> otherwise.
	 */
	public boolean isSufficientlySpecified() {
		return this.type != null && this.name != null && this.description != null && !this.name.isEmpty()
				&& !this.description.isEmpty();
	}

	/**
	 * Checks whether this {@link GraphAssumption} is semantically equal to
	 * <code>otherAssumption</code>, meaning that they both contain the same
	 * semantic values.
	 *
	 * <p>
	 * <b>Note</b>: Even two different {@link GraphAssumption} instances can be
	 * semantically equal if they contain the same values (w.r.t.
	 * <code>equals</code> of the individual fields).
	 * </p>
	 *
	 * @param otherAssumption The {@link GraphAssumption} that should be
	 *                        semantically compared to this {@link GraphAssumption}.
	 * @return <code>true</code> if both {@link GraphAssumption}s are semantically
	 *         equal and <code>false</code> otherwise.
	 */
	public boolean semanticallyEqualTo(GraphAssumption otherAssumption) {
		return otherAssumption != null && Objects.equals(this.getId(), otherAssumption.getId())
				&& Objects.equals(this.name, otherAssumption.name) && Objects.equals(this.type, otherAssumption.type)
				&& Objects.equals(this.affectedEntities, otherAssumption.affectedEntities)
				&& Objects.equals(this.dependencies, otherAssumption.dependencies)
				&& Objects.equals(this.description, otherAssumption.description)
				&& Objects.equals(this.constraint, otherAssumption.constraint)
				&& Objects.equals(this.probabilityOfViolation, otherAssumption.probabilityOfViolation)
				&& Objects.equals(this.risk, otherAssumption.risk)
				&& Objects.equals(this.impact, otherAssumption.impact)
				&& Objects.equals(this.analyzed, otherAssumption.analyzed)
				&& this.manuallyAnalyzed == otherAssumption.manuallyAnalyzed;
	}

	/**
	 * Updates the fields of this {@link GraphAssumption} based on the information
	 * contained in the specified {@link SecurityCheckAssumption}.
	 *
	 * @param securityCheckAssumption The {@link SecurityCheckAssumption} instance
	 *                                with whose information this
	 *                                {@link GraphAssumption} should be updated.
	 */
	public void updateWith(SecurityCheckAssumption securityCheckAssumption) {
		assert securityCheckAssumption.getId().equals(this.getId());

		this.type = securityCheckAssumption.type;
		this.description = securityCheckAssumption.description;
		this.constraint = securityCheckAssumption.constraint;
		this.affectedEntities = securityCheckAssumption.affectedEntities;
		this.probabilityOfViolation = securityCheckAssumption.probabilityOfViolation;
		this.impact = securityCheckAssumption.impact;
		this.analyzed = securityCheckAssumption.analyzed;
	}

	/**
	 * Creates a clone of this {@link GraphAssumption}.
	 *
	 * @return The created clone.
	 */
	@Override
	public GraphAssumption clone() {
		GraphAssumption clone = (GraphAssumption) super.clone();

		// UUID, String and primitive wrapper instances are immutable.
		clone.name = this.name;
		clone.manuallyAnalyzed = this.manuallyAnalyzed;
		clone.dependencies = new HashSet<>(this.dependencies);
		clone.risk = this.risk;

		return clone;
	}
}
