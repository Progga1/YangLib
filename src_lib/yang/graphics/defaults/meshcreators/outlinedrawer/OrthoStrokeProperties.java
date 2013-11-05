package yang.graphics.defaults.meshcreators.outlinedrawer;

import yang.graphics.textures.TextureCoordBounds;
import yang.graphics.textures.TextureCoordinatesQuad;

public class OrthoStrokeProperties {

	public final static int UP_IN = 1 << 0;
	public final static int RIGHT_IN = 1 << 2;
	public final static int DOWN_IN = 1 << 4;
	public final static int LEFT_IN = 1 << 6;
	public final static int UP_OUT = 1 << 1;
	public final static int RIGHT_OUT = 1 << 3;
	public final static int DOWN_OUT = 1 << 5;
	public final static int LEFT_OUT = 1 << 7;

	public final static int UP = UP_IN | UP_OUT;
	public final static int RIGHT = RIGHT_IN | RIGHT_OUT;
	public final static int DOWN = DOWN_IN | DOWN_OUT;
	public final static int LEFT = LEFT_IN | LEFT_OUT;

	public final static int ALL_IN = UP_IN | RIGHT_IN | DOWN_IN | LEFT_IN;
	public final static int ALL_OUT = UP_OUT | RIGHT_OUT | DOWN_OUT | LEFT_OUT;

	public final static int DIR_UP = 1 << 0;
	public final static int DIR_RIGHT = 1 << 1;
	public final static int DIR_DOWN = 1 << 2;
	public final static int DIR_LEFT = 1 << 3;

	public final static int ID_UP = 0;
	public final static int ID_RIGHT = 1;
	public final static int ID_DOWN = 2;
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
		mTexCoordTable = new TextureCoordinatesQuad[256];
		mLineTexCoords = new TextureCoordinatesQuad[4];

		final TextureCoordinatesQuad defTexCoords = new TextureCoordinatesQuad().initBiased(0,0,0.25f,mTexBias);
		for(int i=0;i<256;i++)
			mTexCoordTable[i] = defTexCoords;
		for(int i=0;i<4;i++)
			mLineTexCoords[i] = new TextureCoordinatesQuad().initBiased(0,0.5f,1,0.75f,mTexBias);
	}

	public OrthoStrokeProperties() {
		this(new TextureCoordBounds());
	}

	public void setLineTexCoords(TextureCoordinatesQuad texCoords) {
		texCoords.intoRect(mTexBounds);
		mLineTexCoords[ID_UP] = texCoords;
		mLineTexCoords[ID_RIGHT] = texCoords;
		mLineTexCoords[ID_DOWN] = texCoords;
		mLineTexCoords[ID_LEFT] = texCoords;
		mLineTexFieldWidth = texCoords.getWidth()/mOffsets.length;
	}

	public void setLineTexCoords(float upperPatchYCoord,float rightPatchYCoord,float lowerPatchYCoord,float leftPatchYCoord) {
		mLineTexCoords[ID_UP] = new TextureCoordinatesQuad().init(0,upperPatchYCoord*mPatchSize, 1,upperPatchYCoord*mPatchSize+mPatchSize).intoRect(mTexBounds);
		mLineTexCoords[ID_RIGHT] = new TextureCoordinatesQuad().init(0,rightPatchYCoord*mPatchSize, 1,rightPatchYCoord*mPatchSize+mPatchSize).intoRect(mTexBounds);
		mLineTexCoords[ID_DOWN] = new TextureCoordinatesQuad().init(0,lowerPatchYCoord*mPatchSize, 1,lowerPatchYCoord*mPatchSize+mPatchSize).intoRect(mTexBounds);
		mLineTexCoords[ID_LEFT] = new TextureCoordinatesQuad().init(0,leftPatchYCoord*mPatchSize, 1,leftPatchYCoord*mPatchSize+mPatchSize).intoRect(mTexBounds);
		mLineTexFieldWidth = mTexBounds.getWidth()/mOffsets.length;
	}

	public void setLineTexCoords(float horizontalPatchYCoord,float verticalPatchYCoord) {
		setLineTexCoords(horizontalPatchYCoord,verticalPatchYCoord,horizontalPatchYCoord,verticalPatchYCoord);
	}

	public TextureCoordinatesQuad putPatch(int mask,float texPatchX,float texPatchY,int rotation) {
		final TextureCoordinatesQuad texCoords = createTexCoords(texPatchX,texPatchY,rotation);
		mTexCoordTable[mask] = texCoords;
		return texCoords;
	}

	private void putDirection(int maskShift, TextureCoordinatesQuad texCoords) {
		mTexCoordTable[1 << maskShift] = texCoords;
		mTexCoordTable[1 << (maskShift+1)] = texCoords;
		mTexCoordTable[1 << (maskShift+1) | (1 << maskShift)] = texCoords;
	}

	private TextureCoordinatesQuad createTexCoords(float texPatchX,float texPatchY, int rotation) {
		final TextureCoordinatesQuad texCoords = new TextureCoordinatesQuad().initBiased(texPatchX*mPatchSize, texPatchY*mPatchSize, mPatchSize, mTexBias);
		texCoords.intoRect(mTexBounds);
		texCoords.setRotation(rotation);
		return texCoords;
	}

