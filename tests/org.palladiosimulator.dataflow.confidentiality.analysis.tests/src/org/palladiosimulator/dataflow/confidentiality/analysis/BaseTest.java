package org.palladiosimulator.dataflow.confidentiality.analysis;

import static org.palladiosimulator.dataflow.confidentiality.analysis.AnalysisUtils.TEST_MODEL_PROJECT_NAME;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.palladiosimulator.dataflow.confidentiality.analysis.builder.DataFlowConfidentialityAnalysisBuilder;
import org.palladiosimulator.dataflow.confidentiality.analysis.testmodels.Activator;

@TestInstance(Lifecycle.PER_CLASS)
public class BaseTest {
    protected DataFlowConfidentialityAnalysis onlineShopAnalysis;
    protected DataFlowConfidentialityAnalysis internationalOnlineShopAnalysis;
    protected DataFlowConfidentialityAnalysis travelPlannerAnalysis;

    @BeforeAll
    public void initializeOnlineShopAnalysis() {
        final var usageModelPath = Paths.get("models", "BranchingOnlineShop", "default.usagemodel")
            .toString();
        final var allocationPath = Paths.get("models", "BranchingOnlineShop", "default.allocation")
            .toString();

        onlineShopAnalysis = new DataFlowConfidentialityAnalysisBuilder(TEST_MODEL_PROJECT_NAME, Activator.class)
        		.standalone().legacy()
        		.registerUsageModel(usageModelPath)
        		.registerAllocationModel(allocationPath)
        		.build();

        onlineShopAnalysis.initalizeAnalysis();
    }

    @BeforeAll
    public void initializeInternationalOnlineShopAnalysis() {
        final var usageModelPath = Paths.get("models", "InternationalOnlineShop", "default.usagemodel")
            .toString();
        final var allocationPath = Paths.get("models", "InternationalOnlineShop", "default.allocation")
            .toString();
        internationalOnlineShopAnalysis = new DataFlowConfidentialityAnalysisBuilder(TEST_MODEL_PROJECT_NAME, Activator.class)
        		.standalone().legacy()
        		.registerUsageModel(usageModelPath)
        		.registerAllocationModel(allocationPath)
        		.build();

        internationalOnlineShopAnalysis.initalizeAnalysis();
    }

    @BeforeAll
    public void initializeTravelPlannerAnalysis() {
        final var usageModelPath = Paths.get("models", "TravelPlanner", "travelPlanner.usagemodel")
            .toString();
        final var allocationPath = Paths.get("models", "TravelPlanner", "travelPlanner.allocation")
            .toString();

        travelPlannerAnalysis = new DataFlowConfidentialityAnalysisBuilder(TEST_MODEL_PROJECT_NAME, Activator.class)
        		.standalone().legacy()
        		.registerUsageModel(usageModelPath)
        		.registerAllocationModel(allocationPath)
        		.build();
        travelPlannerAnalysis.initalizeAnalysis();
    }
    
    protected DataFlowConfidentialityAnalysis initializeAnalysis(Path usagePath, Path allocationPath) {
    	var analysis = new DataFlowConfidentialityAnalysisBuilder(TEST_MODEL_PROJECT_NAME, Activator.class)
    			.standalone().legacy()
    			.registerUsageModel(usagePath.toString())
    			.registerAllocationModel(allocationPath.toString())
    			.build();
    	analysis.initalizeAnalysis();
    	return analysis;
    }
    
    protected DataFlowConfidentialityAnalysis initializeAnalysis(Path usagePath, Path allocationPath, Path nodePath) {
    	var analysis = new DataFlowConfidentialityAnalysisBuilder(TEST_MODEL_PROJECT_NAME, Activator.class)
    			.standalone()
    			.registerUsageModel(usagePath.toString())
    			.registerAllocationModel(allocationPath.toString())
    			.registerNodeCharacteristicsModel(nodePath.toString())
    			.build();
    	analysis.initalizeAnalysis();
    	return analysis;
    }
}
