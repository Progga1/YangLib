package yang.graphics.font;

import java.io.File;
import java.util.Properties;

import yang.graphics.textures.TextureCoordinatesQuad;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.translator.AbstractGFXLoader;
import yang.graphics.translator.Texture;
import yang.model.DebugYang;
import yang.systemdependent.AbstractResourceManager;


public class BitmapFont {
	
	public static String[] ASCII = createASCIIArray();
	
	public static TextureFilter TEXTURE_FILTER = TextureFilter.LINEAR_MIP_LINEAR;
	
	public TextureCoordinatesQuad[] mCoordinates;
	public float[][] mPositions2D;
	public float[][] mPositions3D;
	public float[] mWidths,mHeights;
	public float[][] mKerningValues;
	public float mCharNormalizeFactorX;
	public float mCharNormalizeFactorY;
	public float mSpaceWidth;
	public float mSpacing;
	public float mConstantCharDistance;
	public int mKernBoxes;
	public Texture mTexture;

	public float[] mKerningMinX;
	public float[] mKerningMaxX;

	
	public static String[] createASCIIArray() {
		String[] result = new String[255];
		for(int i=0;i<255;i++) {
			result[i] = ""+(char)i;
		}
		return result;
	}
	
	public BitmapFont() {
		
	}
	
	public BitmapFont init(Texture texture,String fontFilename,AbstractResourceManager resourceManager) {
		mTexture = texture;
		Properties properties = resourceManager.loadPropertiesFile("textures"+File.separatorChar+fontFilename+".txt");
		float textureW = Float.parseFloat(properties.getProperty("textureW", "1"));
		float textureH = Float.parseFloat(properties.getProperty("textureH", ""+textureW));
		float dWidth = 1/textureW;
		float dHeight = 1/textureH;
		
		float charHeight = Float.parseFloat(properties.getProperty("charHeight", ""+(0.1f*textureH)))*dHeight;
		float defaultCharWidth = Float.parseFloat(properties.getProperty("defaultCharWidth",""+charHeight*0.5f));
		mCharNormalizeFactorY = 1f/charHeight;
		mCharNormalizeFactorX = mCharNormalizeFactorY * textureW/textureH;
		int firstCharID = Integer.parseInt(properties.getProperty("firstCharID", "32"));
		int lastCharID = Integer.parseInt(properties.getProperty("lastCharID", "128"));
		mKernBoxes = Integer.parseInt(properties.getProperty("kernBoxes", "0"));
		mConstantCharDistance = defaultCharWidth/charHeight*1.5f;
		mSpaceWidth = Float.parseFloat(properties.getProperty("spaceWidth", ""+(0.25f*textureW)))*dWidth*mCharNormalizeFactorX;
		mSpacing = Float.parseFloat(properties.getProperty("spacing", ""+(0.035f*textureW)))*dWidth*mCharNormalizeFactorY;
		
		int len = lastCharID+1;
		mCoordinates = new TextureCoordinatesQuad[len];
		mKerningValues = new float[len][mKernBoxes*2];
		mKerningMinX = new float[len];
		mKerningMaxX = new float[len];
		mPositions2D = new float[len][8];
		mPositions3D = new float[len][12];
		mWidths = new float[len];
		mHeights = new float[len];
		
		for(int i=firstCharID;i<=lastCharID;i++) {
			String values = properties.getProperty(ASCII[i]);
			String[] valuesSplit = values.split(" ");
			TextureCoordinatesQuad newCoordinates = new TextureCoordinatesQuad();
			if(valuesSplit.length>=2) {
				newCoordinates.x1 = Float.parseFloat(valuesSplit[0])*dWidth;
				newCoordinates.y1 = Float.parseFloat(valuesSplit[1])*dHeight;
				if(valuesSplit.length<4) {
					newCoordinates.x2 = newCoordinates.x1+defaultCharWidth*dWidth;
					newCoordinates.y2 = newCoordinates.y1+charHeight*dHeight;
				}else{
					newCoordinates.x2 = Float.parseFloat(valuesSplit[2])*dWidth;
					newCoordinates.y2 = Float.parseFloat(valuesSplit[3])*dHeight;
					int count = Math.min(valuesSplit.length-4, mKernBoxes*2);
					float[] kerningArray = mKerningValues[i];
					for(int k=0;k<count;k++) {
						kerningArray[k] = Float.parseFloat(valuesSplit[k+4]) * dWidth * mCharNormalizeFactorX;
					}
					
					float[] boxes = mKerningValues[i];
					float minKern = 100;
					float maxKern = 0;
					for(int k=0;k<mKernBoxes;k++) {
						float kernVal1 = boxes[k*2];
						if(kernVal1<minKern)
							minKern = kernVal1;
						float kernVal2 = boxes[k*2+1];
						if(kernVal2>maxKern)
							maxKern = kernVal2;
					}
					mKerningMinX[i] = minKern;
					mKerningMaxX[i] = maxKern;
				}
			}
			newCoordinates.refreshCoordArray();
			mCoordinates[i] = newCoordinates;
			float w = (newCoordinates.x2-newCoordinates.x1)*mCharNormalizeFactorX;
			float h = (newCoordinates.y2-newCoordinates.y1)*mCharNormalizeFactorY;
			mWidths[i] = w;
			mHeights[i] = h;
			float[] positions = mPositions2D[i];
			positions[0] = -w*0.5f;
			positions[1] = -h*0.5f;
			positions[2] = w*0.5f;
			positions[3] = -h*0.5f;
			positions[4] = -w*0.5f;
			positions[5] = h*0.5f;
			positions[6] = w*0.5f;
			positions[7] = h*0.5f;
			positions = mPositions3D[i];
			positions[0] = -w*0.5f;
			positions[1] = -h*0.5f;
			positions[2] = 0;
			positions[3] = w*0.5f;
			positions[4] = -h*0.5f;
			positions[5] = 0;
			positions[6] = -w*0.5f;
			positions[7] = h*0.5f;
			positions[8] = 0;
			positions[9] = w*0.5f;
			positions[10] = h*0.5f;
			positions[11] = 0;
		}

		return this;
	}
	
	public BitmapFont init(String filename,AbstractGFXLoader gfxLoader) {
		Texture tex = null;
		if(DebugYang.drawKerning)
			tex = gfxLoader.getImage(filename+"Debug", TEXTURE_FILTER);
		if(tex==null)
			tex = gfxLoader.getImage(filename, TEXTURE_FILTER);
		return init(tex,filename,gfxLoader.mResources);
	}
	
}
