package repast.simphony.systemdynamics.translator;

import repast.simphony.systemdynamics.sdmodel.SystemModel;
import repast.simphony.systemdynamics.support.MappedSubscriptManager;
import repast.simphony.systemdynamics.support.NamedSubscriptManager;
import repast.simphony.systemdynamics.support.SubscriptManager;

public class InformationManagers {

  private static InformationManagers instance = null;

  private ArrayManager arrayManager; // >
  private UnitsManager unitsManager; // >
  private FunctionManager functionManager; // >
  private SystemDynamicsObjectManager systemDynamicsObjectManager; // not static
  private MappedSubscriptManager mappedSubscriptManager; // >
  private NamedSubscriptManager namedSubscriptManager; // >
  private NativeDataTypeManager nativeDataTypeManager; // >
  private SubscriptManager subscriptManager; // empty
  private MacroManager macroManager;
  private MessageManager messageManager;
  
  private SystemModel systemModel;

  public static InformationManagers getInstance() {
    if (instance == null) {
      instance = new InformationManagers();
    }
    return instance;
  }
  
  public static void clear() {
	   instance = null;
	  }

  private InformationManagers() {
    arrayManager = new ArrayManager();
    unitsManager = new UnitsManager();
    functionManager = new FunctionManager();
    systemDynamicsObjectManager = new SystemDynamicsObjectManager();
    mappedSubscriptManager = new MappedSubscriptManager();
    namedSubscriptManager = new NamedSubscriptManager();
    nativeDataTypeManager = new NativeDataTypeManager();
    subscriptManager = new SubscriptManager();
    macroManager = new MacroManager();
    messageManager = new MessageManager();
  }

  public ArrayManager getArrayManager() {
    return arrayManager;
  }

  public UnitsManager getUnitsManager() {
    return unitsManager;
  }

  public FunctionManager getFunctionManager() {
    return functionManager;
  }

  public SystemDynamicsObjectManager getSystemDynamicsObjectManager() {
    return systemDynamicsObjectManager;
  }

  public MappedSubscriptManager getMappedSubscriptManager() {
    return mappedSubscriptManager;
  }

  public NamedSubscriptManager getNamedSubscriptManager() {
    return namedSubscriptManager;
  }

  public NativeDataTypeManager getNativeDataTypeManager() {
    return nativeDataTypeManager;
  }

  public SubscriptManager getSubscriptManager() {
    return subscriptManager;
  }

  public MacroManager getMacroManager() {
    return macroManager;
  }

  public MessageManager getMessageManager() {
    return messageManager;
  }

public SystemModel getSystemModel() {
	return systemModel;
}

public void setSystemModel(SystemModel systemModel) {
	this.systemModel = systemModel;
}

}
