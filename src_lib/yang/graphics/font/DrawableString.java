package yang.graphics.font;

import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.textures.TextureCoordinatesQuad;
import yang.math.objects.matrix.YangMatrix;

public class DrawableString extends FixedString {
	
	public static int CHAR_LINEBREAK = '\n';
	public static int CHAR_TAB = '\t';
	public static int CHAR_SPACE = ' ';
	
	public static final float ANCHOR_LEFT = 0;
	public static final float ANCHOR_CENTER = 0.5f;
	public static final float ANCHOR_RIGHT = 1;
	public static final float ANCHOR_TOP = 0;
	public static final float ANCHOR_MIDDLE = 0.5f;
	public static final float ANCHOR_BOTTOM = 1;
	
	public static DefaultGraphics<?> DEFAULT_GRAPHICS;
	public static BitmapFont DEFAULT_FONT;
	public static float DEFAULT_HORIZONTAL_ANCHOR = ANCHOR_LEFT;
	public static float DEFAULT_VERTICAL_ANCHOR = ANCHOR_TOP;
	
	protected static float[] staticOffsets = new float[2048];
	protected static float[] staticPositions = new float[2048];
	
	//References
	protected DefaultGraphics<?> mGraphics;
	
	//Properties
	public float mLineHeight = 1;
	public boolean mKerningEnabled = true;
	public BitmapFont mFont;
	public boolean mHasZComponent;
	public float mAdditionalSpacing = 0;
	public int mTabs = 4;
	public boolean mIgnoreSpaceAtLineStart = true;
	public float mVerticalAnchor;
	public float mHorizontalAnchor;
	
	//String properties
	public int mRecentCharCount = 0;
	public int mRecentLineCount;
	public float mRecentStringWidth = 0;
	public float mRecentStringHeight = 0;

	//String data
	protected int[] mInterChars;
	public TextureCoordinatesQuad[] mTexCoords;
	protected float[] mConstantPositions;
	public int[] mLetters;
	
	public DrawableString() {
		super();
		setDefaults();
	}
	
	public DrawableString(String string) {
		this();
		allocString(string);
	}
	
	public DrawableString(int capacity) {
		this();
		allocString(capacity);
	}
	
	public DrawableString allocString(int capacity) {
		super.allocString(capacity);
		mTexCoords = new TextureCoordinatesQuad[capacity];
		mLetters = new int[capacity];
		return this;
	}
	
	protected void setDefaults() {
		setGraphics(DEFAULT_GRAPHICS);
		setFont(DEFAULT_FONT);
		mVerticalAnchor = DEFAULT_VERTICAL_ANCHOR;
		mHorizontalAnchor = DEFAULT_HORIZONTAL_ANCHOR;
	}
	
	public DrawableString setGraphics(DefaultGraphics<?> graphics) {
		mGraphics = graphics;
		if(mGraphics!=null)
			mHasZComponent = mGraphics.mPositionDimension==3;
		return this;
	}
	
	public DrawableString setFont(BitmapFont font) {
		mFont = font;
		return this;
	}
	
	public DrawableString setHasZComponent(boolean hasZComponent) {
		mHasZComponent = hasZComponent;
		return this;
	}
	
