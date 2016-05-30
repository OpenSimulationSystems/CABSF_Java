package repast.simphony.gis.visualization.engine;

import repast.simphony.gis.engine.GeographyProjectionRegistryData;
import repast.simphony.scenario.data.ProjectionData;
import repast.simphony.visualization.engine.DefaultProjectionDescriptor;
import repast.simphony.visualization.engine.ProjectionDescriptor;
import repast.simphony.visualization.engine.ProjectionDescriptorFactory;
import repast.simphony.visualization.gis.GisProjectionDescriptor;
import simphony.util.messages.MessageCenter;

/**
 * ProjectionDescritorFactory for that creates ProjectionDescriptors for GIS displays.
 * 
 * @author Eric Tatara
 *
 */
public class GISProjectionDescritorFactory implements ProjectionDescriptorFactory {
	 
	private static final MessageCenter msg = MessageCenter.getMessageCenter(GISProjectionDescritorFactory.class);
	 
	@Override
	public ProjectionDescriptor createDescriptor(ProjectionData proj) {
		
		// GIS displays only handle descriptors for Geography and Network projections.
		
		if (proj.getType().equals(GeographyProjectionRegistryData.NAME)) {
			return new GisProjectionDescriptor(proj);
		} 
		
		else if (proj.getType().equals(ProjectionData.NETWORK_TYPE)) {
				return new DefaultProjectionDescriptor(proj);
		}
		
		else{
			msg.error("Could not find a GIS projection descriptor for: " + proj.getType(), null);
			
			return null;
		}
	}
}