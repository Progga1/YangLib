package yang.graphics.skeletons.defaults.human;

import yang.graphics.skeletons.CartoonBone;
import yang.graphics.skeletons.CartoonSkeleton2D;
import yang.physics.massaggregation.constraints.AngleConstraint;
import yang.physics.massaggregation.constraints.DistanceConstraint;
import yang.physics.massaggregation.elements.Joint;
import yang.physics.massaggregation.elements.JointNormalConstraint;

public abstract class HumanSkeleton2D extends CartoonSkeleton2D{

	public static final float HEADTOPSHIFT = 0.05f;
	public static final float HEADBOTTOMSHIFT = -0.06f;
	
	public static final float PI = (float)Math.PI;
	public float texShiftX = 0;
	
	public Joint mBreastJoint;
	public Joint mHeadJoint;
	public Joint mHipJoint;
	public Joint mLeftKneeJoint;
	public Joint mRightKneeJoint;
	public Joint mLeftFootJoint;
	public Joint mRightFootJoint;
	public Joint mLeftElbowJoint;
	public Joint mRightElbowJoint;
	public Joint mLeftHandJoint;
	public Joint mRightHandJoint;
	public Joint mLeftToesJoint;
	public Joint mRightToesJoint;
	
	public JointNormalConstraint mLeftShoulderJoint;
	public JointNormalConstraint mRightShoulderJoint;
	public JointNormalConstraint mLeftLegJoint;
	public JointNormalConstraint mRightLegJoint;
	
	public CartoonBone mBodyBone;
	public CartoonBone mHeadBone;
	public CartoonBone mLeftUpperArmBone;
	public CartoonBone mRightUpperArmBone;
	public CartoonBone mLeftLowerArmBone;
	public CartoonBone mRightLowerArmBone;
	public CartoonBone mLeftUpperLegBone;
	public CartoonBone mRightUpperLegBone;
	public CartoonBone mLeftLowerLegBone;
	public CartoonBone mRightLowerLegBone;
	public CartoonBone mLeftFootBone;
	public CartoonBone mRightFootBone;
	
	public HumanSkeleton2D() {
		super();
		mLeftToesJoint = null;
		mRightToesJoint = null;
	}

