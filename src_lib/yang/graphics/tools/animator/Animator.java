package yang.graphics.tools.animator;

import yang.events.eventtypes.YangPointerEvent;
import yang.events.eventtypes.YangInputEvent;
import yang.events.listeners.FullEventListener;
import yang.graphics.defaults.Default2DGraphics;
import yang.graphics.skeletons.Skeleton;
import yang.graphics.skeletons.SkeletonCarrier;
import yang.graphics.skeletons.SkeletonEditing;
import yang.graphics.skeletons.animations.Animation;
import yang.graphics.skeletons.animations.AnimationPlayer;
import yang.graphics.skeletons.animations.AnimationSystem;
import yang.graphics.skeletons.animations.KeyFrame;
import yang.graphics.skeletons.elements.Joint;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.util.Camera2D;
import yang.sound.SoundManager;
import yang.util.NonConcurrentList;

public class Animator implements FullEventListener {

	public SoundManager mSound;
	public GraphicsTranslator mGraphics;
	public Default2DGraphics mGraphics2D;
	protected Skeleton mCurSkeleton;
	protected SkeletonCarrier mCurCarrier;
	@SuppressWarnings("rawtypes")
	public AnimationPlayer mCurAnimationPlayer;
	private Camera2D mCamera;
	private SkeletonEditing mSkeletonEditing;
	public NonConcurrentList<Skeleton> mSkeletons;
	public NonConcurrentList<AnimationSystem<?,?>> mAnimationSystems;
	public AnimationSystem<?,?> mCurAnimationSystem;
	public Animation<?> mCurAnimation;
	public KeyFrame mCurFrame;
	private int mSkeletonIndex;
	private int mAnimationIndex;
	private boolean mPhysicsMode;
	private int mFrameIndex;
	private float mCurPntX;
	private float mCurPntY;
	private float mPrevPntX;
	private float mPrevPntY;
	private boolean mPlaying;
	private boolean mPaused;
	public boolean mPoseChanged;
	public float mGravity = -0.2f;
	public boolean mLimitFloor;
	public boolean mDrawSkeleton;
	public float mPrevSX;
	public float mPrevSY;
	
	public Animator(Default2DGraphics graphics2D) {
		mGraphics2D = graphics2D;
		mGraphics = graphics2D.mTranslator;
		mCamera = new Camera2D();
		mCamera.mAdaption = 0.2f;
		mSkeletons = new NonConcurrentList<Skeleton>();
		mSkeletonIndex = -1;
		mSkeletonEditing = new SkeletonEditing();
		Skeleton.CURSOR_TEXTURE = mGraphics.mGFXLoader.getImage("circle");
		mAnimationSystems = new NonConcurrentList<AnimationSystem<?,?>>();
		mPaused = false;
		mPlaying = false;
		mPhysicsMode = false;
		mLimitFloor = false;
		mDrawSkeleton = true;
	}
	
	public void savePose() {
		mCurFrame.mPose.copyFromSkeleton(mCurSkeleton);
	}
	
	public void saveChangedPose() {
		if(mPoseChanged)
			savePose();
		mPoseChanged = false;
	}
	
	public void draw() {
		float gray = 0.1f;
		mGraphics.clear(gray, gray, gray);
		mGraphics2D.switchGameCoordinates(true);
		mGraphics2D.setCamera(mCamera);
		mGraphics.bindTexture(null);
		mGraphics2D.setColor(0.5f);
		mGraphics2D.drawRect(mGraphics2D.normToGameX(mGraphics2D.getScreenLeft()), 0, mGraphics2D.normToGameX(mGraphics2D.getScreenRight()), mGraphics2D.normToGameY(mGraphics2D.getScreenBottom()));
		if(mCurSkeleton!=null) {
			mCurSkeleton.refreshVisualVars();
			mCurSkeleton.draw();
			if(mDrawSkeleton) {
				mCurSkeleton.mCarrier.drawCollision();
				mCurSkeleton.drawEditing(mSkeletonEditing);
			}
		}
		mGraphics.flush();
	}
	
