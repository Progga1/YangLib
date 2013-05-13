package yang.graphics.textures;

import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.translator.Texture;

public abstract class TextureCoordinateSet {

	public GraphicsTranslator mGraphics;
	
	public TextureCoordinateSet(GraphicsTranslator graphics) {
		mGraphics = graphics;
	}
	
	public static TextureCoordinatesQuad[] createTexCoordSequence(float startX,float startY,float width,float height,int count) {
		TextureCoordinatesQuad[] result = new TextureCoordinatesQuad[count];
		for(int i=0;i<count;i++) {
			result[i] = new TextureCoordinatesQuad().init(startX+i*width,startY,startX+i*width+width,startY+height);
		}
		return result;
	}
	
	public static TextureCoordinatesQuad[] createTexCoordSequence(float startX,float startY,float size,int count) {
		return createTexCoordSequence(startX,startY,size,size,count);
	}

	public static TextureCoordinatesQuad[] createTexCoordSequencePixels(Texture texture, int startX, int startY, int width, int height, int count) {
		return createTexCoordSequence((float)startX/texture.mWidth,(float)startY/texture.mHeight,(float)width/texture.mWidth,(float)height/texture.mHeight,count);
	}
	
	public static TextureCoordinatesQuad[] createTexCoordSequencePixels(Texture texture, int startX, int startY, int size, int count) {
		return createTexCoordSequencePixels(texture,startX,startY,size,size,count);
	}
	
}
