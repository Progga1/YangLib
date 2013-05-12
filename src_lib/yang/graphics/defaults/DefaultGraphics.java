package yang.graphics.defaults;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.vecmath.Point4f;

import yang.graphics.FloatColor;
import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.buffers.UniversalVertexBuffer;
import yang.graphics.defaults.meshcreators.StripCreator;
import yang.graphics.font.BitmapFont;
import yang.graphics.font.DrawableString;
import yang.graphics.programs.BasicProgram;
import yang.graphics.textures.TextureCoordinatesQuad;
import yang.graphics.translator.AbstractGraphics;
import yang.graphics.translator.GraphicsTranslator;
import yang.math.MatrixOps;
import yang.math.objects.matrix.YangMatrix;
import yang.model.PrintInterface;
import yang.util.Util;

public abstract class DefaultGraphics<ShaderType extends BasicProgram> extends AbstractGraphics<ShaderType> implements PrintInterface {
	
	public static final float[][] NEUTRAL_ELEMENTS = { { 0, 0 }, { 0, 0 }, { 1, 1, 1, 1 }, { 0, 0, 0, 0 } };

	public static final float[] RECT_TEXTURECOORDS = { 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, };

	public static final float[] RECT_COLORS = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
	
	public static final int ID_POSITIONS = 0;
	public static final int ID_TEXTURES = 1;
	public static final int ID_COLORS = 2;
	public static final int ID_SuppDataS = 3;
	public static final int ID_NORMALS = 4;

	public static final float DEFAULTBIAS = 0.0018f;
	public static final float DEFAULTTEXBIAS = 0;
	public static final float[] NULLCOLOR = { 0, 0, 0, 0 };
	public static final float[] BLACK = { 0, 0, 0, 1 };
	public static final float[] WHITE = { 1, 1, 1, 1 };
	
	public StripCreator mDefaultStripCreator;
	
	protected float[] mInterArray = new float[1024];

	//Properties
	public BitmapFont mDefaultFont;
	public float mDebugOffsetX=0.025f,mDebugOffsetY=0.025f;
	public float mDebugColumnWidth=0.1f,mDebugLineHeight=0.05f;
	
	//State
	public float mCurrentDebugX,mCurrentDebugY;
	public FloatColor mDebugColor = FloatColor.WHITE;
	
	// Buffers
	protected DrawableString mInterString = new DrawableString(2048).setGraphics(this);
	public int mPositionDimension;
	public ShortBuffer mIndexBuffer;
	public FloatBuffer mPositions;
	public FloatBuffer mTextures;
	public FloatBuffer mColors;
	public FloatBuffer mSuppDatas;
	public FloatBuffer mNormals;
	
	protected abstract void refreshResultTransform();
	public abstract void putTransformedPositionRect(YangMatrix transform);

	public DefaultGraphics(GraphicsTranslator translator, int positionBytes) {
		super(translator);
		mPositionDimension = positionBytes;
		mDefaultStripCreator = new StripCreator(this);
	}

	@Override
	public void restart() {
		super.restart();
		setAmbientColor(1);
	}

	@Override
	protected void derivedInit() {
		setAmbientColor(1);
	}

	@Override
	public IndexedVertexBuffer createVertexBuffer(boolean dynamicVertices, boolean dynamicIndices, int maxIndices, int maxVertices) {
		assert mTranslator.preCheck("create vertex buffer");
		IndexedVertexBuffer vertexBuffer = mTranslator.createVertexBuffer(dynamicVertices, dynamicIndices, maxIndices, maxVertices);
		vertexBuffer.init(new int[] { mPositionDimension, 2, 4, 4 }, NEUTRAL_ELEMENTS);
		assert mTranslator.checkErrorInst("Create vertex buffer");
		return vertexBuffer;
	}

