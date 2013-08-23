package yang.util.pools;

public abstract class AbstractYangPool<ElemType> {

	protected Object[] mPool;
	protected int mStackPointer;
	
	protected abstract ElemType createElement();
	
	public void recreate(int capacity) {
		mPool = new Object[capacity];
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
		return elem;
	}
	
	public void free(ElemType elem) {
		assert mStackPointer>0:"No elements to free";
		mPool[--mStackPointer] = elem;
	}
	
	public boolean isFreedCompletely() {
		return mStackPointer==0;
	}
	

	public boolean checkConsistency() {
		for(int i=mStackPointer;i<mPool.length;i++) {
			for(int j=i+1;j<mPool.length;j++) {
				if(mPool[i]==mPool[j])
					return false;
			}
		}
			
		return true;
	}
	
}
