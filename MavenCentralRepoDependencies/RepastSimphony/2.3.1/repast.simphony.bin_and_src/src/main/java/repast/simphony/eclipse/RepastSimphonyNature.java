/**
 * 
 */
package repast.simphony.eclipse;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

/**
 * Repast Simphony's nature.
 * 
 * @author Nick Collier
 */
public class RepastSimphonyNature implements IProjectNature {
  
  private static final String CONFIG_EXTENSION_ID = "repast.simphony.project.config";

  private IProject project;

  @Override
  /**
   * Calls configure on all the repast.simphony.project.config extensions.
   */
  public void configure() throws CoreException {
    IConfigurationElement[] configs = Platform.getExtensionRegistry().getConfigurationElementsFor(CONFIG_EXTENSION_ID);
    for (IConfigurationElement element : configs) {
      IContributor contrib = element.getContributor();
      Object obj = element.createExecutableExtension("class");
      if (obj instanceof IRepastProjectConfigurator) {
        callConfig((IRepastProjectConfigurator) obj, project, contrib.getName());
      }
    }
  }

  private void callConfig(IRepastProjectConfigurator config, IProject project,
      String contribName) {
    try {
      config.configureProject(project);
    } catch (Exception ex) {
      RepastSimphonyPlugin.getInstance().log(
          new CoreException(new Status(Status.ERROR, contribName,
              "Error during project configuration", ex)));
    }
  }
  
  private void callDeconfig(IRepastProjectConfigurator config, IProject project,
      String contribName) {
    try {
      config.deconfigureProject(project);
    } catch (Exception ex) {
      RepastSimphonyPlugin.getInstance().log(
          new CoreException(new Status(Status.ERROR, contribName,
              "Error during project deconfiguration", ex)));
    }
  }

  /**
   * Calls deconfigure on all the the repast.simphony.project.config extensions.
   */
  @Override
  public void deconfigure() throws CoreException {
    IConfigurationElement[] configs = Platform.getExtensionRegistry().getConfigurationElementsFor(CONFIG_EXTENSION_ID);
    for (IConfigurationElement element : configs) {
      IContributor contrib = element.getContributor();
      Object obj = element.createExecutableExtension("class");
      if (obj instanceof IRepastProjectConfigurator) {
        callDeconfig((IRepastProjectConfigurator) obj, project, contrib.getName());
      }
    }
  }

  @Override
  public IProject getProject() {
    return project;
  }

  @Override
  public void setProject(IProject project) {
    this.project = project;
  }
}
