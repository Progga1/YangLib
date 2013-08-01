package yang.graphics.defaults.meshcreators;

import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.defaults.programs.subshaders.properties.LightProperties;
import yang.math.objects.matrix.YangMatrix;

public class LineDrawer3D extends MeshCreator<Default3DGraphics> {

	public float mLineWidth;
	public float mCylinderSamples;
	private CylinderCreator mCylinder;
	
	public LineDrawer3D(Default3DGraphics graphics) {
		super(graphics);
		mLineWidth = 0.05f;
		mCylinderSamples = 8;
		mCylinder = new CylinderCreator(graphics);
	}

	private YangMatrix transform = new YangMatrix();
	
	public void drawLine(float startX,float startY,float startZ, float endX,float endY,float endZ) {
		transform.loadIdentity();
		transform.scale(mLineWidth, 1, mLineWidth);
		mCylinder.drawCylinder(transform, true);
	}
	
}
