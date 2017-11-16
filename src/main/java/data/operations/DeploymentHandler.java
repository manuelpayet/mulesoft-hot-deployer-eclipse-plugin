package data.operations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import component.event.ModelChangedEvent;
import component.viewer.ModelUpdateListener;
import data.dto.DeploymentStatus;
import data.dto.Module;
import utils.EclipsePluginHelper;

public class DeploymentHandler implements ModelChangedEvent {

	private final List<ModelUpdateListener> lstModelUpdate = new ArrayList<>();

	public DeploymentHandler(ModelUpdateListener... listeners) {
		lstModelUpdate.addAll(Arrays.asList(listeners));
	}

	public void addLstModelUpdate(final ModelUpdateListener modelUpdateListener) {
		lstModelUpdate.add(modelUpdateListener);
	}

	@Override
	public void modelChanged(Module module) {
		if (module.isToHotDeploy()) {
			deployModule(module);
		} else {
			undeployModule(module);
		}
	}

	public void deployModule(final Module module) {
		System.out.println(String.format("Déploiement de %s", module));
	}

	public void undeployModule(final Module module) {
		System.out.println(String.format("Suppression de %s", module));
	}

	private List<Module> listModulesFromCurrentState() {
		final EclipsePluginHelper eclipsePluginHelper = EclipsePluginHelper.INSTANCE;
		return eclipsePluginHelper.listWorkspaceProjects().stream()
				.filter(project -> eclipsePluginHelper.hasNature(project, EclipsePluginHelper.M2E_NATURE)).map(this::constructModule)
				.collect(Collectors.toList());
	}

	private Module constructModule(final IProject eclipseProject) {
		String name;
		try {
			name = String.format("%s[%s]", eclipseProject.getDescription().getName(),
					Arrays.asList(eclipseProject.getDescription().getNatureIds()));
			return new Module(name, DeploymentStatus.DEPLOYE, false);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}

	public void updateModulesFromCurrentState() {
		List<Module> listModulesFromCurrentState = listModulesFromCurrentState();
		lstModelUpdate.forEach(eventListener -> eventListener.setModules(listModulesFromCurrentState));
	}
}
