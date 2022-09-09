package org.palladiosimulator.dataflow.confidentiality.analysis;

import java.util.List;
import java.util.function.Predicate;

import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.AbstractActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.ActionSequence;

public interface DataFlowConfidentialityAnalysis {

    public boolean initalizeAnalysis();

    public List<ActionSequence> findAllSequences();

    public List<ActionSequence> evaluateDataFlows(List<ActionSequence> sequences);

    public List<AbstractActionSequenceElement<?>> queryDataFlow(ActionSequence sequence,
            Predicate<? super AbstractActionSequenceElement<?>> condition);
}
