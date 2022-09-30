package org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm;

import java.util.Deque;
import java.util.List;

import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.CharacteristicsCalculator;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.AbstractActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.CallReturnBehavior;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.DataFlowVariable;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.parameter.VariableCharacterisation;
import org.palladiosimulator.pcm.seff.ExternalCallAction;

import com.google.common.collect.Streams;

public class CallingSEFFActionSequenceElement extends SEFFActionSequenceElement<ExternalCallAction>
        implements CallReturnBehavior {

    private final boolean isCalling;

    public CallingSEFFActionSequenceElement(ExternalCallAction element, Deque<AssemblyContext> context,
            boolean isCalling) {
        super(element, context);
        this.isCalling = isCalling;
        // TODO Auto-generated constructor stub
    }

    public CallingSEFFActionSequenceElement(CallingSEFFActionSequenceElement oldElement,
            List<DataFlowVariable> variables) {
        super(oldElement, variables);
        this.isCalling = oldElement.isCalling();
    }

    @Override
    public boolean isCalling() {
        return this.isCalling;
    }

    // TODO: Custom hash and equals required?

    /**
     * Input: ccd . GrantedRoles . User := true Elements: variable.characteristicType.value := Term
     */
    @Override
    public AbstractActionSequenceElement<ExternalCallAction> evaluateDataFlow(List<DataFlowVariable> variables) {
        var elementStream = Streams.concat(super.getElement().getInputVariableUsages__CallAction()
            .stream(),
                super.getElement().getReturnVariableUsage__CallReturnAction()
                    .stream());
        List<VariableCharacterisation> elements = elementStream
            .flatMap(it -> it.getVariableCharacterisation_VariableUsage()
                .stream())
            .toList();

        CharacteristicsCalculator characteristicsCalculator = new CharacteristicsCalculator(variables);
        elements.stream()
            .forEach(it -> characteristicsCalculator.evaluate(it));
        AbstractActionSequenceElement<ExternalCallAction> evaluatedElement = new CallingSEFFActionSequenceElement(this,
                characteristicsCalculator.getCalculatedCharacteristics());
        return evaluatedElement;
    }

    @Override
    public String toString() {
        String calling = isCalling ? "calling" : "returning";
        return String.format("%s / %s (%s, %s))", this.getClass()
            .getSimpleName(), calling,
                this.getElement()
                    .getEntityName(),
                this.getElement()
                    .getId());
    }

}
