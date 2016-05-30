/**
 * 
 */
package repast.simphony.systemdynamics.generator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.xpand2.XpandExecutionContextImpl;
import org.eclipse.xpand2.XpandFacade;
import org.eclipse.xpand2.output.Outlet;
import org.eclipse.xpand2.output.Output;
import org.eclipse.xpand2.output.OutputImpl;

import repast.simphony.systemdynamics.sdmodel.SDModelPackage;
import repast.simphony.systemdynamics.sdmodel.SystemModel;
import repast.simphony.systemdynamics.sdmodel.Variable;

/**
 * Generates java / groovy code from the statechart diagram. 
 * 
 * @author Nick Collier
 */
public class CodeGenerator {
  
  private static final String SRC_GEN = "src-gen";
  
  /**
   * Generates the code in the specified project from a diagram in the specified
   * path. The code will be generated in a src-gen directory in the specified project.
   * If src-gen is not on the project's classpath it will be added.
   * 
   * @param project the project to generate the code into
   * @param path the path to the diagram file
   * @param monitor
   * @throws CoreException 
   */
  public void run(IProject project, IPath path, IProgressMonitor monitor) throws CoreException {
    try {
      XMIResourceImpl resource = new XMIResourceImpl();
      resource.load(new FileInputStream(path.toFile()), new HashMap<Object, Object>());
      
      SystemModel systemModel = null;
      for (EObject obj : resource.getContents()) {
        if (obj.eClass().equals(SDModelPackage.Literals.SYSTEM_MODEL)) {
          systemModel = (SystemModel)obj;
          break;
        }
      }
      
      /*
      IPath srcPath = addSrcPath(project, statemachine.getPackage(), monitor);
      IPath projectLocation = project.getLocation();
      srcPath = projectLocation.append(srcPath.lastSegment());
      
      Output output = new OutputImpl();
      Outlet outlet = new Outlet(srcPath.toPortableString());
      outlet.setOverwrite(true);
      outlet.addPostprocessor(new CodeBeautifier());
      output.addOutlet(outlet);
      
      Map<String, Variable> varsMap = new HashMap<String, Variable>();
      XpandExecutionContextImpl execCtx = new XpandExecutionContextImpl(output, null, varsMap, null, null);
      EmfRegistryMetaModel metamodel = new EmfRegistryMetaModel() {
          @Override
          protected EPackage[] allPackages() {
              return new EPackage[] { StatechartPackage.eINSTANCE,
                  EcorePackage.eINSTANCE,
                  NotationPackage.eINSTANCE};
          }
      };
      execCtx.registerMetaModel(metamodel);
      
      // generate
      XpandFacade facade = XpandFacade.create(execCtx);
      String templatePath = "src::generator::Main";
      facade.evaluate(templatePath, statemachine);
      
      project.getFolder(srcPath.lastSegment()).refreshLocal(IResource.DEPTH_INFINITE, monitor);
      */
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
  
  private IPath addSrcPath(IProject project, String pkg, IProgressMonitor monitor) throws CoreException {
    IJavaProject javaProject = JavaCore.create(project);
    
    // workspace relative
    IPath srcPath = javaProject.getPath().append(SRC_GEN + "/");
    // project relative
    IFolder folder = project.getFolder(SRC_GEN);
 
    if (!folder.exists()) {
      // creates within the project
      folder.create(true, true, monitor);
      IClasspathEntry[] entries = javaProject.getRawClasspath();
      boolean found = false;
      for (IClasspathEntry entry : entries) {
        if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE && entry.getPath().equals(srcPath)) {
          found = true;
          break;
        }
      }

      if (!found) {
        IClasspathEntry[] newEntries = new IClasspathEntry[entries.length + 1];
        System.arraycopy(entries, 0, newEntries, 0, entries.length);
        IClasspathEntry srcEntry = JavaCore.newSourceEntry(srcPath, null);
        newEntries[entries.length] = srcEntry;
        javaProject.setRawClasspath(newEntries, null);
      }

    } else {
      DirectoryCleaner cleaner = new DirectoryCleaner();
      //System.out.println("running cleaner on: " + project.getLocation().append(srcPath.lastSegment()).append(pkg.replace(".", "/")).toPortableString());
      //cleaner.run(project.getLocation().append(srcPath.lastSegment()).append(pkg.replace(".", "/")).toPortableString(), systemModel.getUuid());
    }
    
    return srcPath;
  }
}
