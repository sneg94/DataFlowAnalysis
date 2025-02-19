package org.dataflowanalysis.analysis.tests.constraint.data;

import java.util.List;
import java.util.Map;

import org.dataflowanalysis.analysis.core.AbstractActionSequenceElement;
import org.dataflowanalysis.analysis.core.CharacteristicValue;
import org.dataflowanalysis.analysis.core.DataFlowVariable;
import org.dataflowanalysis.analysis.pcm.core.AbstractPCMActionSequenceElement;

import de.uka.ipd.sdq.identifier.Identifier;

public class ConstraintData {
	private final String nodeID;
	private final List<CharacteristicValueData> nodeCharacteristics;
	private final Map<String, List<CharacteristicValueData>> dataFlowVariables;
	
	public ConstraintData(String nodeID, List<CharacteristicValueData> nodeCharacteristics, Map<String, List<CharacteristicValueData>> dataFlowVariable) {
		this.nodeID = nodeID;
		this.nodeCharacteristics = nodeCharacteristics;
		this.dataFlowVariables = dataFlowVariable;
	}
	
	public boolean matches(AbstractActionSequenceElement<?> element) {
		if (!(element instanceof AbstractPCMActionSequenceElement<?>)) {
			return false;
		}
		AbstractPCMActionSequenceElement<?> sequenceElement = (AbstractPCMActionSequenceElement<?>) element;
		Identifier pcmElement = (Identifier) sequenceElement.getElement();
		return this.nodeID.equals(pcmElement.getId());
	}
	
	public boolean hasNodeCharacteristic(CharacteristicValue actualCharacteristicValue) {
		return hasCharacteristicValue(nodeCharacteristics, actualCharacteristicValue);
	}
	
	public boolean hasDataFlowVariable(DataFlowVariable actualDataFlowVariable) {
		List<CharacteristicValueData> expectedCharacteristicValues = this.dataFlowVariables.get(actualDataFlowVariable.variableName());
		return actualDataFlowVariable.characteristics().stream()
				.allMatch(it -> hasCharacteristicValue(expectedCharacteristicValues, it));
	}
	
	private boolean hasCharacteristicValue(List<CharacteristicValueData> data, CharacteristicValue actualCharacteristicValue) {
		return data.stream()
		.filter(it -> actualCharacteristicValue.getTypeName().equals(it.characteristicType()))
		.anyMatch(it -> actualCharacteristicValue.getValueName().equals(it.characteristicLiteral()));
	}
	
	public int nodeCharacteristicsCount() {
		return this.nodeCharacteristics.size();
	}
	
	public int dataFlowVariablesCount() {
		return this.dataFlowVariables.size();
	}
}
