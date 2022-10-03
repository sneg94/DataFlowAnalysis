package org.palladiosimulator.dataflow.confidentiality.analysis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.palladiosimulator.dataflow.confidentiality.analysis.AnalysisUtils.assertSEFFSequenceElementContent;
import static org.palladiosimulator.dataflow.confidentiality.analysis.AnalysisUtils.assertSequenceElement;
import static org.palladiosimulator.dataflow.confidentiality.analysis.AnalysisUtils.assertSequenceElements;
import static org.palladiosimulator.dataflow.confidentiality.analysis.AnalysisUtils.assertUserSequenceElementContent;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm.CallingSEFFActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm.CallingUserActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm.SEFFActionSequenceElement;

public class DataflowSequenceFinderTest extends AnalysisFeatureTest {

    @ParameterizedTest
    @MethodSource("testCountProvider")
    public void testCount(StandalonePCMDataFlowConfidentialtyAnalysis analysis, int expectedSequences) {
        var allSequences = analysis.findAllSequences();
        assertEquals(expectedSequences, allSequences.size(),
                String.format("Expected two dataflow sequences, but found %s sequences", allSequences.size()));
    }

    private static Stream<Arguments> testCountProvider() {
        return Stream.of(Arguments.of(onlineShopAnalysis, 2), Arguments.of(internationalOnlineShopAnalysis, 1),
                Arguments.of(travelPlannerAnalysis, 2));
    }

    /**
     * Tests the expected sequence of elements
     * <p>
     * Fails if the analysis does not find the correct Classes for the first sequence
     */
    @Test
    public void testOnlineShopPath() {
        var allSequences = onlineShopAnalysis.findAllSequences();

        assertEquals(allSequences.size(), 2);

        assertSequenceElements(allSequences.get(0), CallingUserActionSequenceElement.class, // UserAction-Call:
                                                                                            // ViewEntryLevelSystemCall
                CallingSEFFActionSequenceElement.class, // SEFFAction-Call: DatabaseLoadInventory
                SEFFActionSequenceElement.class, // SEFFAction: Return
                CallingSEFFActionSequenceElement.class, // SEFFAction-Return: DatabaseLoadInventory
                SEFFActionSequenceElement.class, // SEFFAction: Return
                CallingUserActionSequenceElement.class, // UserAction-Return:
                                                        // ViewEntryLevelSystemCall

                CallingUserActionSequenceElement.class, // UserAction-Call: BuyEntryLevelSystemCall
                CallingSEFFActionSequenceElement.class, // SEFFAction-Call: SaveInventoryCall
                CallingSEFFActionSequenceElement.class, // SEFFAction-Return: SaveInventoryCall
                CallingUserActionSequenceElement.class);// UserAction-Return:
                                                        // BuyEntryLevelSystemCall

        assertSequenceElement(allSequences.get(1), 8, SEFFActionSequenceElement.class);
    }

    /**
     * Tests the expected sequence of elements
     * <p>
     * Fails if the analysis does not find the correct Classes for the first sequence
     */
    @Test
    public void testInternationalOnlineShopPath() {
        var allSequences = internationalOnlineShopAnalysis.findAllSequences();
        assertEquals(1, allSequences.size());

        assertSequenceElements(allSequences.get(0), CallingUserActionSequenceElement.class, // UserAction-Call:
                                                                                            // ViewEntryLevelSystemCall
                CallingSEFFActionSequenceElement.class, // SEFFAction-Call: DatabaseLoadInventory
                SEFFActionSequenceElement.class, // SEFF-Action: Return
                CallingSEFFActionSequenceElement.class, // SEFFAction-Return: DatabaseLoadInventory
                SEFFActionSequenceElement.class, // SEFF-Action: Return
                CallingUserActionSequenceElement.class, // UserAction-Return:
                                                        // ViewEntryLevelSystemCall

                CallingUserActionSequenceElement.class, // UserAction-Call: BuyEntryLevelSystemCall
                CallingSEFFActionSequenceElement.class, // SEFFAction-Call: DatabaseStoreInventory
                CallingSEFFActionSequenceElement.class, // SEFFAction-Return: DatabaseStoreInventory
                CallingSEFFActionSequenceElement.class, // SEFFAction-Call: DatabaseStoreUserData
                CallingSEFFActionSequenceElement.class, // SEFFAction-Return: DatabaseStoreUserData
                CallingUserActionSequenceElement.class);// UserAction-Return:
                                                        // BuyEntryLevelSystemCall
    }

