	package org.palladiosimulator.dataflow.confidentiality.analysis.builder.pcm;

import org.eclipse.core.runtime.Plugin;
import org.palladiosimulator.dataflow.confidentiality.analysis.builder.AbstractDataFlowAnalysisBuilder;
import org.palladiosimulator.dataflow.confidentiality.analysis.builder.AnalysisBuilderData;
import org.palladiosimulator.dataflow.confidentiality.analysis.dsl.LegacyStandalonePCMDataFlowConfidentialityAnalysis;
import org.palladiosimulator.dataflow.confidentiality.analysis.dsl.AbstractStandalonePCMDataFlowConfidentialityAnalysis;
import org.palladiosimulator.dataflow.confidentiality.analysis.dsl.StandalonePCMDataFlowConfidentialityAnalysis;

public class PCMDataFlowConfidentialityAnalysisBuilder 
extends AbstractDataFlowAnalysisBuilder<AbstractStandalonePCMDataFlowConfidentialityAnalysis, PCMAnalysisBuilderData, AnalysisBuilderData> {

	public PCMDataFlowConfidentialityAnalysisBuilder() {
		super(new PCMAnalysisBuilderData());
	}
	
	/**
	 * Set the legacy mode of the analysis
	 * @return Returns builder object of the analysis
	 */
	public PCMDataFlowConfidentialityAnalysisBuilder legacy() {
		this.builderData.setLegacy(true);
		return this;
	}
	
	/**
	 * Registers a plugin activator class for the given project
	 * @param pluginActivator Plugin activator class of the modeling project
	 * @return Returns builder object of the analysis
	 */
	public PCMDataFlowConfidentialityAnalysisBuilder registerPluginActivator(Class<? extends Plugin> pluginActivator) {
		this.builderData.setPluginActivator(pluginActivator);
		return this;
	}
	
	/**
	 * Register a new path for a usage model
	 * @param relativeUsageModelPath Relative path to the usage model
	 * @return Returns builder object of the analysis
	 */
	public PCMDataFlowConfidentialityAnalysisBuilder registerUsageModel(String relativeUsageModelPath) {
		this.builderData.setRelativeUsageModelPath(relativeUsageModelPath);
		return this;
	}
	
	/**
	 * Register a new path for an allocation model
	 * @param relativeAllocationModelPath Relative path to the allocation model
	 * @return Returns builder object of the analysis
	 */
	public PCMDataFlowConfidentialityAnalysisBuilder registerAllocationModel(String relativeAllocationModelPath) {
		this.builderData.setRelativeAllocationModelPath(relativeAllocationModelPath);
		return this;
	}
	
	/**
	 * Register a new path for node characteristics
	 * @param relativeNodeCharacteristicsModelPath Relative path to the node characteristics model
	 * @return Returns builder object of the analysis
	 */
	public PCMDataFlowConfidentialityAnalysisBuilder registerNodeCharacteristicsModel(String relativeNodeCharacteristicsModelPath) {
		this.builderData.setRelativeNodeCharacteristicsPath(relativeNodeCharacteristicsModelPath);
		return this;
	}
	
	@Override
	public void copyBuilderData(AnalysisBuilderData builderData) {
		super.builderData.setModelProjectName(builderData.getModelProjectName());
		super.builderData.setStandalone(builderData.isStandalone());
	}
	
	@Override
	public void validateBuilderData() {
		this.builder.forEach(it -> it.validateBuilderData());
		this.builderData.validateData();
	}

	@Override
	public AbstractStandalonePCMDataFlowConfidentialityAnalysis build() {
		this.validateBuilderData();
		if (this.builderData.isLegacy()) {
			return new LegacyStandalonePCMDataFlowConfidentialityAnalysis(builderData, builderData.createAnalysisData());
		} else {
			return new StandalonePCMDataFlowConfidentialityAnalysis(builderData, builderData.createAnalysisData());
		}
	}
}
