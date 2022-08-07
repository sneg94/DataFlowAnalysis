package org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity;

import java.util.ArrayList;
import java.util.List;

public class ActionSequence {

    private final List<AbstractActionSequenceElement<?>> elements;

    public ActionSequence() {
        this.elements = new ArrayList<>();
    }
    
    public ActionSequence(List<AbstractActionSequenceElement<?>> elements) {
        this.elements = elements;
    }
    
    public ActionSequence(ActionSequence sequence) {
        this(sequence.getElements());
    }

    public void addElement(AbstractActionSequenceElement<?> element) {
        this.elements.add(element);
    }
    
    public List<AbstractActionSequenceElement<?>> getElements() {
        return this.elements;
    }

    public void evaluateDataFlow() {
        var iterator = elements.iterator();
        List<DataFlowVariable> currentVariables = new ArrayList<>();

        while (iterator.hasNext()) {
            AbstractActionSequenceElement<?> nextElement = iterator.next();
            currentVariables = nextElement.evaluateDataFlow(currentVariables);
        }
    }
}
