package yang.graphics.defaults.meshcreators;

import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.textures.TextureCoordinatesQuad;

public class BillboardsCreator extends MeshCreator<Default3DGraphics>{

	private static final float HALF_ANGLE_SCALE = 1.41f*0.5f;
	
	private float[] mCameraMatrix;
	private IndexedVertexBuffer mVertexBuffer;
	
	public BillboardsCreator(Default3DGraphics graphics) {
		super(graphics);
	}
	
	public void begin() {
		mCameraMatrix = mGraphics.mCameraMatrix.mMatrix;
		mVertexBuffer = mGraphics.getCurrentVertexBuffer();
	}
	
	private void putApplied(float x,float y,float z,float rectX,float rectY) {
		float appX = x + mCameraMatrix[0]*rectX + mCameraMatrix[1]*rectY;
		float appY = y + mCameraMatrix[4]*rectX + mCameraMatrix[5]*rectY;
		float appZ = z + mCameraMatrix[8]*rectX + mCameraMatrix[9]*rectY;
		mVertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS, appX, appY, appZ);
	}
	
	public void putBillboardPositionsUniScale(float x,float y,float z,float scale,float angle) {
		mVertexBuffer.beginQuad(false);
		if(angle==0) {
			scale *= 0.5f;
			putApplied(x,y,z,-scale,-scale);
			putApplied(x,y,z,scale,-scale);
			putApplied(x,y,z,-scale,scale);
			putApplied(x,y,z,scale,scale);
		}else{
			float dirX = (float)Math.cos(angle+PI/4)*scale*HALF_ANGLE_SCALE;
			float dirY = (float)Math.sin(angle+PI/4)*scale*HALF_ANGLE_SCALE;
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
		mVertexBuffer.beginQuad(false);
		scaleX *= 0.5f;
		scaleY *= 0.5f;
		putApplied(x,y,z,-scaleX,-scaleY);
		putApplied(x,y,z,scaleX,-scaleY);
		putApplied(x,y,z,-scaleX,scaleY);
		putApplied(x,y,z,scaleX,scaleY);
	}

	public void putBillboardTexRect() {
		mVertexBuffer.putVec2(DefaultGraphics.ID_TEXTURES, 0, 1);
		mVertexBuffer.putVec2(DefaultGraphics.ID_TEXTURES, 1, 1);
		mVertexBuffer.putVec2(DefaultGraphics.ID_TEXTURES, 0, 0);
		mVertexBuffer.putVec2(DefaultGraphics.ID_TEXTURES, 1, 0);
	}

	public void putTextureCoords(TextureCoordinatesQuad textureCoordinates) {
		mVertexBuffer.putArray(DefaultGraphics.ID_TEXTURES, textureCoordinates.mAppliedCoordinates);
	}

}