	public void bindBuffers() {
		assert mTranslator.checkErrorInst("PRE bind buffers 2D");
		mTranslator.setAttributeBuffer(mCurrentProgram.mPositionHandle, DefaultGraphics.ID_POSITIONS);
		if (mCurrentProgram.mHasTextureCoords)
			mTranslator.setAttributeBuffer(mCurrentProgram.mTextureHandle, DefaultGraphics.ID_TEXTURES);
		if (mCurrentProgram.mHasColor)
			mTranslator.setAttributeBuffer(mCurrentProgram.mColorHandle, DefaultGraphics.ID_COLORS);
		if (mCurrentProgram.mHasSuppData)
			mTranslator.setAttributeBuffer(mCurrentProgram.mSuppDataHandle, DefaultGraphics.ID_SuppDataS);
		assert mTranslator.checkErrorInst("Bind buffers 2D");
	}

	public void enableBuffers() {
		assert mTranslator.checkErrorInst("PRE enable buffers 2D");
		mTranslator.enableAttributePointer(mCurrentProgram.mPositionHandle);
		if (mCurrentProgram.mHasTextureCoords)
			mTranslator.enableAttributePointer(mCurrentProgram.mTextureHandle);
		if (mCurrentProgram.mHasColor)
			mTranslator.enableAttributePointer(mCurrentProgram.mColorHandle);
		if (mCurrentProgram.mHasSuppData)
			mTranslator.enableAttributePointer(mCurrentProgram.mSuppDataHandle);
		assert mTranslator.checkErrorInst("Enable buffers 2D");
		//if(!mTranslator.checkErrorInst("Enable buffers 2D"))
			//System.err.println("Handles: "+mCurrentProgram.mPositionHandle+" "+mCurrentProgram.mTextureHandle+" "+mCurrentProgram.mColorHandle+" "+mCurrentProgram.mSuppDataHandle);
	}

	public void disableBuffers() {
		assert mTranslator.checkErrorInst("PRE disable buffers 2D");
		mTranslator.disableAttributePointer(mCurrentProgram.mPositionHandle);
		if (mCurrentProgram.mHasTextureCoords)
			mTranslator.disableAttributePointer(mCurrentProgram.mTextureHandle);
		if (mCurrentProgram.mHasColor)
			mTranslator.disableAttributePointer(mCurrentProgram.mColorHandle);
		if (mCurrentProgram.mHasSuppData)
			mTranslator.disableAttributePointer(mCurrentProgram.mSuppDataHandle);
		assert mTranslator.checkErrorInst("Disable buffers 2D");
	}

	protected void updateProgramProjection() {
		BasicProgram program = mCurrentProgram;
		if (program != null) {
			refreshResultTransform();
			if (mWorldTransformEnabled) {
				if (program.mHasWorldTransform) {
					program.setWorldTransform(mWorldTransform.mMatrix);
					program.setProjection(mCameraProjectionMatrix.mMatrix);
				} else {
					mResultTransformationMatrix.multiply(mCameraProjectionMatrix, mWorldTransform);
					program.setProjection(mResultTransformationMatrix.mMatrix);
				}
			} else {
				program.setProjection(mCameraProjectionMatrix.mMatrix);
				if (program.mHasWorldTransform)
					program.setWorldTransform(MatrixOps.IDENTITY);
			}
		}
	}

	@Override
	public boolean setShaderProgram(ShaderType program) {
		assert mTranslator.preCheck("Set shader program");
		if (super.setShaderProgram(program)) {
			if(program.mHasAmbientColor)
				program.setAmbientColor(mAmbientColor);
			program.setTime(mTime);
			assert mTranslator.checkErrorInst("Set shader program");
			return true;
		} else
			return false;
	}

	public float readPosition() {
		return mPositions.get();
	}

	public void readColor(float[] target) {
		if (target == null) {
			mColors.get();
			mColors.get();
			mColors.get();
			mColors.get();
		} else {
			target[0] = mColors.get();
			target[1] = mColors.get();
			target[2] = mColors.get();
			target[3] = mColors.get();
		}
	}

