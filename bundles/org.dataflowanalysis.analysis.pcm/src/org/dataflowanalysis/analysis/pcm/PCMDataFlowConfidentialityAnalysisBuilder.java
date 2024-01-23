package org.dataflowanalysis.analysis.pcm;

import java.util.Optional;

import org.apache.log4j.Logger;
import org.dataflowanalysis.analysis.DataFlowAnalysisBuilder;
import org.dataflowanalysis.analysis.pcm.core.PCMDataCharacteristicsCalculatorFactory;
import org.dataflowanalysis.analysis.pcm.core.PCMNodeCharacteristicsCalculator;
import org.dataflowanalysis.analysis.pcm.resource.PCMResourceProvider;
import org.dataflowanalysis.analysis.pcm.resource.PCMURIResourceProvider;
import org.dataflowanalysis.analysis.utils.ResourceUtils;
import org.eclipse.core.runtime.Plugin;

public class PCMDataFlowConfidentialityAnalysisBuilder 
extends DataFlowAnalysisBuilder {
	private final Logger logger = Logger.getLogger(PCMDataFlowConfidentialityAnalysisBuilder.class);

	private String relativeUsageModelPath;
	private String relativeAllocationModelPath;
	private String relativeNodeCharacteristicsPath;
	private Optional<PCMResourceProvider> customResourceProvider = Optional.empty();

	public PCMDataFlowConfidentialityAnalysisBuilder() {}
	
	/**
	 * Uses a plugin activator class for the given project
	 * @param pluginActivator Plugin activator class of the modeling project
	 * @return Returns builder object of the analysis
	 */
	public PCMDataFlowConfidentialityAnalysisBuilder usePluginActivator(Class<? extends Plugin> pluginActivator) {
		super.usePluginActivator(pluginActivator);
		return this;
	}
	
	/**
	 * Uses a new path for a usage model
	 * @param relativeUsageModelPath Relative path to the usage model
	 * @return Returns builder object of the analysis
	 */
	public PCMDataFlowConfidentialityAnalysisBuilder useUsageModel(String relativeUsageModelPath) {
		this.relativeUsageModelPath = relativeUsageModelPath;
		return this;
	}
	
	/**
	 * Uses a new path for an allocation model
	 * @param relativeAllocationModelPath Relative path to the allocation model
	 * @return Returns builder object of the analysis
	 */
	public PCMDataFlowConfidentialityAnalysisBuilder useAllocationModel(String relativeAllocationModelPath) {
		this.relativeAllocationModelPath = relativeAllocationModelPath;
		return this;
	}
	
	/**
	 * Uses a new path for node characteristics
	 * @param relativeNodeCharacteristicsModelPath Relative path to the node characteristics model
	 * @return Returns builder object of the analysis
	 */
	public PCMDataFlowConfidentialityAnalysisBuilder useNodeCharacteristicsModel(String relativeNodeCharacteristicsModelPath) {
		this.relativeNodeCharacteristicsPath = relativeNodeCharacteristicsModelPath;
		return this;
	}
	
	/**
	 * Uses a new path for node characteristics
	 * @param relativeNodeCharacteristicsModelPath Relative path to the node characteristics model
	 * @return Returns builder object of the analysis
	 */
	public PCMDataFlowConfidentialityAnalysisBuilder useCustomResourceProvider(PCMResourceProvider resourceProvider) {
		this.customResourceProvider = Optional.of(resourceProvider);
		return this;
	}
	
	/**
	 * Sets standalone mode of the analysis
	 * @return Builder of the analysis
	 */
	public PCMDataFlowConfidentialityAnalysisBuilder standalone() {
		super.standalone();
		return this;
	}
	
	/**
	 * Sets the modeling project name of the analysis
	 * @return Builder of the analysis
	 */
	public PCMDataFlowConfidentialityAnalysisBuilder modelProjectName(String modelProjectName) {
		super.modelProjectName(modelProjectName);
		return this;
	}
	
	/**
	 * Determines the effective resource provider for the analysis.
	 * If a custom resource provider was provided, it will always be used
	 * @return Returns the effective resource provider for the analysis
	 */
	private PCMResourceProvider getEffectiveResourceProvider() {
		return this.customResourceProvider.orElseGet(this::getURIResourceProvider);
	}
	
	/**
	 * Creates a new URI resource loader with the given (optional) node characteristic URI
	 * @param nodeCharacteristicsURI Optional URI to the node characteristics model
	 * @return New instance of an URI resource loader with the internally saved values
	 */
	private PCMResourceProvider getURIResourceProvider() {
		return new PCMURIResourceProvider(ResourceUtils.createRelativePluginURI(relativeUsageModelPath, modelProjectName), 
				ResourceUtils.createRelativePluginURI(relativeAllocationModelPath, modelProjectName), 
				ResourceUtils.createRelativePluginURI(relativeNodeCharacteristicsPath, modelProjectName));
	}
	
	/**
	 * Validates the stored data of the analysis
	 */
	protected void validate() {
		super.validate();
		if (this.customResourceProvider.isEmpty() && (this.relativeUsageModelPath == null || this.relativeUsageModelPath.isEmpty())) {
			logger.error("The dataflow analysis requires a path to a usage model", new IllegalStateException("The Analysis requires a usage model"));
		}
		if (this.customResourceProvider.isEmpty() && (this.relativeAllocationModelPath == null || this.relativeAllocationModelPath.isEmpty())) {
			logger.error("The dataflow analysis requires a path to an allocation model", new IllegalStateException("The Analysis requires an allocation model"));
		}
		if (this.customResourceProvider.isPresent()) {
			this.customResourceProvider.get().loadRequiredResources();
			if (!this.customResourceProvider.get().sufficientResourcesLoaded()) {
				logger.error("The custom resource provider could not load all required resources", new IllegalStateException("Could not load all required resources"));
			}
		}
		if (this.relativeNodeCharacteristicsPath == null || this.relativeNodeCharacteristicsPath.isEmpty()) {
			logger.warn("Using node characteristic model without specifying path to the assignment model. No node characteristics will be applied!");
		}
	}

	@Override
	public PCMDataFlowConfidentialityAnalysis build() {
		PCMResourceProvider resourceProvider = this.getEffectiveResourceProvider();
		return new PCMDataFlowConfidentialityAnalysis(new PCMNodeCharacteristicsCalculator(resourceProvider), 
				new PCMDataCharacteristicsCalculatorFactory(resourceProvider), resourceProvider, 
				this.modelProjectName, this.pluginActivator);
	}
}
