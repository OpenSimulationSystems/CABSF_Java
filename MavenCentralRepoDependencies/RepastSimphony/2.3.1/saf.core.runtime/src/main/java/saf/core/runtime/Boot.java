package saf.core.runtime;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;

import org.java.plugin.JpfException;
import org.java.plugin.ObjectFactory;
import org.java.plugin.Plugin;
import org.java.plugin.PluginManager;
import org.java.plugin.PluginManager.PluginLocation;
import org.java.plugin.boot.DefaultPluginsCollector;
import org.java.plugin.registry.Identity;
import org.java.plugin.registry.IntegrityCheckReport;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.util.ExtendedProperties;
import org.xml.sax.SAXException;

import simphony.util.messages.MessageCenter;

/**
 * Boots the plugin infrastructure.
 *
 * @author Nick Collier
 * @version $Revision: 1.11 $ $Date: 2006/06/01 16:31:11 $
 */
public class Boot {

    private static class Restriction {

        private String id;
        private String val;

        private Restriction(String id, String val) {
            this.id = id;
            this.val = val;
        }

        private boolean pass(PluginAttributes pAttribs) {

            SimpleAttribute attrib = pAttribs.getAttribute(id);
            if (attrib == null) {
                return false;
            }

            return attrib.getValue().equals(val);
        }
    }

    private static final String PLUGIN_FOLDER_PROP = "pluginFolders";
    private static final String PLUGIN_DESCRIPTOR_PROP = "plugin.descriptors";
    private static String RUNTIME_DIR_ROOT = "./";

    private static String PLUGIN_RESTRICT_PREFIX = "plugin.restrict.";
    private static final String CORE_PLUGIN_ID = "saf.core.runtime";

    public static void main(String[] args) {
        Boot boot = new Boot();
        PluginManager manager = boot.init(args);
        boot.run(manager, args);
    }

    private MessageCenter center = MessageCenter.getMessageCenter(Boot.class);

    private Collection findPluginLocations(Properties props) throws Exception {
        DefaultPluginsCollector collector = new DefaultPluginsCollector();
        ExtendedProperties eprops = new ExtendedProperties(props);

        if (props.containsKey(PLUGIN_DESCRIPTOR_PROP)) {
            // treat is as the jpf xml file that holds plugin locations
            eprops.put("org.java.plugin.boot.pluginsLocationsDescriptors",
                    props.getProperty(PLUGIN_DESCRIPTOR_PROP));
        } else {
            String pluginFolder = RUNTIME_DIR_ROOT
                    + props.getProperty(PLUGIN_FOLDER_PROP);
            eprops.put("org.java.plugin.boot.pluginsRepositories", pluginFolder);
        }
        collector.configure(eprops);
        Collection locations = collector.collectPluginLocations();

        String exclude = props.getProperty("pluginFolders.exclude", "");
        StringTokenizer tok = new StringTokenizer(exclude, ",");
        List<URL> urls = new ArrayList<URL>();
        while (tok.hasMoreTokens()) {
            String loc = tok.nextToken().trim();
            try {
                URL url = new File(loc).getCanonicalFile().toURI().toURL();
                urls.add(url);
            } catch (IOException ex) {
                center.warn("Error finding directory to exclude from plugin search", ex);
            }
        }

        if (urls.size() > 0) {
            for (Iterator iter = locations.iterator(); iter.hasNext();) {
                PluginManager.PluginLocation loc = (PluginManager.PluginLocation) iter
                        .next();
                for (URL url : urls) {
                    if (loc.getContextLocation().toExternalForm()
                            .startsWith(url.toExternalForm())) {
                        iter.remove();
                    }
                }
            }
        }

        return locations;
    }

    public PluginManager init(String[] args) {

        // load properties
        if (args.length > 1) {
            RUNTIME_DIR_ROOT = args[1];
        }

        System.setProperty("applicationRoot", RUNTIME_DIR_ROOT);

        center = MessageCenter.getMessageCenter(Boot.class);

        try {

            Properties props = new Properties();

            File file = new File(RUNTIME_DIR_ROOT, "boot.properties");

            InputStream strm;
            if (file.exists()) {
                strm = new FileInputStream(file);
            } else {
                // try as resource
                strm = getClass().getClassLoader().getResourceAsStream("boot.properties");
            }
            try {
                props.load(strm);
            } finally {
                strm.close();
            }

            // Publish current folder as configuration parameter
            // to get it available as ${applicationRoot} variable
            // when extended properties are supported
            // props.put("applicationRoot", new File(".").getCanonicalPath());
            props.put("applicationRoot", RUNTIME_DIR_ROOT);

            return initializePluginManager(findPluginLocations(props), props);

        } catch (Exception ex) {
            // todo add error center
            center.error(ex.getMessage(), ex);
        }
        return null;
    }

