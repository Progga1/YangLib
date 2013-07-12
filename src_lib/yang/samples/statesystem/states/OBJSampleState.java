package yang.samples.statesystem.states;

import java.io.IOException;

import yang.graphics.defaults.meshcreators.loaders.OBJLoader;
import yang.math.objects.matrix.YangMatrix;
import yang.samples.statesystem.SampleState;

public class OBJSampleState extends SampleState {

	private OBJLoader mObj;
	
	@Override
	protected void initGraphics() {
		mObj = new OBJLoader(mGraphics3D);
		YangMatrix transform = new YangMatrix();
		transform.scale(0.1f);
		try {
			mObj.loadOBJ(mResources.getInputStream("models/cessna.obj"),mResources.getInputStream("models/vp.mtl"),transform);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void step(float deltaTime) {
		
	}

	@Override
	protected void draw() {
		mGraphics.clear(0,0,0.1f);
		mGraphics3D.activate();
		mGraphics3D.setPerspectiveProjection(0.6f, 0.1f, 100);
		mGraphics3D.setCameraAlphaBeta((float)(mStateTimer),0.4f,1);
		mObj.draw();
	}

	
	
}
