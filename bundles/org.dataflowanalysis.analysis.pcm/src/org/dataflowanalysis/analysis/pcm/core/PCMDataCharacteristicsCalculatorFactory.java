package org.dataflowanalysis.analysis.pcm.core;

import java.util.List;

import org.dataflowanalysis.analysis.core.CharacteristicValue;
import org.dataflowanalysis.analysis.core.DataCharacteristicsCalculator;
import org.dataflowanalysis.analysis.core.DataCharacteristicsCalculatorFactory;
import org.dataflowanalysis.analysis.core.DataFlowVariable;
import org.dataflowanalysis.analysis.resource.ResourceProvider;

public class PCMDataCharacteristicsCalculatorFactory implements DataCharacteristicsCalculatorFactory {
	private final ResourceProvider resourceLoader;
	
	/** 
	 * Creates a new instance of the data characteristics calculator factory
	 * @param resourceLoader Resource loader the characteristics calculators should use
	 */
	public PCMDataCharacteristicsCalculatorFactory(ResourceProvider resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	/**
     * Initialize Characteristic Calculator with initial variables.
     * 
     * @param initialVariables DataFlowVariables of the previous ActionSequence Element
     * @param nodeCharacteristics Node characteristics applied to the node
     */
	@Override
	public DataCharacteristicsCalculator createNodeCalculator(List<DataFlowVariable> initialVariables,
			List<CharacteristicValue> nodeCharacteristics) {
		return new PCMDataCharacteristicsCalculator(initialVariables, nodeCharacteristics, this.resourceLoader);
	}

}
