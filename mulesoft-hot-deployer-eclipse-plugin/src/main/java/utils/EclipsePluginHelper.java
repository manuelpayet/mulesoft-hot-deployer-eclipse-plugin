package utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

public enum EclipsePluginHelper {
	INSTANCE();
	private EclipsePluginHelper() {
	};

	public final static String M2E_NATURE = "org.eclipse.m2e.core.maven2Nature";
	public final static String JAVA_NATURE = "org.eclipse.jdt.core.javanature";
	
	public List<IProject> listWorkspaceProjects() {
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		final IProject[] projects = workspaceRoot.getProjects();
		return Arrays.asList(projects);
	}

	public IProject getProjectFromName(final String projectName) {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
	}
	public Path getProjectLocation(final IProject project) {
		return Paths.get(project.getLocation().toOSString());
	}

	public boolean hasNatures(final IProject project, final String... natureIds) {
		try {
			for(String natureId : natureIds) {
				if(!project.hasNature(natureId)) {
					return false;
				}
			}
			return true;
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}

	public Path getWorkspaceLocation() {
		return Paths.get(ResourcesPlugin.getWorkspace().getRoot().getLocationURI());
	}

	public IProject createOrReturnExistingProject(final String projectName, final String... projectNatures) {
		IWorkspace ws = ResourcesPlugin.getWorkspace();
		IProject project = ws.getRoot().getProject("Hot Deploy Files");
		if (!project.exists()) {
			try {
				project.create(null);
				project.open(null);
				final IProjectDescription description = project.getDescription();
				description.setNatureIds(projectNatures);
				project.setDescription(description, null);
				
			} catch (CoreException e) {
				throw new RuntimeException(e);
			}
		}
		return project;
	}

}
