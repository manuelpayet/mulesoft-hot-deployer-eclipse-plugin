package data.dto;

public enum DeploymentStatus {
	DEPLOYE("Déployé"),
	DEPLOIEMENT_EN_COURS("A déployer"),
	NON_DEPLOYE("Non deployé");
	private final String label;
	private DeploymentStatus(final String label) {
		this.label = label;
	}
	public String getLabel() {
		return label;
	}
}
