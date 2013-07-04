package yang.graphics.textures;

import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.translator.Texture;
import yang.util.NonConcurrentList;

public abstract class TextureCoordinateSet {

	public GraphicsTranslator mGraphics;
	public NonConcurrentList<TextureCoordinatesQuad> mTexCoords;
	protected float mTexWidth,mTexHeight;
	protected float mDefaultBiasX=0,mDefaultBiasY=0;
	
	public static TextureCoordinatesQuad[] createTexCoordSequence(float startX,float startY,float width,float height,float texWidth,float texHeight,int count,float biasX,float biasY) {
		TextureCoordinatesQuad[] result = new TextureCoordinatesQuad[count];
		for(int i=0;i<count;i++) {
			result[i] = new TextureCoordinatesQuad().initBiased(startX+i*width,startY,startX+i*width+width,startY+height,texWidth,texHeight,biasX,biasY);
		}
		return result; 
	}
	
	public static TextureCoordinatesQuad[] createTexCoordSequence(float startX,float startY,float width,float height,float texWidth,float texHeight,int count) {
		return createTexCoordSequence(startX,startY,width,height,texWidth,texHeight,count,0,0);
	}
	
	public static TextureCoordinatesQuad[] createTexCoordSequencePixelsBias(Texture texture, float startX, float startY, float width, float height, int count, float biasX,float biasY) {
		return createTexCoordSequence(startX,startY,width,height,texture.mWidth,texture.mHeight,count,biasX,biasY);
	}
	
	public static TextureCoordinatesQuad[] createTexCoordSequencePixels(Texture texture, float startX, float startY, float width, float height, int count) {
		return createTexCoordSequencePixelsBias(texture,startX,startY,width,height,count,0,0);
	}
	
	public static TextureCoordinatesQuad[] createTexCoordSequencePixels(Texture texture, float startX, float startY, float size, int count) {
		return createTexCoordSequencePixelsBias(texture,startX,startY,size,size,count,0,0);
	}
	
	public TextureCoordinateSet(GraphicsTranslator graphics,float texWidth,float texHeight,float defaultBias) {
		mGraphics = graphics;
		mTexWidth = texWidth;
		mTexHeight = texHeight;
		mDefaultBiasX = defaultBias;
		mDefaultBiasY = defaultBias;
		mTexCoords = new NonConcurrentList<TextureCoordinatesQuad>();
	}
	
	public TextureCoordinateSet(GraphicsTranslator graphics) {
		this(graphics,1,1,0);
	}
	
	public TextureCoordinatesQuad addTexCoords(TextureCoordinatesQuad texCoords) {
		mTexCoords.add(texCoords);
		return texCoords;
	}
	
	public TextureCoordinatesQuad createTexCoordsBiased(float left,float top,float width,float height, float biasX, float biasY) {
		TextureCoordinatesQuad texCoords = new TextureCoordinatesQuad().initBiased(left,top,left+width,top+height,mTexWidth,mTexHeight,biasX,biasY);
		mTexCoords.add(texCoords);
		return texCoords;
	}
	
	public TextureCoordinatesQuad createTexCoords(float left,float top,float width,float height) {
		return createTexCoordsBiased(left,top,width,height,mDefaultBiasX,mDefaultBiasY);
	}
	
	public TextureCoordinatesQuad[] createTexCoordSequenceBias(float startX,float startY,float width,float height,int count,float biasX,float biasY) {
		TextureCoordinatesQuad[] result = new TextureCoordinatesQuad[count];
		for(int i=0;i<count;i++) {
			result[i] = new TextureCoordinatesQuad().initBiased(startX+i*width,startY,startX+i*width+width,startY+height, mTexWidth,mTexHeight, biasX,biasY);
			mTexCoords.add(result[i]);
		}
		return result;
	}
	
	public TextureCoordinatesQuad[] createTexCoordSequence(float startX,float startY,float width,float height,int count) {
		return createTexCoordSequenceBias(startX,startY,width,height,count,mDefaultBiasX,mDefaultBiasY);
	}
	
	public TextureCoordinatesQuad[] createTexCoordSequence(float startX,float startY,float size,int count) {
		return createTexCoordSequence(startX,startY,size,size,count);
	}
	
}
