package yang.samples;

import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.skeletons.defaults.creators.human.HumanSkeleton2D;

public class SampleSkeleton extends HumanSkeleton2D {

	public SampleSkeleton(DefaultGraphics<?> graphics,boolean use3D) {
		super();
		m3D = use3D;
		this.init(graphics);
	}
	
	@Override
	protected void build() {
		super.buildHuman(true, 0.07f,0.18f,0.9f);
		super.buildDefaultLayers();
	}

}
