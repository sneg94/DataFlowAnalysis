package org.dataflowanalysis.analysis.tests.constraint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.file.Paths;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.log4j.Level;
import org.dataflowanalysis.analysis.DataFlowConfidentialityAnalysis;
import org.dataflowanalysis.analysis.core.AbstractActionSequenceElement;
import org.dataflowanalysis.analysis.core.ActionSequence;
import org.dataflowanalysis.analysis.core.CharacteristicValue;
import org.dataflowanalysis.analysis.core.DataFlowVariable;
import org.dataflowanalysis.analysis.tests.constraint.data.ConstraintData;
import org.dataflowanalysis.analysis.tests.constraint.data.ConstraintViolations;
import org.junit.jupiter.api.Test;

public class ConstraintResultTest extends ConstraintTest {
	/**
     * Indicates whether an element in an action sequence violates the constraint of the travel
     * planner model
     * 
     * @param node
     *            Element of the action sequence
     * @return Returns true, if the constraint is violated. Otherwise, the method returns false.
     */
    private boolean travelPlannerCondition(AbstractActionSequenceElement<?> node) {
    	List<String> assignedRoles = node.getNodeCharacteristicIdsWithType("AssignedRoles");
    	List<List<String>> grantedRoles = node.getDataFlowCharacteristicIdsWithType("GrantedRoles");
    	
        printNodeInformation(node);
        
        for(List<String> dataFlowCharacteristicIds : grantedRoles) {
        	if(!dataFlowCharacteristicIds.isEmpty() &&
        			dataFlowCharacteristicIds.stream()
        			.distinct()
        			.filter(it -> assignedRoles.contains(it))
        			.collect(Collectors.toList())
        			.isEmpty()) {
        		return true;
        	}
        }
        return false;
    }

    /**
     * Indicates whether an element in an action sequence violates the constraint of the
     * international online shop model
     * 
     * @param node
     *            Element of the action sequence
     * @return Returns true, if the constraint is violated. Otherwise, the method returns false.
     */
    private boolean internationalOnlineShopCondition(AbstractActionSequenceElement<?> node) {
        List<String> serverLocation = node.getNodeCharacteristicNamesWithType("ServerLocation");
        List<String> dataSensitivity = node.getDataFlowCharacteristicNamesWithType("DataSensitivity").stream()
        		.flatMap(it -> it.stream()).collect(Collectors.toList());
        printNodeInformation(node);

        return dataSensitivity.stream()
                .anyMatch(l -> l.equals("Personal")) && serverLocation.stream()
                        .anyMatch(l -> l.equals("nonEU"));
    }
    
    /**
     * Indicates whether an element in an action sequence violates the constraint of the
     * return test model
     * 
     * @param node
     *            Element of the action sequence
     * @return Returns true, if the constraint is violated. Otherwise, the method returns false.
     */
    private boolean returnCondition(AbstractActionSequenceElement<?> node) {
    	List<String> assignedNode = node.getNodeCharacteristicNamesWithType("AssignedRole");
    	List<String> assignedVariables = node.getDataFlowCharacteristicNamesWithType("AssignedRole").stream()
    			.flatMap(it -> it.stream()).collect(Collectors.toList());
    	
        printNodeInformation(node);
        if (assignedNode.isEmpty() || assignedVariables.isEmpty()) {
        	return false;
        }
        assignedNode.removeAll(assignedVariables);
        return !assignedNode.isEmpty();
    }
    
    /**
     * Tests, whether the analysis correctly identifies violations for the example models
     * <p>
     * Fails if the analysis does not propagate the correct characteristics for each ActionSequence
     */
    @Test
    public void travelPlannerTestConstraintResults() {
    	travelPlannerAnalysis.setLoggerLevel(Level.TRACE);
    	Predicate<AbstractActionSequenceElement<?>> constraint = node -> travelPlannerCondition(node);
    	List<ConstraintData> constraintData = ConstraintViolations.travelPlannerViolations;
    	testAnalysis(travelPlannerAnalysis, constraint, constraintData);
    }
    
