package yang.graphics.skeletons.elements;

import yang.graphics.skeletons.Skeleton;
import yang.math.Geometry;
import yang.util.NonConcurrentList;

public class Joint {

	public static float DEFAULT_FRICTION = 0.9998f;
	public static float TOWARDS_FACTOR = 50;
	public static float AWAY_FACTOR = 160;
	
	//Properties
	public String mName;
	public float mRadius;
	public boolean mFixed;
	public float mass;
	public Skeleton mSkeleton;
	public Joint mAngleParent;
	public float mParentDistance;
	public NonConcurrentList<Joint> mChildren;
	public boolean mSavePose;
	public float mFriction;
	public float mPositionForceFactor = 1;
	public float mForceFactor = 80;
	public boolean mAnimate;
	public boolean mEnabled;
	
	//State
	public float mForceX,mForceY;
	public float mVelX,mVelY;
	public float mPosX, mPosY;
	public float mDragToX,mDragToY;
	public float mTargetPosX,mTargetPosY;
	public boolean mDragging;
	public float mParentCurAngle;
	
	public Joint(String name,Joint parent,float posX,float posY,float radius,Skeleton skeleton) {
		mName = name;
		mFixed = false;
		mPosX = posX;
		mPosY = posY;
		mRadius = radius;
		mFixed = false;
		mass = 1;
		mDragging = false;
		mAnimate = true;
		mSkeleton = skeleton;
		mSavePose = true;
		mChildren = new NonConcurrentList<Joint>();
		mFriction = DEFAULT_FRICTION;
		setParent(parent);
		refreshParentAngle();
		mEnabled = true;
	}
	
	public void setParent(Joint parent) {
		mAngleParent = parent;
		mParentDistance = getDistance(parent);
		if(parent!=null)
			parent.mChildren.add(this);
	}
	
	public void refreshParentAngle() {
		if(mAngleParent==null)
			mParentCurAngle = 0;
		else
			mParentCurAngle = getAngle(mAngleParent);
	}
	
	/**
	 * -1 no child
	 * 0 self
	 * 1+ distance
	 */
	public int childDistance(Joint joint) {
		int count = 0;
		Joint check = this;
		while(check!=null && check!=joint) {
			check = check.mAngleParent;
			count++;
		}
		if(check==null)
			return -1;
		else
			return count;
	}
	
	public boolean isSubChildOf(Joint joint) {
		return childDistance(joint)>=0;
	}
	
	public float getDistance(Joint joint) {
		if(joint==null)
			return -1;
		else
			return Geometry.getDistance(mPosX,mPosY,joint.mPosX,joint.mPosY);
	}
	
	public void setSpeed(Joint preface) {
		mVelX = preface.mVelX;
		mVelY = preface.mVelY;
	}
	
	public void addPositionForce(float posX,float posY,float factor) {
		float dX = posX - mPosX;
		float dY = posY - mPosY;
		float dist = (float)Math.sqrt(dX*dX + dY*dY);
		if(dist>0) {
			float nX = dX/dist;
			float nY = dY/dist;
			float fac;
			fac = (mVelX*dX+mVelY*dY<0)?AWAY_FACTOR:TOWARDS_FACTOR;
			mForceX += fac * dist*nX * mPositionForceFactor * factor;
			mForceY += fac * dist*nY * mPositionForceFactor * factor;
		}
	}
	
	/**
	 * Zero degrees: downwards, CCW
	 */
	public float getAngle(Joint joint) {
		return Geometry.getAngleDown(joint.mPosX,joint.mPosY,mPosX,mPosY);
	}
	
	/**
	 * Zero degrees: downwards, CCW
	 */
	public void setPosByAngle(Joint relativeJoint,float distance,float angle) {
		mParentCurAngle = angle;
		mPosX = relativeJoint.mPosX + (float)(distance*Math.sin(angle));
		mPosY = relativeJoint.mPosY - (float)(distance*Math.cos(angle));//System.out.println(angle+" "+getAngle(relativeJoint));
	}
	
	public void setPosByAngle(Joint relativeJoint,Bone connectingBone,float angle) {
		setPosByAngle(relativeJoint,connectingBone.mDistance,angle);
	}
	
	public void setPosByAngle(float angle) {
		setPosByAngle(mAngleParent,mParentDistance,angle);
	}
	
	public void rotate(float angle) {
		setPosByAngle(mParentCurAngle+angle);
	}
	
	public void physicalStep(float deltaTime) {
		
		if(mEnabled) {
			if(mFixed) {
				mForceX = 0;
				mForceY = 0;
			}
			
			if(mDragging) {
				mTargetPosX += (mDragToX-mTargetPosX)*0.1f;
				mTargetPosY += (mDragToY-mTargetPosY)*0.1f;
				//addPositionForce(mPosX+(mTargetPosX-mPosX)*0.1f,mPosY+(mTargetPosY-mPosY)*0.1f);
				addPositionForce(mTargetPosX,mTargetPosY,1);
			}
			
			mVelX += mForceX/mass * mForceFactor * deltaTime;
			mVelY += mForceY/mass * mForceFactor * deltaTime;
			
			mVelX *= mFriction;
			mVelY *= mFriction;
			
			mPosX += mVelX * deltaTime;
			mPosY += mVelY * deltaTime;
		}
	}
	
