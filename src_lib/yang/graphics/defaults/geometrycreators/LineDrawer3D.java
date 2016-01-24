package yang.graphics.defaults.geometrycreators;

import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.model.FloatColor;
import yang.graphics.translator.glconsts.GLDrawModes;
import yang.math.objects.Point3f;
import yang.math.objects.Vector3f;
import yang.math.objects.YangMatrix;

public class LineDrawer3D extends GeometryCreator<Default3DGraphics> {

	public float mDefaultLineWidth;
	public final CylinderCreator mCylinder;
	public float[] mStartColor = null;
	public float[] mEndColor = null;
	public float[] mSuppData = null;
	public boolean mAutoFillNormals = true;
	public boolean mAutoFillTexCoords = true;

	public LineDrawer3D(Default3DGraphics graphics) {
		super(graphics);
		mDefaultLineWidth = 0.05f;
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
		if(mTranslator.mDrawMode==GLDrawModes.LINELIST) {
			mGraphics.putIndexRelative(0);
			mGraphics.putIndexRelative(1);
			mGraphics.putPosition(startX,startY,startZ);
			mGraphics.putPosition(endX,endY,endZ);
			if(mStartColor!=null) {
				mGraphics.putColor(mStartColor);
				if(mEndColor!=null)
					mGraphics.putColor(mEndColor);
				else
					mGraphics.putColor(mStartColor);
			}
			if(mAutoFillTexCoords) {
				mGraphics.putTextureCoord(0,0);
				mGraphics.putTextureCoord(1,0);
			}
			if(mSuppData!=null) {
				mGraphics.putSuppData(mSuppData);
				mGraphics.putSuppData(mSuppData);
			}
			if(mAutoFillNormals) {
				mGraphics.putNormal(0,1,0);
				mGraphics.putNormal(0,1,0);
			}
		}else{
			transform.loadIdentity();
			vec1.set(endX-startX, endY-startY, endZ-startZ);
			vec2.createOrthoVec(vec1);
			vec3.cross(vec2, vec1);
			vec3.normalize();
			transform.translate(startX, startY, startZ);
			transform.multiplyBaseVectorsRight(vec2,vec1,vec3);
			mCylinder.putPositionsAndIndices(transform, startWidth, endWidth);
			if(mStartColor!=null) {
				if(mEndColor!=null)
					mCylinder.putStartEndColors(mStartColor,mEndColor);
				else
					mCylinder.putColor(mStartColor);
			}
			if(mAutoFillTexCoords) {
				mCylinder.putTextureCoordinates();
			}
			if(mSuppData!=null) {
				mCylinder.putSuppData(mSuppData);
			}
			if(mAutoFillNormals)
				mCylinder.putNormals();
		}
	}

	public void setColorReference(FloatColor color) {
		mStartColor = color.mValues;
	}

	public void drawLine(float startX,float startY,float startZ, float endX,float endY,float endZ) {
		drawLine(startX,startY,startZ, endX,endY,endZ, mDefaultLineWidth, mDefaultLineWidth);
	}

	public void drawLine(Point3f startPoint,Point3f endPoint) {
		drawLine(startPoint.mX,startPoint.mY,startPoint.mZ, endPoint.mX,endPoint.mY,endPoint.mZ, mDefaultLineWidth,mDefaultLineWidth);
	}

	public void drawLine(Point3f startPoint,Point3f endPoint, float startWidth,float endWidth) {
		drawLine(startPoint.mX,startPoint.mY,startPoint.mZ, endPoint.mX,endPoint.mY,endPoint.mZ, startWidth,endWidth);
	}

	public int getLineVertexCount() {
		return mCylinder.getVertexCount();
	}

}