    /**
     * Tests, whether the analysis correctly identifies violations for the example models
     * <p>
     * Fails if the analysis does not propagate the correct characteristics for each ActionSequence
     */
    @Test
    public void internationalOnlineShopTestConstraintResults() {
    	internationalOnlineShopAnalysis.setLoggerLevel(Level.TRACE);
    	Predicate<AbstractActionSequenceElement<?>> constraint = node -> internationalOnlineShopCondition(node);
    	List<ConstraintData> constraintData = ConstraintViolations.internationalOnlineShopViolations;
    	testAnalysis(internationalOnlineShopAnalysis, constraint, constraintData);
    }
    
    /**
     * Tests, whether the analysis correctly identifies violations for the example models
     * <p>
     * Fails if the analysis does not propagate the correct characteristics for each ActionSequence
     */
    @Test
    public void oneAssemblyMultipleResourceTestConstraintResults() {
    	DataFlowConfidentialityAnalysis analysis = 
    			super.initializeAnalysis(Paths.get("models", "OneAssembyMultipleResourceContainerTest", "default.usagemodel"), 
    					Paths.get("models", "OneAssembyMultipleResourceContainerTest", "default.allocation"),
    					Paths.get("models", "OneAssembyMultipleResourceContainerTest", "default.nodecharacteristics"));
    	analysis.setLoggerLevel(Level.TRACE);
    	Predicate<AbstractActionSequenceElement<?>> constraint = node -> internationalOnlineShopCondition(node);
    	List<ConstraintData> constraintData = ConstraintViolations.multipleResourcesViolations;
    	testAnalysis(analysis, constraint, constraintData);
    }
    
    /**
     * Tests, whether the analysis correctly identifies violations for the example models
     * <p>
     * Fails if the analysis does not propagate the correct characteristics for each ActionSequence
     */
    @Test
    public void returnTestConstraintResults() {
    	DataFlowConfidentialityAnalysis returnAnalysis = 
    			super.initializeAnalysis(Paths.get("models", "ReturnTestModel", "default.usagemodel"), Paths.get("models", "ReturnTestModel", "default.allocation"),
    					Paths.get("models", "ReturnTestModel", "default.nodecharacteristics"));
    	Predicate<AbstractActionSequenceElement<?>> constraint = node -> returnCondition(node);
    	returnAnalysis.setLoggerLevel(Level.TRACE);
    	List<ConstraintData> constraintData = ConstraintViolations.returnViolations;
    	testAnalysis(returnAnalysis, constraint, constraintData);
    }
    
    public void testAnalysis(DataFlowConfidentialityAnalysis analysis, Predicate<AbstractActionSequenceElement<?>> constraint, List<ConstraintData> constraintData) {
    	List<ActionSequence> actionSequences = analysis.findAllSequences();
    	List<ActionSequence> evaluatedSequences = analysis.evaluateDataFlows(actionSequences);
    	List<AbstractActionSequenceElement<?>> results = evaluatedSequences.stream()
    			.map(it -> analysis.queryDataFlow(it, constraint))
    			.flatMap(it -> it.stream())
    			.collect(Collectors.toList());
    	
    	assertEquals(constraintData.size(), results.size(), "Incorrect count of violations found");
    	
    	for(ConstraintData constraintNodeData : constraintData) {
    		var violatingNode = results.stream()
    				.filter(it -> constraintNodeData.matches(it))
    				.findFirst();
    		
    		if (violatingNode.isEmpty()) {
    			fail("Could not find node for expected constraint violation");
    		}
    		
    		List<CharacteristicValue> nodeCharacteristics = violatingNode.get().getAllNodeCharacteristics();
    		List<DataFlowVariable> dataFlowVariables = violatingNode.get().getAllDataFlowVariables();
    		
    		assertEquals(constraintNodeData.nodeCharacteristicsCount(), nodeCharacteristics.size());
    		assertEquals(constraintNodeData.dataFlowVariablesCount(), dataFlowVariables.size());
    		
    		for(CharacteristicValue characteristicValue : nodeCharacteristics) {
    			assertTrue(constraintNodeData.hasNodeCharacteristic(characteristicValue));
    		}
    		
    		for(DataFlowVariable dataFlowVariable : dataFlowVariables) {
    			assertTrue(constraintNodeData.hasDataFlowVariable(dataFlowVariable));
    		}
    	}
    }
}
