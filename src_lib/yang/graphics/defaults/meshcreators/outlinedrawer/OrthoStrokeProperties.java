package yang.graphics.defaults.meshcreators.outlinedrawer;

import yang.graphics.textures.TextureCoordBounds;
import yang.graphics.textures.TextureCoordinatesQuad;

public class OrthoStrokeProperties {

	public final static int UP = 1 << 0;
	public final static int RIGHT = 1 << 1;
	public final static int DOWN = 1 << 2;
	public final static int LEFT = 1 << 3;

	public final static int ID_TOP = 0;
	public final static int ID_RIGHT = 1;
	public final static int ID_BOTTOM = 2;
	public final static int ID_LEFT = 3;

	//Properties
	public float mTexBias = 0.005f;
	public float mWidth = 0.1f;
	public float mStretch = 1;
	public float mStraightLineWidthFactor = 1;

	public TextureCoordinatesQuad[] mTexCoordTable;
	public TextureCoordinatesQuad[] mLineTexCoords;
	protected float mPatchSize = 0.25f;
	public float mLineTexFieldWidth;
	public TextureCoordBounds mTexBounds;

	public int[] mOffsets = {0,1,0,1,3,3,1,0};

	public OrthoStrokeProperties(TextureCoordBounds texBounds) {
		mTexBounds = texBounds;
		mTexCoordTable = new TextureCoordinatesQuad[16];
		mLineTexCoords = new TextureCoordinatesQuad[4];

		for(int i=0;i<16;i++)
			mTexCoordTable[i] = new TextureCoordinatesQuad().initBiased(0,0,0.25f,mTexBias);
		for(int i=0;i<4;i++)
			mLineTexCoords[i] = new TextureCoordinatesQuad().initBiased(0,0.5f,1,0.75f,mTexBias);
	}

	public OrthoStrokeProperties() {
		this(new TextureCoordBounds());
	}

	public void setLineTexCoords(TextureCoordinatesQuad texCoords) {
		texCoords.intoRect(mTexBounds);
		mLineTexCoords[ID_TOP] = texCoords;
		mLineTexCoords[ID_RIGHT] = texCoords;
		mLineTexCoords[ID_BOTTOM] = texCoords;
		mLineTexCoords[ID_LEFT] = texCoords;
		mLineTexFieldWidth = texCoords.getWidth()/mOffsets.length;
	}

	public void setLineTexCoords(float upperPatchYCoord,float rightPatchYCoord,float lowerPatchYCoord,float leftPatchYCoord) {
		mLineTexCoords[ID_TOP] = new TextureCoordinatesQuad().init(0,upperPatchYCoord*mPatchSize, 1,upperPatchYCoord*mPatchSize+mPatchSize).intoRect(mTexBounds);
		mLineTexCoords[ID_RIGHT] = new TextureCoordinatesQuad().init(0,rightPatchYCoord*mPatchSize, 1,rightPatchYCoord*mPatchSize+mPatchSize).intoRect(mTexBounds);
		mLineTexCoords[ID_BOTTOM] = new TextureCoordinatesQuad().init(0,lowerPatchYCoord*mPatchSize, 1,lowerPatchYCoord*mPatchSize+mPatchSize).intoRect(mTexBounds);
		mLineTexCoords[ID_LEFT] = new TextureCoordinatesQuad().init(0,leftPatchYCoord*mPatchSize, 1,leftPatchYCoord*mPatchSize+mPatchSize).intoRect(mTexBounds);
		mLineTexFieldWidth = mTexBounds.getWidth()/mOffsets.length;
	}

	public void setLineTexCoords(float horizontalPatchYCoord,float verticalPatchYCoord) {
		setLineTexCoords(horizontalPatchYCoord,verticalPatchYCoord,horizontalPatchYCoord,verticalPatchYCoord);
	}

	public void putPatch(int mask,float texPatchX,float texPatchY,int rotation) {
		final TextureCoordinatesQuad texCoords = new TextureCoordinatesQuad().initBiased(texPatchX*mPatchSize, texPatchY*mPatchSize, mPatchSize, mTexBias);
		texCoords.intoRect(mTexBounds);
		texCoords.setRotation(rotation);
		mTexCoordTable[mask] = texCoords;
	}

	public void putPatch(int mask,float texPatchX,float texPatchY) {
		putPatch(mask,texPatchX,texPatchY,0);
	}

	public void putTurns(float texPatchX,float texPatchY) {
		putPatch(LEFT | DOWN, texPatchX, texPatchY);
		putPatch(UP | LEFT, texPatchX, texPatchY, TextureCoordinatesQuad.ROTATE_CCW_90);
		putPatch(RIGHT | UP, texPatchX, texPatchY, TextureCoordinatesQuad.ROTATE_180);
		putPatch(DOWN | RIGHT, texPatchX, texPatchY, TextureCoordinatesQuad.ROTATE_CW_90);
	}

	public void putEndings(float texPatchX,float texPatchY) {
		putPatch(UP, texPatchX, texPatchY, TextureCoordinatesQuad.ROTATE_CW_90);
		putPatch(RIGHT, texPatchX, texPatchY);
		putPatch(DOWN, texPatchX, texPatchY, TextureCoordinatesQuad.ROTATE_CCW_90);
		putPatch(LEFT, texPatchX, texPatchY, TextureCoordinatesQuad.ROTATE_180);
	}

	public void putMerging(float texPatchX,float texPatchY) {
		putPatch(RIGHT | DOWN | LEFT, texPatchX, texPatchY);
		putPatch(RIGHT | DOWN | UP, texPatchX, texPatchY, TextureCoordinatesQuad.ROTATE_CW_90);
		putPatch(LEFT | DOWN | UP, texPatchX, texPatchY, TextureCoordinatesQuad.ROTATE_CCW_90);
		putPatch(RIGHT | LEFT | UP, texPatchX, texPatchY, TextureCoordinatesQuad.ROTATE_180);
	}

}
