package org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.CharacteristicsCalculator;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.AbstractActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.CallReturnBehavior;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.CharacteristicValue;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.DataFlowVariable;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.parameter.VariableCharacterisation;
import org.palladiosimulator.pcm.parameter.VariableUsage;
import org.palladiosimulator.pcm.repository.Parameter;
import org.palladiosimulator.pcm.seff.ExternalCallAction;

public class CallingSEFFActionSequenceElement extends SEFFActionSequenceElement<ExternalCallAction>
        implements CallReturnBehavior {

    private final boolean isCalling;

    public CallingSEFFActionSequenceElement(ExternalCallAction element, Deque<AssemblyContext> context, List<Parameter> variableUsage,
            boolean isCalling) {
        super(element, context, variableUsage);
        this.isCalling = isCalling;
    }

    public CallingSEFFActionSequenceElement(CallingSEFFActionSequenceElement oldElement, List<DataFlowVariable> dataFlowVariables, List<CharacteristicValue> nodeVariables) {
        super(oldElement, dataFlowVariables, nodeVariables);
        this.isCalling = oldElement.isCalling();
    }

    @Override
    public boolean isCalling() {
        return this.isCalling;
    }

    // TODO: Custom hash and equals required?

    @Override
    public AbstractActionSequenceElement<ExternalCallAction> evaluateDataFlow(Deque<List<DataFlowVariable>> variables) {
    	List<DataFlowVariable> newDataFlowVariables;
    	if (this.isCalling()) {
    		newDataFlowVariables = variables.getLast();
    	} else {
    		variables.pop();
    		newDataFlowVariables = variables.getLast();
    	}
    	
    	List<CharacteristicValue> nodeVariables = this.evaluateNodeCharacteristics();
        List<VariableCharacterisation> variableCharacterisations = this.isCalling ? 
        		super.getElement().getInputVariableUsages__CallAction().stream()
        		.flatMap(it -> it.getVariableCharacterisation_VariableUsage()
                        .stream())
                    .collect(Collectors.toList())
                : 
                super.getElement().getReturnVariableUsage__CallReturnAction().stream()
                .flatMap(it -> it.getVariableCharacterisation_VariableUsage()
                        .stream())
                .collect(Collectors.toList());

        CharacteristicsCalculator characteristicsCalculator = new CharacteristicsCalculator(newDataFlowVariables, nodeVariables);
        variableCharacterisations.stream()
            .forEach(it -> characteristicsCalculator.evaluate(it));
        AbstractActionSequenceElement<ExternalCallAction> evaluatedElement = new CallingSEFFActionSequenceElement(this,
                characteristicsCalculator.getCalculatedCharacteristics(), nodeVariables);
        return evaluatedElement;
    }

    @Override
    public List<DataFlowVariable> getAvailableDataFlowVariables(List<DataFlowVariable> variables) {
    	List<String> availableVariableNames = this.getParameter().stream()
    			.map(it -> it.getParameterName())
    			.collect(Collectors.toList());
    	if (!this.isCalling()) {
    		availableVariableNames.add("RETURN");
    	}
    	return variables.stream()
    			.filter(it -> availableVariableNames.contains(it.variableName()))
    			.collect(Collectors.toList());
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
