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

	public void drawCylinder(YangMatrix transform,boolean calcNormals,float bottomRadius,float topRadius) {
		IndexedVertexBuffer vertexBuffer = mGraphics.getCurrentVertexBuffer();
		if(transform==null)
			transform = YangMatrix.IDENTITY;
		float alpha = 0;
		float omega = 2*PI/mSamples;
		int startIndex = vertexBuffer.getCurrentIndexWriteCount();
		for(int i=0;i<mSamples;i++) {
			float x = (float)Math.sin(alpha);
			float z = (float)Math.cos(alpha);
			vertexBuffer.putTransformed3D(DefaultGraphics.ID_POSITIONS, x*bottomRadius, 0, z*bottomRadius, transform.mMatrix);
			vertexBuffer.putTransformed3D(DefaultGraphics.ID_POSITIONS, x*topRadius, 1, z*topRadius, transform.mMatrix);
			if(i>0) {
				int i2 = 2*i;
				//vertexBuffer.putRectIndices(2*i-2,(i2%mSamples),i2-1,(i2+1)%mSamples);
				vertexBuffer.putRectIndices(i2-2,i2,i2-1,i2+1);
			}
			alpha += omega;
		}
		vertexBuffer.putRectIndices(mSamples*2-2,0,mSamples*2-1,1);
		if(calcNormals)
			mGraphics.fillNormals(startIndex);
	}
	
	public void drawCylinder(YangMatrix transform,boolean calcNormals) {
		drawCylinder(transform,calcNormals,1,1);
	}
	
	public void putColor(float[] color) {
		mGraphics.mCurrentVertexBuffer.putArrayMultiple(DefaultGraphics.ID_COLORS, color, mSamples*2+2);
	}
	
}
