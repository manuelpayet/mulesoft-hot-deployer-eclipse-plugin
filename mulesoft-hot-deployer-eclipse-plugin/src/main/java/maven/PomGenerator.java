package maven;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import utils.EclipsePluginHelper;

public enum PomGenerator {
	INSTANCE;

	private final static String POM_LAUNCHER = "<project>                                                                   \n"
			+ "	<modelVersion>4.0.0</modelVersion>                                       \n"
			+ "	<groupId>com.manuelpayet</groupId>                                       \n"
			+ "	<artifactId>maven-invoker</artifactId>                                   \n"
			+ "	<version>1</version>                                                     \n"
			+ "	<packaging>pom</packaging>                                               \n"
			+ "	<build>                                                                  \n"
			+ "		<plugins>                                                            \n"
			+ "			<plugin>                                                         \n"
			+ "				<artifactId>maven-invoker-plugin</artifactId>                \n"
			+ "				<groupId>org.apache.maven.plugins</groupId>                  \n"
			+ "				<version>3.0.1</version>                                     \n"
			+ "				<configuration>                                              \n"
			+ "					<cloneProjectsTo></cloneProjectsTo>                      \n"
			+ "					<streamLogs>true</streamLogs>							 \n"
			+ "                 <mavenHome>[EMBEDDED_MAVEN_HOME]</mavenHome>"			
			+ "				</configuration>                                             \n"
			+ "				<executions>                                                 \n"
			+ "					<execution>                                              \n"
			+ "						<id>construct-modules</id>                           \n"
			+ "						<goals>                                              \n"
			+ "							<goal>run</goal>                                 \n"
			+ "						</goals>                                             \n"
			+ "						<phase>package</phase>                               \n"
			+ "						<configuration>                                      \n"
			+ "							<pom>${project.basedir}/pom.xml</pom>            \n"
			+ "							<goals>                                          \n"
			+ "								<goal>clean</goal>                           \n"
			+ "								<goal>package</goal>                         \n"
			+ "								<goal>-DskipTests</goal>                     \n"
			+ "								<goal>-T1C</goal>                            \n"
			+ "							</goals>                                         \n"
			+ "						</configuration>                                     \n"
			+ "					</execution>                                             \n"
			+ "					<execution>                                              \n"
			+ "						<id>copy-modules</id>                                \n"
			+ "						<phase>package</phase>                               \n"
			+ "						<goals>                                              \n"
			+ "							<goal>run</goal>                                 \n"
			+ "						</goals>                                             \n"
			+ "						<configuration>                                      \n"
			+ "							<pom>${project.basedir}/pom.xml</pom>            \n"
			+ "							<goals>                                          \n"
			+ "								<goal>--non-recursive</goal>				 \n"
			+ "								<goal>antrun:run</goal>  					 \n"
			+ "							</goals>                                         \n"
			+ "						</configuration>                                     \n"
			+ "					</execution>                                             \n"
			+ "				</executions>                                                \n"
			+ "			</plugin>                                                        \n"
			+ "		</plugins>                                                           \n"
			+ "	</build>                                                                 \n"
			+ "</project>                                                                  \n";

	private final static String POM_TEMPLATE = "	<project>                                 \n"
			+ "		<modelVersion>4.0.0</modelVersion>                                        \n"
			+ "		<groupId>com.mycompany.app</groupId>                                      \n"
			+ "		<artifactId>my-app</artifactId>                                           \n"
			+ "		<version>1</version>                                                      \n"
			+ "		<packaging>pom</packaging>                                                \n"
			+ "     <properties><maven.deploy.skip>true</maven.deploy.skip></properties>"
			+ "		<modules>                                                                 \n"
			+ "			[MODULES]															  \n"
			+ "		</modules>                                                                \n"
			+ "		<build>                                                                   \n"
			+ "			<plugins>                                                             \n"
			+ "				<plugin>                                                          \n"
			+ "					<artifactId>maven-antrun-plugin</artifactId>                  \n"
			+ "					<version>1.7</version>                                        \n"
			+ "	           			<configuration>                                           \n"
			+ "	           				<tasks>                                               \n"
			+ "	           						<delete dir=\"[BUILD_TARGET]\"/>   	          \n"
			+ "	           						<mkdir dir=\"[BUILD_TARGET]\"/>   	          \n"
			+ "	           						[ANT_TASKS]								      \n"
			+ "	           				</tasks>                                              \n"
			+ "	           			</configuration>                                          \n"
			+ "	           			<goals>                                                   \n"
			+ "	           				<goal>run</goal>                                      \n"
			+ "	           			</goals>                                                  \n"
			+ "	           	</plugin>                                                         \n"
			+ "			</plugins>                                                            \n"
			+ "		</build>                                                                  \n"
			+ "	</project>																      ";