	public void refreshPhysics() {
		if(mPhysicsMode) {
			mCurSkeleton.mConstantForceX = 0;
			mCurSkeleton.mConstantForceY = mGravity;
			mCurSkeleton.mLowerLimit = 0;
			mCurSkeleton.setFriction(Joint.DEFAULT_FRICTION);
		}else{
			mCurSkeleton.mConstantForceX = 0;
			mCurSkeleton.mConstantForceY = 0;
			if(mLimitFloor)
				mCurSkeleton.mLowerLimit = 0;
			else
				mCurSkeleton.mLowerLimit = -100;
			mCurSkeleton.setFriction(0.98f);
		}
	}
	
	public void setPhysicsMode(boolean enable) {
		mPhysicsMode = enable;
		refreshPhysics();
	}
	
	public void step(float deltaTime) {
		mCamera.update();
		if(mCurSkeleton!=null) {
			if(mPhysicsMode) {
				mCurSkeleton.applyConstraints(deltaTime);
			}else if(mPlaying) {
				if(!mPaused)
					mCurAnimationPlayer.proceed(deltaTime);
			}else{
				
				mCurSkeleton.applyConstraints(deltaTime);
			}
		}
	}

	public void addSkeleton(Skeleton skeleton,AnimationSystem<?,?> animationSystem) {
		if(!skeleton.isInitialized())
			skeleton.init(mGraphics2D);
		mAnimationSystems.add(animationSystem);
		mSkeletons.add(skeleton);
		if(mSkeletons.size()==1) {
			selectSkeleton(0);
		}
	}
	
