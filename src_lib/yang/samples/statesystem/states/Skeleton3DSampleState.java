package yang.samples.statesystem.states;

import yang.events.eventtypes.YangPointerEvent;
import yang.graphics.defaults.programs.subshaders.AmbientSubShader;
import yang.graphics.defaults.programs.subshaders.CameraPerVertexVectorSubShader;
import yang.graphics.defaults.programs.subshaders.DiffuseLightSubShader;
import yang.graphics.defaults.programs.subshaders.MtDiffuseSubShader;
import yang.graphics.defaults.programs.subshaders.NormalSubShader;
import yang.graphics.defaults.programs.subshaders.properties.LightProperties;
import yang.graphics.defaults.programs.subshaders.properties.SpecularMatProperties;
import yang.graphics.defaults.programs.subshaders.realistic.LightSubShader;
import yang.graphics.defaults.programs.subshaders.realistic.SpecularLightSubShader;
import yang.graphics.model.FloatColor;
import yang.graphics.programs.permutations.BasicSubShader;
import yang.graphics.programs.permutations.ShaderPermutations;
import yang.graphics.programs.permutations.SubShader;
import yang.graphics.skeletons.JointEditData;
import yang.graphics.skeletons.Skeleton3D;
import yang.graphics.skeletons.elements.Joint;
import yang.graphics.translator.glconsts.GLMasks;
import yang.samples.SampleSkeleton;
import yang.samples.statesystem.SampleStateCameraControl;

public class Skeleton3DSampleState extends SampleStateCameraControl {

	public Skeleton3D mSkeleton3D;
	public SampleSkeleton mSkeleton;
	public ShaderPermutations mShader;
	private LightProperties mLight;
	
	@Override
	public void initGraphics() {
		mSkeleton = new SampleSkeleton(mGraphics2D);
		mSkeleton3D = new Skeleton3D(mGraphics3D,mSkeleton).initLines();
		mLight = new LightProperties();
		SubShader[] subShaders = new SubShader[]{
				new BasicSubShader(true,true,true),new NormalSubShader(true,true),
				new MtDiffuseSubShader(FloatColor.WHITE),
				new LightSubShader(mLight),new DiffuseLightSubShader(),
				new CameraPerVertexVectorSubShader(mCamera.getCameraInstance()),new SpecularLightSubShader(new SpecularMatProperties()),
				new AmbientSubShader(new FloatColor(0.3f))
				};
		//mShader = mGraphics.addProgram(new DefaultObjShader(mGraphics3D,mCamera,mLight,new FloatColor(0.3f)));
		mShader = mGraphics.addProgram(new ShaderPermutations(mGraphics,subShaders));
		mLight.mDirection.setAlphaBeta(0.4f, 0.4f);
		mSkeleton.mBreastJoint.mFixed = false;
		mSkeleton.setFriction(0.98f);
		mCamera.mZoom = 1.5f;
		mCamera.mFocusY = 1;
		refreshCamera();
	}
	
	@Override
	protected void step(float deltaTime) {
		
		mSkeleton.applyConstraints(deltaTime);
	}

	@Override
	protected void draw() {
		mSkeleton.refreshVisualVars();
		
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
		mGraphics3D.drawDebugCoordinateAxes(FloatColor.RED,FloatColor.GREEN,FloatColor.YELLOW,1,0.3f);

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
		if(code=='p') {
			mSkeleton.mConstantForceY = -0.025f;
		}
		if(code=='l') {
			mLight.mDirection.setAlphaBeta(mCamera.mViewAlpha,mCamera.mViewBeta);
		}
	}
	
	@Override
	public void keyUp(int code) {
		super.keyUp(code);
	}
	
	@Override
	public void pointerDown(float x,float y,YangPointerEvent event) {
		if(event.mButton==YangPointerEvent.BUTTON_LEFT) {
			Joint pJoint = mSkeleton3D.pickJoint(x,y,mCamera.mZoom);
			if(pJoint!=null) {
				mSkeleton3D.setJointSelected(pJoint,true);
				pJoint.startDrag();
			}
		}
		super.pointerDown(x, y, event);
	}

	@Override
	public void pointerMoved(float x,float y,YangPointerEvent event) {
		Joint pJoint = mSkeleton3D.pickJoint(x,y,mCamera.mZoom);
		mSkeleton3D.mHoverJoint = pJoint;
	}
	
	@Override
	public void pointerDragged(float x,float y,YangPointerEvent event) {
		super.pointerDragged(x, y, event);
		mSkeleton3D.mHoverJoint = null;
		//Joint pJoint = mSkeleton3D.pickJoint(x,y,mZoom);

		if(event.mButton == YangPointerEvent.BUTTON_LEFT) {
			float dragX = mCamera.mPntDeltaX*mCamera.mZoom;
			float dragY = mCamera.mPntDeltaY*mCamera.mZoom;
			mGraphics3D.getCameraRightVector(mCamRight);
			mGraphics3D.getCameraUpVector(mCamUp);
			for(Joint joint:mSkeleton3D.getJoints()) {
				JointEditData data = mSkeleton3D.getJointEditData(joint);
				if(data.mSelected)
					joint.drag(dragX*mCamRight.mX+dragY*mCamUp.mX,dragX*mCamRight.mY+dragY*mCamUp.mY,dragX*mCamRight.mZ+dragY*mCamUp.mZ);
			}
		}
	}
	
	@Override
	public void pointerUp(float x,float y,YangPointerEvent event) {
		super.pointerUp(x,y,event);
		Joint pJoint = mSkeleton3D.pickJoint(x,y,mCamera.mZoom);
		if(pJoint!=null)
			mSkeleton3D.setJointSelected(pJoint,false);
		mSkeleton3D.unselectAllJoints();
	}
	
}
