package yang.samples.statesystem.states;

import yang.graphics.model.FloatColor;
import yang.graphics.textures.TextureProperties;
import yang.graphics.textures.TextureRenderTarget;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.translator.glconsts.GLMasks;
import yang.math.objects.YangMatrix;
import yang.samples.statesystem.SampleState;

public class NestedTextureRenderTargetsSampleState extends SampleState {

	private static final int RECURSION_DEPTH = 8;
	private static FloatColor color1 = new FloatColor(0.95f,0.8f,0.1f);
	private static FloatColor color2 = new FloatColor(0.1f,0.1f,0.7f);
	
	private TextureRenderTarget[] mRenderTargets = new TextureRenderTarget[RECURSION_DEPTH];
	private YangMatrix mTransform = new YangMatrix();
	
	@Override
	public void initGraphics() {
		for(int i=0;i<RECURSION_DEPTH;i++) {
			mRenderTargets[i] = mGraphics.createRenderTarget(512, 512, new TextureProperties(TextureFilter.LINEAR));
		}
	}
	
	@Override
	protected void step(float deltaTime) {
		
	}

	private void drawLevel(int level) {
		mGraphics.setTextureRenderTarget(mRenderTargets[level]);
		mGraphics.clear(level%2==0?color1:color2, GLMasks.DEPTH_BUFFER_BIT);
		if(level<RECURSION_DEPTH-1)
			drawLevel(level+1);
		mGraphics.leaveTextureRenderTarget();
		mGraphics3D.setOrthogonalProjection(-2,2);
		mGraphics.bindTexture(mRenderTargets[level].mTargetTexture);
		mGraphics3D.drawCubeCentered(mTransform);
	}
	
	@Override
	protected void draw() {
		mGraphics3D.activate();
		mGraphics3D.resetCamera();
		mGraphics3D.setWhite();
		mGraphics.switchCulling(true);
		mGraphics.clear(0,0,0, GLMasks.DEPTH_BUFFER_BIT);
		mGraphics.switchZBuffer(true);
		mTransform.loadIdentity();
		mTransform.scale(1.2f);
		mTransform.rotateX(0.2f);
		mTransform.rotateY((float)mStateTimer);
		drawLevel(0);
		mGraphics.bindTexture(null);
		//mGraphics3D.drawCubeCentered(mTransform);
		
	}

}
