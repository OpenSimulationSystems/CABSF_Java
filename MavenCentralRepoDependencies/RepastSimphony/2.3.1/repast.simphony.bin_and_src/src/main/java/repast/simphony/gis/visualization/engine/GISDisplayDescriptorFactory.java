package repast.simphony.gis.visualization.engine;

import repast.simphony.visualization.engine.DisplayDescriptor;
import repast.simphony.visualization.gui.DisplayDescriptorFactory;

public class GISDisplayDescriptorFactory implements DisplayDescriptorFactory {

	@Override
	public DisplayDescriptor createDescriptor(String name) {
		return new GISDisplayDescriptor(name);
	}
}