	// ---RECTS---

	public void drawQuad(YangMatrix worldTransform, YangMatrix textureTransform) {
		mCurrentVertexBuffer.beginQuad(mTranslator.mWireFrames);
		putTransformedPositionRect(worldTransform);
		putTransformedTextureRect(textureTransform);
		putColorRect(mCurColor);
		putSuppDataRect(mCurSuppData);
	}
	
	public void drawQuad(float worldX1,float worldY1,float worldX2,float worldY2,float worldX3,float worldY3,float worldX4,float worldY4, TextureCoordinatesQuad texCoordinates) {
		mCurrentVertexBuffer.beginQuad(mTranslator.mWireFrames);
		putPosition(worldX1,worldY1);
		putPosition(worldX2,worldY2);
		putPosition(worldX3,worldY3);
		putPosition(worldX4,worldY4);
		if(texCoordinates!=null)
			putTextureArray(texCoordinates.mAppliedCoordinates);
		else
			putTextureArray(RECT_TEXTURECOORDS);
		putColorRect(mCurColor);
		putSuppDataRect(mCurSuppData);
	}

	public void drawQuad(YangMatrix worldTransform, TextureCoordinatesQuad texCoordinates) {
		mCurrentVertexBuffer.beginQuad(mTranslator.mWireFrames);
		putTransformedPositionRect(worldTransform);
		if(texCoordinates!=null)
			putTextureArray(texCoordinates.mAppliedCoordinates);
		else
			putTextureArray(RECT_TEXTURECOORDS);
		putColorRect(mCurColor);
		putSuppDataRect(mCurSuppData);
	}

	public void drawQuad(YangMatrix transform) {
		drawQuad(transform, mTexIdentity);
	}

	public void drawRect(float worldX1, float worldY1, float worldX2, float worldY2, float texX1, float texY1, float texX2, float texY2) {
		mCurrentVertexBuffer.beginQuad(mTranslator.mWireFrames);
		putPositionRect(worldX1, worldY1, worldX2, worldY2);
		putTextureRect(texX1, texY1, texX2, texY2);
		putColorRect(mCurColor);
		putSuppDataRect(mCurSuppData);
	}

	public void drawRect(float worldX1, float worldY1, float worldX2, float worldY2, TextureCoordinatesQuad texCoordinates) {
		mCurrentVertexBuffer.beginQuad(mTranslator.mWireFrames);
		putPositionRect(worldX1, worldY1, worldX2, worldY2);
		putTextureArray(texCoordinates.mAppliedCoordinates);
		putColorRect(mCurColor);
		putSuppDataRect(mCurSuppData);
	}

	public void drawRect(float worldX1, float worldY1, float worldX2, float worldY2, YangMatrix textureTransform) {
		mCurrentVertexBuffer.beginQuad(mTranslator.mWireFrames);
		putPositionRect(worldX1, worldY1, worldX2, worldY2);
		putTransformedTextureRect(textureTransform);
		putColorRect(mCurColor);
		putSuppDataRect(mCurSuppData);
	}

	public void drawRect(float worldX1, float worldY1, float worldX2, float worldY2) {
		mCurrentVertexBuffer.beginQuad(mTranslator.mWireFrames);
		putPositionRect(worldX1, worldY1, worldX2, worldY2);
		putTextureArray(this.mTexIdentity.mAppliedCoordinates);
		putColorRect(mCurColor);
		putSuppDataRect(mCurSuppData);
	}

	// ---PUT-POSITIONS---

	public abstract void putPosition(float x, float y);
	public abstract void putPosition(float x, float y,YangMatrix transform);

	public void putPositionArray(float[] positions) {
		mPositions.put(positions);
	}

	public void putPositionQuad(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
		putPosition(x1, y1);
		putPosition(x2, y2);
		putPosition(x3, y3);
		putPosition(x4, y4);
	}