	public void createStringPositions(float[] positionTarget,float[] offsetsTarget) {
		int c=0;
		int o=0;
		float lineShift = 0;
		mRecentStringWidth = 0;
		mRecentCharCount = 0;
		mRecentLineCount = 1;
		mRecentStringHeight = 0;
		float spacing = mFont.mSpacing+mAdditionalSpacing;
		int lstVal = -1;
		int curLineCharCount = 0;
		float spaceWidth;
		if(mKerningEnabled)
			spaceWidth = mFont.mSpaceWidth;
		else
			spaceWidth = mFont.mConstantCharDistance;
		spaceWidth += mAdditionalSpacing;
		
		float charX = 0;
		float charY = -mLineHeight;
		int i;
		int count = mLength+1;
		for(i=0;i<count;i++) {
			int val;
			if(i==mLength)
				val = CHAR_LINEBREAK;
			else
				val = mChars[i];
			if(val>0) {
				if(val == CHAR_LINEBREAK) {
					//Line break
					if(lstVal>0) {
						if(mKerningEnabled) {
							charX += mFont.mKerningMaxX[lstVal]+spacing;
						}else{
							charX += mFont.mConstantCharDistance+spacing;
						}
					}
					if(charX>mRecentStringWidth)
						mRecentStringWidth = charX;
					lstVal = -1;
					charY -= mLineHeight;
					curLineCharCount = 0;
					mRecentLineCount++;
					mRecentStringHeight+=mLineHeight;
					if(offsetsTarget!=null) {
						if(mHorizontalAnchor>0) {
							int k=o-1;
							float shift = -(charX+lineShift)*mHorizontalAnchor;
							while(k>=0 && offsetsTarget[k]!=Float.MIN_VALUE) {
								offsetsTarget[k] += shift;
								k--;
							}
						}
						offsetsTarget[o++] = Float.MIN_VALUE;
					}
				}else if(val == CHAR_SPACE) {
					//Space
					if(lstVal>0 && (!mIgnoreSpaceAtLineStart || curLineCharCount>0)) {
						if(mKerningEnabled)
							charX += mFont.mKerningMaxX[lstVal]+mFont.mSpaceWidth;
						else
							charX += spacing*2;
					}
					lstVal = -1;
				}else if(val == CHAR_TAB) {
					//Tab
					int tabs = (curLineCharCount/mTabs+1)*mTabs-curLineCharCount;
					charX += spaceWidth*tabs;
					lstVal = -1;
				}else{
					//Character
					if(lstVal>0) {
						if(mKerningEnabled) {
							//Kerning
							float maxKernShift = 0;
							float[] kernBoxes1 = mFont.mKerningValues[lstVal];
							float[] kernBoxes2 = mFont.mKerningValues[val];
							for(int k=0;k<mFont.mKernBoxes;k++) {
								float shift = kernBoxes1[k*2+1]-kernBoxes2[k*2];
								if(shift>maxKernShift) {
									maxKernShift = shift;
								}
							}
							charX += maxKernShift+spacing;
						}else
							charX += mFont.mConstantCharDistance;
						charX += mAdditionalSpacing;
					}else{
						//first char of line
						if(curLineCharCount==0) {
							if(mKerningEnabled) {
								lineShift = -mFont.mKerningMinX[val];
								charX = lineShift;
							}else{
								charX = 0;
							}
						}
					}
					
					TextureCoordinatesQuad coords = mFont.mCoordinates[val];
					mLetters[mRecentCharCount] = val;
					mTexCoords[mRecentCharCount] = coords;
					float w = mFont.mWidths[val];
					float h = mFont.mHeights[val];
					float uX;
					if(mKerningEnabled)
						uX = charX;
					else
						uX = charX+mFont.mConstantCharDistance*0.5f-w*0.5f;
					if(positionTarget!=null) {
						positionTarget[c++] = uX;
						positionTarget[c++] = charY;
						if(mHasZComponent)
							positionTarget[c++] = 0;
						positionTarget[c++] = uX + w;
						positionTarget[c++] = charY;
						if(mHasZComponent)
							positionTarget[c++] = 0;
						positionTarget[c++] = uX;
						positionTarget[c++] = charY + h;
						if(mHasZComponent)
							positionTarget[c++] = 0;
						positionTarget[c++] = uX + w;
						positionTarget[c++] = charY + h;
						if(mHasZComponent)
							positionTarget[c++] = 0;
					}
					if(offsetsTarget!=null) {
						offsetsTarget[o++] = uX;
					}
					mRecentCharCount++;
					curLineCharCount++;
					lstVal = val;
				}
			}
			
		}
		
	}

