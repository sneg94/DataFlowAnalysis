package rest.tests;

import java.util.ArrayList;
import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rest.entities.AssumptionType;
import rest.entities.GraphAssumption;
import rest.entities.ModelEntity;

/**
 * Class for evaluating scenarios related to the CoronaWarnApp.
 */
public class EvaluationScenario extends EvaluationBase {
	protected Collection<GraphAssumption> assumptions = new ArrayList<GraphAssumption>();
	protected GraphAssumption mainAssumption = null;
	protected GraphAssumption subAssumption1 = null;
	protected GraphAssumption subAssumption2 = null;

	/**
	 * Initializes the graph assumptions before each test.
	 */
	@BeforeEach
	public void initializeGraphAssumptions() {
		// initialize first subAssumption
		this.subAssumption1 = new GraphAssumption();
		this.subAssumption1.setName("Sub-Assumption 1");
		ModelEntity modelEntity1 = new ModelEntity("_JiPBICHdEd6lJo4DCALHMw", "default.allocation", "Allocation",
				"defaultAllocation", null);
		this.subAssumption1.getAffectedEntities().add(modelEntity1);
		this.subAssumption1.setManuallyAnalyzed(false);
		this.subAssumption1.setRisk(null);
		this.subAssumption1.setDescription("");
		this.subAssumption1.setConstraint(null);
		this.subAssumption1.setProbabilityOfViolation(0.0);
		this.subAssumption1.setType(AssumptionType.RESOLVE_UNCERTAINTY);

		// initialize second subAssumption
		this.subAssumption2 = new GraphAssumption();
		this.subAssumption2.setName("Sub-Assumption 2");
		ModelEntity modelEntity2 = new ModelEntity("_dqFLECHbEd6tG9VclJz3cw", "default.repository", "Repository",
				"defaultRepository", null);
		this.subAssumption1.getAffectedEntities().add(modelEntity2);
		this.subAssumption2.setManuallyAnalyzed(false);
		this.subAssumption2.setRisk(null);
		this.subAssumption2.setDescription("");
		this.subAssumption2.setConstraint(null);
		this.subAssumption2.setProbabilityOfViolation(0.0);
		this.subAssumption2.setType(AssumptionType.RESOLVE_UNCERTAINTY);

		// initialize mainAssumption
		this.mainAssumption = new GraphAssumption();
		this.mainAssumption.setName("Main Assumption");
		this.mainAssumption.setManuallyAnalyzed(false);
		this.mainAssumption.setRisk(null);
		this.mainAssumption.setDescription("");
		this.mainAssumption.setConstraint(null);
		this.mainAssumption.setProbabilityOfViolation(0.0);
		this.mainAssumption.setType(AssumptionType.RESOLVE_UNCERTAINTY);
		this.mainAssumption.getDependencies().add(subAssumption1.getId());
		this.mainAssumption.getDependencies().add(subAssumption2.getId());

	}

	@Test
	public void testOneSubAssumptionViolation() {
		configureTest(true);
		assertSubAssumptionViolations(true, assumptions);
	}

	@Test
	public void testTwoSubAssumptionViolations() {
		configureTest(false);
		assertSubAssumptionViolations(true, assumptions);

	}

	@Test
	public void testNoSubAssumptionViolations() {
		configForNoViolation();
		assertSubAssumptionViolations(false, assumptions);

	}

	@Test
	void testProbabilityViolationIfOneViolation() {
		configureTest(true);
		assertPobabilityViolationIsSet(1.0, "Main Assumption", assumptions);
		assertPobabilityViolationIsSet(1.0, "Sub-Assumption 2", assumptions);
		assertPobabilityViolationIsSet(0.0, "Sub-Assumption 1", assumptions);
	}

	@Test
	void testProbabilityViolationIfTwoViolations() {
		configureTest(false);
		assertPobabilityViolationIsSet(1.0, "Main Assumption", assumptions);
		assertPobabilityViolationIsSet(1.0, "Sub-Assumption 2", assumptions);
		assertPobabilityViolationIsSet(1.0, "Sub-Assumption 1", assumptions);
	}

	@Test
	void testProbabilityViolationIfNoViolations() {
		configForNoViolation();
		assertPobabilityViolationIsSet(0.0, "Main Assumption", assumptions);
		assertPobabilityViolationIsSet(0.0, "Sub-Assumption 2", assumptions);
		assertPobabilityViolationIsSet(0.0, "Sub-Assumption 1", assumptions);
	}

	@Test
	void testAnalyzed() {
		configureTest(false);
		assertAnalyzedIsSet(true, assumptions);
	}

	/**
	 * Helper method to configure the test scenarios with one or two violations.
	 *
	 * @param oneViolation Boolean indicating whether there is one or two
	 *                     violations.
	 */
	private void configureTest(boolean oneViolation) {
		String description = "Node Constraints: IllegalDeploymentLocation";
		if (oneViolation) {
			subAssumption2.setDescription(description);
		} else {
			subAssumption1.setDescription(description);
			subAssumption2.setDescription(description);
		}
		this.assumptions.add(mainAssumption);
		this.assumptions.add(subAssumption1);
		this.assumptions.add(subAssumption2);
		dataFlowAnalysisAdapter.setAssumptions(assumptions);
	}

	private void configForNoViolation() {
		this.subAssumption1.setDescription("");
		this.subAssumption2.setDescription("");
		this.assumptions.add(mainAssumption);
		this.assumptions.add(subAssumption1);
		this.assumptions.add(subAssumption2);
		this.dataFlowAnalysisAdapter.setAssumptions(assumptions);
	}
}
