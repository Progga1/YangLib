package yang.util.pools;

import yang.model.DebugYang;

public abstract class AbstractYangRingBuffer<ElemType> {

	protected Object[] mBuffer;
	private int mCapacity;
	protected int mPointer;
	public int mResetPointer = 0;
	public static int allocCount = 0;

	protected abstract ElemType createElement();

	public void recreate(int capacity) {
		mCapacity = capacity;
		mBuffer = new Object[capacity];
		for(int i=0;i<capacity;i++) {
			mBuffer[i] = createElement();
		}
		mPointer = 0;
	}

	public int getCapacity() {
		return mCapacity;
	}

	public ElemType alloc() {
		assert mPointer<mCapacity:"Pool too small";
		@SuppressWarnings("unchecked")
		ElemType elem = (ElemType)mBuffer[mPointer++];
		if(mPointer>=mCapacity)
			mPointer = mResetPointer;
		allocCount++;
		return elem;
	}

	public void protectPreviousInstances() {
		mResetPointer = mPointer;
	}

}
