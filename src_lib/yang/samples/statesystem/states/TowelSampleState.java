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
import yang.graphics.skeletons.defaults.creators.JointGridCreator;
import yang.graphics.textures.TextureProperties;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.textures.enums.TextureWrap;
import yang.graphics.translator.Texture;
import yang.graphics.translator.glconsts.GLMasks;
import yang.math.objects.YangMatrix;
import yang.physics.massaggregation.MassAggregation;
import yang.physics.massaggregation.editing.JointEditData;
import yang.physics.massaggregation.editing.Skeleton3DEditing;
import yang.physics.massaggregation.elements.Joint;
import yang.samples.statesystem.SampleStateCameraControl;

public class TowelSampleState extends SampleStateCameraControl {

	public static FloatColor COLOR = new FloatColor(0.8f,0.8f,0.95f,0.87f);
	public static boolean SEMI_TRANSPARENT = true;
	public static boolean NEUTRAL = false;
	public static boolean SKY = false;

	public Skeleton3DEditing mSkeleton3D;
	public JointGridCreator mTowel;
	public MassAggregation mSkeleton;
	public ShaderPermutations mShader;
	private LightProperties mLight;
	private boolean mMultiSelect = false;

	private float mForceAngle = 0.6f;
	private float mForceAmplitude = 0.08f;
	private float mForceFreq = 0.4f;
	private Texture mTex;
	private Texture mSkyTex;

	@Override
	public void initGraphics() {
		super.initGraphics();
		mTowel = new JointGridCreator();
		mTex = mGFXLoader.getImage("curtain1",new TextureProperties(TextureWrap.REPEAT,TextureFilter.LINEAR_MIP_LINEAR));
		mSkyTex = mGFXLoader.getImage("sky",new TextureProperties(TextureWrap.REPEAT,TextureFilter.LINEAR_MIP_LINEAR));
		YangMatrix transform = new YangMatrix();
		transform.scale(2);
		transform.translate(-0.5f,0);
		mTowel.mStrength = 8;
		if(mStateSystem.mPlatformKey.startsWith("PC")) {
			mSkeleton = mTowel.create(18,18, transform);
			mSkeleton.mAccuracy = 30;
		}else{
			SEMI_TRANSPARENT = false;
			mSkeleton = mTowel.create(11,11, transform);
			mSkeleton.mAccuracy = 8;
		}
		if(!SEMI_TRANSPARENT)
			COLOR.setAlpha(1);
		mTowel.initGraphics(mGraphics3D);
		mSkeleton3D = new Skeleton3DEditing(mGraphics3D,mSkeleton).initLines();
		mLight = new LightProperties();
		final SubShader[] subShaders = new SubShader[]{
				new BasicSubShader(true,true,true),new NormalSubShader(true,true),
				new DiffuseMatSubShader(FloatColor.WHITE),
				new LightSubShader(mLight),new DiffuseLightSubShader(),
				new CameraPerVertexVectorSubShader(mGraphics3D),new SpecularLightSubShader(new SpecularMatProperties()),
				new AmbientSubShader(new FloatColor(0.8f,0.8f,0.9f)),
				new ColorFactorSubShader(mGraphics3D)
				};
		mShader = mGraphics.addProgram(new ShaderPermutations(mGraphics,subShaders));
		mLight.mDirection.setAlphaBeta(0.4f, 0.4f);
		mSkeleton.setFriction(0.998f);
		mCamera.setZoom(2.4f);
		mCamera.mFocus.mY = 0.6f;
		if(!NEUTRAL)
			mCamera.setViewAngle(0.1f, 0.1f);

		if(!NEUTRAL) {
			mSkeleton.mConstantForceY = -0.2f;
			mTowel.pickJoint(0,1).mFixed = true;
			mTowel.pickJoint(0,1).mX += 0.04f;
			mTowel.pickJoint(1,1).mFixed = true;
			mTowel.pickJoint(1,1).mX -= 0.04f;
		}
	}

	@Override
	protected void step(float deltaTime) {
		super.step(deltaTime);

		if(!NEUTRAL) {
			float f = (float)(Math.sin(mStateTimer*2*PI*mForceFreq)+0.95f)*mForceAmplitude;
			mSkeleton.mConstantForceX = (float)(f*Math.sin(mForceAngle));
			mSkeleton.mConstantForceZ = -(float)(f*Math.cos(mForceAngle));

			if(mStateSystem.mEventQueue.isKeyDown(Keys.RIGHT))
				mForceAngle += deltaTime;
			if(mStateSystem.mEventQueue.isKeyDown(Keys.LEFT))
				mForceAngle -= deltaTime;

			if(mStateSystem.mEventQueue.isKeyDown(Keys.UP))
				mForceAmplitude += deltaTime;
			if(mStateSystem.mEventQueue.isKeyDown(Keys.DOWN))
				mForceAmplitude -= deltaTime;
		}

		mSkeleton.physicalStep(deltaTime);
	}

	@Override
	protected void draw() {
		//mSkeleton.refreshVisualVars();

		mGraphics3D.activate();

		mGraphics.clear(0f,0f,0.3f, GLMasks.DEPTH_BUFFER_BIT);
		mGraphics.switchZBuffer(!SEMI_TRANSPARENT);
		mGraphics.switchCulling(false);
		mGraphics3D.setWhite();
		mGraphics3D.setColorFactor(1);

		mCamera.mOrthogonalProjection = false;
		super.setCamera();

		if(SKY) {
			mGraphics3D.setDefaultProgram();
			mGraphics.bindTexture(mSkyTex);
			mGraphics3D.drawSphere(6,6, 0,0,0, 90, 2,1);
		}

		mGraphics.bindTexture(mTex);
		mGraphics3D.setShaderProgram(mShader);
		mLight.mDiffuse.set(0.95f);

		mTowel.drawDefault(COLOR, null);
		if(SEMI_TRANSPARENT)
			mGraphics3D.sort();
	}

	@Override
	public void keyDown(int code) {
		super.keyDown(code);
		if(code=='s') {
			mSkeleton.mConstantForceY = -0.025f;
		}
		if(code=='l') {
			mLight.mDirection.setAlphaBeta(mCamera.getViewAlpha(),mCamera.getViewBeta());
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
