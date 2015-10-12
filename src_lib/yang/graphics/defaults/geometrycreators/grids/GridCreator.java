package yang.graphics.defaults.geometrycreators.grids;

import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.defaults.geometrycreators.GeometryCreator;
import yang.graphics.model.FloatColor;
import yang.graphics.textures.TextureCoordinatesQuad;
import yang.math.Interpolation;

public class GridCreator<GraphicsType extends DefaultGraphics<?>> extends GeometryCreator<GraphicsType>{

	protected final static float[][] ZERO_FLOAT = {{0}};
	protected final static float[][] ONE_FLOAT = {{1}};

	protected int mCurXCount;
	protected int mCurYCount;
	protected float mCurDimX;
	protected float mCurDimY;
	protected float mCurLeft;
	protected float mCurTop;
	protected float mCurXFac;
	protected float mCurYFac;
	public boolean mSwapXY;

	protected int mStartRow;
	protected int mStartColumn;
	protected float mRelationX;
	protected float mRelationY;
	protected float[][] mCurValues;

	public GridCreator(GraphicsType graphics) {
		super(graphics);
	}

	public void init(int vertexCountX,int vertexCountY,float width,float height) {
		mCurXCount = vertexCountX;
		mCurYCount = vertexCountY;
		mCurDimX = width;
		mCurDimY = height;
	}

	public void putAllIndices() {
		mGraphics.getCurrentVertexBuffer().putGridIndices(mCurXCount,mCurYCount);
	}

	public void putIndexPatch(int patchX, int patchY) {
		mGraphics.getCurrentVertexBuffer().putGridIndexPatch(mCurXCount, patchX,patchY);
	}

	public void initBatch(int vertexCountX,int vertexCountY,float width,float height) {
		final int indices = vertexCountX*vertexCountY*6;
		final int vertices = vertexCountX*vertexCountY;
		mGraphics.startBatchRecording(indices,vertices,false,false);
		init(vertexCountX,vertexCountY,width,height);
	}

	public void putVec4Map(float[][] map,int bufferIndex) {
		final float relationX = map[0].length/4 / mCurXCount;
		final float relationY = map.length / mCurYCount;
		if(relationX!=1 || relationY!=1) {
			final IndexedVertexBuffer vertexBuffer = mGraphics.getCurrentVertexBuffer();
			if((relationX==(int)relationX) && (relationY==(int)relationY)) {
				//Ordinal number relation
				final int stepSizeX = (int)relationX;
				final int stepSizeY = (int)relationY;
				for(int y=0;y<mCurYCount;y++) {
					final float[] mapLine = map[y*stepSizeY];
					final float xCount = mCurXCount*stepSizeX*4;
					for(int x=0;x<xCount;x+=stepSizeX*4) {
						vertexBuffer.putVec4(bufferIndex, mapLine[x],mapLine[x+1],mapLine[x+2],mapLine[x+3]);
					}
				}
			}else{
				//Interpolation
				for(int y=0;y<mCurYCount;y++) {
					for(int x=0;x<mCurXCount;x++) {
						vertexBuffer.putVec4(bufferIndex, Interpolation.bilinInterpolate(map,y,x,4,0),Interpolation.bilinInterpolate(map,y,x,4,1),Interpolation.bilinInterpolate(map,y,x,4,2),Interpolation.bilinInterpolate(map,y,x,4,3));
					}
				}
			}
		}else{
			//Same sizes
			for(final float[] row:map) {
				mGraphics.getCurrentVertexBuffer().putArray(bufferIndex,row);
			}
		}
	}

	public void putGridPositions(float[][] positions) {
		for(final float[] row:positions) {
			mGraphics.getCurrentVertexBuffer().putArray(DefaultGraphics.ID_POSITIONS,row);
		}
	}

	public void putGridPositions(float[] positions) {
		mGraphics.getCurrentVertexBuffer().putArray(DefaultGraphics.ID_POSITIONS,positions);
	}

	public void putGridColors(float[][] colors) {
		putVec4Map(colors,DefaultGraphics.ID_COLORS);
	}

	public void putGridColor(float[] color) {
		mGraphics.putColor(color, mCurXCount*mCurYCount);
	}

	public void putGridSuppData(float[] data) {
		mGraphics.putSuppDataMultiple(data, mCurXCount*mCurYCount);
	}

	public void putGridSuppData(float[][] SuppData) {
		putVec4Map(SuppData,DefaultGraphics.ID_SUPPDATA);
	}

