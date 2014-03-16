package yang.graphics.defaults.geometrycreators.grids;

import yang.graphics.defaults.Default3DGraphics;

public class Grid3DCreator extends GridCreator<Default3DGraphics> {

	public boolean mAutoFillNormals = true;
	public float mCurDimZ;

	public Grid3DCreator(Default3DGraphics graphics) {
		super(graphics);
		mCurDimZ = 1;
		mSwapXY = false;
	}

	@Override
	public void init(int vertexCountX,int vertexCountY,float width,float height) {
		init(vertexCountX, vertexCountY, width, height, 1);
	}

	public void init(int vertexCountX,int vertexCountY,float width,float height,float depth) {
		super.init(vertexCountX, vertexCountY, width, height);
		mCurDimZ = depth;
	}

	public void initBatch(int vertexCountX,int vertexCountY,float width,float height,float depth) {
		super.initBatch(vertexCountX, vertexCountY, width, height);
		mCurDimZ = depth;
	}

	protected void mergeNormals() {
		for(int row=0;row<mCurYCount;row++) {
			mGraphics.mergeNormals(row*mCurXCount,row*mCurXCount+mCurXCount-1);
		}
	}

}
