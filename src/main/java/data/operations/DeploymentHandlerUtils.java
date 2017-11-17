package data.operations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import data.dto.DeploymentStatus;
import data.dto.Module;
import data.operations.impl.ModuleSummary;

public enum DeploymentHandlerUtils {
	INSTANCE;
	public List<Path> getZipFiles(final List<Path> paths) {
		return paths.stream().filter(path -> path.toString().endsWith(".zip")).collect(Collectors.toList());
	}

	public List<Path> getAnchors(final List<Path> paths) {
		return paths.stream().filter(path -> path.toString().endsWith("-anchor.txt")).collect(Collectors.toList());
	}

	public List<Path> getDirectories(final List<Path> paths) {
		return paths.stream().filter(Files::isDirectory).collect(Collectors.toList());
	}

	public boolean isMulesoftManaged(final ModuleSummary moduleSummary) {
		final boolean result;
		if (null == moduleSummary) {
			result = false;
		} else if (isMulesoftManaged(moduleSummary.getDirectoryPath())
				|| isMulesoftManaged(moduleSummary.getZipPath())) {
			result = true;
		} else {
			result = false;
		}
		return result;
	}

	public DeploymentStatus getDeploymentStatus(final ModuleSummary moduleSummary) {
		DeploymentStatus deploymentStatus = DeploymentStatus.INCONNU;
		if (null == moduleSummary) {
			deploymentStatus = DeploymentStatus.NON_DEPLOYE;
		} else {
			if (null != moduleSummary.getAnchorPath() && null == moduleSummary.getZipPath()) {
				deploymentStatus = DeploymentStatus.DEPLOYE;
			}
			
			if (null != moduleSummary.getAnchorPath() && null != moduleSummary.getZipPath()) {
				deploymentStatus = DeploymentStatus.DEPLOIEMENT_EN_COURS;
			}

			if (null == moduleSummary.getAnchorPath()
					&& (null != moduleSummary.getZipPath())) {
				deploymentStatus = DeploymentStatus.DEPLOIEMENT_EN_COURS;
			}
		}
		return deploymentStatus;
	}

	public boolean isMulesoftManaged(final Path path) {
		return null != path && !path.getFileName().toString().contains("-SNAPSHOT");
	}

	public String extractModuleNameFromZip(final Path path) {
		return path.getFileName().toString().replaceAll("(-[0-9]\\.[0-9]\\.[0-9]-SNAPSHOT)?.zip$", "");
	}

	public String extractModuleNameFromAnchor(final Path path) {
		return path.getFileName().toString().replaceAll("(-[0-9]\\.[0-9]\\.[0-9]-SNAPSHOT)?\\-anchor.txt$", "");
	}

	public String extractModuleNameFromDirectory(final Path path) {
		return path.getFileName().toString().replaceAll("-[0-9]\\.[0-9]\\.[0-9]-SNAPSHOT$", "");
	}

	public Map<String, ModuleSummary> groupPathsByModuleType(final Path appDeploymentFolder) {
		final List<Path> deploymentFolderContent;
		try {
			deploymentFolderContent = Files.list(appDeploymentFolder).collect(Collectors.toList());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		final Map<String, ModuleSummary> groupedPaths = new HashMap<>();
		final List<Path> zipFiles = DeploymentHandlerUtils.INSTANCE.getZipFiles(deploymentFolderContent);
		final List<Path> directories = DeploymentHandlerUtils.INSTANCE.getDirectories(deploymentFolderContent);
		final List<Path> anchors = DeploymentHandlerUtils.INSTANCE.getAnchors(deploymentFolderContent);
		for (final Path zipFile : zipFiles) {
			final String moduleName = DeploymentHandlerUtils.INSTANCE.extractModuleNameFromZip(zipFile);
			final ModuleSummary moduleSummary = new ModuleSummary();
			moduleSummary.setZipPath(zipFile);
			groupedPaths.put(moduleName, moduleSummary);
		}

		for (final Path directory : directories) {
			final String moduleName = DeploymentHandlerUtils.INSTANCE.extractModuleNameFromDirectory(directory);
			final ModuleSummary moduleSummary;
			if (!groupedPaths.containsKey(moduleName)) {
				moduleSummary = new ModuleSummary();
				groupedPaths.put(moduleName, moduleSummary);
			} else {
				moduleSummary = groupedPaths.get(moduleName);
			}
			moduleSummary.setDirectoryPath(directory);
		}

		for (final Path anchor : anchors) {
			final String moduleName = DeploymentHandlerUtils.INSTANCE.extractModuleNameFromAnchor(anchor);
			final ModuleSummary moduleSummary;
			if (!groupedPaths.containsKey(moduleName)) {
				moduleSummary = new ModuleSummary();
				groupedPaths.put(moduleName, moduleSummary);
			} else {
				moduleSummary = groupedPaths.get(moduleName);
			}
			moduleSummary.setAnchorPath(anchor);
		}

		return groupedPaths;
	}
	
	public Map<String, Module> mapModulesByName(final List<Module> lstModules) {
		return lstModules.stream().collect(Collectors.toMap(Module::getModuleName, Function.identity()));
	}
}
