package utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
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
	public final static String MAVEN_MULE_NATURE="org.mule.tooling.maven.mavenNature";

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
			for (String natureId : natureIds) {
				if (!project.hasNature(natureId)) {
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

	public IFile createFileWithContent(final IProject projectTarget, final String filename, String content) {
		final IFile projectPom = projectTarget.getFile(filename);
		try {
			if (projectPom.exists()) {
				projectPom.delete(true, null);
			}
			projectPom.create(new ByteArrayInputStream(content.getBytes()), true, null);
		} catch (CoreException coreException) {
			throw new RuntimeException(coreException);
		}
		return projectPom;
	}

	public void copyClassPathZipToEclipseProject(final String classPathZip, final String destinationFolderInProject,
			final IProject project) {
		try (final ZipInputStream zipInputStream = new ZipInputStream(
				this.getClass().getResourceAsStream(classPathZip))) {
			final IFolder folder = project.getFolder(destinationFolderInProject);
			folder.delete(true, false, null);
			folder.create(true, true, null);
			ZipEntry zipEntry;
			while (null != (zipEntry = zipInputStream.getNextEntry())) {
				if (zipEntry.isDirectory()) {
					folder.getFolder(zipEntry.getName()).create(true, true, null);
				} else {
					folder.getFile(zipEntry.getName()).create(inputStreamFromZipInputstream(zipEntry, zipInputStream), true, null);
				}
			}
		} catch (IOException | CoreException e) {
			throw new RuntimeException(e);
		}

	}
	
	private InputStream inputStreamFromZipInputstream(final ZipEntry entry, final ZipInputStream inputStream) throws IOException {
		System.out.println(entry);
		final byte[] zipEntryContent = new byte[(int) entry.getSize()];
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		int lengthRead = 0;
		if(entry.getSize()>0) {
			while(-1 != (lengthRead = inputStream.read(zipEntryContent))) {
				byteArrayOutputStream.write(zipEntryContent, 0, lengthRead);
			}
		}
		return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
	}

	
}
