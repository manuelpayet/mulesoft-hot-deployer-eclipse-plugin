package data.dto;

public enum DeploymentStatus {
	DEPLOYE("Déployé"),
	DEPLOIEMENT_EN_COURS("Déploiement en cours"),
	NON_DEPLOYE("Non deployé"),
	INCONNU("Inconnu");
	private final String label;
	private DeploymentStatus(final String label) {
		this.label = label;
	}
	public String getLabel() {
		return label;
	}
}
