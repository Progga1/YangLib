package yang.graphics.defaults.geometrycreators;

import yang.graphics.buffers.AbstractVertexBuffer;
import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.Default2DGraphics;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.translator.GraphicsTranslator;

public class FanCreator {
	
	public DefaultGraphics<?> mGraphics;
	public GraphicsTranslator mTranslator;
	public IndexedVertexBuffer mVertexBuffer;
	
	protected int id = 0;
	private int d;
	private short mStartID;
	
	/**
	 * initializes a FanCreator to a certain degree (1-lowest)
	 * @param graphics
	 * @param degree 1: default triangle fan (at least 3 Points needed) <br>
	 * 2: dual row triangle fan (at least 5 Points needed) <br>
	 * n: n-row triangle fan (at least 2*n+1 Points needed)
	 */
	public FanCreator(DefaultGraphics<?> graphics, int degree) {
		mGraphics = graphics;
		mTranslator = graphics.mTranslator;
		
		d = degree;
	}
	

	/**
	 * Sets the center of the fan
	 * @param x
	 * @param y
	 */
	public void startFan(float x, float y) {
		id = 0;
		mVertexBuffer = mGraphics.mCurrentVertexBuffer;
		mGraphics.putPosition(x, y);
		mStartID = (short)( mGraphics.getCurrentVertexBuffer().getBufferPosition(DefaultGraphics.ID_POSITIONS)-3);		
	}
	
	/**
	 * Adds one point to the fan. 
	 * For a fan of degree n, n points are needed to complete one column of the fan.
	 * Continue has to be called n*columns to have a complete fan 
	 * @param x
	 * @param y
	 */
	public void continueFan(float x, float y) {
		id++;
		
		if( id <= d ) { 
			mGraphics.putPosition(x,y);			
			
		} else {			
			
			if(id % d == 1 || d==1) {
				mVertexBuffer.putIndex((short)(mStartID+id));
				mVertexBuffer.putIndex((short)(mStartID+id-d));
				mVertexBuffer.putIndex((short)(mStartID+id-id));			
				
			} else {
				
				mVertexBuffer.putIndex((short)(mStartID+id));
				mVertexBuffer.putIndex((short)(mStartID+id-1));
				mVertexBuffer.putIndex((short)(mStartID+id-d));
				
				mVertexBuffer.putIndex((short)(mStartID+id-1));
				mVertexBuffer.putIndex((short)(mStartID+id-d-1));
				mVertexBuffer.putIndex((short)(mStartID+id-d));
			}
			
			mGraphics.putPosition(x,y);			
		}		
	}


	/**
	 * connects the first with the last column
	 */
	public void closeFan() {

		short a = 1;
		
		mVertexBuffer.putIndex((short)(mStartID+id-id-1+a));
		mVertexBuffer.putIndex((short)(mStartID+id-id+a));
		mVertexBuffer.putIndex((short)(mStartID+id-d+a));
		
		
		for(int i = 1; i<d; i++) {			
			mVertexBuffer.putIndex((short)(mStartID+id -i-1+a));
			mVertexBuffer.putIndex((short)(mStartID+id -i+a));
			mVertexBuffer.putIndex((short)(mStartID+id-id+d-i+a));
			
			mVertexBuffer.putIndex((short)(mStartID+id -i-1+a));
			mVertexBuffer.putIndex((short)(mStartID+id -id+d-i-1+a));
			mVertexBuffer.putIndex((short)(mStartID+id-id+d-i+a));	
			
		}
	}
	
	
}
