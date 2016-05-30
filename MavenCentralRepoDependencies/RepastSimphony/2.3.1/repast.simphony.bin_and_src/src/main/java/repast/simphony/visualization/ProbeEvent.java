package repast.simphony.visualization;

import java.util.List;

/**
 * A semantic event indicating that a probe of an object has occured.
 * 
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class ProbeEvent {

  public enum Type {
    REGION, POINT
  };

  private Object source;
  private List<?> probedObjects;
  private Type type = Type.POINT;

  
  
  public ProbeEvent(Object source, List<?> probedObjects) {
    this(source, probedObjects, Type.POINT);
  }
  
  public ProbeEvent(Object source, List<?> probedObjects, Type type) {
    this.probedObjects = probedObjects;
    this.source = source;
    this.type = type;
  }

  /**
   * Gets the list of objects that have been probed.
   * 
   * @return the list of objects that have been probed.
   */
  public List<?> getProbedObjects() {
    return probedObjects;
  }

  /**
   * Gets the source of the probe. For example, if the probing is done via a
   * mouse in an IDisplay's panel, then the source will be the IDisplay.
   * 
   * @return the source of the probe.
   */
  public Object getSource() {
    return source;
  }
  
  /**
   * Gets the type of this probe.
   * 
   * @return the type of this probe.
   */
  public Type getType() {
    return type;
  }
}