	public void addSkeleton(Class<? extends Skeleton> skeletonClass,AnimationSystem<?,?> animationSystem) {
		try {
			Skeleton skeleton = skeletonClass.newInstance();
			skeleton.init(mGraphics2D);
			addSkeleton(skeleton,animationSystem);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
	}
	
	public void selectSkeleton(int index) {
		mCurSkeleton = mSkeletons.get(index);
		mCurCarrier = mCurSkeleton.mCarrier;
		mCurAnimationSystem = mAnimationSystems.get(index);
		mCurAnimationPlayer = mCurCarrier.getAnimationPlayer();
		mSkeletonIndex = index;
		selectAnimation(0);
		refreshPhysics();
	}
	
	public void selectAnimation(int index) {
		mCurAnimation = mCurAnimationSystem.mAnimations.get(index);
		mCurAnimationPlayer.setAnimation(mCurAnimation);
		mAnimationIndex = index;
		selectFrame(0,true);
	}
	
	public void selectFrame(int index,boolean apply) {
		mFrameIndex = index;
		mCurFrame = mCurAnimation.mFrames[mFrameIndex];
		mCurAnimationPlayer.setNormalizedAnimationTime((float)mCurFrame.mFirstFrame/mCurAnimation.mFrameCount);
		if(apply)
			mCurFrame.mPose.applyPose(mCurSkeleton);
	}
	
	public void reselect() {
		selectFrame(mFrameIndex,true);
	}
	
	public boolean selectSkeleton(Class<? extends Skeleton> skeletonClass) {
		int i=0;
		for(Skeleton skeleton:mSkeletons) {
			if(skeletonClass==skeleton.getClass()) {
				selectSkeleton(i);
				return true;
			}
			i++;
		}
		return false;
	}
	
	public boolean selectSkeleton(Skeleton skeleton) {
		int i=0;
		for(Skeleton skel:mSkeletons) {
			if(skel==skeleton) {
				selectSkeleton(i);
				return true;
			}
			i++;
		}
		return false;
	}
	
	public void centerCamera() {
		if(mCurSkeleton==null)
			mCamera.set(0,1,1.5f);
		else
			mCamera.set(mCurSkeleton.mBoundariesRect.getCenterX(), mCurSkeleton.mBoundariesRect.getCenterY(), Math.max(mCurSkeleton.mBoundariesRect.getWidth(),mCurSkeleton.mBoundariesRect.getHeight())*1.1f);
	}
	
	@SuppressWarnings("unchecked")
	public void play() {
		if(mPlaying)
			return;
		saveChangedPose();
		mPlaying = true;
		mPaused = false;
		mCurAnimationPlayer.setAnimation(mCurAnimation);
	}
	
	public void setPaused(boolean paused) {
		mPaused = paused;
	}
	
	public boolean isPlaying() {
		return mPlaying;
	}
	
	public boolean isPaused() {
		return mPaused;
	}
	
	public void stop() {
		mPlaying = false;
		mPaused = false;
		reselect();
	}

	public void previousFrame(boolean applyFrame) {
		saveChangedPose();
		if(mFrameIndex<=0)
			selectFrame(mCurAnimation.mFrames.length-1,applyFrame);
		else
			selectFrame(mFrameIndex-1,applyFrame);
	}
	
	public void nextFrame(boolean applyFrame) {
		saveChangedPose();
		if(mFrameIndex>=mCurAnimation.mFrames.length-1)
			selectFrame(0,applyFrame);
		else
			selectFrame(mFrameIndex+1,applyFrame);
	}
	
	public void rawEvent(YangInputEvent event) {
		
	}

	public void pointerMoved(float x, float y, YangPointerEvent event) {
		
	}
	
	public void pointerDown(float x,float y,YangPointerEvent event) {
		mCurPntX = mGraphics2D.normToGameX(x,y);
		mCurPntY = mGraphics2D.normToGameY(x,y);
		mPrevPntX = mCurPntX;
		mPrevPntY = mCurPntY;
		mPrevSX = x;
		mPrevSY = y;
		switch(event.mButton) {
		case YangPointerEvent.BUTTON_LEFT:
			mSkeletonEditing.mMarkedJoint = mCurSkeleton.pickJoint(mCurPntX, mCurPntY);
			if(mSkeletonEditing.mMarkedJoint!=null)
				mSkeletonEditing.mMarkedJoint.startDrag();
			break;
		case YangPointerEvent.BUTTON_MIDDLE:
			
			break;
		case YangPointerEvent.BUTTON_RIGHT:
			Joint pick = mCurSkeleton.pickJoint(mCurPntX, mCurPntY);
			if(pick!=null)
				pick.mFixed ^= true;
			break;
		}
	}
	
	public void pointerDragged(float x,float y,YangPointerEvent event) {
		
		mPrevPntX = mCurPntX;
		mPrevPntY = mCurPntY;
		mCurPntX = mGraphics2D.normToGameX(x,y);
		mCurPntY = mGraphics2D.normToGameY(x,y);
		float deltaX = mCurPntX-mPrevPntX;
		float deltaY = mCurPntY-mPrevPntY;
		
		switch(event.mButton) {
			case YangPointerEvent.BUTTON_LEFT:
				if(mSkeletonEditing.mMarkedJoint!=null) {
					mSkeletonEditing.mMarkedJoint.drag(deltaX, deltaY);
					mPoseChanged = true;
				}
				break;
			case YangPointerEvent.BUTTON_MIDDLE:
				mCamera.move((mPrevSX-x)*mCamera.getZoom(),(mPrevSY-y)*mCamera.getZoom());
				break;
		}
		
		mPrevSX = x;
		mPrevSY = y;
	}
	
	public void pointerUp(float x,float y,YangPointerEvent event) {
		if(mSkeletonEditing.mMarkedJoint!=null) {
			mSkeletonEditing.mMarkedJoint.endDrag();
			if(mSkeletonEditing.mMarkedJoint.mFixed)
				mSkeletonEditing.mMarkedJoint.setSpeed(0,0);
		}
		mSkeletonEditing.mMarkedJoint = null;
		
	}

	public void keyDown(int code) {
		
	}

	public void keyUp(int code) {
		
	}

	public void zoom(float factor) {
		mCamera.zoom(factor);
	}

	public boolean isPhysicsActive() {
		return mPhysicsMode;
	}
	
	public void setDrawSkeleton(boolean draw) {
		mDrawSkeleton = draw;
	}

	public boolean isSkeletonDrawn() {
		return mDrawSkeleton;
	}

	public Skeleton getCurrentSkeleton() {
		return mCurSkeleton;
	}

	public Animation<?> getCurrentAnimation() {
		return mCurAnimation;
	}
}
