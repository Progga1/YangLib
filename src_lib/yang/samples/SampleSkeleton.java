package yang.samples;

import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.skeletons.defaults.HumanSkeleton;

public class SampleSkeleton extends HumanSkeleton {

	public SampleSkeleton(DefaultGraphics<?> graphics) {
		this.init(graphics);
	}
	
	@Override
	protected void build() {
		super.buildHuman(true, 0.07f,0.18f,0.9f);
		super.buildDefaultLayers();
	}

}
