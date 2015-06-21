package yang.android.graphics;

import yang.events.Keys;
import yang.events.eventtypes.YangKeyEvent;
import yang.graphics.translator.GraphicsTranslator;
import yang.model.App;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class YangKeyTouchSurface extends YangTouchSurface implements OnKeyListener {

	public YangKeyTouchSurface(YangActivity activity) {
		super(activity);
		setFocusable(true);
		setFocusableInTouchMode(true);
		setOnKeyListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {

		if ((keyCode == KeyEvent.KEYCODE_SEARCH && event.getAction() == KeyEvent.ACTION_UP) || (keyCode == KeyEvent.KEYCODE_MENU && event.isLongPress()) || (keyCode == KeyEvent.KEYCODE_BACK && event.isLongPress())) {
			App.systemCalls.openKeyBoard();
			return true;
		}

		//down or up
		final int action = event.getAction()==KeyEvent.ACTION_DOWN?YangKeyEvent.ACTION_KEYDOWN:YangKeyEvent.ACTION_KEYUP;

		if (event.isPrintingKey()) {
			mEventQueue.putKeyEvent((char)event.getUnicodeChar(),action);
		} else {
			//enter and such
			switch (keyCode) {
				case KeyEvent.KEYCODE_DEL: mEventQueue.putKeyEvent(Keys.BACKSPACE,action);	break;
				case KeyEvent.KEYCODE_ENTER: mEventQueue.putKeyEvent(Keys.ENTER,action);	break;
			}
		}

		return false;
	}

	public GraphicsTranslator getGraphics() {
		return mSceneRenderer.getGraphics();
	}


	@Override
	public boolean onCheckIsTextEditor() {
		//needed for softkeyboard input
		return true;
	}

	@Override
	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
		//needed for softkeyboard input
		outAttrs.imeOptions |= EditorInfo.IME_FLAG_NO_EXTRACT_UI |     EditorInfo.IME_ACTION_NONE;
		outAttrs.actionLabel = null;
		outAttrs.initialCapsMode = 0;
		outAttrs.initialSelEnd = outAttrs.initialSelStart = -1;
		return new BaseInputConnection(this, false);
	}
}