	public DrawableString setConstant() {
		if(mConstantPositions==null)
			mConstantPositions = new float[mCapacity*8];
		createStringPositions(mConstantPositions,null);
		applyAnchors(mHorizontalAnchor,mVerticalAnchor,mConstantPositions);
		return this;
	}
	
	protected void applyAnchors(float horizontal,float vertical,float[] positionTarget) {
		float shiftX = -horizontal*mRecentStringWidth;
		float shiftY = vertical*mRecentStringHeight;
		int c = 0;
		for(int i=0;i<mRecentCharCount*4;i++) {
			positionTarget[c++] += shiftX;
			positionTarget[c++] += shiftY;
			if(mHasZComponent)
				c++;
		}
	}

	
	public float calcNormalizedStringWidth() {
		if(mConstantPositions==null)
			createStringPositions(null,null);
		return mRecentStringWidth;
	}
	
	public DrawableString setHorizontalAnchor(float anchor) {
		mHorizontalAnchor = anchor;
		return this;
	}
	
	public DrawableString setVerticalAnchor(float anchor) {
		mVerticalAnchor = anchor;
		return this;
	}
	
	public DrawableString setAnchors(float horizontalAnchor,float verticalAnchor) {
		mHorizontalAnchor = horizontalAnchor;
		mVerticalAnchor = verticalAnchor;
		return this;
	}
	
	protected void putColors() {
		for(int i=0;i<mRecentCharCount;i++) {
			mGraphics.putColorRect(mGraphics.mCurColor);
			mGraphics.putAddColorRect(mGraphics.mCurAddColor);
		}
	}

	protected void putVertexProperties() {
		IndexedVertexBuffer vertexBuffer = mGraphics.mCurrentVertexBuffer;
		short offset = (short)vertexBuffer.getCurrentVertexWriteCount();
		for(int i=0;i<mRecentCharCount;i++) {
			vertexBuffer.beginQuad(false,offset);
			vertexBuffer.putArray(DefaultGraphics.ID_TEXTURES, mTexCoords[i].mAppliedCoordinates);
			offset += 4;
		}
		
		putColors();
	}
	
	public void draw(YangMatrix transform) {
		float[] positions;
		YangMatrix resultTransf;
		if(mConstantPositions==null){
			createStringPositions(staticPositions,null);
			positions = staticPositions;
			mGraphics.mInterTransf2.loadIdentity();
			mGraphics.mInterTransf2.translate(-mRecentStringWidth*mHorizontalAnchor, mRecentStringHeight*mVerticalAnchor);
			mGraphics.mInterTransf2.multiplyLeft(transform);
			resultTransf = mGraphics.mInterTransf2;
		}else{
			positions = mConstantPositions;
			resultTransf = transform;
		}
		
		IndexedVertexBuffer vertexBuffer = mGraphics.mCurrentVertexBuffer;
		
		mGraphics.mTranslator.bindTexture(mFont.mTexture);
		
		putVertexProperties();

		vertexBuffer.putTransformedArray(DefaultGraphics.ID_POSITIONS,positions,mRecentCharCount*4,mGraphics.mPositionDimension,resultTransf.mMatrix);
	}
	
	public void draw(float x,float y,float lineHeight) {
		mGraphics.mInterTransf1.loadIdentity();
		mGraphics.mInterTransf1.translate(x, y);
		mGraphics.mInterTransf1.scale(lineHeight);
		draw(mGraphics.mInterTransf1);
	}
	
	public void draw(float x,float y,float lineHeight,float rotation) {
		mGraphics.mInterTransf1.loadIdentity();
		mGraphics.mInterTransf1.translate(x, y);
		mGraphics.mInterTransf1.scale(lineHeight);
		mGraphics.mInterTransf1.rotateZ(rotation);
		draw(mGraphics.mInterTransf1);
	}
	
}
