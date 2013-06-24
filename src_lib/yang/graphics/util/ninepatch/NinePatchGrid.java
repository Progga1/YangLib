package yang.graphics.util.ninepatch;

import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.model.FloatColor;

public class NinePatchGrid {

	public float mBorderLeft,mBorderBottom,mBorderTop,mBorderRight;
	public FloatColor mColor;
	public NinePatchTexCoords mTexCoords;
	
	public NinePatchGrid() {
		mColor = FloatColor.WHITE.clone();
	}
	
	public NinePatchGrid setBorderSize(float left,float top,float right,float bottom) {
		mBorderLeft = left;
		mBorderTop = top;
		mBorderRight = right;
		mBorderBottom = bottom;
		return this;
	}
	
	public NinePatchGrid setBorderSize(float size) {
		setBorderSize(size,size,size,size);
		return this;
	}
	
	public NinePatchGrid setTextureBorder(NinePatchTexCoords textureBorder) {
		mTexCoords = textureBorder;
		return this;
	}
	
	public NinePatchGrid setTextureBorder(float left,float top,float right,float bottom) {
		mTexCoords = new NinePatchTexCoords().init(left,top,right,bottom);
		return this;
	}
	
	public void setTextureBorder(float size) {
		this.setTextureBorder(size, size, size, size);
	}
	
	public void draw(IndexedVertexBuffer vertexBuffer,float left,float bottom,float right,float top) {
		vertexBuffer.putGridIndices(4, 4);
		vertexBuffer.putVec12(DefaultGraphics.ID_POSITIONS, 
				left,bottom,0, left+mBorderLeft,bottom,0, right-mBorderRight,bottom,0, right,bottom,0
				);
		vertexBuffer.putVec12(DefaultGraphics.ID_POSITIONS, 
				left,bottom+mBorderBottom,0, left+mBorderLeft,bottom+mBorderBottom,0, right-mBorderRight,bottom+mBorderBottom,0, right,bottom+mBorderBottom,0
				);
		vertexBuffer.putVec12(DefaultGraphics.ID_POSITIONS, 
				left,top-mBorderTop,0, left+mBorderLeft,top-mBorderTop,0, right-mBorderRight,top-mBorderTop,0, right,top-mBorderTop,0
				);
		vertexBuffer.putVec12(DefaultGraphics.ID_POSITIONS, 
				left,top,0, left+mBorderLeft,top,0, right-mBorderRight,top,0, right,top,0
				);
		vertexBuffer.putArray(DefaultGraphics.ID_TEXTURES, mTexCoords.mTexCoords);
		vertexBuffer.putArrayMultiple(DefaultGraphics.ID_COLORS, mColor.mValues, 16);
	}
	
	public void drawCentered(IndexedVertexBuffer vertexBuffer,float centerX,float centerY,float width,float height) {
		draw(vertexBuffer,centerX-width*0.5f,centerY-height*0.5f,centerX+width*0.5f,centerY+height*0.5f);
	}
	
	public NinePatchGrid cloneWithTextureOffset(float offsetX,float offsetY) {
		NinePatchGrid result = new NinePatchGrid();
		result.setBorderSize(mBorderLeft,mBorderTop,mBorderRight,mBorderBottom);
		result.mColor.set(mColor);
		result.setTextureBorder(mTexCoords.cloneWithOffset(offsetX, offsetY));
		return result;
	}
	
	public NinePatchGrid clone() {
		return cloneWithTextureOffset(0,0);
	}
	
}
