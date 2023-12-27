package org.dataflowanalysis.analysis.resource.pcm;

import org.dataflowanalysis.analysis.resource.ResourceProvider;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.repository.RepositoryPackage;
import org.palladiosimulator.pcm.resourceenvironment.ResourceenvironmentPackage;
import org.palladiosimulator.pcm.system.SystemPackage;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

public interface PCMResourceProvider extends ResourceProvider {
	/**
	 * Returns the usage model that the resource loader has loaded
	 * @return Usage model saved in the resources
	 */
	public UsageModel getUsageModel();
	
	/**
	 * Returns the allocation model that the resource loader has loaded
	 * @return Allocation model saved in the resources
	 */
	public Allocation getAllocation();
	
	/**
	 * Determines, whether the resource loader has sufficient resources to run the analysis
	 * @return This method returns true, if the analysis can be executed with the resource loader. Otherwise, the method returns false
	 */
	public default boolean sufficientResourcesLoaded() {
		if (this.getUsageModel() == null || this.getAllocation() == null) {
			return false;
		}
		if (this.lookupElementOfType(RepositoryPackage.eINSTANCE.getRepository()).isEmpty()) {
			return false;
		}
		if (this.lookupElementOfType(SystemPackage.eINSTANCE.getSystem()).isEmpty()) {
			return false;
		}
		if (this.lookupElementOfType(ResourceenvironmentPackage.eINSTANCE.getResourceEnvironment()).isEmpty()) {
			return false;
		}
		return true;
	}
}
