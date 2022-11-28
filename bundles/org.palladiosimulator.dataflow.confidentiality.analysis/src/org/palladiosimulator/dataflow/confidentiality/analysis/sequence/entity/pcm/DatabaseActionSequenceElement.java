package org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.pcm;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.palladiosimulator.dataflow.confidentiality.analysis.PCMAnalysisUtils;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.AbstractActionSequenceElement;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.CharacteristicValue;
import org.palladiosimulator.dataflow.confidentiality.analysis.sequence.entity.DataFlowVariable;
import org.palladiosimulator.dataflow.confidentiality.pcm.model.confidentiality.repository.OperationalDataStoreComponent;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.allocation.AllocationContext;
import org.palladiosimulator.pcm.allocation.AllocationPackage;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;

public class DatabaseActionSequenceElement<T extends OperationalDataStoreComponent> extends AbstractPCMActionSequenceElement<T> {
	private final Logger logger = Logger.getLogger(DatabaseActionSequenceElement.class);
	
	private DataStore dataStore;
	private boolean isWriting;
	
	public DatabaseActionSequenceElement(T element, Deque<AssemblyContext> context, boolean isWriting, DataStore dataStore) {
        super(element, context);
        this.isWriting = isWriting;
        this.dataStore = dataStore;
    }
	
	public DatabaseActionSequenceElement(DatabaseActionSequenceElement<T> oldElement, 
			List<DataFlowVariable> dataFlowVariables, 
			List<CharacteristicValue> nodeVariables) {
		super(oldElement, dataFlowVariables, nodeVariables);
		this.isWriting = oldElement.isWriting();
		this.dataStore = oldElement.getDataStore();
	}

	@Override
	public AbstractActionSequenceElement<T> evaluateDataFlow(List<DataFlowVariable> variables) {
		List<CharacteristicValue> nodeVariables = this.evaluateNodeCharacteristics();
		List<DataFlowVariable> newDataFlowVariables = new ArrayList<>(variables);
		if (this.isWriting()) {
			if (this.getDataStore().getDatabaseVariableName().isEmpty()) {
				logger.error("Writing to datastore element with name " + this.getDataStore().getDatabaseComponentName() + " failed, as datastore is never written to");
				throw new IllegalStateException("Invalid Action Sequence, datastore is read before reading");
			}
			String dataSourceName = this.getDataStore().getDatabaseVariableName().get();
			DataFlowVariable dataSource = newDataFlowVariables.stream()
					.filter(it -> it.variableName().equals(dataSourceName))
					.findAny().orElse(new DataFlowVariable(dataSourceName));
			dataStore.setCharacteristicValues(dataSource.characteristics());
			return new DatabaseActionSequenceElement<>(this, newDataFlowVariables, nodeVariables);
		}
		DataFlowVariable modifiedVariable = new DataFlowVariable("RETURN");
		List<CharacteristicValue> storedData = dataStore.getCharacteristicValues();
		for(CharacteristicValue characteristicValue : storedData) {
			modifiedVariable = modifiedVariable.addCharacteristic(characteristicValue);
		}
		newDataFlowVariables.add(modifiedVariable);
		return new DatabaseActionSequenceElement<>(this, newDataFlowVariables, nodeVariables);
	}
	
    protected List<CharacteristicValue> evaluateNodeCharacteristics() {
    	List<CharacteristicValue> nodeVariables = new ArrayList<>();
    	
    	var allocations = PCMAnalysisUtils.lookupElementOfType(AllocationPackage.eINSTANCE.getAllocation()).stream()
    			.filter(Allocation.class::isInstance)
    			.map(Allocation.class::cast)
    			.collect(Collectors.toList());
    	
    	var allocation = allocations.stream()
    			.filter(it -> it.getAllocationContexts_Allocation().stream()
    					.map(alloc -> alloc.getAssemblyContext_AllocationContext())
    					.anyMatch(this.getContext().getFirst()::equals)
    					)
    			.findFirst()
    			.orElseThrow();
    	
    	var allocationContexts = allocation.getAllocationContexts_Allocation();
    	    	
    	for (AllocationContext allocationContext : allocationContexts) {
    		if (this.getContext().contains(allocationContext.getAssemblyContext_AllocationContext())) {
        		nodeVariables.addAll(this.evaluateNodeCharacteristics(allocationContext.getResourceContainer_AllocationContext()));
    		}
    	}
    	return nodeVariables;
    }
	
	public boolean isWriting() {
		return this.isWriting;
	}
	
	public DataStore getDataStore() {
		return dataStore;
	}

	@Override
	public String toString() {
		String writing = isWriting ? "writing" : "reading";
        return String.format("%s / %s (%s, %s))", this.getClass()
            .getSimpleName(), writing,
                this.getElement()
                    .getEntityName(),
                this.getElement()
                    .getId());
	}
}
