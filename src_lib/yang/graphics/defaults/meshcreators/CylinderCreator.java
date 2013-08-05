package yang.graphics.defaults.meshcreators;

import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.defaults.DefaultGraphics;
import yang.math.objects.matrix.YangMatrix;

public class CylinderCreator extends MeshCreator<Default3DGraphics> {

	public int mSamples;
	
	public CylinderCreator(Default3DGraphics graphics) {
		super(graphics);
		mSamples = 8;
	}

	public void drawCylinder(YangMatrix transform,float bottomRadius,float topRadius) {
		IndexedVertexBuffer vertexBuffer = mGraphics.getCurrentVertexBuffer();
		if(transform==null)
			transform = YangMatrix.IDENTITY;
		float alpha = 0;
		float omega = 2*PI/mSamples;
		int startIndex = vertexBuffer.getCurrentIndexWriteCount();
		int startVertex = vertexBuffer.getCurrentVertexWriteCount();
		for(int i=0;i<mSamples;i++) {
			float x = (float)Math.sin(alpha);
			float z = (float)Math.cos(alpha);
			vertexBuffer.putTransformed3D(DefaultGraphics.ID_POSITIONS, x*bottomRadius, 0, z*bottomRadius, transform.mMatrix);
			vertexBuffer.putTransformed3D(DefaultGraphics.ID_POSITIONS, x*topRadius, 1, z*topRadius, transform.mMatrix);
			if(i>0) {
				int i2 = startVertex+2*i;
				//vertexBuffer.putRectIndices(2*i-2,(i2%mSamples),i2-1,(i2+1)%mSamples);
				vertexBuffer.putRectIndices(i2-2,i2,i2-1,i2+1);
			}
			alpha += omega;
		}
		vertexBuffer.putRectIndices(startVertex+mSamples*2-2,startVertex,startVertex+mSamples*2-1,startVertex+1);
//		if(calcNormals)
//			mGraphics.fillNormals(0);
	}
	
	public void drawCylinder(YangMatrix transform) {
		drawCylinder(transform,1,1);
	}
	
	public void putColor(float[] color) {
		mGraphics.mCurrentVertexBuffer.putArrayMultiple(DefaultGraphics.ID_COLORS, color, mSamples*2+2);
	}

	public int getVertexCount() {
		return mSamples*2;
	}
	
}
