package yang.android.graphics;

import yang.graphics.events.Keys;
import yang.graphics.events.eventtypes.AbstractKeyEvent;
import yang.graphics.translator.GraphicsTranslator;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class KeyTouchSurface extends AndroidTouchSurface implements OnKeyListener {

	private EditText mEditText;
	
	public KeyTouchSurface(Context context, EditText editText) {
		super(context);
		
		mEditText = editText;
		setKeepScreenOn(true);
	}
	
	public void onBackPressed() {
		if (mEventQueue!=null) {
			mEventQueue.putKeyEvent(Keys.ESC, AbstractKeyEvent.ACTION_KEYDOWN);
			mEventQueue.putKeyEvent(Keys.ESC, AbstractKeyEvent.ACTION_KEYUP);
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}

	public boolean onKey(View v, int keyCode, KeyEvent event) {		
		if ((keyCode == KeyEvent.KEYCODE_SEARCH && event.getAction() == KeyEvent.ACTION_UP)
				|| (keyCode == KeyEvent.KEYCODE_MENU && event.isLongPress())) {
			InputMethodManager inputMgr = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMgr.toggleSoftInput(0, 0);
			return true;
		}
		
		
		if (keyCode == KeyEvent.KEYCODE_ENTER) {
			if (mEditText.getText().length() == 1) {
				mEventQueue.putKeyEvent(mEditText.getText().charAt(0),AbstractKeyEvent.ACTION_KEYDOWN);
				mEventQueue.putKeyEvent(mEditText.getText().charAt(0),AbstractKeyEvent.ACTION_KEYUP);
			}
			mEditText.setText("");
		}
		return false;
	}

	public GraphicsTranslator getGraphics() {
		return mSceneRenderer.getGraphics();
	}
}
