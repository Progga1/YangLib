package yang.util.pools;

public class YangRingBuffer<ElemType> extends AbstractYangRingBuffer<ElemType> {

	private Class<ElemType> mClass;

	public YangRingBuffer(int capacity,Class<ElemType> elemClass) {
		mClass = elemClass;
		recreate(capacity);
	}

	@Override
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
