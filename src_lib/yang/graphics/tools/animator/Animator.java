package yang.graphics.tools.animator;

import yang.events.eventtypes.YangEvent;
import yang.events.eventtypes.YangPointerEvent;
import yang.events.eventtypes.YangSensorEvent;
import yang.events.listeners.YangEventListener;
import yang.graphics.defaults.Default2DGraphics;
import yang.graphics.defaults.DefaultAnimationPlayer;
import yang.graphics.skeletons.CartoonSkeleton2D;
import yang.graphics.skeletons.SkeletonCarrier;
import yang.graphics.skeletons.SkeletonEditing;
import yang.graphics.skeletons.animations.Animation;
import yang.graphics.skeletons.animations.AnimationPlayer;
import yang.graphics.skeletons.animations.AnimationSystem;
import yang.graphics.skeletons.animations.KeyFrame;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.translator.Texture;
import yang.graphics.util.Camera2D;
import yang.model.Rect;
import yang.physics.massaggregation.elements.Joint;
import yang.sound.AbstractSoundManager;
import yang.util.NonConcurrentList;

public class Animator implements YangEventListener {

	public AbstractSoundManager mSound;
	public GraphicsTranslator mGraphics;
	public Default2DGraphics mGraphics2D;
	protected CartoonSkeleton2D mCurSkeleton;
	protected Texture mCurTexture;
	protected SkeletonCarrier mCurCarrier;
	@SuppressWarnings("rawtypes")
	public AnimationPlayer mCurAnimationPlayer;
	private Camera2D mCamera;
	private SkeletonEditing mSkeletonEditing;
	public NonConcurrentList<CartoonSkeleton2D> mSkeletons;
	public NonConcurrentList<Texture> mTextures;
	public NonConcurrentList<AnimationSystem<?,?>> mAnimationSystems;
	public NonConcurrentList<AnimationPlayer<?>> mAnimationPlayers;
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
	private Rect mBoundaries;
	
