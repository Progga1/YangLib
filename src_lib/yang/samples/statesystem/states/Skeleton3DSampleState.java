package yang.samples.statesystem.states;

import yang.events.Keys;
import yang.events.eventtypes.SurfacePointerEvent;
import yang.graphics.defaults.programs.subshaders.AmbientSubShader;
import yang.graphics.defaults.programs.subshaders.ColorFactorSubShader;
import yang.graphics.defaults.programs.subshaders.DiffuseLightSubShader;
import yang.graphics.defaults.programs.subshaders.dataproviders.CameraPerVertexVectorSubShader;
import yang.graphics.defaults.programs.subshaders.dataproviders.DiffuseMatSubShader;
import yang.graphics.defaults.programs.subshaders.dataproviders.NormalSubShader;
import yang.graphics.defaults.programs.subshaders.properties.LightProperties;
import yang.graphics.defaults.programs.subshaders.properties.SpecularMatProperties;
import yang.graphics.defaults.programs.subshaders.realistic.LightSubShader;
import yang.graphics.defaults.programs.subshaders.realistic.SpecularLightSubShader;
import yang.graphics.model.FloatColor;
import yang.graphics.programs.permutations.BasicSubShader;
import yang.graphics.programs.permutations.ShaderPermutations;
import yang.graphics.programs.permutations.SubShader;
import yang.graphics.skeletons.defaults.creators.human.HumanoidSkeletonCreator3D;
import yang.graphics.skeletons.defaults.creators.human.HumanoidSkeletonProperties;
import yang.graphics.translator.glconsts.GLMasks;
import yang.physics.massaggregation.MassAggregation;
import yang.physics.massaggregation.editing.JointEditData;
import yang.physics.massaggregation.editing.Skeleton3DEditing;
import yang.physics.massaggregation.elements.Joint;
import yang.samples.statesystem.SampleStateCameraControl;

public class Skeleton3DSampleState extends SampleStateCameraControl {

	public Skeleton3DEditing mSkeleton3D;
	public HumanoidSkeletonCreator3D mSkeletonCreator;
	public MassAggregation mSkeleton;
	public ShaderPermutations mShader;
	private LightProperties mLight;
	private boolean mMultiSelect = false;

	@Override
	public void initGraphics() {
		super.initGraphics();
		mSkeletonCreator = new HumanoidSkeletonCreator3D();
		mSkeleton = mSkeletonCreator.create(new HumanoidSkeletonProperties());
		mSkeleton3D = new Skeleton3DEditing(mGraphics3D,mSkeleton).initLines();
		mLight = new LightProperties();
		final SubShader[] subShaders = new SubShader[]{
				new BasicSubShader(true,true,true),new NormalSubShader(true,true),
				new DiffuseMatSubShader(FloatColor.WHITE),
				new LightSubShader(mLight),new DiffuseLightSubShader(),
				new CameraPerVertexVectorSubShader(mGraphics3D),new SpecularLightSubShader(new SpecularMatProperties()),
				new AmbientSubShader(new FloatColor(0.3f)),
				new ColorFactorSubShader(mGraphics3D)
				};
		//mShader = mGraphics.addProgram(new DefaultObjShader(mGraphics3D,mCamera,mLight,new FloatColor(0.3f)));
		mShader = mGraphics.addProgram(new ShaderPermutations(mGraphics,subShaders));
		mLight.mDirection.setAlphaBeta(0.4f, 0.4f);
		mSkeletonCreator.mBreastJoint.mFixed = false;
		mSkeleton.setFriction(0.98f);
		mCamera.setZoom(1.5f);
		mCamera.mFocusY = 1;
	}

	@Override
	protected void step(float deltaTime) {
		super.step(deltaTime);
		mSkeleton.physicalStep(deltaTime);
	}

