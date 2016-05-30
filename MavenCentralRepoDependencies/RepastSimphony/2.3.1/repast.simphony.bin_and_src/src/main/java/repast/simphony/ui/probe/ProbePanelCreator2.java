package repast.simphony.ui.probe;

import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;

import repast.simphony.context.Context;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;
import simphony.util.messages.MessageCenter;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Creates a panel showing the properties of a probed object.
 * 
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class ProbePanelCreator2 {

  private MessageCenter msgCenter = MessageCenter.getMessageCenter(ProbePanelCreator2.class);
  // one model for the probed object, and others for the locations
  private List<ProbeModel> models = new ArrayList<ProbeModel>();
  private ProbeInfo pbInfo;
  private Object target;
  private Map<Class<?>, LocationProbeProvider> locationProviderMap;
  
  public ProbePanelCreator2(Object objectToProbe) {
    this.target = objectToProbe;
    try {
      pbInfo = ProbeIntrospector.getInstance().getProbeInfo(objectToProbe.getClass());
    } catch (Exception ex) {
      msgCenter.warn("Error creating probe panel.", ex);
    }
  }

  private List<ProbedPropertyUICreator> createProperties(Map<Class<?>, PPUICreatorFactory> creatorMap, boolean wrap) {
    List<ProbedPropertyUICreator> props = new ArrayList<ProbedPropertyUICreator>();
    for (MethodPropertyDescriptor pd : pbInfo.methodPropertyDescriptors()) {
      DefaultProbedPropertyUICreator prop = ProbedPropertyFactory.createProbedProperty(pd, wrap);
      if (prop != null)
        props.add(prop);
    }
    
    for (FieldPropertyDescriptor fpd : pbInfo.fieldPropertyDescriptor()) {
      Class<?> clazz = fpd.getField().getType();
      PPUICreatorFactory fac  = null;
      
      // try to find exact match
      for (Class<?> key : creatorMap.keySet()) {
        if (clazz.equals(key)) fac = creatorMap.get(key);
      }
      
      // get assignable
      if (fac == null) {
     // try to find exact match
        for (Class<?> key : creatorMap.keySet()) {
          if (key.isAssignableFrom(clazz)) fac = creatorMap.get(key);
        }
      }
      
      if (fac != null) {
        try {
          props.add(fac.createUICreator(target, fpd));
        } catch (Exception ex) {
          msgCenter.warn("Error while creating probe for " + target, ex);
        }
      }
    }
    
    return props;
  }

  private JPanel createPanel(List<ProbedPropertyUICreator> props, String title) {
    Collections.sort(props, new Comparator<ProbedPropertyUICreator>() {
      public int compare(ProbedPropertyUICreator o1, ProbedPropertyUICreator o2) {
        return o1.getDisplayName().compareTo(o2.getDisplayName());
      }
    });
    FormLayout layout = new FormLayout("3dlu, right:pref, 6dlu, pref:grow", "");
    DefaultFormBuilder builder = new DefaultFormBuilder(layout);
    builder.setDefaultDialogBorder();
    builder.appendSeparator(title);
    builder.setLeadingColumnOffset(1);
    builder.nextLine();

    ProbeModel model = new ProbeModel(target);
    models.add(model);
    for (ProbedPropertyUICreator prop : props) {
      JComponent component = prop.getComponent(model);
      if (component instanceof JFormattedTextField) {
        component.addFocusListener(tempFocusCommitter);
      } else if (component instanceof JPanel) {
        for (Component jcomponent : ((JPanel) component).getComponents()) {
          if (jcomponent instanceof JFormattedTextField) {
            jcomponent.addFocusListener(tempFocusCommitter);
          }
        }
      }
      builder.append(prop.getDisplayName() + ":", component);
      builder.nextLine();
    }

    if (target != null) {
      builder.appendSeparator("Locations");
      addLocations(builder);
    }

    return builder.getPanel();
  }

  private void addLocations(DefaultFormBuilder builder) {
    // see if we can find the context for it and the projections
    Context<?> context = ContextUtils.getContext(target);

    if (context != null) {
      for (Grid<?> grid : context.getProjections(Grid.class)) {
        GridLocationProbe probe = new GridLocationProbe(target, grid);
        // wrap in model to do the binding.
        ProbeModel model = new ProbeModel(probe);
        models.add(model);

        try {
          StringProbedProperty prop = new StringProbedProperty(probe.getLocationDescriptor());

          JComponent component = prop.getComponent(model);
          builder.append(grid.getName() + ":", component);
          builder.nextLine();
        } catch (IntrospectionException ex) {
          msgCenter.warn("Error while creating grid location probe.", ex);
        }
      }

      for (ContinuousSpace<?> space : context.getProjections(ContinuousSpace.class)) {
        SpaceLocationProbe probe = new SpaceLocationProbe(target, space);
        // wrap in model to do the binding.
        ProbeModel model = new ProbeModel(probe);
        models.add(model);
        try {
          StringProbedProperty prop = new StringProbedProperty(probe.getLocationDescriptor());
          JComponent component = prop.getComponent(model);
          builder.append(space.getName() + ":", component);
          builder.nextLine();
        } catch (IntrospectionException ex) {
          msgCenter.warn("Error while creating grid location probe.", ex);
        }
      }
   }

    if (target instanceof ValueLayerProbeObject2D) {
      ValueLayerLocationProbe probe = new ValueLayerLocationProbe(target);
      ProbeModel model = new ProbeModel(probe);
      models.add(model);
      try {
        StringProbedProperty prop = new StringProbedProperty(probe.getLocationDescriptor());

        JComponent component = prop.getComponent(model);
        builder.append("value layer:", component);
        builder.nextLine();
      } catch (IntrospectionException ex) {
        msgCenter.warn("Error while creating grid location probe.", ex);
      }
    }
    
    // Last look through any available LocationProbeProviders for the object's location 
    for (LocationProbeProvider provider : locationProviderMap.values()){
    	Map<String,LocationProbe> locationProbes = provider.getLocations(target);

    	for (String projectionName : locationProbes.keySet()){
    		LocationProbe probe = locationProbes.get(projectionName);
    		ProbeModel model = new ProbeModel(probe);
    		models.add(model);

    		try {
    			StringProbedProperty prop = new StringProbedProperty(probe.getLocationDescriptor());

    			JComponent component = prop.getComponent(model);
    			builder.append(projectionName + ":", component);
    			builder.nextLine();
    		} catch (IntrospectionException ex) {
    			msgCenter.warn("Error while creating location probe for " + projectionName + ".", ex);
    		}
    	}
    }
  }

  // we need this because moving focus from a one of the parameter
  // or probed fields to a menu item does not normally cause the
  // the field to commit an edit. If the menu item works with the
  // field the menu item will then be working with the old field
  // value not the new one
  private FocusAdapter tempFocusCommitter = new FocusAdapter() {

    @Override
    public void focusLost(FocusEvent e) {
      if (e.getSource() instanceof JFormattedTextField && e.isTemporary()) {
        JFormattedTextField source = ((JFormattedTextField) e.getSource());
        try {
          source.commitEdit();
          source.setValue(source.getValue());
        } catch (ParseException e1) {
          source.setValue(source.getValue());
        }

      }
    }
  };

  public Probe getProbe(Map<Class<?>, PPUICreatorFactory> creatorMap, 
  		Map<Class<?>, LocationProbeProvider> locationProviderMap, boolean wrap) {
    List<ProbedPropertyUICreator> props = createProperties(creatorMap, wrap);
    this.locationProviderMap = locationProviderMap;
    try {
      String title = ProbedPropertyFactory.createProbedTitle(pbInfo, target);
      JPanel panel = createPanel(props, title);
      return new Probe(models, panel, title);
    } catch (IllegalArgumentException e) {
      msgCenter.warn("Error creating probe.", e);
    } catch (IllegalAccessException e) {
      msgCenter.warn("Error creating probe.", e);
    } catch (InvocationTargetException e) {
      msgCenter.warn("Error creating probe.", e);
    }
    return null;
  }
}
