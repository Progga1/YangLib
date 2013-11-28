package yang.physics.massaggregation.editing;

import yang.graphics.defaults.Default3DGraphics;
import yang.math.objects.Point3f;
import yang.physics.massaggregation.MassAggregation;
import yang.physics.massaggregation.elements.Joint;
import yang.util.YangList;

public class Skeleton3DCollection {

	public Default3DGraphics mGraphics3D;
	public YangList<Skeleton3DEditing> mSkeletons = new YangList<Skeleton3DEditing>();
	public Skeleton3DEditing mLastPickedSkeleton;
	public Skeleton3DEditing mHoverSkeleton = null;
	public Skeleton3DEditing[] mSelectedSkeletons = new Skeleton3DEditing[32];

	public Skeleton3DCollection(Default3DGraphics graphics3D) {
		mGraphics3D = graphics3D;
	}

	public void addSkeleton(Skeleton3DEditing skeleton3D) {
		mSkeletons.add(skeleton3D);
	}

	public void addSkeleton(MassAggregation skeleton) {
		addSkeleton(new Skeleton3DEditing(mGraphics3D,skeleton));
	}

	public void setFriction(float friction) {
		for(Skeleton3DEditing skeleton:mSkeletons) {
			skeleton.mSkeleton.setFriction(friction);
		}
	}

	public void refreshSkeletonData() {
		for(Skeleton3DEditing skeleton:mSkeletons) {
			skeleton.refreshSkeletonData();
		}
	}

	public void physicalStep(float deltaTime) {
		for(Skeleton3DEditing skeleton:mSkeletons) {
			skeleton.mSkeleton.physicalStep(deltaTime);
		}
	}

	public void draw() {
		for(Skeleton3DEditing skeleton:mSkeletons) {
			skeleton.draw();
		}
	}

	public Joint pickJoint2D(float x, float y, float zoom, float radiusFactor) {
		for(Skeleton3DEditing skeleton:mSkeletons) {
			Joint result = skeleton.pickJoint2D(x, y, zoom, radiusFactor);
			if(result!=null) {
				mLastPickedSkeleton = skeleton;
				return result;
			}
		}
		mLastPickedSkeleton = null;
		return null;
	}

	public Joint pickJoint3D(Point3f position,float pickRadius, float radiusFactor) {
		for(Skeleton3DEditing skeleton:mSkeletons) {
			Joint result = skeleton.pickJoint3D(position,pickRadius,radiusFactor);
			if(result!=null) {
				mLastPickedSkeleton = skeleton;
				return result;
			}
		}
		mLastPickedSkeleton = null;
		return null;
	}

	public void setHover(Skeleton3DEditing hoverSkeleton, Joint hoverJoint) {
		if(hoverSkeleton==null || hoverJoint==null)
			setNoHover();
		else{
			mHoverSkeleton = hoverSkeleton;
			mHoverSkeleton.mHoverJoint = hoverJoint;
		}
	}

	public void setNoHover() {
		if(mHoverSkeleton==null)
			return;
		mHoverSkeleton.mHoverJoint = null;
		mHoverSkeleton = null;
	}

	public int getSelectionCount() {
		int count = 0;
		for(Skeleton3DEditing skeleton:mSkeletons) {
			count += skeleton.getSelectionCount();
		}
		return count;
	}

	public void setJointSelected(Skeleton3DEditing skeleton, Joint joint, int index,boolean recursive) {
		skeleton.setJointSelected(joint, index, recursive);
		mSelectedSkeletons[index] = skeleton;
	}

	public void unselectJoint(int index) {
		if(mSelectedSkeletons[index]!=null) {
			mSelectedSkeletons[index].unselectJointGroup(index);
			mSelectedSkeletons[index] = null;
		}
	}
}