    private PluginManager initializePluginManager(Collection locations, Properties props)
            throws JpfException, IOException, ParserConfigurationException, SAXException {

        ObjectFactory fac = ObjectFactory.newInstance(new ExtendedProperties(props));
        PluginManager pluginManager = fac.createManager();

        List<PluginLocation> validLocations = new ArrayList<PluginLocation>();

        List<Restriction> restrictions = new ArrayList<Restriction>();
        for (Object key : props.keySet()) {
            if (key.toString().startsWith(PLUGIN_RESTRICT_PREFIX)) {
                String strKey = key.toString();
                String restrictId = strKey.substring(strKey.lastIndexOf(".") + 1,
                        strKey.length());
                String restrictVal = props.getProperty(strKey);
                restrictions.add(new Restriction(restrictId, restrictVal));
            }
        }

        if (restrictions.size() > 0) {

            for (Iterator iter = locations.iterator(); iter.hasNext();) {
                PluginLocation location = (PluginLocation) iter.next();
                PluginReader reader = new PluginReader(location.getManifestLocation());
                PluginAttributes pluginAttribs = reader.parse();

                boolean pass = true;
                for (Restriction restriction : restrictions) {
                    if (!restriction.pass(pluginAttribs)) {
                        pass = false;
                        break;
                    }
                }

                if (pass) {
                    validLocations.add(location);
                }
            }

        } else {
            validLocations.addAll(locations);
        }

        Map map = pluginManager.publishPlugins(validLocations
                .toArray(new PluginManager.PluginLocation[validLocations.size()]));

        // Check plug-in's integrity
        IntegrityCheckReport integrityCheckReport = pluginManager.getRegistry()
                .checkIntegrity(pluginManager.getPathResolver(), true);
        if (integrityCheckReport.countErrors() != 0) {
            // something wrong with the plugin set
            center.fatal(integrityCheckReport2str(integrityCheckReport),
                    new RuntimeException("Invalid plugin configuration"));
            System.exit(1);
        }

        for (Iterator iter = map.values().iterator(); iter.hasNext();) {
            Identity id = (Identity) iter.next();
            // System.out.println(id.getId());
            PluginDescriptor desc = pluginManager.getRegistry().getPluginDescriptor(
                    id.getId());
            // System.out.println(desc);
            pluginManager.getPluginClassLoader(desc);
        }

        return pluginManager;
    }

    private String integrityCheckReport2str(IntegrityCheckReport report) {
        StringBuffer buf = new StringBuffer();

        for (Iterator it = report.getItems().iterator(); it.hasNext();) {
            IntegrityCheckReport.ReportItem item = (IntegrityCheckReport.ReportItem) it
                    .next();
            if (item.getSeverity() != IntegrityCheckReport.Severity.ERROR) {
                continue;
            }
            buf.append(item.getMessage());
            buf.append("\n\n\n");
        }

        buf.append("full integrity check report:\r\n");
        buf.append("-------------- REPORT BEGIN -----------------\r\n");
        for (Iterator it = report.getItems().iterator(); it.hasNext();) {
            IntegrityCheckReport.ReportItem item = (IntegrityCheckReport.ReportItem) it
                    .next();
            buf.append("\tseverity=").append(item.getSeverity()).append("; code=")
                    .append(item.getCode()).append("; message=")
                    .append(item.getMessage()).append("; source=")
                    .append(item.getSource()).append("\r\n");
        }
        buf.append("-------------- REPORT END -----------------");
        return buf.toString();
    }

    private void run(PluginManager pluginManager, String[] args) {

        try {

            Plugin corePlugin = pluginManager.getPlugin(CORE_PLUGIN_ID);
            if (corePlugin == null) {
                throw new Exception("Cannot find core plugin");
            }

            corePlugin.getClass().getMethod("run", String[].class)
                    .invoke(corePlugin, (Object) args);

        } catch (Exception ex) {
            // todo add error center
            center.error(ex.getMessage(), ex);
        }
    }
}
