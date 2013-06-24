package yang.graphics.font;

import java.util.HashMap;

import yang.graphics.model.FloatColor;

public class StringStyle {

	public FloatColor[] mPalette;
	public HashMap<String,Integer> mColorHash;
	protected int mColorCount;
	
	public StringStyle() {
		
	}
	
	public void initPalette(int capacity) {
		mPalette = new FloatColor[capacity];
		for(int i=0;i<capacity;i++) {
			mPalette[i] = FloatColor.WHITE.clone();
		}
		mColorHash = new HashMap<String,Integer>(capacity);
	}
	
	public void setPalette(FloatColor... colors) {
		mPalette = colors;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public StringStyle clone() {
		StringStyle result = new StringStyle();
		result.initPalette(mPalette.length);
		for(int i=0;i<mPalette.length;i++)
			result.mPalette[i].set(mPalette[i]);
		result.mColorHash = (HashMap<String,Integer>)mColorHash.clone();
		return result;
	}
	
	public int addColor(FloatColor color) {
		mPalette[mColorCount] = color;
		return mColorCount++;
	}

	public int addColor(String key, FloatColor color) {
		Integer prev = mColorHash.get(key.toUpperCase());
		if(prev==null) {
			int id = addColor(color);
			mColorHash.put(key, id);
			return id;
		}else{
			mColorHash.put(key, prev);
			return prev;
		}
	}

	public FloatColor getColorByKey(String key) {
		int id = mColorHash.get(key.toUpperCase());
		if(id<0 || id>=mPalette.length)
			return null;
		else
			return mPalette[id];
	}
	
}
