package yang.graphics.skeletons.pose;

import yang.graphics.skeletons.CartoonSkeleton2D;
import yang.math.Geometry;
import yang.math.MathConst;
import yang.physics.massaggregation.elements.Joint;
import yang.physics.massaggregation.elements.JointNormalConstraint;
import yang.util.Util;


public class AnglePose2D extends Posture<AnglePose2D,CartoonSkeleton2D>{

	public final static float PI2 = (float)Math.PI/2;

	public float[] mAngles;

	protected void init(){ };

	public AnglePose2D(float[] angles) {
		mAngles = angles;
		init();
	}

	public AnglePose2D() {
		this(null);
	}

	@Override
	public void applyPose(CartoonSkeleton2D skeleton,AnglePose2D interpolationPose,float weight) {
		int c = 0;
		final float dWeight = 1-weight;
		skeleton.mCurrentPose = this;
		for(final Joint joint:skeleton.mJoints) {
			if(c>=mAngles.length)
				break;
			//By normal constraint
			if(joint instanceof JointNormalConstraint) {
				((JointNormalConstraint)joint).setPosByConstraint();
			}else{
				final Joint parent = joint.mAngleParent;
				if(parent==null) {
					//By position
					if(joint.mAnimate) {
						float x = mAngles[c++];
						float y = mAngles[c++];
						if(weight!=1) {
							x = x*weight + interpolationPose.mAngles[c-2]*dWeight;
							y = y*weight + interpolationPose.mAngles[c-1]*dWeight;
						}
						if(skeleton.mRotation==0) {
							joint.mPosX = x;
							joint.mPosY = y;
						}else{
							joint.mPosX = -Geometry.rotateGetX(-x,y,skeleton.mRotAnchorX,skeleton.mRotAnchorY,skeleton.mRotation);
							joint.mPosY = Geometry.rotateGetY(-x,y,skeleton.mRotAnchorX,skeleton.mRotAnchorY,skeleton.mRotation);
						}
					}else{
						c += 2;
					}
				}else{
					//By angle
					if(joint.mAnimate) {
						float angle;
						if(c>=mAngles.length)
							angle = 0;
						else
							angle = mAngles[c];
						while(angle>PI)
							angle -= 2*PI;
						while(angle<-PI)
							angle += 2*PI;
						if(weight!=1) {
							float prevAngle = interpolationPose.mAngles[c];
							while(prevAngle>PI)
								prevAngle -= 2*PI;
							while(prevAngle<-PI)
								prevAngle += 2*PI;
							if(Math.abs(prevAngle-angle)>PI) {
								float diff;
								if(prevAngle>angle) {
									diff = -2*PI+prevAngle-angle;
								}else{
									diff = 2*PI+prevAngle-angle;
								}
								angle = angle + (diff)*dWeight;

							}else
								angle = angle*weight + prevAngle*dWeight;
						}
						joint.setPosByAngle(angle+skeleton.mRotation);
					}
					c++;
				}

			}

		}
	}

	@Override
	public void copyFromSkeleton(CartoonSkeleton2D skeleton) {
		int c = 0;
		//get array length
		for(final Joint joint:skeleton.mJoints) {
			if(!(joint instanceof JointNormalConstraint)) {
				final Joint parent = joint.mAngleParent;
				if(parent==null) {
					c+=2;
				}else{
					c++;
				}
			}
		}
		if(c!=mAngles.length)
			mAngles = new float[c];
		c = 0;
		for(final Joint joint:skeleton.mJoints) {
			//By normal constraint
			if(!(joint instanceof JointNormalConstraint)) {
				final Joint parent = joint.mAngleParent;
				if(parent==null) {
					//By position
					mAngles[c++] = joint.mPosX;
					mAngles[c++] = joint.mPosY;
				}else{
					//By angle
					float angle = joint.getParentAngle();
					while(angle>MathConst.PI)
						angle -= MathConst.PI*2;
					while(angle<-MathConst.PI)
						angle += MathConst.PI*2;
					mAngles[c++] = angle;
				}
			}

		}
	}

	@Override
	public String toSourceCode() {
		String res = "";
		for(final float angle:mAngles) {
			if(res!="")
				res += ",";
			res += Util.round(angle,1000)+"f";
		}
		res = "new float[]{"+res+"}";
		return res;
	}

	@Override
	public String getClassName() {
		return "AnglePose2D";
	}

}
