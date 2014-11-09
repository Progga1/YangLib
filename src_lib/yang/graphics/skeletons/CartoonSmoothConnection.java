package yang.graphics.skeletons;

import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.textures.TextureCoordinatesQuad;
import yang.graphics.translator.GraphicsTranslator;
import yang.physics.massaggregation.elements.Joint;

public class CartoonSmoothConnection extends CartoonBone {

	public Joint mControlJoint;
	private int mSampleCount;
	private float mInitialDist;
	public ConnectionShapes mShapes;
	public int mMirrorY = -1;

	public CartoonSmoothConnection(GraphicsTranslator graphics, String name, Joint startJoint, Joint endJoint, Joint controlJoint, ConnectionShapes shapes) {
		super(graphics, name, startJoint, endJoint, shapes.getSampleCount() * 2);
		mCelShading = false;
		mShapes = shapes;
		mSampleCount = mShapes.getSampleCount();
		mControlJoint = controlJoint;

		mIndexCount = shapes.getSegmentCount()*6;

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
		float w = mWidth1*2;

//		float scaleX = 1+(mShiftY1+mShiftY2);
		w *= 1.2f-Math.abs(0.5f-t1);
		for (int i = 0; i < mSampleCount; i++) {

			float sX = (mShapes.mScales1[k]*t1 + mShapes.mScales2[k]*t2)*w;
			float sY = (mShapes.mScales1[k+1]*t1 + mShapes.mScales2[k+1]*t2)*w;
//			float d = (float)Math.sqrt(sX*sX+sY*sY);
//			d = 1/d*w;
//			sX *= d;
//			sY *= d;

			float x = (mShapes.mPositions1[k]*t1 + mShapes.mPositions2[k]*t2);
			float y = mShapes.mPositions1[k+1]*t1 +  mShapes.mPositions2[k+1]*t2;

			float resX = x + sX;
			float resY = y + sY;
			mVertX[k] = resX * rightX + resY * upX + mJoint1.mX;
			mVertY[k] = resX * rightY + resY * upY + mJoint1.mY;
			resX = x - sX;
			resY = y - sY;
			mVertX[k + 1] = resX * rightX + resY * upX + mJoint1.mX;
			mVertY[k + 1] = resX * rightY + resY * upY + mJoint1.mY;

			k += 2;
		}
	}

	@Override
	public void putIndices(short startIndex, boolean contour) {
		mVertexBuffer.putStripIndices(startIndex,mShapes.getSegmentCount());
	}

	@Override
	protected void putTextureCoordBuffer(boolean contour) {
		TextureCoordinatesQuad texCoords = (contour?mTexCoords:mContourTexCoords).get(mCurTexCoords);
		float left = texCoords.getBiasedLeft();
		float top = texCoords.getBiasedTop();
		float w = texCoords.getBiasedWidth();
		float h = texCoords.getBiasedHeight();
		float d = 1f/(mSampleCount-1) * h;
		for (int i = 0; i < mSampleCount; i++) {
			float y = top+d*i;
			mVertexBuffer.putVec2(DefaultGraphics.ID_TEXTURES, left,y);
			mVertexBuffer.putVec2(DefaultGraphics.ID_TEXTURES, left+w,y);
		}
	}

	@Override
	public CartoonSmoothConnection clone() {
		CartoonSmoothConnection newBone = new CartoonSmoothConnection(mGraphics, mName + "_copy", mJoint1, mJoint2, mControlJoint, mShapes);
		newBone.setContour(mContourX1, mContourY1, mContourX2, mContourY2, mContourX3, mContourY3, mContourX4, mContourY4);
		newBone.setShift(mShiftX1, mShiftY1, mShiftX2, mShiftY2);
		newBone.setWidth(mWidth1, mWidth2);
		return newBone;
	}

}
