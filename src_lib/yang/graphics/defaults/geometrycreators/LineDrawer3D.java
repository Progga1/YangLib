package yang.graphics.defaults.geometrycreators;

import yang.graphics.defaults.Default3DGraphics;
import yang.math.objects.Point3f;
import yang.math.objects.Vector3f;
import yang.math.objects.matrix.YangMatrix;

public class LineDrawer3D extends GeometryCreator<Default3DGraphics> {

	public float mLineWidth;
	public final CylinderCreator mCylinder;


	public LineDrawer3D(Default3DGraphics graphics) {
		super(graphics);
		mLineWidth = 0.05f;
		mCylinder = new CylinderCreator(graphics);
		setSamples(8);
	}

	public void setSamples(int samples) {
		mCylinder.mSamples = samples;
	}

	private final YangMatrix transform = new YangMatrix();

	private final Vector3f vec1 = new Vector3f();
	private final Vector3f vec2 = new Vector3f();
	private final Vector3f vec3 = new Vector3f();

	public void drawLine(float startX,float startY,float startZ, float endX,float endY,float endZ, float startWidth,float endWidth) {
		transform.loadIdentity();
		vec1.set(endX-startX, endY-startY, endZ-startZ);
		vec2.createOrthoVec(vec1);
		vec3.cross(vec2, vec1);
		vec3.normalize();
		transform.translate(startX, startY, startZ);
		transform.multiplyBaseVectorsRight(vec2,vec1,vec3);
		mCylinder.putPositions(transform, startWidth, endWidth);
	}

	public void drawLine(float startX,float startY,float startZ, float endX,float endY,float endZ) {
		drawLine(startX,startY,startZ, endX,endY,endZ, mLineWidth, mLineWidth);
	}

	public void drawLine(Point3f startPoint,Point3f endPoint) {
		drawLine(startPoint.mX,startPoint.mY,startPoint.mZ, endPoint.mX,endPoint.mY,endPoint.mZ, mLineWidth,mLineWidth);
	}

	public void drawLine(Point3f startPoint,Point3f endPoint, float startWidth,float endWidth) {
		drawLine(startPoint.mX,startPoint.mY,startPoint.mZ, endPoint.mX,endPoint.mY,endPoint.mZ, startWidth,endWidth);
	}

	public int getLineVertexCount() {
		return mCylinder.getVertexCount();
	}

}