	public void putPositionRect(float x1, float y1, float x2, float y2, float bias) {
		putPosition(x1 - bias, y1 - bias);
		putPosition(x2 + bias, y1 - bias);
		putPosition(x1 - bias, y2 + bias);
		putPosition(x2 + bias, y2 + bias);
	}

	public void putPositionRect(float x1, float y1, float x2, float y2) {
		putPositionRect(x1, y1, x2, y2, DEFAULTBIAS);
	}

	// ---PUT-TEXTURES---

	public void putTextureArray(float[] texCoords) {
		mTextures.put(texCoords);
	}

	public void putTextureCoord(float u, float v) {
		mTextures.put(u);
		mTextures.put(v);
	}
	
	public void putTextureCoordPair(float u1, int v1, float u2, float v2) {
		mTextures.put(u1);
		mTextures.put(v1);
		mTextures.put(u2);
		mTextures.put(v2);
	}

	public void putTextureRect(float x1, float y1, float x2, float y2, float bias) {
		putTextureCoord(x1 - bias, y1 - bias);
		putTextureCoord(x2 + bias, y1 - bias);
		putTextureCoord(x1 - bias, y2 + bias);
		putTextureCoord(x2 + bias, y2 + bias);
	}

	public void putTextureRect(float x1, float y1, float x2, float y2) {
		putTextureRect(x1, y1, x2, y2, DEFAULTTEXBIAS);
	}

	public void putTransformedTextureRect(YangMatrix transform) {
		mCurrentVertexBuffer.putTransformed2D(ID_TEXTURES,0,1, transform.mMatrix);
		mCurrentVertexBuffer.putTransformed2D(ID_TEXTURES,1,1, transform.mMatrix);
		mCurrentVertexBuffer.putTransformed2D(ID_TEXTURES,0,0, transform.mMatrix);
		mCurrentVertexBuffer.putTransformed2D(ID_TEXTURES,1,0, transform.mMatrix);
	}

	// ---PUT-COLORS---

	public void putColor(float r, float g, float b, float a) {
		mColors.put(r);
		mColors.put(g);
		mColors.put(b);
		mColors.put(a);
	}

	public void putColor(float[] color) {
		mColors.put(color);
	}

	public void putColor(FloatColor color) {
		mColors.put(color.mValues);
	}

	public void putColorRect(float[] color) {
		if(mCurrentProgram.mHasColor) {
			putColor(color);
			putColor(color);
			putColor(color);
			putColor(color);
		}
	}

	public void putColorWhite(int amount) {
		while (amount > 0) {
			putColor(WHITE);
			amount--;
		}
	}

	public void putColor(float[] color, int amount) {
		while (amount > 0) {
			putColor(color);
			amount--;
		}
	}

	// ---AMBIENT-COLOR---

	public void setAmbientColor(float r, float g, float b) {
		flush();
		mAmbientColor[0] = r;
		mAmbientColor[1] = g;
		mAmbientColor[2] = b;
		mAmbientColor[3] = 1;
		if (mCurrentProgram != null && mCurrentProgram.mHasAmbientColor)
			mCurrentProgram.setAmbientColor(r, g, b);
	}

	public void setAmbientColor(float r, float g, float b, float a) {
		flush();
		mAmbientColor[0] = r;
		mAmbientColor[1] = g;
		mAmbientColor[2] = b;
		mAmbientColor[3] = a;
		if (mCurrentProgram != null && mCurrentProgram.mHasAmbientColor)
			mCurrentProgram.setAmbientColor(r, g, b, a);
	}

	public void setAmbientColor(float brightness) {
		assert mTranslator.preCheck("Set ambient color");
		flush();
		mAmbientColor[0] = brightness;
		mAmbientColor[1] = brightness;
		mAmbientColor[2] = brightness;
		mAmbientColor[3] = 1;
		if (mCurrentProgram != null && mCurrentProgram.mHasAmbientColor)
			mCurrentProgram.setAmbientColor(brightness, brightness, brightness, 1);
	}

