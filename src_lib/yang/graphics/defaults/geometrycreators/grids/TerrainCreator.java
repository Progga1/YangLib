package yang.graphics.defaults.geometrycreators.grids;

import java.nio.ShortBuffer;

import yang.graphics.buffers.DrawBatch;
import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.interfaces.KernelFunction;
import yang.graphics.textures.TextureProperties;
import yang.graphics.translator.Texture;
import yang.graphics.util.TextureCreator;
import yang.math.objects.Point3f;
import yang.math.objects.Vector3f;
import yang.math.objects.YangMatrix;

public class TerrainCreator extends Grid3DCreator {

	public static final float[][] ZERO_HEIGHT = new float[1][1];
	private Vector3f tempVec1 = new Vector3f();
	private Vector3f tempVec2 = new Vector3f();
	private Vector3f tempVec3 = new Vector3f();

	public TerrainCreator(Default3DGraphics graphics) {
		super(graphics);
	}

	public void putTerrainPositionRect(float[][] heightValues,int startRow,int startColumn,int rows,int columns,YangMatrix transform) {
		if(heightValues==null)
			heightValues = ZERO_HEIGHT;

		prepareManual(heightValues,startRow,startColumn,rows,columns);
		final float left = mCurLeft;
		final float top = mCurTop;
		float xFac = mCurXFac;
		float yFac = mCurYFac;
		for(int row=0;row<mCurYCount;row++) {
			final float y = top - mCurDimY + row*yFac;
			for(int col=0;col<mCurXCount;col++) {
				final float x = left + col*xFac;
				final float z = interpolate(row,col);
				if(transform!=null)
					mGraphics.putPosition(x,y,z,transform);
				else
					mGraphics.putPosition(x,y,z);
			}
		}
		if(mAutoFillNormals)
			putNormals();
	}

//	public void putIndicesDeltaThreshold(float[][] heightValues,float deltaThreshold) {
//		IndexedVertexBuffer buffer = mGraphics.getCurrentVertexBuffer();
//		ShortBuffer indices = buffer.mIndexBuffer;
//
//		int width = mCurXCount;
//		int height = mCurYCount;
//
//		short c = (short)buffer.getCurrentVertexWriteCount();
//		for(int row=0;row<height-1;row++) {
//			for(int col=0;col<width-1;col++){
//
//				if(row>0 && col>0 && row<height-1 && col<width-1) {
//					float z = interpolate(row,col);
//					float zLeft = interpolate(row,col-1);
//					float zTop = interpolate(row-1,col);
//					float zRight = interpolate(row,col+1);
//					float zBottom = interpolate(row+1,col);
//					float dzLeft = Math.abs(z-zLeft);
//					float dzTop = Math.abs(z-zTop);
//					float dzRight = Math.abs(z-zRight);
//					float dzBottom = Math.abs(z-zBottom);
//					float dzLeftBottom = Math.abs(z-interpolate(row-1,col+1));
//					float dzLeftTop = Math.abs(z-interpolate(row-1,col-1));
//					float dzRightTop = Math.abs(z-interpolate(row+1,col-1));
//					float dzRightBottom = Math.abs(z-interpolate(row+1,col+1));
//					if(z<=0.01f || zRight<0.01f || zBottom<0.01f || dzLeft>deltaThreshold || dzTop>deltaThreshold || dzRight>deltaThreshold || dzBottom>deltaThreshold || dzLeftBottom>deltaThreshold || dzLeftTop>deltaThreshold || dzRightTop>deltaThreshold || dzRightBottom>deltaThreshold) {
//						c++;
//						continue;
//					}
//				}
//
//				indices.put(c);
//				indices.put((short)(c+1));
//				indices.put((short)(c+width));
//				indices.put((short)(c+1+width));
//				indices.put((short)(c+width));
//				indices.put((short)(c+1));
//				mGraphics.putTriangleIndicesMaxArea(c,(short)(c+1),(short)(c+width),0.1f);
//				mGraphics.putTriangleIndicesMaxArea((short)(c+1+width),(short)(c+width),(short)(c+1),0.1f);
//
//				c++;
//			}
//			c++;
//		}
//	}

	public float calcPointInterpolated(int row,int column,Point3f target) {
		target.mX = mCurLeft+column*mCurXFac;
		target.mY = mCurTop - mCurDimY + row*mCurYFac;
		return target.mZ = interpolate(row,column);
	}
	
	public float calcPointNearest(int row,int column,Point3f target) {
		target.mX = mCurLeft+column*mCurXFac;
		target.mY = mCurTop - mCurDimY + row*mCurYFac;
		return target.mZ = mCurValues[row][column];
	}

	public float calcPointInterpolated(int pointNr,Point3f target) {
		return calcPointInterpolated(pointNr/mCurXCount,pointNr%mCurXCount, target);
	}

	public void putTerrainPositionRect(float[][] heightValues,YangMatrix transform) {
		if(heightValues==null)
			heightValues = ZERO_HEIGHT;
		putTerrainPositionRect(heightValues,0,0,heightValues.length,heightValues[0].length,transform);
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
