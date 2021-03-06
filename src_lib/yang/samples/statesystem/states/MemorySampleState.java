package yang.samples.statesystem.states;

import yang.math.objects.YangMatrix;
import yang.samples.statesystem.SampleState;
import yang.util.pools.YangPool;
import yang.util.pools.YangRingBuffer;

public class MemorySampleState extends SampleState {

	private YangPool<YangMatrix> mMatrixPool;
	private YangRingBuffer<YangMatrix> mRingBuffer;

	@Override
	public void postInit() {
		mMatrixPool = new YangPool<YangMatrix>(16,YangMatrix.class);
		mRingBuffer = new YangRingBuffer<YangMatrix>(16,YangMatrix.class);
	}

	@Override
	protected void step(float deltaTime) {

	}

	@Override
	protected void draw() {
		mGraphics.clear(0,0,0.1f);
		mGraphics.bindTexture(null);
		mGraphics2D.activate();
		mGraphics2D.setWhite();

		//Use pool with alloc-free
		YangMatrix mat1 = mMatrixPool.alloc();	//ALLOC 1
		YangMatrix mat2 = mMatrixPool.alloc();	//ALLOC 2
		mat1.setTranslation(-1.2f,-0.5f);
		mGraphics2D.drawQuad(mat1);
		mMatrixPool.free(mat1);					//FREE 1

		mat2.setTranslation(0.2f,0.5f);
		mat2.scale(0.2f);
		YangMatrix mat3 = mMatrixPool.alloc();	//ALLOC 3
		mat3.setTranslation(0.5f,-0.5f);
		mat3.rotateZ((float)mStateTimer);
		mat3.scale(0.3f, 0.2f);
		mat3.translate(-0.5f,-0.5f);
		mGraphics2D.drawQuad(mat2);
		mGraphics2D.drawQuad(mat3);

		mMatrixPool.free(mat3);					//FREE 2
		mMatrixPool.free(mat2);					//FREE 3

		assert mMatrixPool.isFreedCompletely():"Memory leaks";
		assert mMatrixPool.checkConsistency():"Multiple frees on element";

		//Use ring buffer, only alloc necessary
		YangMatrix mat = mRingBuffer.alloc();
		mat.setTranslation(0.75f,0.75f);
		mat.scale(0.1f);
		mGraphics2D.drawQuad(mat);
	}

}