	private final static String MODULE_TEMPLATE = "<module>../../../../../../../../../../../../../../../../../../../../%s</module>		";
	private final static String ANT_TASKS_TEMPLATE = " <copy todir=\"[BUILD_TARGET]\">											         \n"
			+ " 	<fileset dir=\"[BASE_DIR]\"> 								    						 \n"
			+ " 		<include name=\"*-SNAPSHOT.zip\"/> 																	   		\n"
			+ " 	</fileset>"
			+ "</copy>																						\n";

	public void generatePomForEclipseProjects(final IProject projectTarget,
			final List<IProject> eclipseMavenProjectList) {
		final String modules = generateModules(eclipseMavenProjectList);
		String tempPom = replaceInTemplate(POM_TEMPLATE, "MODULES", modules);

		final String buildTarget = Paths
				.get(EclipsePluginHelper.INSTANCE.getProjectLocation(projectTarget).toString(), "modules-hot-deploy")
				.toString().replaceAll("\\\\", "/");
		tempPom = replaceInTemplate(tempPom, "BUILD_TARGET", buildTarget);
		tempPom = replaceInTemplate(tempPom, "ANT_TASKS", generateAntTasks(buildTarget, eclipseMavenProjectList));

		EclipsePluginHelper.INSTANCE.createFileWithContent(projectTarget, "pom.xml", tempPom);
		final String mavenHome = Paths.get(EclipsePluginHelper.INSTANCE.getProjectLocation(projectTarget).toString(), MavenConstant.MAVEN_TARGET_FOLDER).toString().replaceAll("\\\\", "/");
		final String mavenInvokerPomContent = replaceInTemplate(POM_LAUNCHER, "EMBEDDED_MAVEN_HOME", mavenHome);
		EclipsePluginHelper.INSTANCE.createFileWithContent(projectTarget, "maven-invoker-pom.xml", mavenInvokerPomContent);
		EclipsePluginHelper.INSTANCE.copyClassPathZipToEclipseProject(MavenConstant.MAVEN_ZIP_CLASSPATH,
				MavenConstant.MAVEN_TARGET_FOLDER, projectTarget);
	}

	private String generateModules(final List<IProject> eclipseMavenProjectList) {
		final StringBuilder modulesStringBuilder = new StringBuilder();
		for (final IProject mavenProject : eclipseMavenProjectList) {
			final Path fullPath = EclipsePluginHelper.INSTANCE.getProjectLocation(mavenProject);
			modulesStringBuilder.append(String.format(MODULE_TEMPLATE,
					fullPath.toString().replace(fullPath.getRoot().toString(), "").replaceAll("\\\\", "/")));
			modulesStringBuilder.append("\n");
		}
		return modulesStringBuilder.toString();
	}

	private String generateAntTasks(final String buildTarget, final List<IProject> eclipseMavenProjectList) {
		final StringBuilder antTasksBuilder = new StringBuilder();
		for (final IProject mavenProject : eclipseMavenProjectList) {
			String antTask = replaceInTemplate(ANT_TASKS_TEMPLATE, "BUILD_TARGET", buildTarget);
			antTask = replaceInTemplate(antTask, "BASE_DIR",
					Paths.get(EclipsePluginHelper.INSTANCE.getProjectLocation(mavenProject).toString(), "target")
							.toString().replaceAll("\\\\", "/"));
			antTasksBuilder.append(antTask);
		}
		return antTasksBuilder.toString();
	}

	private String replaceInTemplate(final String template, final String marker, final String content) {
		return template.replaceAll(String.format("\\[%s\\]", marker), content);
	}

}