	@Override
	protected void draw() {
		//mSkeleton.refreshVisualVars();

		mGraphics3D.activate();
		mGraphics.clear(0f,0f,0.3f, GLMasks.DEPTH_BUFFER_BIT);
		mGraphics.switchZBuffer(true);
		mGraphics.switchCulling(true);

		mGraphics3D.setWhite();
		mGraphics3D.setColorFactor(1);
		mGraphics.bindTexture(null);
		mGraphics3D.setShaderProgram(mShader);
		mLight.mDiffuse.set(0.5f);
		super.setCamera();
		mSkeleton3D.draw();

		mGraphics3D.setDefaultProgram();
		mGraphics3D.drawDebugCoordinateAxes(FloatColor.RED,FloatColor.GREEN,FloatColor.YELLOW,0.5f,0.3f);



		mGraphics2D.activate();
		mGraphics.switchZBuffer(false);

//		mGraphics2D.setCamera(0, 0.5f, 2);
//		mSkeleton.draw();

//		mGraphics2D.switchGameCoordinates(false);
//		mGraphics2D.setColor(1,1,1,0.8f);
//		mGraphics3D.getToScreenTransform(mProjectMatrix);
//		for(Joint joint:mSkeleton.mJoints) {
//			mProjectMatrix.apply3D(joint.mPosX,joint.mPosY,joint.mPosZ,mReprojectPos);
//			mGraphics2D.drawRectCentered(mReprojectPos.mX, mReprojectPos.mY, joint.getOutputRadius()*2/mZoom);
//		}
	}

	@Override
	public void keyDown(int code) {
		super.keyDown(code);
		if(code=='s') {
			mSkeleton.mConstantForceY = -0.025f;
		}
		if(code=='l') {
			mLight.mDirection.setAlphaBeta(mCamera.mViewAlpha,mCamera.mViewBeta);
		}
		if(code==Keys.CTRL)
			mMultiSelect = true;
	}

	@Override
	public void keyUp(int code) {
		super.keyUp(code);
		if(code==Keys.CTRL)
			mMultiSelect = false;
	}

	@Override
	public void pointerDown(float x,float y,SurfacePointerEvent event) {
		if(event.mButton==SurfacePointerEvent.BUTTON_LEFT) {
			final Joint pJoint = mSkeleton3D.pickJoint2D(x,y,mCamera.mZoom,1.75f);
			if(pJoint!=null) {
				mSkeleton3D.setJointSelected(pJoint,event.mId);
				pJoint.startDrag();
			}
		}
		super.pointerDown(x, y, event);
	}

	@Override
	public void pointerMoved(float x,float y,SurfacePointerEvent event) {
		final Joint pJoint = mSkeleton3D.pickJoint2D(x,y,mCamera.mZoom,1);
		mSkeleton3D.mHoverJoint = pJoint;
	}

	@Override
	public void pointerDragged(float x,float y,SurfacePointerEvent event) {

		mSkeleton3D.mHoverJoint = null;
		//Joint pJoint = mSkeleton3D.pickJoint(x,y,mZoom);

		if(mSkeleton3D.getSelectionCount()>0) {
			if(event.mButton == SurfacePointerEvent.BUTTON_LEFT) {
				final float dragX = event.mDeltaX*mCamera.mZoom;
				final float dragY = event.mDeltaY*mCamera.mZoom;
				mGraphics3D.getCameraRightVector(mCamRight);
				mGraphics3D.getCameraUpVector(mCamUp);
				for(final Joint joint:mSkeleton3D.getJoints()) {
					final JointEditData data = mSkeleton3D.getJointEditData(joint);
					if(data.mSelectionGroup==event.mId)
						joint.dragWorld(dragX*mCamRight.mX+dragY*mCamUp.mX,dragX*mCamRight.mY+dragY*mCamUp.mY,dragX*mCamRight.mZ+dragY*mCamUp.mZ);
				}
			}
		}else{
			super.pointerDragged(x, y, event);
		}
	}

	@Override
	public void pointerUp(float x,float y,SurfacePointerEvent event) {
		super.pointerUp(x,y,event);
		if(!mMultiSelect)
			mSkeleton3D.unselectJointGroup(event.mId);
	}

	@Override
	public void zoom(float value) {
		if(mSkeleton3D.getSelectionCount()<=0)
			super.zoom(value);
	}

}
