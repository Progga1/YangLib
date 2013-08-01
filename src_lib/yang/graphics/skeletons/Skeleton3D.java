package yang.graphics.skeletons;

import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.defaults.meshcreators.LineDrawer3D;

public class Skeleton3D {

	public Default3DGraphics mGraphics3D;
	public LineDrawer3D mLineDrawer;
	
	public Skeleton3D(Default3DGraphics graphics3D) {
		mGraphics3D = graphics3D;
	}
	
	public Skeleton3D initLines(int cylinderSamples,float lineWidth) {
		mLineDrawer = new LineDrawer3D(mGraphics3D);
		mLineDrawer.setSamples(cylinderSamples);
		mLineDrawer.mLineWidth = lineWidth;
		return this;
	}
	
	public void draw() {
		mLineDrawer.drawLine(0,0,0.5f, 1,1,0);
		mGraphics3D.fillBuffers();
	}
	
	public Skeleton3D initLines() {
		return initLines(8,0.05f);
	}
	
}
