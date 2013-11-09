package yang.graphics.defaults.meshcreators.grids;

import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.interfaces.KernelFunction;
import yang.graphics.textures.TextureProperties;
import yang.graphics.translator.Texture;
import yang.graphics.util.TextureCreator;
import yang.math.objects.matrix.YangMatrix;

public class TerrainCreator extends Grid3DCreator {

	public boolean mAutoFillNormals = true;

	public TerrainCreator(Default3DGraphics graphics) {
		super(graphics);
	}

	public void putTerrainPositionRect(float[][] heightValues,YangMatrix transform) {
		final float left = -mCurDimX*0.5f;
		final float top = mCurDimY*0.5f;
		compRelations(heightValues);
		for(int row=0;row<mCurYCount;row++) {
			final float y = top - mCurDimY + (float)row/(mCurYCount-1)*mCurDimY;
			for(int col=0;col<mCurXCount;col++) {
				final float x = left + (float)col/(mCurXCount-1)*mCurDimX;
				final float z = interpolate(row,col);
				if(transform!=null)
					mGraphics.putPosition(x,y,z,transform);
				else
					mGraphics.putPosition(x,y,z);
			}
		}
		if(mAutoFillNormals)
			mGraphics.fillNormals(0);
	}

	public void putTerrainPositionRect(float[][] heightValues) {
		putTerrainPositionRect(heightValues,null);
	}

	public void putTerrainPositionRect(YangMatrix transform) {
		putTerrainPositionRect(null,transform);
	}

	public void createCoast(float[][] heightValues, float[][] target, float waterLevel, KernelFunction kernel,float zeroWeight,float factor) {
		final int width = heightValues[0].length;
		final int height = heightValues.length;
		final int kSize = kernel.mRadius;
		//float middleWeight = kernel.getCenterWeight();
		//float max = 0;
		for(int y=0;y<height;y++) {
			final float[] targetLine = target[y];
			final float[] terrainLine = heightValues[y];
			for(int x=0;x<width;x++) {
				if(terrainLine[x]>=waterLevel) {
					targetLine[x] = 1;
				}else{
					//Kernel
					float weightSum = 0;
					float value = 0;
					for(int i=-kSize;i<=kSize;i++) {
						final int kY = y+i;
						if(kY>=0 && kY<height) {
							final float[] terrainKernelLine = heightValues[kY];
							for(int j=-kSize;j<=kSize;j++) {
								final int kX = x+j;
								if(kX>=0 && kX<width) {
									final float weight = kernel.mWeights[i+kSize][j+kSize];
//									if(weight>max)
//										max = weight;
									if(terrainKernelLine[kX]>=waterLevel) {
										value += weight;
										weightSum += weight;
									}else{
										weightSum += weight*zeroWeight;
									}
								}
							}
						}
					}
					final float sum = value/weightSum*2;
					targetLine[x] = sum;
					//targetLine[x] = max / middleWeight;
				}
			}
		}
	}

	public float[][] createCoastArray(float[][] heightValues, float waterLevel, KernelFunction kernel,float zeroWeight,float factor) {
		final float[][] result = new float[heightValues.length][heightValues[0].length];
		createCoast(heightValues,result,waterLevel,kernel,zeroWeight,factor);
		return result;
	}

	public Texture createCoastTexture(float[][] heightValues, float waterLevel, KernelFunction kernel, TextureProperties textureSettings,float zeroWeight,float factor) {
		final float[][] coast = createCoastArray(heightValues,waterLevel,kernel,zeroWeight,factor);
		return mTranslator.createAndInitTexture(TextureCreator.createGrayScaleTexture(coast,4), textureSettings);
	}

}