	public Animator(Default2DGraphics graphics2D) {
		mGraphics2D = graphics2D;
		mGraphics = graphics2D.mTranslator;
		mCamera = new Camera2D();
		mCamera.mAdaption = 0.2f;
		mSkeletons = new NonConcurrentList<CartoonSkeleton2D>();
		mTextures = new NonConcurrentList<Texture>();
		mAnimationPlayers = new NonConcurrentList<AnimationPlayer<?>>();
		mSkeletonIndex = -1;
		mCurTexture = null;
		mSkeletonEditing = new SkeletonEditing();
		CartoonSkeleton2D.CURSOR_TEXTURE = mGraphics.mGFXLoader.getImage("circle");
		mAnimationSystems = new NonConcurrentList<AnimationSystem<?,?>>();
		mPaused = false;
		mPlaying = false;
		mPhysicsMode = false;
		mLimitFloor = false;
		mDrawSkeleton = true;
		mBoundaries = new Rect();
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
		while(mTextures.size()<mSkeletons.size()) {
			mTextures.add(mSkeletons.get(mTextures.size()).getDefaultTexture(mGraphics.mGFXLoader));
		}
		
		float gray = 0.1f;
		mGraphics.clear(gray, gray, gray);
		mGraphics2D.switchGameCoordinates(true);
		mGraphics2D.setCamera(mCamera);
		
		//Floor
		mGraphics.bindTexture(null);
		mGraphics2D.setColor(0.5f);
		mGraphics2D.drawRect(mGraphics2D.normToGameX(mGraphics2D.getScreenLeft()), 0, mGraphics2D.normToGameX(mGraphics2D.getScreenRight()), mGraphics2D.normToGameY(mGraphics2D.getScreenBottom()));
		mGraphics2D.setColor(0.2f);
		mGraphics2D.drawLine(-100, 1, 100, 1, 0.01f);
		mGraphics2D.drawLine(-100, 2, 100, 2, 0.01f);
		mGraphics2D.setColor(0.3f);
		mGraphics2D.drawLine(0, 0, 0, 100, 0.01f);
		
		if(mCurSkeleton!=null) {
			mCurSkeleton.refreshVisualData();
			mGraphics.bindTexture(mTextures.get(mSkeletonIndex));
			mCurSkeleton.draw();
			if(mDrawSkeleton) {
				//mCurSkeleton.mCarrier.drawCollision();
				mCurSkeleton.drawEditing(mGraphics2D,mSkeletonEditing);
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

	public void addSkeleton(CartoonSkeleton2D skeleton,AnimationSystem<?,?> animationSystem,AnimationPlayer<?> animationPlayer) {
		if(!skeleton.isInitialized())
			skeleton.init(mGraphics2D);
		mAnimationSystems.add(animationSystem);
		mSkeletons.add(skeleton);
		if(animationPlayer==null)
			animationPlayer = new DefaultAnimationPlayer(skeleton,null);
		mAnimationPlayers.add(animationPlayer);
		if(mSkeletons.size()==1) {
			selectSkeleton(0);
		}
	}
	
	public void addSkeleton(CartoonSkeleton2D skeleton,AnimationSystem<?,?> animationSystem) {
		addSkeleton(skeleton,animationSystem,null);
	}
	
	public void addSkeleton(Class<? extends CartoonSkeleton2D> skeletonClass,AnimationSystem<?,?> animationSystem) {
		try {
			CartoonSkeleton2D skeleton = skeletonClass.newInstance();
			skeleton.init(mGraphics2D);
			addSkeleton(skeleton,animationSystem,null);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
	}
	
	public void selectSkeleton(int index) {
		mCurSkeleton = mSkeletons.get(index);
		mCurSkeleton.mScale = 1;
		mCurCarrier = mCurSkeleton.mCarrier;
		mCurAnimationSystem = mAnimationSystems.get(index);
		mCurAnimationPlayer = mAnimationPlayers.get(index);
		mSkeletonIndex = index;
		selectAnimation(0);
		refreshPhysics();
	}
	
	public void selectAnimation(int index) {
		mCurAnimation = mCurAnimationSystem.mAnimations.get(index);
		mCurAnimation.mAutoAnimate = true;
		mCurAnimationPlayer.mLockedAnimation = false;
		mCurAnimationPlayer.setAnimation(mCurAnimation);
		mAnimationIndex = index;
		selectKeyFrame(0,true);
	}
	
	public void selectKeyFrame(int index,boolean apply) {
		mCurFrame = mCurAnimation.mKeyFrames[index];
		mFrameIndex = index;

		if(!apply) 
			mCurAnimationPlayer.mCurrentAnimationTime = (float)mCurFrame.mFirstFrame/mCurAnimation.mFrameCount*mCurAnimationPlayer.mCurrentAnimation.mTotalDuration;
		else{
			mCurAnimationPlayer.setNormalizedAnimationTime((float)mCurFrame.mFirstFrame/mCurAnimation.mFrameCount);
			if(!mCurAnimationPlayer.mCurrentAnimation.mAutoAnimate)
				mCurFrame.mPose.applyPose(mCurSkeleton);
		}
		
	}
	
	public void reselect() {
		selectKeyFrame(mFrameIndex,true);
	}
	
	public boolean selectSkeleton(Class<? extends CartoonSkeleton2D> skeletonClass) {
		int i=0;
		for(CartoonSkeleton2D skeleton:mSkeletons) {
			if(skeletonClass==skeleton.getClass()) {
				selectSkeleton(i);
				return true;
			}
			i++;
		}
		return false;
	}
	
	public boolean selectSkeleton(CartoonSkeleton2D skeleton) {
		int i=0;
		for(CartoonSkeleton2D skel:mSkeletons) {
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
		else{
			mCurSkeleton.get2DBoundaries(mBoundaries);
			mCamera.set(mBoundaries.getCenterX(), mBoundaries.getCenterY(), Math.max(mBoundaries.getWidth(),mBoundaries.getHeight())*1.1f);
		}
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
			selectKeyFrame(mCurAnimation.mKeyFrames.length-1,applyFrame);
		else
			selectKeyFrame(mFrameIndex-1,applyFrame);
	}
	
	public void nextFrame(boolean applyFrame) {
		saveChangedPose();
		if(mFrameIndex>=mCurAnimation.mKeyFrames.length-1)
			selectKeyFrame(0,applyFrame);
		else
			selectKeyFrame(mFrameIndex+1,applyFrame);
	}
	
	public boolean rawEvent(YangEvent event) {
		return false;
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
			mSkeletonEditing.mMainMarkedJoint = mCurSkeleton.pickJoint2D(mCurPntX, mCurPntY);
			if(mSkeletonEditing.mMainMarkedJoint!=null)
				mSkeletonEditing.mMainMarkedJoint.startDrag();
			break;
		case YangPointerEvent.BUTTON_MIDDLE:
			
			break;
		case YangPointerEvent.BUTTON_RIGHT:
			Joint pick = mCurSkeleton.pickJoint2D(mCurPntX, mCurPntY);
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
				if(mSkeletonEditing.mMainMarkedJoint!=null) {
					mSkeletonEditing.mMainMarkedJoint.drag(deltaX, deltaY);
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
		if(mSkeletonEditing.mMainMarkedJoint!=null) {
			mSkeletonEditing.mMainMarkedJoint.endDrag();
			if(mSkeletonEditing.mMainMarkedJoint.mFixed)
				mSkeletonEditing.mMainMarkedJoint.setVelocity(0,0);
		}
		mSkeletonEditing.mMainMarkedJoint = null;
		
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

	public CartoonSkeleton2D getCurrentSkeleton() {
		return mCurSkeleton;
	}

	public Animation<?> getCurrentAnimation() {
		return mCurAnimation;
	}

	@Override
	public void sensorChanged(YangSensorEvent event) {
		
	}
}
