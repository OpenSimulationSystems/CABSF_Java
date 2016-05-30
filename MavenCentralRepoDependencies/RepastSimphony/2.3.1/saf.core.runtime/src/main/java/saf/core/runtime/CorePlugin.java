package saf.core.runtime;

import org.java.plugin.Plugin;
import org.java.plugin.PluginLifecycleException;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.ExtensionPoint;
import simphony.util.messages.MessageCenter;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Nick Collier
 * @version $Revision: 1.6 $ $Date: 2006/02/07 20:39:51 $
 */
public class CorePlugin extends Plugin {

	private static final String APP_RUN_ID = "IApplicationRunnable";
	private IApplicationRunnable appRunner;

	protected void doStart() throws Exception {

	}

	protected void doStop() throws Exception {

	}


	public void run(String[] args) {
		try {
			loadAppPlugin();
			loadUIPlugin();
			runApplicationRunnable(args);
		} catch (Exception ex) {
			MessageCenter.getMessageCenter(getClass()).error("Error instantiating plugins", ex);
			// todo ErrorCenters
		}
	}

	private void loadUIPlugin() throws PluginLifecycleException, NoSuchMethodException,
					IllegalAccessException, InvocationTargetException {
		Plugin plugin = getManager().getPlugin("saf.core.ui");
		plugin.getClass().getMethod("initialize", new Class[]{}).invoke(plugin);
	}

	private void loadAppPlugin() throws PluginLifecycleException, ClassNotFoundException,
					IllegalAccessException, InstantiationException, PluginDefinitionException {
		ExtensionPoint extPoint = getManager().getRegistry().getExtensionPoint(getDescriptor().getId(), APP_RUN_ID);
		if (extPoint.getConnectedExtensions().size() != 1) {
			throw new PluginDefinitionException("Plugin must implement one and only one IApplicationRunnable");
		}
		Extension ext = (Extension) extPoint.getConnectedExtensions().iterator().next();
		Plugin plugin = getManager().getPlugin(ext.getDeclaringPluginDescriptor().getId());
		Class pluginCls = plugin.getClass();
		Class appRunnerClass = pluginCls.getClassLoader().loadClass(ext.getParameter("class").valueAsString());
		if (pluginCls.equals(appRunnerClass)) {
			appRunner = (IApplicationRunnable) plugin;
		} else {
			appRunner = (IApplicationRunnable) appRunnerClass.newInstance();
		}
	}

	private void runApplicationRunnable(String[] args) {
		appRunner.run(args);
	}
}

