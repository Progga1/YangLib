package yang.graphics.skeletons;

import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.textures.TextureCoordinatesQuad;
import yang.graphics.translator.GraphicsTranslator;
import yang.physics.massaggregation.elements.Joint;

public class CartoonSmoothTriangleConnection extends CartoonBone {

	public Joint mControlJoint;
	private int mSampleCount;
	private float mInitialDist;
	public ConnectionShapes mShapes;
	public int mTriangleCount;
	public int mMirrorY = -1;

	public CartoonSmoothTriangleConnection(GraphicsTranslator graphics, String name, Joint startJoint, Joint endJoint, Joint controlJoint, ConnectionShapes shapes) {
		super(graphics, name, startJoint, endJoint, shapes.getSampleCount()+2);
		mCelShading = false;
		mShapes = shapes;
		mSampleCount = mShapes.getSampleCount();
		mTriangleCount = mShapes.getSampleCount();
		mControlJoint = controlJoint;

		mIndexCount = mTriangleCount*3;

		recalculate();
		refreshVisualVars();
	}

	@Override
	public void recalculate() {
		refreshGeometry();
		this.mInitialDist = mDistance;
	}

	@Override
	public void refreshVisualVars() {
		// super.refreshVisualVars();

		this.refreshGeometry();

		float t1 = mDistance/this.mInitialDist;

		if (t1 < 0)
			t1 = 0;
		if (t1 > 1)
			t1 = 1;
		float t2 = 1-t1;

		float upX = -mNormDirY*mInitialDist*mMirrorY;
		float upY = mNormDirX*mInitialDist*mMirrorY;
		float rightX = mNormDirX*mInitialDist * 1.2f;
		float rightY = mNormDirY*mInitialDist * 1.2f;
		int k = 0;
		int v = 0;
		float w = mWidth1*2;

//		float scaleX = 1+(mShiftY1+mShiftY2);
		w *= 1.2f-Math.abs(0.5f-t1);
		boolean doubleSeg;
		for (int i = 0; i < mSampleCount; i++) {

			doubleSeg = i==0 || i==mSampleCount-1;

			float sX = (mShapes.mScales1[k]*t1 + mShapes.mScales2[k]*t2)*w;
			float sY = (mShapes.mScales1[k+1]*t1 + mShapes.mScales2[k+1]*t2)*w;
//			float d = (float)Math.sqrt(sX*sX+sY*sY);
//			d = 1/d*w;
//			sX *= d;
//			sY *= d;

			float x = (mShapes.mPositions1[k]*t1 + mShapes.mPositions2[k]*t2);
			float y = mShapes.mPositions1[k+1]*t1 +  mShapes.mPositions2[k+1]*t2;

			float resX;
			float resY;

			if(doubleSeg || i%2==1) {
				resX = x + sX;
				resY = y + sY;
				mVertX[v] = resX * rightX + resY * upX + mJoint1.mX;
				mVertY[v] = resX * rightY + resY * upY + mJoint1.mY;
				v++;
			}
			if(doubleSeg || i%2==0) {
				resX = x - sX;
				resY = y - sY;
				mVertX[v] = resX * rightX + resY * upX + mJoint1.mX;
				mVertY[v] = resX * rightY + resY * upY + mJoint1.mY;
				v++;
			}
			k += 2;
		}
	}

	@Override
	public void putIndices(short startIndex, boolean contour) {

		mVertexBuffer.putStripIndices(startIndex,mTriangleCount);
	}

	@Override
	protected void putTextureCoordBuffer(boolean contour) {
		TextureCoordinatesQuad texCoords = (contour?mTexCoords:mContourTexCoords).get(mCurTexCoords);

		float left = texCoords.mAppliedCoordinates[TextureCoordinatesQuad.ID_LEFT_TOP_X];
		float top = texCoords.mAppliedCoordinates[TextureCoordinatesQuad.ID_LEFT_TOP_Y];
		float w = texCoords.mAppliedCoordinates[TextureCoordinatesQuad.ID_RIGHT_TOP_X]-texCoords.mAppliedCoordinates[TextureCoordinatesQuad.ID_LEFT_TOP_X];
		float h = texCoords.mAppliedCoordinates[TextureCoordinatesQuad.ID_RIGHT_BOTTOM_Y]-texCoords.mAppliedCoordinates[TextureCoordinatesQuad.ID_RIGHT_TOP_Y];
		float d = 1f/(mSampleCount-1) * h;
		boolean doubleSample;
		for (int i = 0; i < mSampleCount; i++) {
			doubleSample = i==0 || i==mSampleCount-1;
//			float posY = d*i;
			float posY = mShapes.mPositions1[i*2] * h;
			float y = top+posY;
			if(doubleSample || i%2==1) {
				mVertexBuffer.putVec2(DefaultGraphics.ID_TEXTURES, left,y);
			}
			if(doubleSample || i%2==0) {
				mVertexBuffer.putVec2(DefaultGraphics.ID_TEXTURES, left+w,y);
			}

		}

	}

	@Override
	public CartoonSmoothTriangleConnection clone() {
		CartoonSmoothTriangleConnection newBone = new CartoonSmoothTriangleConnection(mGraphics, mName + "_copy", mJoint1, mJoint2, mControlJoint, mShapes);
		newBone.setContour(mContourX1, mContourY1, mContourX2, mContourY2, mContourX3, mContourY3, mContourX4, mContourY4);
		newBone.setShift(mShiftX1, mShiftY1, mShiftX2, mShiftY2);
		newBone.setWidth(mWidth1, mWidth2);
		return newBone;
	}

}
