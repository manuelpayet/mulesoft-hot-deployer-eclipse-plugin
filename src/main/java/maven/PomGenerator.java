package maven;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import utils.EclipsePluginHelper;

public enum PomGenerator {
	INSTANCE;

	private final static String POM_TEMPLATE = "<project>                                                                    \n"  +
			"  <modelVersion>4.0.0</modelVersion>                                                                            \n"  +
			"  <groupId>com.mycompany.app</groupId>                                                                          \n"  +
			"  <artifactId>my-app</artifactId>                                                                               \n"  +
			"  <version>1</version>                                                                                          \n"  +
			"  <packaging>pom</packaging>                                                                                    \n"  +
			"      <build>                                                                                                   \n"  +
			"        <plugins>                                                                                               \n"  +
			"          <plugin>                                                                                              \n"  +
			"            <artifactId>maven-invoker-plugin</artifactId>                                                       \n"  +
			"            <version>3.0.1</version>                                                                            \n"  +
			"			<configuration>                                                                                      \n"  +
			"			  <projectsDirectory>%s</projectsDirectory> 	                                                     \n"  +
			"			  <cloneProjectsTo>${project.build.directory}/modules-hot-deploy</cloneProjectsTo>					 \n"  +
			"              <pomIncludes>                                                                                     \n"  +
			"				%s                                     		                                                     \n"  +
			"				</pomIncludes>                                                                                   \n"  +
			"			  <streamLogs>true</streamLogs>                                                                      \n"  +
			"			  <goals>      																						 \n"  +
			"			   <goal>-o</goal>                                                                                   \n"  +
			"			   <goal>clean</goal>                                                                                \n"  +
			"			   <goal>package</goal>                                                                              \n"  +
			"			   <goal>-DskipTests</goal>                                                                          \n"  +
			"			  </goals>                                                                                           \n"  +
			"			  <parallelThreads>4</parallelThreads>                                                               \n"  +
			"            </configuration>                                                                                    \n"  +
			"            <executions>                                                                                        \n"  +
			"              <execution>                                                                                       \n"  +
			"                <id>install</id>                                                                                \n"  +
			"                <goals>                                                                                         \n"  +
			"                  <goal>run</goal>                                                                              \n"  +
			"                </goals>                                                                                        \n"  +
			"              </execution>                                                                                      \n"  +
			"            </executions>                                                                                       \n"  +
			"          </plugin>                                                                                             \n"  +
			"        </plugins>                                                                                              \n"  +
			"      </build>                                                                                                  \n"  +
			"</project>                                                                                                      ";
	private final static String MODULE_TEMPLATE = "<pomInclude>%s/pom.xml</pomInclude>";

	
	public IFile generatePomForEclipseProjects(final IProject projectTarget, final List<IProject> eclipseMavenProjectList) {
		final StringBuilder modulesStringBuilder = new StringBuilder();
		for (final IProject mavenProject : eclipseMavenProjectList) {
			final Path fullPath = EclipsePluginHelper.INSTANCE.getProjectLocation(mavenProject);
			modulesStringBuilder.append(String.format(MODULE_TEMPLATE, fullPath.getFileName().toString()));
			modulesStringBuilder.append("\n");
		}
		final String tempPom = String.format(POM_TEMPLATE, EclipsePluginHelper.INSTANCE.getProjectLocation(eclipseMavenProjectList.get(0)).getParent(), modulesStringBuilder.toString());
		
		final IProject eclipseProject = projectTarget;
		final IFile projectPom = eclipseProject.getFile("pom.xml");
		try {
			if(projectPom.exists()) {
				projectPom.delete(true, null);
			}
			projectPom.create(new ByteArrayInputStream(tempPom.getBytes()), true, null);
		} catch(CoreException coreException) {
			throw new RuntimeException(coreException);
		}
		
		return projectPom;
	}
}