	public void startDrag() {
		mDragToX = mPosX;
		mDragToY = mPosY;
		mTargetPosX = mPosX;
		mTargetPosY = mPosY;
		mDragging = true;
	}
	
	public void drag(float deltaX,float deltaY) {
		mDragToX += deltaX/mSkeleton.mCarrier.getScale();
		mDragToY += deltaY/mSkeleton.mCarrier.getScale();
	}
	
	public void endDrag() {
		mDragging = false;
	}
	
	public float getOutputRadius() {
		return Math.max(0.01f,Math.min(0.1f,mRadius));
	}

	public void addForce(float fX, float fY) {
		this.mForceX += fX;
		this.mForceY += fY;
	}
	
	public void applyConstraint() { }

	public void setNormalDirection(float worldX, float worldY,Joint parent,float distance,boolean ortho,boolean invert,float xOffset,float yOffset) {
		if(parent == null)
			return;
		float baseX = parent.mPosX;
		float baseY = parent.mPosY;
		float deltaX = mSkeleton.toJointX(worldX)-baseX;
		float deltaY = mSkeleton.toJointY(worldY)-baseY;
		if(invert) {
			deltaX = -deltaX;
			deltaY = -deltaY;
		}
		float dist = Geometry.getDistance(deltaX, deltaY);
		if(dist==0)
			return;
		float nX,nY;
		if(ortho) {
			nX = -deltaY/dist;
			nY = deltaX/dist;
		}else{
			nX = deltaX/dist;
			nY = deltaY/dist;
		}
		if(xOffset!=0 || yOffset!=0) {
			nX += xOffset*0;
			nY += yOffset;
			dist = Geometry.getDistance(nX, nY);
			nX /= dist;
			nY /= dist;
		}
		mPosX = baseX + nX*distance;
		mPosY = baseY + nY*distance;
	}
	
	public void setNormalDirection(float worldX, float worldY,Joint parent,float distance,boolean ortho,float xOffset,float yOffset) {
		setNormalDirection(worldX,worldY,parent,distance,ortho,false,xOffset,yOffset);
	}
	
	public void setNormalDirection(float worldX, float worldY,boolean ortho,float xOffset,float yOffset) {
		setNormalDirection(worldX,worldY,mAngleParent,mParentDistance,ortho,xOffset,yOffset);
	}

	public void setNormalDirection(float worldX, float worldY) {
		setNormalDirection(worldX,worldY,mAngleParent,mParentDistance,false,0,0);
	}

	public void setPos(float x, float y) {
		mPosX = x;
		mPosY = y;
	}
	
	public void setPos(Joint joint) {
		mPosX = joint.mPosX;
		mPosY = joint.mPosY;
	}
	
	@Override
	public String toString() {
		return "BONE:"+this.mName;
	}

	public void setPosIK(float posX, float posY) {
		mPosX = posX;
		mPosY = posY;
		
		float baseX = mAngleParent.mAngleParent.mPosX;
		float baseY = mAngleParent.mAngleParent.mPosY;

		float deltaX = posX - baseX;
		float deltaY = posY - baseY;
		float dist = Geometry.getDistance(deltaX, deltaY);
		float limDist = mParentDistance*2-0.001f;
		if(dist>limDist) {
			deltaX = deltaX/dist * limDist;
			deltaY = deltaY/dist * limDist;
			dist = limDist;
			mPosX = baseX+deltaX;
			mPosY = baseY+deltaY;
		}
		float shiftX;
		float shiftY;
		if(dist==0) {
			shiftX = 0;
			shiftY = this.mAngleParent.mParentDistance;
		}else{
			float shift = (float)Math.sqrt(mParentDistance*mParentDistance - dist*dist*0.25f);
			float nX = deltaX/dist;
			float nY = deltaY/dist;
			shiftX = nY*shift;
			shiftY = -nX*shift;
		}
		mAngleParent.setPos(
				baseX + deltaX/2 + shiftX,
				baseY + deltaY/2 + shiftY
				);
	}

	public void setNormalDirectionIK(float relativeX, float relativeY, Skeleton skeleton, float straight, float orthoOffset) {
		float shoulderX = skeleton.getJointWorldX(mAngleParent.mAngleParent);
		float shoulderY = skeleton.getJointWorldY(mAngleParent.mAngleParent);
		float dirX = relativeX - shoulderX;
		float dirY = relativeY - shoulderY;
		float dist = Geometry.getDistance(dirX, dirY);
		
		if(dist==0)
			return;

		if(orthoOffset!=0) {
			float x = dirX;
			dirX += dirY*orthoOffset;
			dirY += -x*orthoOffset;
		}
		
		float normX = dirX/dist;
		float normY = dirY/dist;
		float armLength = mParentDistance*2*straight;
		
		this.setPosIK(mAngleParent.mAngleParent.mPosX+normX*armLength, mAngleParent.mAngleParent.mPosY+normY*armLength);
	}

	public float getParentAngle() {
		return getAngle(mAngleParent);
	}

	public void recalculate() {
		mParentDistance = getDistance(mAngleParent);
	}

	public void setSpeed(float velX,float velY) {
		mVelX = velX;
		mVelY = velY;
	}

}