	protected void buildHuman(boolean addFeet,float legDist,float shoulderDist, float rightShoulderFac) {
		float legZ = 0.2f;
		float armZ = 0.3f;
		float leftShoulderFac = 1.15f;
		float legShift = 0.045f;
		float locScaleX = 1.15f;
		float locScaleY = 1.0f;
		float locScaleWidth = 1.0f*locScaleX;
		float kneeDist = 0.001f;
		float leftFootY = 0.065f;
		float rightFootY = 0.095f;
		
		//--- Joints ---
		float midX = 0;
		
		mBreastJoint = new Joint("Breast",null,midX, 1.5f*locScaleY, 0.1f*locScaleX,this);
		mHeadJoint = new Joint("Head",mBreastJoint,midX,2*locScaleY, 0.1f*locScaleX,this);
		mHipJoint = new Joint("Hip",mBreastJoint,midX,0.9f*locScaleY, 0.1f*locScaleX,this);
		mLeftKneeJoint = new Joint("LeftKnee",null,midX-(legDist+legShift)*locScaleX, 0.5f*locScaleY-kneeDist, 0.1f*locScaleX,this);
		mRightKneeJoint = new Joint("RightKnee",null,midX+(legDist+legShift)*locScaleX, 0.5f*locScaleY+kneeDist, 0.1f*locScaleX,this);
		mLeftFootJoint = new Joint("LeftFoot",mLeftKneeJoint,midX-(legDist+legShift*2)*locScaleX, leftFootY*locScaleY, 0.1f*locScaleX,this);
		mRightFootJoint = new Joint("RightFoot",mRightKneeJoint,midX+(legDist+legShift*2)*locScaleX, rightFootY*locScaleY, 0.1f*locScaleX,this);
		mLeftElbowJoint = new Joint("LeftElbow",null,midX-shoulderDist*locScaleX*leftShoulderFac, 1.17f*locScaleY, 0.08f*locScaleX,this);
		mRightElbowJoint = new Joint("RightElbow",null,midX+shoulderDist*rightShoulderFac*locScaleX, 1.16f*locScaleY, 0.08f*locScaleX,this);
		mLeftHandJoint = new Joint("LeftHand",mLeftElbowJoint,midX-shoulderDist*locScaleX*leftShoulderFac, 0.85f*locScaleY, 0.06f*locScaleX,this);
		mRightHandJoint = new Joint("RightHand",mRightElbowJoint,midX+shoulderDist*rightShoulderFac*locScaleX, 0.9f*locScaleY, 0.06f*locScaleX,this);
		if(addFeet) {
			mLeftToesJoint = new Joint("LeftToes",mLeftFootJoint,midX-(legDist+legShift*2)*locScaleX+0.18f, leftFootY*locScaleY, 0.06f*locScaleX,this);
			mRightToesJoint = new Joint("RightToes",mRightFootJoint,midX+(legDist+legShift*2)*locScaleX+0.18f, rightFootY*locScaleY, 0.06f*locScaleX,this);
		}
		
		//Body
		mBodyBone = new CartoonBone(mTranslator,"Body",mBreastJoint,mHipJoint);
		mBodyBone.putTextureCoords(texShiftX, 0.5f, texShiftX+0.25f, 0.875f);
		mBodyBone.setWidth(0.23f*locScaleWidth);
		mBodyBone.setShift(-0.01f,0.06f,-0.01f,-0.1f);
		
		
		//--- Normal constraint joints ---
		mLeftShoulderJoint = new JointNormalConstraint("LeftShoulder",mBreastJoint,mBodyBone,0.17f,shoulderDist*locScaleX*leftShoulderFac,0.02f,this);
		mRightShoulderJoint = new JointNormalConstraint("RightShoulder",mBreastJoint,mBodyBone,0.10f,-shoulderDist*rightShoulderFac*locScaleX,0.023f,this);
		mLeftLegJoint = new JointNormalConstraint("LeftLeg",mHipJoint,mBodyBone,0.94f,legDist*locScaleX,0.02f,this);
		mRightLegJoint = new JointNormalConstraint("RightLeg",mHipJoint,mBodyBone,0.97f,-legDist*locScaleX,0.02f,this);
		
		mLeftElbowJoint.setParent(mLeftShoulderJoint);
		mRightElbowJoint.setParent(mRightShoulderJoint);
		mLeftKneeJoint.setParent(mLeftLegJoint);
		mRightKneeJoint.setParent(mRightLegJoint);
		
		//Add joints in dependency-compatible order
		super.addJoint(mBreastJoint);
		super.addJoint(mHipJoint);
		super.addJoint(mHeadJoint);
		super.addJoint(mLeftShoulderJoint);
		super.addJoint(mRightShoulderJoint);
		super.addJoint(mLeftLegJoint);
		super.addJoint(mRightLegJoint);
		super.addJoint(mLeftKneeJoint);
		super.addJoint(mRightKneeJoint);
		super.addJoint(mLeftFootJoint);
		super.addJoint(mRightFootJoint);
		super.addJoint(mLeftElbowJoint);
		super.addJoint(mRightElbowJoint);
		super.addJoint(mLeftHandJoint);
		super.addJoint(mRightHandJoint);
		if(addFeet) {
			super.addJoint(mLeftToesJoint);
			super.addJoint(mRightToesJoint);
		}
		
		//head
		mHeadBone = new CartoonBone(mTranslator,"Head",mHeadJoint,mBreastJoint);
		mHeadBone.putTextureCoords(texShiftX, 0, texShiftX+0.25f, 0.25f);
		mHeadBone.putTextureCoords(texShiftX, 0.25f, texShiftX+0.25f, 0.5f);
		mHeadBone.setWidth(0.35f);
		mHeadBone.setShift(-0.06f,HEADTOPSHIFT,-0.015f,HEADBOTTOMSHIFT);
		
		//arms
		mLeftUpperArmBone = new CartoonBone(mTranslator,"LeftUpperArm",mLeftShoulderJoint,mLeftElbowJoint);
		mLeftUpperArmBone.putTextureCoords(texShiftX+0.25f, 0, texShiftX+0.375f, 0.25f);
		mLeftUpperArmBone.setWidth(0.115f*locScaleWidth);
		mLeftUpperArmBone.setShift(-0.015f,0.07f,0,-0.06f);
		mLeftLowerArmBone = new CartoonBone(mTranslator,"LeftLowerLeg",mLeftElbowJoint,mLeftHandJoint);
		mLeftLowerArmBone.putTextureCoords(texShiftX+0.25f, 0.25f, texShiftX+0.375f, 0.5f);
		mLeftLowerArmBone.setWidth(0.1f*locScaleWidth);
		mLeftLowerArmBone.setShift(0,0.08f,0,-0.05f);
		
		mRightUpperArmBone = new CartoonBone(mTranslator,"LeftUpperArm",mRightShoulderJoint,mRightElbowJoint);
		mRightUpperArmBone.putTextureCoords(texShiftX+0.375f, 0, texShiftX+0.5f, 0.25f);
		mRightUpperArmBone.setWidth(0.115f*locScaleWidth);
		mRightUpperArmBone.setShift(0,0.06f,0,-0.06f); 
		mRightLowerArmBone = new CartoonBone(mTranslator,"LeftLowerArm",mRightElbowJoint,mRightHandJoint);
		mRightLowerArmBone.putTextureCoords(texShiftX+0.375f, 0.25f, texShiftX+0.5f, 0.5f);
		mRightLowerArmBone.setWidth(0.09f*locScaleWidth);
		mRightLowerArmBone.setShift(0,0.08f,0,-0.05f);
		
		//legs
		mLeftUpperLegBone = new CartoonBone(mTranslator,"LeftUpperLeg",mLeftLegJoint,mLeftKneeJoint);
		mLeftUpperLegBone.putTextureCoords(texShiftX+0.25f, 0.5f, texShiftX+0.375f, 0.75f);
		mLeftUpperLegBone.setWidth(0.12f*locScaleWidth);
		mLeftUpperLegBone.setShift(0.008f,0.08f,0.008f,-0.07f);
		mRightUpperLegBone = new CartoonBone(mTranslator,"RightUpperLeg",mRightLegJoint,mRightKneeJoint);
		mRightUpperLegBone.putTextureCoords(texShiftX+0.375f, 0.5f, texShiftX+0.5f, 0.75f);
		mRightUpperLegBone.setWidth(0.112f*locScaleWidth);
		mRightUpperLegBone.setShift(0,0.08f,0,-0.07f);
		
		mLeftLowerLegBone = new CartoonBone(mTranslator,"LeftLowerLeg",mLeftKneeJoint,mLeftFootJoint);
		mLeftLowerLegBone.putTextureCoords(texShiftX+0.25f, 0.75f, texShiftX+0.375f, 1);
		mLeftLowerLegBone.setWidth(0.115f*locScaleWidth);
		mLeftLowerLegBone.setShift(-0.005f,0.08f,0,-0.05f);
		mRightLowerLegBone = new CartoonBone(mTranslator,"RightLowerLeg",mRightKneeJoint,mRightFootJoint);
		mRightLowerLegBone.putTextureCoords(texShiftX+0.375f, 0.75f, texShiftX+0.5f, 1);
		mRightLowerLegBone.setWidth(0.11f*locScaleWidth);
		mRightLowerLegBone.setShift(-0.005f,0.08f,0,-0.05f);
		
		//feet
		if(addFeet) {
			float footShift = -0.03f;
			mLeftFootBone = new CartoonBone(mTranslator,"LeftFoot",mLeftFootJoint,mLeftToesJoint);
			mLeftFootBone.putTextureCoords(texShiftX, 7/8f, texShiftX+1/16f, 1);
			mLeftFootBone.putTextureCoords(texShiftX+3/16f, 7/8f, texShiftX+4/16f, 1);
			mLeftFootBone.setWidth(0.052f*locScaleWidth);
			mLeftFootBone.setShift(footShift,0.09f,footShift,-0.07f);
			mRightFootBone = new CartoonBone(mTranslator,"RightFoot",mRightFootJoint,mRightToesJoint);
			mRightFootBone.putTextureCoords(texShiftX+1/16f, 7/8f, texShiftX+2/16f, 1);
			mRightFootBone.putTextureCoords(texShiftX+5/16f, 7/8f, texShiftX+6/16f, 1);
			mRightFootBone.setWidth(0.052f*locScaleWidth);
			mRightFootBone.setShift(footShift,0.09f,footShift,-0.07f);
		}
		
		if(m3D) {
			mLeftLegJoint.mPosZ = legZ*0.9f;
			mRightLegJoint.mPosZ = -legZ*0.9f;
			mLeftKneeJoint.mPosZ = legZ;
			mRightKneeJoint.mPosZ = -legZ;
			mLeftFootJoint.mPosZ = legZ;
			mRightFootJoint.mPosZ = -legZ;
			if(mLeftToesJoint!=null) {
				mLeftToesJoint.mPosZ = legZ;
				mRightToesJoint.mPosZ = -legZ;
			}
			mLeftShoulderJoint.mPosZ = armZ*0.9f;
			mRightShoulderJoint.mPosZ = -armZ*0.9f;
			mLeftElbowJoint.mPosZ = armZ;
			mRightElbowJoint.mPosZ = -armZ;
			mLeftHandJoint.mPosZ = armZ;
			mRightHandJoint.mPosZ = -armZ;
		}

		//--- Contours ---
		mLeftUpperArmBone.setContour(1.1f,-0.2f,0.9f,0.2f);
		mLeftUpperArmBone.mContourY1 = -0.3f;
		mLeftUpperArmBone.mContourX2 = 0.6f;		
		mLeftUpperArmBone.mContourX4 = 1.1f;
		mRightUpperArmBone.setContour(1,0.5f,0.8f,0.8f);
		mLeftLowerArmBone.setContour(1,0.25f,1,0.5f);
		mRightLowerArmBone.copyContour(mLeftLowerArmBone);
		mLeftUpperLegBone.setContour(0.8f,-0.1f,0.65f,0.55f);
		mLeftUpperLegBone.mContourX1 = 0.7f;
		mLeftUpperLegBone.mContourX2 = 0.45f;
		mLeftUpperLegBone.mContourY2 = -1.8f;
		mRightUpperLegBone.setContour(0.85f,0,0.6f,0.55f);
		mLeftLowerLegBone.setContour(1,0.4f,1,0.8f);
		mRightLowerLegBone.copyContour(mLeftLowerLegBone);
		mBodyBone.setContour(0.8f, 0.5f, 0.7f, 0.7f);
		mBodyBone.mContourX2 = 0.1f;
		mBodyBone.mContourY2 = -0.2f;
		mBodyBone.mContourX4 = 1;
		mHeadBone.setContour(0.45f,0.5f,0.4f,0.3f);
		mHeadBone.mContourX3 = 0.9f;
		mHeadBone.mContourY4 = 1.2f;
		if(addFeet) {
			mLeftFootBone.setContour(0.6f,1,0.85f,0.65f);
			mLeftFootBone.mContourX1 = 0.5f;
			mLeftFootBone.mContourY1 = 0.5f;
			mLeftFootBone.mContourX2 = 0.6f;
			mLeftFootBone.mContourY2 = 0.5f;
			mRightFootBone.copyContour(mLeftFootBone);
		}
		
		

		//Angle constraints
		super.addConstraint(new AngleConstraint(mLeftUpperLegBone,mLeftLowerLegBone, -3*PI/4,-0.01f));
		super.addConstraint(new AngleConstraint(mRightUpperLegBone,mRightLowerLegBone, -3*PI/4,-0.01f));
		super.addConstraint(new AngleConstraint(mLeftUpperArmBone,mLeftLowerArmBone, 0,3*PI/4));
		super.addConstraint(new AngleConstraint(mRightUpperArmBone,mRightLowerArmBone, 0,3*PI/4));
		super.addConstraint(new AngleConstraint(mBodyBone,mLeftUpperLegBone, -PI/3,8*PI/9));
		super.addConstraint(new AngleConstraint(mBodyBone,mRightUpperLegBone, -PI/3,8*PI/9));
		super.addConstraint(new AngleConstraint(mBodyBone,mHeadBone, -PI/2,PI/2));
		
		if(addFeet) {
			super.addConstraint(new AngleConstraint(mLeftLowerLegBone,mLeftFootBone, PI/8,10*PI/11));
			super.addConstraint(new AngleConstraint(mRightLowerLegBone,mRightFootBone, PI/8,10*PI/11));
		}
		
		if(m3D) {
			addConstraint(new DistanceConstraint(new CartoonBone(mGraphics.mTranslator,"LeftShoulderConstraint",mLeftShoulderJoint,mBreastJoint)));
			addConstraint(new DistanceConstraint(new CartoonBone(mGraphics.mTranslator,"RightShoulderConstraint",mRightShoulderJoint,mBreastJoint)));
			addConstraint(new DistanceConstraint(new CartoonBone(mGraphics.mTranslator,"LeftLegConstraint",mLeftLegJoint,mHipJoint)));
			addConstraint(new DistanceConstraint(new CartoonBone(mGraphics.mTranslator,"RightLegConstraint",mRightLegJoint,mHipJoint)));
		}
		
		mBreastJoint.mFixed = true;
	}
	
	public void multTexCoords(float factorX,float factorY) {
		this.texCoordsIntoRect(0,0, factorX,factorY);
	}
	
	protected void buildDefaultLayers() {
		super.addBone(mRightLowerArmBone,1);
		super.addBone(mRightUpperArmBone,1);
		super.addBone(mRightUpperLegBone,2);
		super.addBone(mRightLowerLegBone,2);
		super.addBone(mBodyBone,2);
		super.addBone(mHeadBone,3);
		super.addBone(mLeftUpperLegBone,4);
		super.addBone(mLeftLowerLegBone,4);
		super.addBone(mLeftLowerArmBone,6);
		super.addBone(mLeftUpperArmBone,6);
		
		if(mRightFootBone!=null) {
			super.addBone(mRightFootBone,2);
			super.addBone(mLeftFootBone,4);
		}
	}
	
}