	public void putGridColors(FloatColor[][] colors) {
		final int stepSize = colors.length / mCurYCount;
		for(int row=0;row<mCurYCount;row+=stepSize) {
			final FloatColor[] colorRow = colors[row];
			for(int col=0;col<mCurXCount;col+=stepSize) {
				mGraphics.putColor(colorRow[col]);
			}
		}
	}

	public void putGridWhite() {
		mGraphics.putColorWhite(mCurXCount*mCurYCount);
	}

	public void putGridSuppDataZero() {
		mGraphics.putSuppDataZero(mCurXCount*mCurYCount);
	}

	public void putGridNeutralColors() {
		putGridWhite();
		putGridSuppDataZero();
	}

	public void putGridTextureCoordinates(float[][] coords) {
		for(final float[] row:coords) {
			mGraphics.getCurrentVertexBuffer().putArray(DefaultGraphics.ID_TEXTURES,row);
		}
	}

	public void putGridTextureCoordinates(TextureCoordinatesQuad texCoords) {
		putGridTextureRect(texCoords.getBiasedTop(),texCoords.getBiasedTop(), texCoords.getBiasedRight(),texCoords.getBiasedBottom());
	}

	public void putGridTextureRect(float left,float top, float right,float bottom) {
		final float width = right-left;
		final float height = top-bottom;
		for(int row=0;row<mCurYCount;row++) {
			final float y = bottom + (float)row/(mCurYCount-1)*height;
			for(int col=0;col<mCurXCount;col++) {
				final float x = left + (float)col/(mCurXCount-1)*width;
				mGraphics.putTextureCoord(x,y);
			}
		}
	}

	public void putGridTextureRect(TextureCoordinatesQuad texCoords) {
		putGridTextureRect(texCoords.getBiasedLeft(),texCoords.getBiasedTop(),texCoords.getBiasedRight(),texCoords.getBiasedBottom());
	}

	public void putGridTextureCoordinates(float texSquareSize) {
		//float ratio = mCurTerrainWidth/mCurTerrainHeight;
		final float xCount = mCurDimX / texSquareSize;
		final float yCount = mCurDimY / texSquareSize;
		putGridTextureRect(0,0,xCount,yCount);
	}

	public void putGridTextureNormalRect(boolean invertV) {
		final float v = invertV?1:0;
		putGridTextureRect(0,v,1,1-v);
	}

	public void putGridTextureNormalRectScaleCentered(float scaleX,float scaleY) {
		float width = 1/scaleX;
		float height = 1/scaleY;
		float cutX = (1-width)*0.5f;
		float cutY = (1-height)*0.5f;
		putGridTextureRect(cutX,cutY,1-cutX,1-cutY);
	}

	public void putGridTextureNormalRect() {
		putGridTextureRect(0,0,1,1);
	}

	public void prepareManual(float[][] values,int startRow,int startColumn,int rows,int columns) {
		mCurValues = values;
		mStartRow = startRow;
		mStartColumn = startColumn;
		if(values!=null) {
			if(mSwapXY) {
				mRelationX = (float)(rows-1) / (mCurXCount-1);
				mRelationY = (float)(columns-1) / (mCurYCount-1);
			}else{
				mRelationX = (float)(columns-1) / (mCurXCount-1);
				mRelationY = (float)(rows-1) / (mCurYCount-1);
			}
		}else{
			mRelationX = 1;
			mRelationY = 1;
		}
		mCurLeft = -mCurDimX*0.5f;
		mCurTop = mCurDimY*0.5f;
		mCurXFac = 1f/(mCurXCount-1)*mCurDimX;
		mCurYFac = 1f/(mCurYCount-1)*mCurDimY;
	}

	public void prepareManual(float[][] values) {
		if(values==null)
			prepareManual(null,0,0,0,0);
		else
			prepareManual(values,0,0,values.length,values[0].length);
	}

	protected float interpolate(int row,int column) {
		if(mCurValues==null)
			return 1;
		else{
			if(mSwapXY)
				return Interpolation.bilinInterpolate(mCurValues, mStartColumn+column*mRelationX, mStartRow+row*mRelationY);
			else
				return Interpolation.bilinInterpolate(mCurValues, mStartRow+row*mRelationY, mStartColumn+column*mRelationX);
		}
	}

	public int getCurrentVertexCount() {
		return mCurXCount*mCurYCount;
	}

	public int getCurrentIndexCount() {
		return mCurXCount*mCurYCount*6;
	}

}
