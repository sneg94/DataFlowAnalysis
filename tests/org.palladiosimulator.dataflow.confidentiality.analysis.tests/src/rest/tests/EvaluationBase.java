package rest.tests;

/**
 * Abstract class for evaluation tests related to the CoronaWarnApp.
 */
public abstract class EvaluationBase extends TestBase {

	@Override
	protected String getBaseFolderName() {
		return "casestudies/CaseStudy-CoronaWarnApp";
	}

	@Override
	protected String getFolderName() {
		return "CoronaWarnApp_UncertaintyScenario2";
	}

}
