package yang.graphics.util.ninepatch;

import java.nio.ShortBuffer;

import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.translator.AbstractGraphics;

public class NinePatchGrid {

	public boolean mNoFill = false;
	public float mBorderLeft,mBorderBottom,mBorderTop,mBorderRight;
	public NinePatchTexCoords mTexCoords;

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

	public void draw(AbstractGraphics<?> graphics,float left,float bottom,float right,float top) {
		final IndexedVertexBuffer vertexBuffer = graphics.mCurrentVertexBuffer;

		if(mNoFill) {
			final ShortBuffer indexBuffer = vertexBuffer.mIndexBuffer;
			short c = (short)vertexBuffer.getCurrentVertexWriteCount();
			for(int i=0;i<3;i++) {
				for(int j=0;j<3;j++){
					if(i!=1 || j!=1) {
						indexBuffer.put(c);
						indexBuffer.put((short)(c+1));
						indexBuffer.put((short)(c+4));
						indexBuffer.put((short)(c+1+4));
						indexBuffer.put((short)(c+4));
						indexBuffer.put((short)(c+1));
					}
					c++;
				}
				c++;
			}
		}else{
			vertexBuffer.putGridIndices(4, 4);
		}

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
		vertexBuffer.putArrayMultiple(DefaultGraphics.ID_COLORS, graphics.mCurColor, 16);
	}

	public void drawCentered(AbstractGraphics<?> graphics,float centerX,float centerY,float width,float height) {
		draw(graphics,centerX-width*0.5f,centerY-height*0.5f,centerX+width*0.5f,centerY+height*0.5f);
	}

	public NinePatchGrid cloneWithTextureOffset(float offsetX,float offsetY) {
		final NinePatchGrid result = new NinePatchGrid();
		result.setBorderSize(mBorderLeft,mBorderTop,mBorderRight,mBorderBottom);
		result.setTextureBorder(mTexCoords.cloneWithOffset(offsetX, offsetY));
		return result;
	}

	@Override
	public NinePatchGrid clone() {
		return cloneWithTextureOffset(0,0);
	}

}
