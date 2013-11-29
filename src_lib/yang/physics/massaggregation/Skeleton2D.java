package yang.physics.massaggregation;

import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.translator.Texture;
import yang.physics.massaggregation.elements.Joint;

public class Skeleton2D extends MassAggregation {

	public static Texture CURSOR_TEXTURE;

	public float mRotation;
	public float mRotAnchorX;
	public float mRotAnchorY;
	public int mLookDirection;

	public Skeleton2D() {
		super();
		m3D = false;
		mRotation = 0;
		mLookDirection = 1;
	}

	public static void drawEditing(MassAggregation massAggregation,SkeletonEditing skeletonEditing,DefaultGraphics<?> graphics,float offsetX,float offsetY,int lookDirection) {
		Joint markedJoint;
		if(skeletonEditing==null)
			markedJoint = null;
		else
			markedJoint = skeletonEditing.mMainMarkedJoint;
		final float worldPosX = massAggregation.mCarrier.getWorldX() + offsetX;
		final float worldPosY = massAggregation.mCarrier.getWorldY() + offsetY;
		final float scale = massAggregation.mCarrier.getScale()*massAggregation.mScale;
		final int mirrorFac = lookDirection;

		final GraphicsTranslator translator = graphics.mTranslator;
		graphics.setDefaultProgram();
		translator.bindTexture(CURSOR_TEXTURE);
		graphics.setColorFactor(1);

		for(final Joint joint:massAggregation.mJoints)
			if(joint.mEnabled){
				final float alpha = (markedJoint==joint)?1:0.6f;
				if(joint.mFixed)
					graphics.setColor(1, 0, 0, alpha);
				else
					graphics.setColor(0.8f,0.8f,0.8f,alpha);
				graphics.drawRectCentered(worldPosX + joint.mPosX*scale * mirrorFac, worldPosY + joint.mPosY*scale, joint.getOutputRadius()*2);
			}

		translator.bindTexture(null);
		graphics.setColor(0.8f, 0.1f, 0,0.8f);
		for(final Joint joint:massAggregation.mJoints)
			if(joint.mEnabled && joint.mAngleParent!=null){
				graphics.drawLine(
						worldPosX + joint.mPosX*scale * mirrorFac, worldPosY + joint.mPosY*scale,
						worldPosX + joint.mAngleParent.mPosX*scale * mirrorFac, worldPosY + joint.mAngleParent.mPosY*scale,
						0.015f
						);
		}

	}

	public Joint getJoint(int id) {
		return mJoints.get(id);
	}

}
