## Palladio-Addons-DataFlowConfidentiality-Analysis

This repository contains an analysis to extract dataflows from a palladio modelling project,
analyze their data characteristics and formulate constraints to check if definied bounds are respected by the model.
For more information visit the [official page](https://fluidtrust.ipd.kit.edu/home/) of the project.

## Installation

- Install the version `2022-12` of the Eclipse Modelling Tools from the
  [official site](https://www.eclipse.org/downloads/packages/release/2022-12/r/eclipse-modeling-tools)
- Clone this repository
- Import the [dependencies.p2f](https://raw.githubusercontent.com/PalladioSimulator/Palladio-Addons-DataFlowConfidentiality-Analysis/main/dependencies.p2f) file into Eclipse to install the dependencies of the project.
  This is achieved by going to File->Import->General->Install from File
- Import the projects in the bundles folder into Eclipse

## Usage

First step of using the project is to create a modelling project in Eclipse.
Furthermore, the Activator class,
an example found at the project tests/org.palladiosimulator.dataflow.confidentiality.analysis.tests,
is needed to load the models.
It determines the base location of the modelling project.
The `PROJECT_NAME` in the example code is determined by the project name
(e.g. org.palladiosimulator.dataflow.confidentiality.analysis.tests) of the modelling project that should be analyzed.
The `USAGE_MODEL_PATH`, `ALLOCATION_MODEL_PATH` and `NODE_MODEL_PATH` describe the path of to the usage, allocation and node characteristics model relative to the root of the modelling project.

Currently, the analysis only supports analyzing a project which has a activator.
Should you wish to use models outside of a project, you must use relative paths from an project with an Activator class.
This is step is required, as standalone initialization of the analysis requires an eclipse project to work.

A basic analysis can be executed with the following example:

```java
public class Main {
  public static void main(String[] args) {
      DataFlowConfidentialityAnalysis analysis = new DataFlowAnalysisBuilder()
          .standalone()
          .modelProjectName("<PROJECT_NAME>")
          .useBuilder(new PCMDataFlowConfidentialityAnalysisBuilder())
          .usePluginActivator(Activator.class)
          .useUsageModel("<USAGE_MODEL_PATH>")
          .useAllocationModel("<ALLOCATION_MODEL_PATH>")
          .useNodeCharacteristicsModel("<NODE_MODEL_PATH>")
          .build();

      analysis.setLoggerLevel(Level.TRACE); // Set desired logger level. Level.TRACE provides additional propagation Information
      analysis.initializeAnalysis();

      List<ActionSequence> actionSequences = analysis.findAllSequences();

      List<ActionSequence> propagationResult = analysis.evaluateDataFlows(actionSequences);
      
      for(ActionSequence actionSequence : propagationResult) {
        List<AbstractActionSequenceElement<?>> violations = analysis.queryDataFlow(actionSequence,
          it -> false // Constraint goes here, return true, if constraint is violated
        );
      }
    }
}
```

Additional examples for the TravelPlanner, InternationalOnlineShop and BranchingOnlineShop can be found in the tests and testmodel projects, that can be found at the tests folder in the root of the project.



## Sub-assumption Analyzer

In addition to the provided analysis, I have developed a Sub-assumption Analyzer as a security analysis tool that utilizes data flow analysis to verify assumptions by examining sub-assumptions. Assumptions are only fulfilled if their corresponding sub-assumptions are also fulfilled. The security analysis serves as an adapter for the Front-End Assumption Analyzer, which can be found at [Assumption Analyzer](https://github.com/sneg94/GUI).



