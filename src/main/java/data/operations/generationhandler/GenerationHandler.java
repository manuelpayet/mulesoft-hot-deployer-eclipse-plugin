package data.operations.generationhandler;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent.Kind;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import component.event.ModelChangedEventListener;
import component.viewer.ModelUpdateListener;
import data.dto.DeploymentStatus;
import data.dto.Module;
import data.operations.DirectoryWatcher;
import data.operations.event.FileChangedEventListener;
import data.operations.impl.ModuleSummary;
import utils.EclipsePluginHelper;

public class GenerationHandler implements ModelChangedEventListener, FileChangedEventListener {

	private final List<ModelUpdateListener> lstModelUpdate = new ArrayList<>();
	private final DirectoryWatcher directoryWatcher;
	final Path appDeploymentFolder;

	public GenerationHandler(ModelUpdateListener... listeners) {
		lstModelUpdate.addAll(Arrays.asList(listeners));
		this.appDeploymentFolder = Paths.get(EclipsePluginHelper.INSTANCE.getWorkspaceLocation().toString(), ".mule",
				"apps");
		this.directoryWatcher = new DirectoryWatcher(appDeploymentFolder, this);
		this.directoryWatcher.startPolling();
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

	private List<Module> listModulesFromAppsFolderState() {

		final Map<String, ModuleSummary> deploymentSummary = GenerationHandlerUtils.INSTANCE
				.groupPathsByModuleType(appDeploymentFolder);

		final EclipsePluginHelper eclipsePluginHelper = EclipsePluginHelper.INSTANCE;
		return eclipsePluginHelper.listWorkspaceProjects().stream()
				.filter(project -> eclipsePluginHelper.hasNatures(project, EclipsePluginHelper.M2E_NATURE,
						EclipsePluginHelper.JAVA_NATURE))
				.map(project -> this.constructModule(project, deploymentSummary)).collect(Collectors.toList());
	}

	private Module constructModule(final IProject eclipseProject, final Map<String, ModuleSummary> deploymentSummary) {

		try {
			final String name = eclipseProject.getDescription().getName();
			final GenerationHandlerUtils deploymentHandlerUtils = GenerationHandlerUtils.INSTANCE;
			final boolean isMulesoftManaged = deploymentHandlerUtils.isMulesoftManaged(deploymentSummary.get(name));
			final DeploymentStatus deploymentStatus = deploymentHandlerUtils
					.getDeploymentStatus(deploymentSummary.get(name));
			return new Module(name, deploymentStatus, isMulesoftManaged);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}

	public void updateModulesFromCurrentState() {
		lstModelUpdate.forEach(this::updateModulesFromCurrentState);
	}

	public void updateModulesFromCurrentState(final ModelUpdateListener modelUpdateListener) {
		final Map<String, Module> modulesInModelToUpdate = GenerationHandlerUtils.INSTANCE
				.mapModulesByName(modelUpdateListener.getModules());
		final List<Module> listModulesFromCurrentState = listModulesFromAppsFolderState();
		for (final Module module : listModulesFromCurrentState) {
			final Module moduleFromModel = modulesInModelToUpdate.containsKey(module.getModuleName())
					? modulesInModelToUpdate.get(module.getModuleName()) : module;
			module.setToHotDeploy(moduleFromModel.isToHotDeploy());
		}
		modelUpdateListener.setModules(listModulesFromCurrentState);
	}

	public List<IProject> getSelectedProjects() {
		final List<String> selectedProject = getModelUpdate().getModules().stream().filter(Module::isToHotDeploy)
				.map(Module::getModuleName).collect(Collectors.toList());
		return EclipsePluginHelper.INSTANCE.listWorkspaceProjects().stream()
				.filter(project -> selectedProject.contains(project.getName())).collect(Collectors.toList());
	}

	private ModelUpdateListener getModelUpdate() {
		return lstModelUpdate.get(0);
	}

	public void selectAllProjects() {
		final List<Module> lstModuleUpdated = getModelUpdate().getModules().stream().map((module) -> {
			module.setToHotDeploy(true);
			return module;
		}).collect(Collectors.toList());
		getModelUpdate().setModules(lstModuleUpdated);
	}
	
	public void unselectAllProjects() {
		final List<Module> lstModuleUpdated = getModelUpdate().getModules().stream().map((module) -> {
			module.setToHotDeploy(false);
			return module;
		}).collect(Collectors.toList());
		getModelUpdate().setModules(lstModuleUpdated);
	}

	@Override
	public void fileChanged(Path path, Kind<?> eventKind) {
		System.out.println(String.format("%s -> %s", eventKind, path));
		updateModulesFromCurrentState();
	}
}
