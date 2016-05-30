/**
 * 
 */
package repast.simphony.ui.probe;

import java.util.Iterator;

import org.java.plugin.PluginClassLoader;
import org.java.plugin.PluginManager;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.ExtensionPoint;

import repast.simphony.ui.RSApplication;
import repast.simphony.ui.RSGUIConstants;
import saf.core.runtime.PluginDefinitionException;

/**
 * Loads any custom probe extensions into the ProbeManager.
 * 
 * @author Nick Collier
 */
public class ProbeExtensionLoader {

  private PluginManager manager;
  private RSApplication rsApp;

  public ProbeExtensionLoader(RSApplication rsApp, PluginManager pluginManager) {
    this.manager = pluginManager;
    this.rsApp = rsApp;
  }

  public void loadExtensions() throws PluginDefinitionException {

			loadFieldProbeExtensions();
			loadLocationProbeProviderxtensions();
  }
  
  protected void loadFieldProbeExtensions() throws PluginDefinitionException{
    ExtensionPoint extPoint = manager.getRegistry().getExtensionPoint(RSGUIConstants.GUI_PLUGIN_ID,
        RSGUIConstants.FIELD_PROBE_EXT_ID);

    for (@SuppressWarnings("rawtypes")
    Iterator iter = extPoint.getConnectedExtensions().iterator(); iter.hasNext();) {

      Extension ext = (Extension) iter.next();
      String pluginID = ext.getDeclaringPluginDescriptor().getId();
      String className = "";
      try {
        String probedObjectClass = ext.getParameter("probedObjectClass").valueAsString();

        PluginClassLoader pluginClassLoader = manager.getPluginClassLoader(ext
            .getDeclaringPluginDescriptor());
        Class<?> probeObjClass = Class.forName(probedObjectClass, false, pluginClassLoader);
        if (!rsApp.getProbeManager().hasUICreatorFactory(probeObjClass)) {
          className = ext.getParameter("creatorClass").valueAsString();
          Class<?> clazz = Class.forName(className, true, pluginClassLoader);
          PPUICreatorFactory factory = (PPUICreatorFactory) clazz.newInstance();
          factory.init(rsApp);
          rsApp.getProbeManager().addPPUICreator(probeObjClass, factory);
        }

      } catch (ClassCastException e) {
        throw new PluginDefinitionException("createClass class '" + className + "'in plugin '" + pluginID
            + "' must implement PPUICreatorFactory", e);
      } catch (ClassNotFoundException e) {
        throw new PluginDefinitionException(
            "Unable to create class specified in plugin '" + pluginID + "'", e);
      } catch (NoClassDefFoundError e) {
        throw new PluginDefinitionException(
            "Unable to create class specified in plugin '" + pluginID + "'", e);
      } catch (IllegalAccessException e) {
        throw new PluginDefinitionException(
            "Unable to create class specified in plugin '" + pluginID + "'", e);
      } catch (InstantiationException e) {
        throw new PluginDefinitionException(
            "Unable to create class specified in plugin '" + pluginID + "'", e);
      }
    }
  }

  // TODO Projections: Maybe move this functionality into the display registry
  //       just to have it all in one place.
  protected void loadLocationProbeProviderxtensions() throws PluginDefinitionException{
    ExtensionPoint extPoint = manager.getRegistry().getExtensionPoint(RSGUIConstants.GUI_PLUGIN_ID,
        RSGUIConstants.PROBE_LOCATION_PROVIDER_EXT_ID);

    for (@SuppressWarnings("rawtypes")
    Iterator iter = extPoint.getConnectedExtensions().iterator(); iter.hasNext();) {

      Extension ext = (Extension) iter.next();
      String pluginID = ext.getDeclaringPluginDescriptor().getId();
      String providerClassName = "";
      try {
        String projectionClassName = ext.getParameter("projectionClass").valueAsString();
        PluginClassLoader pluginClassLoader = manager.getPluginClassLoader(ext
            .getDeclaringPluginDescriptor());
        Class<?> projectionClass = Class.forName(projectionClassName, false, pluginClassLoader);
        
        if (!rsApp.getProbeManager().hasLocationProbeProvider(projectionClass)) {
        	providerClassName = ext.getParameter("providerClass").valueAsString();
          Class<?> providerClass = Class.forName(providerClassName, true, pluginClassLoader);         
          LocationProbeProvider provider = (LocationProbeProvider) providerClass.newInstance();
          rsApp.getProbeManager().addLocationProbeProvider(projectionClass, provider);
        }

      } catch (ClassCastException e) {
        throw new PluginDefinitionException("providerClass class '" + providerClassName + "'in plugin '" + pluginID
            + "' must implement PPUICreatorFactory", e);
      } catch (ClassNotFoundException e) {
        throw new PluginDefinitionException(
            "Unable to create class specified in plugin '" + pluginID + "'", e);
      } catch (NoClassDefFoundError e) {
        throw new PluginDefinitionException(
            "Unable to create class specified in plugin '" + pluginID + "'", e);
      } catch (IllegalAccessException e) {
        throw new PluginDefinitionException(
            "Unable to create class specified in plugin '" + pluginID + "'", e);
      } catch (InstantiationException e) {
        throw new PluginDefinitionException(
            "Unable to create class specified in plugin '" + pluginID + "'", e);
      }
    }
  }
}
