package yang.graphics.defaults.geometrycreators;

import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.textures.TextureCoordinatesQuad;

public class BillboardsCreator extends GeometryCreator<Default3DGraphics>{

	private static final float HALF_ANGLE_SCALE = 1.41f*0.5f;

	private IndexedVertexBuffer mVertexBuffer;

	public BillboardsCreator(Default3DGraphics graphics) {
		super(graphics);
	}

	private void putApplied(float x,float y,float z,float rectX,float rectY) {
		final float[] viewMat = mGraphics.getCameraProjection().getViewTransformReference().mValues;
		final float appX = x + viewMat[0]*rectX + viewMat[1]*rectY;
		final float appY = y + viewMat[4]*rectX + viewMat[5]*rectY;
		final float appZ = z + viewMat[8]*rectX + viewMat[9]*rectY;
		mVertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS, appX, appY, appZ);
	}

	public void putBillboardPositionsUniScale(float x,float y,float z,float scale,float angle) {
		mVertexBuffer = mGraphics.getCurrentVertexBuffer();
		mVertexBuffer.beginQuad(false);
		if(angle==0) {
			scale *= 0.5f;
			putApplied(x,y,z,-scale,-scale);
			putApplied(x,y,z,scale,-scale);
			putApplied(x,y,z,-scale,scale);
			putApplied(x,y,z,scale,scale);
		}else{
			final float dirX = (float)Math.cos(angle+PI/4)*scale*HALF_ANGLE_SCALE;
			final float dirY = (float)Math.sin(angle+PI/4)*scale*HALF_ANGLE_SCALE;
//			putVec4(bufId,offsetX-dirX,offsetY-dirY,offsetX+dirY,offsetY-dirX);
//			putVec4(bufId,offsetX-dirY,offsetY+dirX,offsetX+dirX,offsetY+dirY);
			putApplied(x,y,z,-dirX,-dirY);
			putApplied(x,y,z,dirY,-dirX);
			putApplied(x,y,z,-dirY,dirX);
			putApplied(x,y,z,dirX,dirY);
		}

	}

	//TODO also for rotation
	public void putBillboardPositionsXYScale(float x,float y,float z,float scaleX,float scaleY) {
		mVertexBuffer = mGraphics.getCurrentVertexBuffer();
		mVertexBuffer.beginQuad(false);
		scaleX *= 0.5f;
		scaleY *= 0.5f;
		putApplied(x,y,z,-scaleX,-scaleY);
		putApplied(x,y,z,scaleX,-scaleY);
		putApplied(x,y,z,-scaleX,scaleY);
		putApplied(x,y,z,scaleX,scaleY);
	}

	public void putBillboardTexRect() {
		mVertexBuffer = mGraphics.getCurrentVertexBuffer();
		mVertexBuffer.putVec2(DefaultGraphics.ID_TEXTURES, 0, 1);
		mVertexBuffer.putVec2(DefaultGraphics.ID_TEXTURES, 1, 1);
		mVertexBuffer.putVec2(DefaultGraphics.ID_TEXTURES, 0, 0);
		mVertexBuffer.putVec2(DefaultGraphics.ID_TEXTURES, 1, 0);
	}

	public void putTextureCoords(TextureCoordinatesQuad textureCoordinates) {
		mVertexBuffer = mGraphics.getCurrentVertexBuffer();
		mVertexBuffer.putArray(DefaultGraphics.ID_TEXTURES, textureCoordinates.mAppliedCoordinates);
	}

}
