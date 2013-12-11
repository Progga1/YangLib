package yang.graphics.skeletons.pose;

import yang.graphics.skeletons.CartoonSkeleton2D;
import yang.math.Geometry;
import yang.math.MathConst;
import yang.physics.massaggregation.elements.Joint;
import yang.physics.massaggregation.elements.JointNormalConstraint;
import yang.util.Util;


public class AnglePosture2D extends Posture<AnglePosture2D,CartoonSkeleton2D>{

	public final static float PI2 = (float)Math.PI/2;

	public AnglePosture2D(float[] angles) {
		super(angles);
	}

	public AnglePosture2D() {
		super();
	}

	@Override
	public void applyPosture(CartoonSkeleton2D skeleton,AnglePosture2D interpolationPose,float weight) {
		int c = 0;
		final float dWeight = 1-weight;
		skeleton.mCurrentPose = this;
		for(final Joint joint:skeleton.mJoints) {
			if(c>=mData.length)
				break;
			//By normal constraint
			if(joint instanceof JointNormalConstraint) {
				((JointNormalConstraint)joint).setPosByConstraint();
			}else{
				final Joint parent = joint.mAngleParent;
				if(parent==null) {
					//By position
					if(joint.mAnimate) {
						float x = mData[c++];
						float y = mData[c++];
						if(weight!=1) {
							x = x*weight + interpolationPose.mData[c-2]*dWeight;
							y = y*weight + interpolationPose.mData[c-1]*dWeight;
						}
						if(skeleton.mRotation==0) {
							joint.mX = x;
							joint.mY = y;
						}else{
							joint.mX = -Geometry.rotateGetX(-x,y,skeleton.mRotAnchorX,skeleton.mRotAnchorY,skeleton.mRotation);
							joint.mY = Geometry.rotateGetY(-x,y,skeleton.mRotAnchorX,skeleton.mRotAnchorY,skeleton.mRotation);
						}
					}else{
						c += 2;
					}
				}else{
					//By angle
					if(joint.mAnimate) {
						float angle;
						if(c>=mData.length)
							angle = 0;
						else
							angle = mData[c];
						while(angle>PI)
							angle -= 2*PI;
						while(angle<-PI)
							angle += 2*PI;
						if(weight!=1) {
							float prevAngle = interpolationPose.mData[c];
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
						joint.setPosByAngle2D(angle+skeleton.mRotation);
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
		if(c!=mData.length)
			mData = new float[c];
		c = 0;
		for(final Joint joint:skeleton.mJoints) {
			//By normal constraint
			if(!(joint instanceof JointNormalConstraint)) {
				final Joint parent = joint.mAngleParent;
				if(parent==null) {
					//By position
					mData[c++] = joint.mX;
					mData[c++] = joint.mY;
				}else{
					//By angle
					float angle = joint.getParentAngle();
					while(angle>MathConst.PI)
						angle -= MathConst.PI*2;
					while(angle<-MathConst.PI)
						angle += MathConst.PI*2;
					mData[c++] = angle;
				}
			}

		}
	}

	@Override
	public String toSourceCode() {
		String res = "";
		for(final float angle:mData) {
			if(res!="")
				res += ",";
			res += Util.round(angle,1000)+"f";
		}
		res = "new float[]{"+res+"}";
		return res;
	}

	@Override
	public void applyForceBased(CartoonSkeleton2D skeleton, AnglePosture2D interpolationPose, float weight) {
		// TODO Auto-generated method stub

	}

}
