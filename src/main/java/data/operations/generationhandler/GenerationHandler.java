package data.operations.generationhandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.WatchEvent.Kind;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;

import component.event.ModelChangedEventListener;
import component.viewer.ModelUpdateListener;
import data.dto.Module;
import data.operations.DirectoryWatcher;
import data.operations.event.FileChangedEventListener;
import utils.EclipsePluginHelper;

public class GenerationHandler implements ModelChangedEventListener, FileChangedEventListener {

	private final List<ModelUpdateListener> lstModelUpdate = new ArrayList<>();
	private final DirectoryWatcher directoryWatcher;
	private final Path appDeploymentFolder;
	public final static String PROJECT_NAME_FOR_DEPLOYMENT = "Hot Deploy Files";

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
	}

	public void deployModulesFromFolder(final boolean undeployExistingModules) {
		final Path modulesToDeployFolder = Paths.get(EclipsePluginHelper.INSTANCE
				.getProjectLocation(EclipsePluginHelper.INSTANCE.getProjectFromName(PROJECT_NAME_FOR_DEPLOYMENT))
				.toString(), "target", "modules-hot-deploy");
		if (undeployExistingModules) {
			undeployExistingModules();
		}
		System.out
				.println(String.format("Déploiement de l'ensemble des modules présents dans %s", appDeploymentFolder));
		try (final Stream<Path> modulesToDeployFolderStream = Files.list(modulesToDeployFolder)){
			final List<Path> modulesToDeploy = modulesToDeployFolderStream
					.filter(path -> path.toFile().isDirectory())
					.filter(path -> Paths.get(path.toString(), "target").toFile().isDirectory())
					.map(path -> Paths.get(path.toString(), "target")).map(this::getModuleToDeployFromPath)
					.collect(Collectors.toList());
			System.out.println(
					String.format("Copie de %s dans le dossier de déploiement..., Mulesoft se chargera du reste :)",
							modulesToDeploy));

			for (final Path moduleToDeploy : modulesToDeploy) {
				final Path destinationFile = Paths.get(appDeploymentFolder.toString(), moduleToDeploy.getFileName().toString());
				System.out.println(String.format("%s->%s", moduleToDeploy, appDeploymentFolder));
				Files.copy(moduleToDeploy, destinationFile, StandardCopyOption.REPLACE_EXISTING,
						StandardCopyOption.COPY_ATTRIBUTES);
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Path getModuleToDeployFromPath(final Path pathContainingModule) {
		try (final Stream<Path> pathList = Files.list(pathContainingModule)){
			final Optional<Path> module = 
					pathList.filter(element -> element.toFile().isFile()
							&& element.getFileName().toString().matches(".*[0-9]\\.[0-9]\\.[0-9]\\-SNAPSHOT\\.zip"))
					.findFirst();
			if (!module.isPresent()) {
				throw new IllegalStateException(
						String.format("impossible de trouver le module à déployer dans %s", pathContainingModule));
			}
			return module.get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void undeployExistingModules() {
		final List<Module> listModulesFromDeploymentFolders = GenerationHandlerUtils.INSTANCE
				.listModulesFromAppsFolderState(appDeploymentFolder);
		final List<Module> hotDeployedModules = listModulesFromDeploymentFolders.stream()
				.filter(module -> !module.isMulesoftManaged()).collect(Collectors.toList());
		for (final Module module : hotDeployedModules) {
			GenerationHandlerUtils.INSTANCE.markModuleToUndeployInDeploymentFolder(appDeploymentFolder, module);
		}
	}

	public void updateModulesFromCurrentState() {
		lstModelUpdate.forEach(this::updateModulesFromCurrentState);
	}

	public void updateModulesFromCurrentState(final ModelUpdateListener modelUpdateListener) {
		final Map<String, Module> modulesInModelToUpdate = GenerationHandlerUtils.INSTANCE
				.mapModulesByName(modelUpdateListener.getModules());
		final List<Module> listModulesFromCurrentState = GenerationHandlerUtils.INSTANCE
				.listModulesFromAppsFolderState(appDeploymentFolder);
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

	public void invokeMavenForSelectedModules() {
		GenerationHandlerUtils.INSTANCE.invokeMavenForSelectedModules(PROJECT_NAME_FOR_DEPLOYMENT,
				getSelectedProjects());
	}

	@Override
	public void fileChanged(Path path, Kind<?> eventKind) {
		System.out.println(String.format("%s -> %s", eventKind, path));
		updateModulesFromCurrentState();
	}

	private ModelUpdateListener getModelUpdate() {
		return lstModelUpdate.get(0);
	}
}
