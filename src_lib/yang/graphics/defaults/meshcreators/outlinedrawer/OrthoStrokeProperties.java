package yang.graphics.defaults.meshcreators.outlinedrawer;

import yang.graphics.textures.TextureCoordBounds;
import yang.graphics.textures.TextureCoordinatesQuad;

public class OrthoStrokeProperties {

	public final static int UP = 1 << 0;
	public final static int RIGHT = 1 << 1;
	public final static int DOWN = 1 << 2;
	public final static int LEFT = 1 << 3;
	
	public float mTexBias = 0.005f;
	public TextureCoordinatesQuad[] mTexCoordTable;
	public TextureCoordinatesQuad[] mLineTexCoords;
	public float mWidth = 0.1f;
	public float mLineTexWidthFactor = 4;
	protected float mPatchSize = 0.25f;
	public float mFieldWidth;

	public int[] mOffsets = {0,1,0,1,3,3,1,0};
	
	public OrthoStrokeProperties() {
		mTexCoordTable = new TextureCoordinatesQuad[16];
		mLineTexCoords = new TextureCoordinatesQuad[4];
		
		for(int i=0;i<16;i++)
			mTexCoordTable[i] = new TextureCoordinatesQuad().initBiased(0,0,0.25f,mTexBias);
		for(int i=0;i<4;i++)
			mLineTexCoords[i] = new TextureCoordinatesQuad().initBiased(0,0.5f,1,0.75f,mTexBias);
	}
	
	public OrthoStrokeProperties finish(TextureCoordBounds texBounds) {
		for(TextureCoordinatesQuad texCoords:mTexCoordTable)
			texCoords.intoRect(texBounds);
		for(TextureCoordinatesQuad texCoords:mLineTexCoords)
			texCoords.intoRect(texBounds);
		return this;
	}
	
	public void setLineTexCoords(TextureCoordinatesQuad texCoords) {
		mLineTexCoords[0] = texCoords;
		mLineTexCoords[1] = texCoords;
		mLineTexCoords[2] = texCoords;
		mLineTexCoords[3] = texCoords;
		mFieldWidth = texCoords.getWidth()/mOffsets.length;
	}
	
	public void putPatch(int mask,int texPatchX,int texPatchY,int rotation) {
		TextureCoordinatesQuad texCoords = new TextureCoordinatesQuad().initBiased(texPatchX*mPatchSize, texPatchY*mPatchSize, mPatchSize, mTexBias);
		texCoords.rotateCoords(rotation);
		mTexCoordTable[mask] = texCoords;
	}
	
	public void putTurns(int texPatchX,int texPatchY) {
		putPatch(LEFT | DOWN, texPatchX, texPatchY);
		putPatch(UP | LEFT, texPatchX, texPatchY, TextureCoordinatesQuad.ROTATE_CCW_90);
		putPatch(RIGHT | UP, texPatchX, texPatchY, TextureCoordinatesQuad.ROTATE_180);
		putPatch(DOWN | RIGHT, texPatchX, texPatchY, TextureCoordinatesQuad.ROTATE_CW_90);
	}
	
	public void putEndings(int texPatchX,int texPatchY) {
		putPatch(UP, texPatchX, texPatchY, TextureCoordinatesQuad.ROTATE_CW_90);
		putPatch(RIGHT, texPatchX, texPatchY);
		putPatch(DOWN, texPatchX, texPatchY, TextureCoordinatesQuad.ROTATE_CCW_90);
		putPatch(LEFT, texPatchX, texPatchY, TextureCoordinatesQuad.ROTATE_180);
	}
	
	public void putMerging(int texPatchX,int texPatchY) {
		putPatch(RIGHT | DOWN | LEFT, texPatchX, texPatchY);
		putPatch(RIGHT | DOWN | UP, texPatchX, texPatchY, TextureCoordinatesQuad.ROTATE_CW_90);
		putPatch(LEFT | DOWN | UP, texPatchX, texPatchY, TextureCoordinatesQuad.ROTATE_CCW_90);
		putPatch(RIGHT | LEFT | UP, texPatchX, texPatchY, TextureCoordinatesQuad.ROTATE_180);
	}
	
	public void putPatch(int mask,int texPatchX,int texPatchY) {
		putPatch(mask,texPatchX,texPatchY,0);
	}
	
}
