package yang.graphics.defaults.geometrycreators;

import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.translator.GraphicsTranslator;

public class FanCreator {

	public DefaultGraphics<?> mGraphics;
	public IndexedVertexBuffer mVertexBuffer;

	protected int id = 0;
	private int d;
	private short mStartID;
	private float	mPosZ;

	/**
	 * initializes a FanCreator to a certain degree (1-lowest)
	 * @param graphics
	 * @param degree 1: default triangle fan (at least 3 Points needed) <br>
	 * 2: dual row triangle fan (at least 5 Points needed) <br>
	 * n: n-row triangle fan (at least 2*n+1 Points needed)
	 */
	public FanCreator(DefaultGraphics<?> graphics, int degree) {
		mGraphics = graphics;

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
		mStartID = (short)( mVertexBuffer.getCurrentVertexWriteCount()-1);
	}
	
	/**
	 * Sets the center of the fan, give the specific buffer for updating batches
	 * @param x
	 * @param y
	 */
	public void startFan(float x, float y, IndexedVertexBuffer buffer) {
		id = 0;
		mVertexBuffer = buffer;
		
		if(buffer == null) mVertexBuffer = mGraphics.mCurrentVertexBuffer;
		
		mVertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS, x,y, mPosZ);
//		mGraphics.putPosition(x, y);
		mStartID = (short)( mVertexBuffer.getCurrentVertexWriteCount()-1);
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
			mVertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS, x,y, mPosZ);
//			mGraphics.putPosition(x,y);
			

		} else {

			final int offs = mStartID+id;

			if(id % d == 1 || d==1) {
				mVertexBuffer.putIndex((short)(offs));
				mVertexBuffer.putIndex((short)(offs-d));
				mVertexBuffer.putIndex((short)(offs-id));

			} else {

				mVertexBuffer.putIndex((short)(offs));
				mVertexBuffer.putIndex((short)(offs-1));
				mVertexBuffer.putIndex((short)(offs-d));

				mVertexBuffer.putIndex((short)(offs-1));
				mVertexBuffer.putIndex((short)(offs-d-1));
				mVertexBuffer.putIndex((short)(offs-d));
			}

			mVertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS, x,y, mPosZ);
//			mGraphics.putPosition(x,y);
		}
	}


	/**
	 * connects the first with the last column
	 */
	public void closeFan() {

		mVertexBuffer.putIndex((mStartID));
		mVertexBuffer.putIndex((short)(mStartID + 1));
		mVertexBuffer.putIndex((short)(mStartID +id - d + 1));

		for(int i = 1; i<d; i++) {
			mVertexBuffer.putIndex((short)(mStartID + id - i	));
			mVertexBuffer.putIndex((short)(mStartID + id - i + 1));
			mVertexBuffer.putIndex((short)(mStartID + d  - i + 1));

			mVertexBuffer.putIndex((short)(mStartID + id - i	));
			mVertexBuffer.putIndex((short)(mStartID + d  - i	));
			mVertexBuffer.putIndex((short)(mStartID + d  - i + 1));
		}
	}


	public void setCurrentZ(float posZ) {
		mPosZ = posZ; 
	}


}
