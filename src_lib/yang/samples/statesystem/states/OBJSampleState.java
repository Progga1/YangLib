package yang.samples.statesystem.states;

import java.io.IOException;

import yang.events.Keys;
import yang.events.eventtypes.YangPointerEvent;
import yang.graphics.defaults.meshcreators.loaders.OBJLoader;
import yang.graphics.defaults.programs.LightProgram;
import yang.graphics.translator.glconsts.GLMasks;
import yang.math.objects.matrix.YangMatrix;
import yang.samples.statesystem.SampleState;

public class OBJSampleState extends SampleState {

	private OBJLoader[] mObj = new OBJLoader[4];
	private LightProgram mLightProgram;
	private int mCurObjIndex = 0;
	
	@Override
	protected void initGraphics() {
		mLightProgram = mGraphics.addProgram(LightProgram.class);
	}
	
	
	@Override
	public void start() {
		try {
			YangMatrix transform = new YangMatrix();
			
			transform.loadIdentity();
			transform.scale(0.1f);
			mObj[0] = new OBJLoader(mGraphics3D);
			mObj[0].loadOBJ(mResources.getInputStream("models/cessna.obj"),mGFXLoader,transform);
			
			transform.loadIdentity();
			transform.scale(0.5f);
			mObj[1] = new OBJLoader(mGraphics3D);
			mObj[1].loadOBJ(mResources.getInputStream("models/peapodboat.obj"),mGFXLoader,transform);
			
			transform.loadIdentity();
			transform.translate(0, 0.55f);
			transform.scale(0.2f);
			mObj[2] = new OBJLoader(mGraphics3D);
			mObj[2].loadOBJ(mResources.getInputStream("models/supermario.obj"),mGFXLoader,transform);
			
			mObj[3] = new OBJLoader(mGraphics3D);
			mObj[3].loadOBJ(mResources.getInputStream("models/cube.obj"),mGFXLoader,null);
			
			for(OBJLoader obj:mObj) {
				obj.computeStaticNormals();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	protected void step(float deltaTime) {
		
	}

	@Override
	protected void draw() {
		mGraphics.clear(0,0,0.1f,GLMasks.DEPTH_BUFFER_BIT);
		mGraphics3D.activate();
		mGraphics3D.setWhite();
		mGraphics3D.setPerspectiveProjection(0.6f, 0.1f, 100);
		mGraphics3D.setCameraAlphaBeta((float)(mStateTimer*0.05f),0.45f,2);
//		if(false) {
			mGraphics3D.setShaderProgram(mLightProgram);
			mLightProgram.setLightDirectionNormalized(0.407f, -0.207f, -0.407f);
			mLightProgram.setLightProperties(0.1f, 1, 0, 1);
//		}else{
//			
//		}
		mGraphics.switchCulling(false);
		mGraphics.switchZBuffer(true);
		mGraphics3D.resetGlobalTransform();
		mGraphics3D.setGlobalTransformEnabled(true);
		mGraphics3D.mWorldTransform.rotateX(-(float)mStateTimer*0.004f);
		mGraphics3D.mWorldTransform.rotateY(-(float)mStateTimer*1.0f);
		mObj[mCurObjIndex].draw();
	}

	@Override
	public void pointerUp(float x,float y,YangPointerEvent event) {
		mCurObjIndex = (mCurObjIndex+1)%mObj.length;
	}
	
	@Override
	public void keyUp(int code) {
		super.keyUp(code);
		if(code==Keys.LEFT)
			mCurObjIndex--;
		if(code==Keys.RIGHT)
			mCurObjIndex = (mCurObjIndex+1)%mObj.length;
		if(mCurObjIndex<0)
			mCurObjIndex = mObj.length-1;
	}
	
	@Override
	public void stop() {
		mGraphics3D.resetGlobalTransform();
		mGraphics3D.setGlobalTransformEnabled(false);
	}
	
}
