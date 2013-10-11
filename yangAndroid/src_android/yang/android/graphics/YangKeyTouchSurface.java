package yang.android.graphics;

import yang.events.Keys;
import yang.events.eventtypes.YangKeyEvent;
import yang.graphics.translator.GraphicsTranslator;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;

public class YangKeyTouchSurface extends YangTouchSurface implements OnKeyListener {

	public YangKeyTouchSurface(Context context) {
		super(context);
		setFocusable(true);
		setFocusableInTouchMode(true);
		setOnKeyListener(this);
		setKeepScreenOn(true);
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

		if ((keyCode == KeyEvent.KEYCODE_SEARCH && event.getAction() == KeyEvent.ACTION_UP) || (keyCode == KeyEvent.KEYCODE_MENU && event.isLongPress())) {
			InputMethodManager inputMgr = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMgr.showSoftInput(this, InputMethodManager.SHOW_FORCED);
			return true;
		}

		//down or up
		int action = event.getAction()==KeyEvent.ACTION_DOWN?YangKeyEvent.ACTION_KEYDOWN:YangKeyEvent.ACTION_KEYUP;

		if (event.isPrintingKey()) {
			mEventQueue.putKeyEvent((char)event.getUnicodeChar(),action);
		} else {
			//enter and such
			switch (keyCode) {
				case KeyEvent.KEYCODE_DEL: mEventQueue.putKeyEvent(Keys.BACKSPACE,action);	break;
				case KeyEvent.KEYCODE_ENTER: mEventQueue.putKeyEvent(Keys.ENTER,action);	break;
				//TODO more events
			}
		}


//		if (keyCode == KeyEvent.KEYCODE_ENTER) {
//			if (mEditText.getText().length() == 1) {
//				mEventQueue.putKeyEvent(mEditText.getText().charAt(0),YangKeyEvent.ACTION_KEYDOWN);
//				mEventQueue.putKeyEvent(mEditText.getText().charAt(0),YangKeyEvent.ACTION_KEYUP);
//			} else if (mEditText.getText().length() > 1) {
//
//				//fkey parser
//				String text = mEditText.getText().toString();
//				boolean fKey = text.charAt(0) == 'f';
//				if (fKey) {
//					int num = -1;
//					try {
//						num = Integer.parseInt(text.substring(1, text.length()))-1;
//						mEventQueue.putKeyEvent(Keys.F1+num,YangKeyEvent.ACTION_KEYDOWN);
//						mEventQueue.putKeyEvent(Keys.F1+num,YangKeyEvent.ACTION_KEYUP);
//					} catch (Exception e) {
//
//					}
//				}
//			}
//			mEditText.setText("");
//		}
		return false;
	}

	public GraphicsTranslator getGraphics() {
		return mSceneRenderer.getGraphics();
	}
}
