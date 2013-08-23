package yang.util.pools;

public abstract class AbstractYangPool<ElemType> {

	protected Object[] mPool;
	protected Object[] mReferenced;
	protected int mStackPointer;
	
	protected abstract ElemType createElement();
	
	public void recreate(int capacity) {
		mPool = new Object[capacity];
		mReferenced = new Object[capacity];
		for(int i=0;i<capacity;i++) {
			mPool[i] = createElement();
		}
		mStackPointer = 0;
	}
	
	public int getCapacity() {
		return mPool.length;
	}
	
	public ElemType alloc() {
		assert mStackPointer<mPool.length:"Pool too small";
		@SuppressWarnings("unchecked")
		ElemType elem = (ElemType)mPool[mStackPointer++];
		//mPool[mStackPointer++] = null;
		return elem;
	}
	
	public void free(ElemType elem) {
		assert mStackPointer>0:"No elements to free";
		mPool[mStackPointer--] = elem;
	}
	
	public boolean isAllocated() {
		return mStackPointer>0;
	}
	
}
