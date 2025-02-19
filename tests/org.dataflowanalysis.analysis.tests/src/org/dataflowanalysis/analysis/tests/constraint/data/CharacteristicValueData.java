package org.dataflowanalysis.analysis.tests.constraint.data;

public record CharacteristicValueData(String characteristicType, String characteristicLiteral) {
	public CharacteristicValueData(String characteristicType, String characteristicLiteral) {
		this.characteristicType = characteristicType;
		this.characteristicLiteral = characteristicLiteral;
	}
}