//	public void putPatchCombined(int directionMask,float texPatchX,float texPatchY, int rotation) {
//		final TextureCoordinatesQuad texCoords = createTexCoords(texPatchX,texPatchY,rotation);
//		if((directionMask & UP) != 0)
//			putDirection(ID_UP,texCoords);
//		if((directionMask & RIGHT) != 0)
//			putDirection(ID_RIGHT,texCoords);
//		if((directionMask & DOWN) != 0)
//			putDirection(ID_DOWN,texCoords);
//		if((directionMask & LEFT) != 0)
//			putDirection(ID_LEFT,texCoords);
//	}

	public void putPatchCombined(int directionMask,float texPatchX,float texPatchY, int rotation) {
		final TextureCoordinatesQuad texCoords = createTexCoords(texPatchX,texPatchY,rotation);
//		mTexCoordTable[directionMask] = texCoords;
//		mTexCoordTable[directionMask & ~ALL_IN] = texCoords;
//		mTexCoordTable[directionMask & ~ALL_OUT] = texCoords;
//		System.out.println(Util.intToBin(directionMask & ~ALL_OUT,8)+" "+Util.intToBin(directionMask & ~ALL_IN,8));

//		directionMask &= ALL_IN;
//		for(int i=0;i<256;i++) {
//			final int subMask = (i & directionMask);
//			if(subMask!=0) {
//				mTexCoordTable[subMask] = texCoords;
//				mTexCoordTable[subMask | (subMask << 1)] = texCoords;
//				mTexCoordTable[(subMask << 1)] = texCoords;
//			}
//		}

		final boolean upMask = (directionMask & UP)!=0;
		final boolean rightMask = (directionMask & RIGHT)!=0;
		final boolean downMask = (directionMask & DOWN)!=0;
		final boolean leftMask = (directionMask & LEFT)!=0;

		for(int i=0;i<256;i++) {
//			final int dirMask = (i & DIR_UP) | ((i & DIR_RIGHT)<<1) | ((i & DIR_DOWN)<<2) | ((i & DIR_LEFT)<<3);
//			if((directionMask & i)!=0) {
//				mTexCoordTable[dirMask] = texCoords;
//				mTexCoordTable[dirMask << 1] = texCoords;
//				mTexCoordTable[dirMask | (dirMask << 1)] = texCoords;
//			}

			final boolean upOk = (!upMask && (i&UP)==0) || (upMask && (i&UP)!=0);
			final boolean rightOk = (!rightMask && (i&RIGHT)==0) || (rightMask && (i&RIGHT)!=0);
			final boolean downOk = (!downMask && (i&DOWN)==0) || (downMask && (i&DOWN)!=0);
			final boolean leftOk = (!leftMask && (i&LEFT)==0) || (leftMask && (i&LEFT)!=0);
			if(upOk && rightOk && downOk && leftOk) {
				mTexCoordTable[i] = texCoords;
			}
		}
	}

	public void putPatch(int mask,float texPatchX,float texPatchY) {
		putPatch(mask,texPatchX,texPatchY,0);
	}

	public void putTurns(float texPatchX,float texPatchY) {
//		putPatch(LEFT | DOWN, texPatchX, texPatchY, 0);
//		putPatch(UP | LEFT, texPatchX, texPatchY, TextureCoordinatesQuad.ROTATE_CCW_90);
//		putPatch(RIGHT | UP, texPatchX, texPatchY, TextureCoordinatesQuad.ROTATE_180);
//		putPatch(DOWN | RIGHT, texPatchX, texPatchY, TextureCoordinatesQuad.ROTATE_CW_90);

		putPatchCombined(LEFT | DOWN, texPatchX, texPatchY, TextureCoordinatesQuad.ROTATE_NONE);
		putPatchCombined(UP | LEFT, texPatchX, texPatchY, TextureCoordinatesQuad.ROTATE_CCW90);
		putPatchCombined(RIGHT | UP, texPatchX, texPatchY, TextureCoordinatesQuad.ROTATE_180);
		putPatchCombined(DOWN | RIGHT, texPatchX, texPatchY, TextureCoordinatesQuad.ROTATE_CW90);
	}

	public void putTurns(float outerTurnTexPatchX,float outerTurnTexPatchY,float innerTurnTexPatchX,float innerTurnTexPatchY) {
		putTurns(outerTurnTexPatchX,outerTurnTexPatchY);

		putPatch(LEFT_OUT | DOWN_IN, innerTurnTexPatchX, innerTurnTexPatchY, TextureCoordinatesQuad.ROTATE_180);
		putPatch(UP_OUT | LEFT_IN, innerTurnTexPatchX, innerTurnTexPatchY, TextureCoordinatesQuad.ROTATE_CW90);
		putPatch(RIGHT_OUT | UP_IN, innerTurnTexPatchX, innerTurnTexPatchY, TextureCoordinatesQuad.ROTATE_NONE);
		putPatch(DOWN_OUT | RIGHT_IN, innerTurnTexPatchX, innerTurnTexPatchY, TextureCoordinatesQuad.ROTATE_CCW90);
	}

	public void putEndings(float outTexPatchX,float outTexPatchY,float inTexPatchX,float inTexPatchY) {
		putPatch(UP_IN, inTexPatchX,inTexPatchY, TextureCoordinatesQuad.ROTATE_CW90).setFlipped(true, true);
		putPatch(UP_OUT, outTexPatchX,outTexPatchY, TextureCoordinatesQuad.ROTATE_CW90);
		putPatch(RIGHT_IN, inTexPatchX,inTexPatchY, TextureCoordinatesQuad.ROTATE_NONE).setFlipped(true, true);
		putPatch(RIGHT_OUT, outTexPatchX,outTexPatchY, TextureCoordinatesQuad.ROTATE_NONE);
		putPatch(DOWN_IN, inTexPatchX,inTexPatchY, TextureCoordinatesQuad.ROTATE_CCW90).setFlipped(true, true);
		putPatch(DOWN_OUT, outTexPatchX,outTexPatchY, TextureCoordinatesQuad.ROTATE_CCW90);
		putPatch(LEFT_IN, inTexPatchX,inTexPatchY, TextureCoordinatesQuad.ROTATE_180).setFlipped(true, true);
		putPatch(LEFT_OUT, outTexPatchX,outTexPatchY, TextureCoordinatesQuad.ROTATE_180);
	}

	public void putEndings(float texPatchX,float texPatchY) {
		putPatch(UP_IN, texPatchX,texPatchY, TextureCoordinatesQuad.ROTATE_CW90).setFlipped(false,true);
		putPatch(UP_OUT, texPatchX,texPatchY, TextureCoordinatesQuad.ROTATE_CW90);
		putPatch(RIGHT_IN, texPatchX,texPatchY, TextureCoordinatesQuad.ROTATE_NONE).setFlipped(false,true);
		putPatch(RIGHT_OUT, texPatchX,texPatchY, TextureCoordinatesQuad.ROTATE_NONE);
		putPatch(DOWN_IN, texPatchX,texPatchY, TextureCoordinatesQuad.ROTATE_CCW90).setFlipped(false,true);
		putPatch(DOWN_OUT, texPatchX,texPatchY, TextureCoordinatesQuad.ROTATE_CCW90);
		putPatch(LEFT_IN, texPatchX,texPatchY, TextureCoordinatesQuad.ROTATE_180).setFlipped(false,true);
		putPatch(LEFT_OUT, texPatchX,texPatchY, TextureCoordinatesQuad.ROTATE_180);
	}

	public void putStraight(float texPatchX,float texPatchY) {
		putPatchCombined(LEFT | RIGHT, texPatchX,texPatchY, TextureCoordinatesQuad.ROTATE_NONE);
		putPatchCombined(UP | DOWN, texPatchX,texPatchY, TextureCoordinatesQuad.ROTATE_CW90);
		putPatch(LEFT_IN | RIGHT_OUT, texPatchX,texPatchY, TextureCoordinatesQuad.ROTATE_NONE);
		putPatch(LEFT_OUT | RIGHT_IN, texPatchX,texPatchY, TextureCoordinatesQuad.ROTATE_180);
		putPatch(UP_IN | DOWN_OUT, texPatchX,texPatchY, TextureCoordinatesQuad.ROTATE_CCW90);
		putPatch(UP_OUT | DOWN_IN, texPatchX,texPatchY, TextureCoordinatesQuad.ROTATE_CW90);
	}

	public void putMerging(float texPatchX,float texPatchY) {
		putPatchCombined(RIGHT | DOWN | LEFT, texPatchX,texPatchY, TextureCoordinatesQuad.ROTATE_NONE);
		putPatchCombined(RIGHT | DOWN | UP, texPatchX,texPatchY, TextureCoordinatesQuad.ROTATE_CW90);
		putPatchCombined(LEFT | DOWN | UP, texPatchX,texPatchY, TextureCoordinatesQuad.ROTATE_CCW90);
		putPatchCombined(RIGHT | LEFT | UP, texPatchX,texPatchY, TextureCoordinatesQuad.ROTATE_180);
	}

	public void putCross(float texPatchX,float texPatchY) {
		putPatchCombined(UP | RIGHT | DOWN | LEFT, texPatchX,texPatchY, TextureCoordinatesQuad.ROTATE_NONE);
	}

}
