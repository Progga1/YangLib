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
	
	public static TextureCoordinatesQuad[] createTexCoordSequence(float startX,float startY,float size,int count) {
		return createTexCoordSequence(startX,startY,size,size,count);
	}
	
	public static TextureCoordinatesQuad[] createTexCoordSequencePixelsBias(Texture texture, int startX, int startY, int width, int height, int count, int biasX,int biasY) {
		TextureCoordinatesQuad[] result = new TextureCoordinatesQuad[count];
		for(int i=0;i<count;i++) {
			result[i] = new TextureCoordinatesQuad().initBiasedI(startX+i*width,startY,startX+i*width+width,startY+height,texture.mWidth,texture.mHeight,biasX,biasY);
		}
		return result;
	}

	public static TextureCoordinatesQuad[] createTexCoordSequencePixels(Texture texture, int startX, int startY, int width, int height, int count) {
		return createTexCoordSequencePixelsBias(texture,startX,startY,width,height,count,0,0);
	}
	
	public static TextureCoordinatesQuad[] createTexCoordSequencePixels(Texture texture, int startX, int startY, int size, int count) {
		return createTexCoordSequencePixelsBias(texture,startX,startY,size,size,count,0,0);
	}
	
}
