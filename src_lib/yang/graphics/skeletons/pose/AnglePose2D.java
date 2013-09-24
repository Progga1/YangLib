package yang.graphics.skeletons.pose;

import yang.graphics.skeletons.CartoonSkeleton2D;
import yang.math.Geometry;
import yang.math.MathConst;
import yang.physics.massaggregation.MassAggregation;
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
		float dWeight = 1-weight;
		skeleton.mCurrentPose = this;
		for(Joint joint:skeleton.mJoints) {
			//By normal constraint
			if(joint instanceof JointNormalConstraint) {
				((JointNormalConstraint)joint).setPosByConstraint();
			}else{
				Joint parent = joint.mAngleParent;
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
							angle = skeleton.mRotation;
						else
							angle = mAngles[c]+skeleton.mRotation;
						if(weight!=1) {
							float prevAngle = interpolationPose.mAngles[c];
							if(Math.abs(prevAngle-angle)>MathConst.PI) {
								float diff;
								if(prevAngle>angle) {
									diff = 2*MathConst.PI-(prevAngle-angle);
								}else
									diff = 2*MathConst.PI-(angle-prevAngle);
								angle = angle + (diff)*dWeight;
							}else
								angle = angle*weight + prevAngle*dWeight;
						}
						joint.setPosByAngle(angle);
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
		for(Joint joint:skeleton.mJoints) {
			if(!(joint instanceof JointNormalConstraint)) {
				Joint parent = joint.mAngleParent;
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
		for(Joint joint:skeleton.mJoints) {
			//By normal constraint
			if(!(joint instanceof JointNormalConstraint)) {
				Joint parent = joint.mAngleParent;
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
		for(float angle:mAngles) {
			if(res!="")
				res += ",";
			res += Util.round(angle,1000)+"f";
		}
		res = "new float[]{"+res+"}";
		return res;
	}
	
	@Override
	public String getClassName() {
		return "AnglePose";
	}
	
}
