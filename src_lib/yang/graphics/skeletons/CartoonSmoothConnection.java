package yang.graphics.skeletons;

import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.translator.GraphicsTranslator;
import yang.physics.massaggregation.elements.Joint;


public class CartoonSmoothConnection extends CartoonBone {

	public Joint mControlJoint;
	private int mSubSegmentCount;
	private float mInitialDist;
	public ConnectionShapes mShapes;

	public CartoonSmoothConnection(GraphicsTranslator graphics,String name,Joint startJoint,Joint endJoint,Joint controlJoint,ConnectionShapes shapes) {
		super(graphics,name,startJoint,endJoint,shapes.getSegmentCount()*2);
		mCelShading = false;

		mControlJoint = controlJoint;
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
//		super.refreshVisualVars();

		float t = this.mInitialDist/mDistance;
		if(t<0)
			t = 0;
		if(t>1)
			t = 1;

	}

	@Override
	protected void putTextureCoordinates() {
		for(int i=0;i<mVertexCount;i++) {
			mVertexBuffer.putVec2(DefaultGraphics.ID_TEXTURES,0,0);
		}
	}

	@Override
	protected void putContourTextureCoordinates() {
		for(int i=0;i<mVertexCount;i++) {
			mVertexBuffer.putVec2(DefaultGraphics.ID_TEXTURES,0,0);
		}
	}

	@Override
	public CartoonSmoothConnection clone() {
		CartoonSmoothConnection newBone = new CartoonSmoothConnection(mGraphics,mName+"_copy",mJoint1,mJoint2,mControlJoint,mShapes);
		newBone.setContour(mContourX1, mContourY1, mContourX2, mContourY2, mContourX3, mContourY3, mContourX4, mContourY4);
		newBone.setShift(mShiftX1, mShiftY1, mShiftX2, mShiftY2);
		newBone.setWidth(mWidth1, mWidth2);
		return newBone;
	}

}
