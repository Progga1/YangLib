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
		mShapes = shapes;
		mSampleCount = mShapes.getSampleCount();
		mControlJoint = controlJoint;

		mIndexCount = shapes.getSegmentCount()*6;

		recalculate();
		refreshVisualVars(0);
	}

	@Override
	public void recalculate() {
		refreshGeometry();
		this.mInitialDist = mDistance;
	}

	@Override
	public void refreshVisualVars(float contourFactor) {
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

			float resX1 = x + sX;
			float resY1 = y + sY;
			mVertX[k] = resX1 * rightX + resY1 * upX + mJoint1.mX;
			mVertY[k] = resX1 * rightY + resY1 * upY + mJoint1.mY;
			float resX2 = x - sX;
			float resY2 = y - sY;
			mVertX[k + 1] = resX2 * rightX + resY2 * upX + mJoint1.mX;
			mVertY[k + 1] = resX2 * rightY + resY2 * upY + mJoint1.mY;

			if(contourFactor>0) {
				float fac = contourFactor * 2 * mContourY1;
				float dx = mVertX[k+1] - mVertX[k];
				float dy = mVertY[k+1] - mVertY[k];
				mContourVertX[k] = mVertX[k] - dx*fac;
				mContourVertY[k] = mVertY[k] - dy*fac;
				mContourVertX[k+1] = mVertX[k+1] + dx*fac;
				mContourVertY[k+1] = mVertY[k+1] + dy*fac;
			}

			k += 2;
		}
	}

	@Override
	public void putIndices(short startIndex, boolean contour) {
		mVertexBuffer.putStripSegmentIndices(startIndex,mShapes.getSegmentCount());
	}

	@Override
	protected void putTextureCoordBuffer(boolean contour) {
		TextureCoordinatesQuad texCoords = (contour?mTexCoords:mContourTexCoords).get(mCurTexCoords);

		float left = texCoords.mAppliedCoordinates[TextureCoordinatesQuad.ID_LEFT_TOP_X];
		float top = texCoords.mAppliedCoordinates[TextureCoordinatesQuad.ID_LEFT_TOP_Y];
		float w = texCoords.mAppliedCoordinates[TextureCoordinatesQuad.ID_RIGHT_TOP_X]-texCoords.mAppliedCoordinates[TextureCoordinatesQuad.ID_LEFT_TOP_X];
		float h = texCoords.mAppliedCoordinates[TextureCoordinatesQuad.ID_RIGHT_BOTTOM_Y]-texCoords.mAppliedCoordinates[TextureCoordinatesQuad.ID_RIGHT_TOP_Y];
		float d = 1f/(mSampleCount-1) * h;
		for (int i = 0; i < mSampleCount; i++) {
//			float posY = i*d;
			float posY = mShapes.mPositions1[i*2] * h;
			float y = top+posY;
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
