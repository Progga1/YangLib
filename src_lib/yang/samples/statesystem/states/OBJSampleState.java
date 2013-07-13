package yang.samples.statesystem.states;

import java.io.IOException;

import yang.graphics.defaults.meshcreators.loaders.OBJLoader;
import yang.graphics.defaults.programs.LightProgram;
import yang.graphics.translator.glconsts.GLMasks;
import yang.math.objects.matrix.YangMatrix;
import yang.samples.statesystem.SampleState;

public class OBJSampleState extends SampleState {

	private OBJLoader mObj;
	private LightProgram mLightProgram;
	
	@Override
	protected void initGraphics() {
		
		mLightProgram = mGraphics.addProgram(LightProgram.class);
		
		

	}
	
	
	@Override
	public void start() {
		try {
			YangMatrix transform = new YangMatrix();
			//transform.translate(0, 0.5f);
			transform.scale(0.5f);
			mObj = new OBJLoader(mGraphics3D);
			mObj.loadOBJ(mResources.getInputStream("models/PeaPodBoat.obj"),mGFXLoader,transform);
			mObj.computeStaticNormals();
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
		mGraphics3D.setCameraAlphaBeta((float)(mStateTimer*0.05f),0.5f,2);
		mGraphics3D.setShaderProgram(mLightProgram);
		mGraphics.switchCulling(false);
		mGraphics.switchZBuffer(true);
		mLightProgram.setLightDirectionNormalized(0.407f, -0.207f, -0.407f);
		mLightProgram.setLightProperties(0.1f, 1, 0, 1);
		mGraphics3D.resetGlobalTransform();
		mGraphics3D.setGlobalTransformEnabled(true);
		mGraphics3D.mWorldTransform.rotateX(-(float)mStateTimer*0.004f);
		mGraphics3D.mWorldTransform.rotateY(-(float)mStateTimer*1.0f);
		mObj.draw();
	}

	@Override
	public void stop() {
		mGraphics3D.resetGlobalTransform();
		mGraphics3D.setGlobalTransformEnabled(false);
	}
	
}
