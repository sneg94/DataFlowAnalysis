package org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm;

import java.util.Deque;
import java.util.List;
import java.util.Objects;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.AbstractActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.CharacteristicValue;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.DataFlowVariable;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;

public abstract class AbstractPCMActionSequenceElement<T extends EObject> extends AbstractActionSequenceElement<T> {

    private final Deque<AssemblyContext> context;
    private final T element;	


    /**
     * Constructs a new Action Sequence Element with the underlying Palladio Element and Assembly Context
     * @param element Underlying Palladio Element of the Sequence Element
     * @param context Assembly context of the Palladio Element
     */
    public AbstractPCMActionSequenceElement(T element, Deque<AssemblyContext> context) {
        this.element = element;
        this.context = context;
    }
    
    /**
     * Builds a new Sequence element with an existing element and a list of Node and DataFlow variables
     * @param oldElement Old element, which element and context should be copied
     * @param dataFlowVariables DataFlow variables, which should be present for the action sequence element
     * @param nodeCharacteristics Node characteristics, which should be present for the action sequence element
     */
    public AbstractPCMActionSequenceElement(AbstractPCMActionSequenceElement<T> oldElement, List<DataFlowVariable> dataFlowVariables, List<CharacteristicValue> nodeCharacteristics) {
    	super(dataFlowVariables, nodeCharacteristics);
    	this.element = oldElement.getElement();
    	this.context = oldElement.getContext();
    }

    /**
     * Return the saved element of the sequence element
     * @return
     */
    public T getElement() {
        return element;
    }

    /**
     * Returns the assembly contexts of the sequence element
     * @return Returns a {@link Deque} of {@link AssemblyContext}s that the sequence element has
     */
    public Deque<AssemblyContext> getContext() {
        return context;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(context, element);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        @SuppressWarnings("rawtypes")
        AbstractPCMActionSequenceElement other = (AbstractPCMActionSequenceElement) obj;
        return Objects.equals(context, other.context) && Objects.equals(element, other.element);
    }

}