    /**
     * Tests the expected sequence of elements
     * <p>
     * Fails if the analysis does not find the correct Classes for the first sequence
     */
    @Test
    public void testTravelPlannerPath() {
        var allSequences = travelPlannerAnalysis.findAllSequences();
        assertEquals(2, allSequences.size());

        assertSequenceElements(allSequences.get(0), CallingUserActionSequenceElement.class, // UserAction-Call:
                                                                                            // Store
                                                                                            // CreditCardDetails
                CallingUserActionSequenceElement.class, // UserAction-Return: Store
                                                        // CreditCardDetails

                CallingUserActionSequenceElement.class, // UserAction-Call: Look for flights
                CallingSEFFActionSequenceElement.class, // SEFFAction-Call: Request flights from
                                                        // Airline
                CallingSEFFActionSequenceElement.class, // SEFFAction-Call: Read flights from
                                                        // Database
                CallingSEFFActionSequenceElement.class, // SEFFAction-Return: Read flights from
                                                        // Database
                SEFFActionSequenceElement.class, // SEFFAction: Select flights based on query and
                                                 // return selection
                CallingSEFFActionSequenceElement.class, // SEFFAction-Return: Request flights from
                                                        // Airline
                SEFFActionSequenceElement.class, // SEFFAction: Return found flights
                CallingUserActionSequenceElement.class, // UserAction-Return: Look for flights1

                CallingUserActionSequenceElement.class, // UserAction-Call: Load CreditCardDetails
                CallingUserActionSequenceElement.class, // UserAction-Return: Load CreditCardDetails

                CallingUserActionSequenceElement.class, // UserAction-Call: Book flight using
                                                        // CreditCardDetails
                CallingSEFFActionSequenceElement.class, // SEFFAction-Call: Ask Airline to book
                                                        // flight
                SEFFActionSequenceElement.class, // SEFF-Action: Return confirmation
                CallingSEFFActionSequenceElement.class, // SEFFAction-Return: Ask Airline to book
                                                        // flight
                SEFFActionSequenceElement.class, // SEFF-Action: Return confirmation
                CallingUserActionSequenceElement.class);// UserAction-Return: Book flight using
                                                        // CreditCardDetails

        assertSequenceElements(allSequences.get(1), CallingUserActionSequenceElement.class, // UserAction-Call:
                                                                                            // Add
                                                                                            // scheduled
                                                                                            // flight
                CallingUserActionSequenceElement.class); // UserAction-Return: Add scheduled flight
    }

    /**
     * Tests the content of the action sequences
     * <p>
     * Fails if the analysis does not find the correct entity name for elements in the
     * ActionSequence
     */
    @Test
    public void testOnlineShopContent() {
        var allSequences = onlineShopAnalysis.findAllSequences();
        assertTrue(allSequences.size() > 0);

        assertUserSequenceElementContent(allSequences.get(0), 0, "ViewEntryLevelSystemCall");
        assertSEFFSequenceElementContent(allSequences.get(0), 1, "DatabaseLoadInventory");
    }

    /**
     * Tests the content of the action sequences
     * <p>
     * Fails if the analysis does not find the correct entity name for elements in the
     * ActionSequence
     */
    @Test
    public void testInternationalOnlineShopContent() {
        var allSequences = internationalOnlineShopAnalysis.findAllSequences();
        assertTrue(allSequences.size() > 0);

        assertUserSequenceElementContent(allSequences.get(0), 6, "BuyEntryLevelSystemCall");
        assertSEFFSequenceElementContent(allSequences.get(0), 9, "DatabaseStoreUserData");
    }

    /**
     * Tests the content of the action sequences
     * <p>
     * Fails if the analysis does not find the correct entity name for elements in the
     * ActionSequence
     */
    @Test
    public void testTravelPlannerContent() {
        var allSequences = travelPlannerAnalysis.findAllSequences();
        assertTrue(allSequences.size() > 0);

        assertUserSequenceElementContent(allSequences.get(0), 2, "look for flights");
        assertSEFFSequenceElementContent(allSequences.get(0), 13, "ask airline to book flight");
    }
}
