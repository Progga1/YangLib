package yang.samples.statesystem.states;

import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.translator.Texture;
import yang.graphics.translator.glconsts.GLMasks;
import yang.math.MathFunc;
import yang.math.objects.matrix.YangMatrix;
import yang.samples.statesystem.SampleState;

public class MatrixStackSampleState extends SampleState {

	private Texture mCubeTex;
	private YangMatrix mTransform;
	
	@Override
	protected void initGraphics() {
		mCubeTex = mGFXLoader.getImage("grass",TextureFilter.LINEAR_MIP_LINEAR);
		mTransform = new YangMatrix();
		mTransform.initStack(16);
	}
	
	@Override
	protected void step(float deltaTime) {
		
	}

	protected void drawSphere() {
		mGraphics3D.drawSphere(20, 20, mTransform, 2, 1.5f);
	}
	
	@Override
	protected void draw() {
		mGraphics3D.activate();
		mGraphics3D.setDefaultView();
		mGraphics3D.setWhite();
		mGraphics.switchZBuffer(true);
		mGraphics.clear(0,0,0,0,GLMasks.DEPTH_BUFFER_BIT);
		mTransform.stackClear();
		
		mTransform.loadIdentity();
		mTransform.scale(0.7f);
		mTransform.translate(MathFunc.sin(mStateTimer*1.1f)*0.3f, 0);
		mTransform.rotateY((float)(mStateTimer*0.1f));
		mTransform.scale(0.3f);
		mTransform.rotateZ((float)(mStateTimer*0.6f));
		mGraphics.bindTexture(mCubeTex);
		drawSphere();
		mTransform.stackPush();
		mTransform.scale(0.6f);
		mTransform.stackPush();
		mTransform.translate(3, 0);
		mTransform.rotateY((float)mStateTimer);
		drawSphere();
		mTransform.stackPop();
		mTransform.translate(-5, 0);
		drawSphere();
		mTransform.rotateY((float)(mStateTimer*0.9f));
		mTransform.scale(0.7f);
		mTransform.translate(3, 0);
		drawSphere();
	}
	
	
}
