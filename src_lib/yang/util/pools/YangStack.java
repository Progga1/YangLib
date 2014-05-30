package yang.util.pools;

public class YangStack<ElementType>{

	public ElementType[] mElements;
	public int mElementCount;

	public YangStack(ElementType[] elementArray,Class<ElementType> elementClass) {
		mElements = elementArray;
		for(int i=0;i<elementArray.length;i++) {
			try {
				mElements[i] = elementClass.newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		clear();
	}

	public YangStack(ElementType[] elementArray) {
		mElements = elementArray;
		clear();
	}

	public void clear() {
		mElementCount = 0;
	}

	public ElementType allocAndPush() {
		return mElements[mElementCount++];
	}

	public ElementType pop() {
		return mElements[--mElementCount];
	}

	public boolean isEmpty() {
		return mElementCount==0;
	}

}
