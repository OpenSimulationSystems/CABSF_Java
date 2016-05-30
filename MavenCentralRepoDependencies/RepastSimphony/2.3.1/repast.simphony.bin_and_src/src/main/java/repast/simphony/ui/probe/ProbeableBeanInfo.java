package repast.simphony.ui.probe;

import java.util.Map;

/**
 * @author Nick Collier
 */
public class ProbeableBeanInfo {

  private Map<String, String> nameMap;
  private OldProbeModel bean;

  public ProbeableBeanInfo(OldProbeModel bean, Map<String, String> nameMap) {
    this.nameMap = nameMap;

    this.bean = bean;
  }

  public OldProbeModel getBean() {
    return bean;
  }

  /**
   * Gets the display name mapped to the specified property name.
   * 
   * @param propName
   *          the property name
   * 
   * @return the display name mapped to the specified property name.
   */
  public String getDisplayName(String propName) {
    return nameMap.get(propName);
  }
}
