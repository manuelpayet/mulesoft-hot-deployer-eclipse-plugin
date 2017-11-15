package data.dto;

public class Module {
	private final String moduleName;
	private final DeploymentStatus deploymentStatus;
	private final boolean mulesoftManaged;
	private boolean toHotDeploy;
	
	public Module(String moduleName, DeploymentStatus deploymentStatus, boolean mulesoftManaged) {
		super();
		this.moduleName = moduleName;
		this.deploymentStatus = deploymentStatus;
		this.mulesoftManaged = mulesoftManaged;
	}

	public boolean isToHotDeploy() {
		return toHotDeploy;
	}

	public void setToHotDeploy(boolean toHotDeploy) {
		this.toHotDeploy = toHotDeploy;
	}

	public String getModuleName() {
		return moduleName;
	}

	public DeploymentStatus getDeploymentStatus() {
		return deploymentStatus;
	}

	public boolean isMulesoftManaged() {
		return mulesoftManaged;
	}
	
}
