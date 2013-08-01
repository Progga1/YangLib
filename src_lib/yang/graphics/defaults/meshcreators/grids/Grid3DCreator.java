package yang.graphics.defaults.meshcreators.grids;

import yang.graphics.defaults.Default3DGraphics;

public class Grid3DCreator extends GridCreator<Default3DGraphics> {

	public float mCurDimZ;
	
	public Grid3DCreator(Default3DGraphics graphics) {
		super(graphics);
		mCurDimZ = 1;
		mSwapXY = false;
	}
	
	@Override
	public void begin(int vertexCountX,int vertexCountY,float width,float height) {
		begin(vertexCountX, vertexCountY, width, height, 1);
	}
	
	public void begin(int vertexCountX,int vertexCountY,float width,float height,float depth) {
		super.begin(vertexCountX, vertexCountY, width, height);
		mCurDimZ = depth;
	}
	
	public void beginBatch(int vertexCountX,int vertexCountY,float width,float height,float depth) {
		super.beginBatch(vertexCountX, vertexCountY, width, height);
		mCurDimZ = depth;
	}

	protected void mergeNormals() {
		for(int row=0;row<mCurYCount;row++) {
			mGraphics.mergeNormals(row*mCurXCount,row*mCurXCount+mCurXCount-1);
		}
	}
	
}
