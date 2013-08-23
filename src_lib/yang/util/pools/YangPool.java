package yang.util.pools;

public class YangPool<ElemType> extends AbstractYangPool<ElemType> {
	
	private Class<ElemType> mClass;
	
	public YangPool(int capacity,Class<ElemType> elemClass) {
		mClass = elemClass;
		recreate(capacity);
	}
	
	public ElemType createElement() {
		try {
			return mClass.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
}
