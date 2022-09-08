package org.palladiosimulator.dataflow.confidentiality.analysis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.ActionSequence;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm.CallingSEFFActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm.CallingUserActionSequenceElement;

public class AnalysisUtils {

    private AnalysisUtils() {
        // Utility class
    }

    public static String TEST_MODEL_PROJECT_NAME = "org.palladiosimulator.dataflow.confidentiality.analysis.testmodels";

    public static void assertSequenceElement(ActionSequence sequence, int index, Class<?> expectedType) {
        assertNotNull(sequence.elements());
        assertTrue(sequence.elements()
            .size() >= index + 1);

        Class<?> actualType = sequence.elements()
            .get(index)
            .getClass();

        assertEquals(expectedType, actualType, createProblemMessage(index, expectedType, actualType));
    }

    public static void assertSequenceElements(ActionSequence sequence, Class<?>... expectedElementTypes) {
        var elements = sequence.elements();

        assertEquals(sequence.elements()
            .size(), expectedElementTypes.length);

        for (int i = 0; i < expectedElementTypes.length; i++) {
            Class<?> expectedType = expectedElementTypes[i];
            Class<?> actualType = elements.get(i)
                .getClass();

            assertEquals(expectedType, actualType, createProblemMessage(i, expectedType, actualType));

        }
    }

    private static String createProblemMessage(int index, Class<?> expectedType, Class<?> actualType) {
        return String.format("Type missmatch at index %d. Expected: %s, actual: %s.", index,
                expectedType.getSimpleName(), actualType.getSimpleName());
    }
    
    public static void assertSEFFSequenceElementContent(ActionSequence sequence, int index, String expectedName) {
    	assertNotNull(sequence.elements());
    	assertTrue(sequence.elements().size() >= index + 1);
    	
    	var element = sequence.elements().get(index);
    	
    	assertInstanceOf(CallingSEFFActionSequenceElement.class, element);
    	
    	var sequenceElement = (CallingSEFFActionSequenceElement) element;
    	assertEquals(expectedName, sequenceElement.getElement().getEntityName());
    }
    
    public static void assertUserSequenceElementContent(ActionSequence sequence, int index, String expectedName) {
    	assertNotNull(sequence.elements());
    	assertTrue(sequence.elements().size() >= index + 1);
    	
    	var element = sequence.elements().get(index);
    	
    	assertInstanceOf(CallingUserActionSequenceElement.class, element);
    	
    	var sequenceElement = (CallingUserActionSequenceElement) element;
    	assertEquals(expectedName, sequenceElement.getElement().getEntityName());
    }

}
