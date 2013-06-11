package yang.graphics.textures;

import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.translator.Texture;

public abstract class TextureCoordinateSet {

	public GraphicsTranslator mGraphics;
	
	public TextureCoordinateSet(GraphicsTranslator graphics) {
		mGraphics = graphics;
	}
	
	public static TextureCoordinatesQuad[] createTexCoordSequenceBias(float startX,float startY,float width,float height,int count,float biasX,float biasY) {
		TextureCoordinatesQuad[] result = new TextureCoordinatesQuad[count];
		for(int i=0;i<count;i++) {
			result[i] = new TextureCoordinatesQuad().init(startX+i*width+biasX,startY+biasY,startX+i*width+width-biasX,startY+height-biasY);
		}
		return result;
	}
	
	public static TextureCoordinatesQuad[] createTexCoordSequence(float startX,float startY,float width,float height,int count) {
		return createTexCoordSequenceBias(startX,startY,width,height,count,0,0);
	}
	
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
	
	public static TextureCoordinatesQuad[] createTexCoordSequence(float startX,float startY,float size,int count) {
		return createTexCoordSequence(startX,startY,size,size,count);
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
	
}