	// ---PUT-ADD-COLORS---

	public void putSuppData(float r, float g, float b, float a) {
		mSuppDatas.put(r);
		mSuppDatas.put(g);
		mSuppDatas.put(b);
		mSuppDatas.put(a);
	}

	public void putSuppDataBlack(int amount) {
		while (amount > 0) {
			putSuppData(NULLCOLOR);
			amount--;
		}
	}

	public void putSuppData(float[] color) {
		mSuppDatas.put(color);
	}

	public void putSuppData(FloatColor color) {
		mSuppDatas.put(color.mValues);
	}

	public void putSuppDataRect(float[] color) {
		putSuppData(color);
		putSuppData(color);
		putSuppData(color);
		putSuppData(color);
	}

	public void putSuppData(float[] color, int amount) {
		while (amount > 0) {
			putSuppData(color);
			amount--;
		}
	}

	public String buffersToString() {
		String res = "";
		res += "Indices:\t" + Util.bufferToString(mIndexBuffer) + "\n";
		res += "Positions:\t" + Util.bufferToString(mPositions) + "\n";
		res += "Textures:\t" + Util.bufferToString(mTextures) + "\n";
		res += "Colors:\t\t" + Util.bufferToString(mColors) + "\n";
		return res;
	}

	public void check() {
		mIndexBuffer.position(0);
		for (int i = 0; i < mCurrentVertexBuffer.getIndexCount(); i++) {
			int index = mIndexBuffer.get();
			if (index >= mCurrentVertexBuffer.getVertexCount())
				throw new RuntimeException();
		}
		mIndexBuffer.position(0);
	}

	public void setVertexBuffer(IndexedVertexBuffer vertexBuffer) {
		super.setVertexBuffer(vertexBuffer);
		UniversalVertexBuffer buf = (UniversalVertexBuffer) vertexBuffer;
		mIndexBuffer = buf.mIndexBuffer;
		FloatBuffer[] buffers = buf.mFloatBuffers;
		mPositions = buffers[ID_POSITIONS];
		mTextures = buffers[ID_TEXTURES];
		mColors = buffers[ID_COLORS];
		mSuppDatas = buffers[ID_SuppDataS];
		if (buffers.length > 4)
			mNormals = buffers[ID_NORMALS];
		else
			mNormals = null;
	}

	public void drawRect(Point4f rect, TextureCoordinatesQuad texCoords) {
		drawRect(rect.x, rect.y, rect.z, rect.w, texCoords);
	}

	public void drawRect(Point4f rect) {
		drawRect(rect.x, rect.y, rect.z, rect.w);
	}

	public void onPreDraw() {
		updateProgramProjection();
	}

	public void setTime(float time) {
		mTime = time;
		if (mCurrentProgram != null)
			mCurrentProgram.setTime(time);
	}
	
	public void drawString(YangMatrix transform,DrawableString string) {
		string.draw(transform);
	}
	
	public void drawString(float x,float y,float lineHeight,DrawableString string) {
		string.draw(x,y,lineHeight);
	}
	
	public void drawString(float x,float y,float lineHeight,float rotation,DrawableString string) {
		string.draw(x,y,lineHeight,rotation);
	}
	
	public void print(DrawableString string) {
		setColor(mDebugColor);
		drawString(mDebugOffsetX+mCurrentDebugX,mCurrentDebugY,mDebugLineHeight,string);
		mCurrentDebugX += mDebugColumnWidth;
	}
	
	public void println(DrawableString string) {
		print(string);
		mCurrentDebugX = 0;
		mCurrentDebugY += mDebugLineHeight;
	}
	
	public void debugPrint(Object s) {
		mInterString.setString(s.toString());
		print(mInterString);
	}
	
	public void debugPrintln(Object s) {
		mInterString.setString(s.toString());
		println(mInterString);
	}
	

}
