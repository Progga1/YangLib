package yang.graphics.defaults.meshcreators;

import yang.graphics.FloatColor;
import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.DefaultGraphics;
import yang.util.Util;

public class GridCreator<GraphicsType extends DefaultGraphics<?>> extends MeshCreator<GraphicsType>{

	protected final static float[][] ZERO_FLOAT = {{0}};
	protected final static float[][] ONE_FLOAT = {{1}};
	
	protected int mCurXCount;
	protected int mCurYCount;
	protected float mCurDimX;
	protected float mCurDimY;
	public boolean mSwapXY;
	
	protected float mRelationX;
	protected float mRelationY;
	private float[][] mCurValues;
	
	public GridCreator(GraphicsType graphics) {
		super(graphics);
	}	

	public void begin(int vertexCountX,int vertexCountY,float width,float height) {
		mGraphics.getCurrentVertexBuffer().putGridIndices(vertexCountX,vertexCountY);
		mCurXCount = vertexCountX;
		mCurYCount = vertexCountY;
		mCurDimX = width;
		mCurDimY = height;
		mCurValues = null;
	}
	
	public void beginBatch(int vertexCountX,int vertexCountY,float width,float height) {
		int indices = vertexCountX*vertexCountY*6;
		int vertices = vertexCountX*vertexCountY;
		mGraphics.startBatchRecording(indices,vertices,false,false);
		begin(vertexCountX,vertexCountY,width,height);
	}
	
	public void putVec4Map(float[][] map,int bufferIndex) {
		float relationX = map[0].length/4 / mCurXCount;
		float relationY = map.length / mCurYCount;
		if(relationX!=1 || relationY!=1) {
			IndexedVertexBuffer vertexBuffer = mGraphics.getCurrentVertexBuffer();
			if((relationX==(int)relationX) && (relationY==(int)relationY)) {
				//Ordinal number relation
				int stepSizeX = (int)relationX;
				int stepSizeY = (int)relationY;
				for(int y=0;y<mCurYCount;y++) {
					float[] mapLine = map[y*stepSizeY];
					float xCount = mCurXCount*stepSizeX*4;
					for(int x=0;x<xCount;x+=stepSizeX*4) {
						vertexBuffer.putVec4(bufferIndex, mapLine[x],mapLine[x+1],mapLine[x+2],mapLine[x+3]);
					}
				}
			}else{
				//Interpolation
				for(int y=0;y<mCurYCount;y++) {
					for(int x=0;x<mCurXCount;x++) {
						vertexBuffer.putVec4(bufferIndex, Util.bilinInterpolate(map,y,x,4,0),Util.bilinInterpolate(map,y,x,4,1),Util.bilinInterpolate(map,y,x,4,2),Util.bilinInterpolate(map,y,x,4,3));
					}
				}
			}
		}else{
			//Same sizes
			for(float[] row:map) {
				mGraphics.getCurrentVertexBuffer().putArray(bufferIndex,row);
			}
		}
	}
	
	public void putGridPositions(float[][] positions) {
		for(float[] row:positions) {
			mGraphics.getCurrentVertexBuffer().putArray(DefaultGraphics.ID_POSITIONS,row);
		}
	}
	
	public void putGridColors(float[][] colors) {
		putVec4Map(colors,DefaultGraphics.ID_COLORS);
	}
	
	public void putGridColor(float[] color) {
		mGraphics.putColor(color, mCurXCount*mCurYCount);
	}
	
	public void putGridAddColor(float[] color) {
		mGraphics.putAddColor(color, mCurXCount*mCurYCount);
	}
	
	public void putGridAddColors(float[][] addColors) {
		putVec4Map(addColors,DefaultGraphics.ID_ADDCOLORS);
	}
	
	public void putGridColors(FloatColor[][] colors) {
		int stepSize = colors.length / mCurYCount;
		for(int row=0;row<mCurYCount;row+=stepSize) {
			FloatColor[] colorRow = colors[row];
			for(int col=0;col<mCurXCount;col+=stepSize) {
				mGraphics.putColor(colorRow[col]);
			}
		}
	}
	
	public void putGridAddColors(FloatColor[][] colors) {
		int stepSize = colors.length / mCurYCount;
		for(int row=0;row<mCurYCount;row+=stepSize) {
			FloatColor[] colorRow = colors[row];
			for(int col=0;col<mCurXCount;col+=stepSize) {
				mGraphics.putAddColor(colorRow[col]);
			}
		}
	}
	
	public void putGridTexCoords(float[][] coords) {
		for(float[] row:coords) {
			mGraphics.getCurrentVertexBuffer().putArray(DefaultGraphics.ID_TEXTURES,row);
		}
	}
	
	public void putGridWhite() {
		mGraphics.putColorWhite(mCurXCount*mCurYCount);
	}
	
	public void putGridAddBlack() {
		mGraphics.putAddColorBlack(mCurXCount*mCurYCount);
	}
	
	public void putGridNeutralColors() {
		putGridWhite();
		putGridAddBlack();
	}
	
	public void putTerrainTextureRect(float left,float top, float right,float bottom) {
		float width = right-left;
		float height = top-bottom;
		for(int row=0;row<mCurYCount;row++) {
			float y = bottom + (float)row/(mCurYCount-1)*height;
			for(int col=0;col<mCurXCount;col++) {
				float x = left + (float)col/(mCurXCount-1)*width;
				mGraphics.putTextureCoord(x,y);
			}
		}
	}
	
	public void putGridTextureCoordinates(float texSquareSize) {
		//float ratio = mCurTerrainWidth/mCurTerrainHeight;
		float xCount = mCurDimX / texSquareSize;
		float yCount = mCurDimY / texSquareSize;
		putTerrainTextureRect(0,0,xCount,yCount);
	}
	
	public void putGridTextureNormalRect(boolean invertV) {
		float v = invertV?1:0;
		putTerrainTextureRect(0,v,1,1-v);
	}
	
	public void putGridTextureNormalRect() {
		putTerrainTextureRect(0,0,1,1);
	}
	
	protected void compRelations(float[][] values) {
		mCurValues = values;
		if(values!=null) {
			if(mSwapXY) {
				mRelationX = (float)(values.length-1) / (mCurXCount-1);
				mRelationY = (float)(values[0].length-1) / (mCurYCount-1);
			}else{
				mRelationX = (float)values[0].length / mCurXCount;
				mRelationY = (float)values.length / mCurYCount;
			}
		}else{
			mRelationX = 1;
			mRelationY = 1;
		}
	}
	
	protected float interpolate(int row,int column) {
		if(mCurValues==null)
			return 1;
		else{
			if(mSwapXY)
				return Util.bilinInterpolate(mCurValues, column*mRelationX, row*mRelationY);
			else
				return Util.bilinInterpolate(mCurValues, row*mRelationY, column*mRelationX);
		}
	}
	
}
