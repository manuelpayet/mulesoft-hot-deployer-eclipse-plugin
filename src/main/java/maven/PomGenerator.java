package maven;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.core.resources.IProject;

import utils.EclipsePluginHelper;

public enum PomGenerator {
	INSTANCE;

	private final static String POM_TEMPLATE = "<project>                                                                   \n"
			+ "  <modelVersion>4.0.0</modelVersion>                                        \n"
			+ "  <groupId>com.mycompany.app</groupId>                                      \n"
			+ "  <artifactId>my-app</artifactId>                                           \n"
			+ "  <version>1</version>                                                      \n"
			+ "  <packaging>pom</packaging>                                                \n"
			+ "  <modules>                                                                 \n" + "%s"
			+ "  </modules>                                                                \n" + "</project>";
	private final static String MODULE_TEMPLATE = "<module>../../../../../../../../../../../../../../../../%s</module>";

	public Path generatePomForEclipseProjects(final List<IProject> eclipseMavenProjectList) {
		final StringBuilder modulesStringBuilder = new StringBuilder();
		for (final IProject mavenProject : eclipseMavenProjectList) {
			final Path pathWithDrive = EclipsePluginHelper.INSTANCE.getProjectLocation(mavenProject);
			final String stringWithoutDrive = pathWithDrive.toString().replace(pathWithDrive.getRoot().toString(), "");
			modulesStringBuilder.append(String.format(MODULE_TEMPLATE, stringWithoutDrive));
			modulesStringBuilder.append("\n");
		}
		final String tempPom = String.format(POM_TEMPLATE, modulesStringBuilder.toString());
		final Path pomDestination = Paths.get(EclipsePluginHelper.INSTANCE.getWorkspaceLocation().toString(),
				"hot-deploy-generated-pom.xml");
		try {
			Files.write(pomDestination, tempPom.getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return pomDestination;
	}
}
