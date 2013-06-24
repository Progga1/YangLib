package yang.pc;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import yang.events.EventQueueHolder;
import yang.events.YangEventQueue;
import yang.events.Keys;
import yang.events.eventtypes.YangKeyEvent;
import yang.events.eventtypes.YangPointerEvent;

public class PCEventHandler  implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
	
	private EventQueueHolder mEventListener;
	private YangEventQueue mEventQueue;
	
	public PCEventHandler(EventQueueHolder eventListener) {
		this.mEventListener = eventListener;
		mEventQueue = eventListener.getEventQueue();
	}
	
	private void putKeyEvent(KeyEvent event,int action) {
		int key;
		switch (event.getKeyCode()) {
			case KeyEvent.VK_ALT:			key = Keys.ALT;			break;
			case KeyEvent.VK_CONTROL:		key = Keys.CTRL;			break;
			case KeyEvent.VK_DOWN:			key = Keys.DOWN;			break;
			case KeyEvent.VK_UP:			key = Keys.UP;				break;
			case KeyEvent.VK_RIGHT:			key = Keys.RIGHT;			break;
			case KeyEvent.VK_LEFT:			key = Keys.LEFT;			break;
			case KeyEvent.VK_ESCAPE:		key = Keys.ESC;			break;
			case KeyEvent.VK_F1:			key = Keys.F1;				break;
			case KeyEvent.VK_F2:			key = Keys.F2;				break;
			case KeyEvent.VK_F3:			key = Keys.F3;				break;
			case KeyEvent.VK_F4:			key = Keys.F4;				break;
			case KeyEvent.VK_F5:			key = Keys.F5;				break;
			case KeyEvent.VK_F6:			key = Keys.F6;				break;
			case KeyEvent.VK_F7:			key = Keys.F7;				break;
			case KeyEvent.VK_F8:			key = Keys.F8;				break;
			case KeyEvent.VK_F9:			key = Keys.F9;				break;
			case KeyEvent.VK_F10:			key = Keys.F10;			break;
			case KeyEvent.VK_F11:			key = Keys.F11;			break;
			case KeyEvent.VK_F12:			key = Keys.F12;			break;
			case KeyEvent.VK_TAB:			key = Keys.TAB;			break;
			case KeyEvent.VK_ENTER:			key = Keys.ENTER;			break;
			case KeyEvent.VK_BACK_SPACE:	key = Keys.BACKSPACE;		break;
			default:						key = event.getKeyChar();	break;
		}
		mEventQueue.putKeyEvent(key, action);
	}
	
	@Override
	public void keyPressed(KeyEvent event) {
		putKeyEvent(event,YangKeyEvent.ACTION_KEYDOWN);
	}

	@Override
	public void keyReleased(KeyEvent event) {
		putKeyEvent(event,YangKeyEvent.ACTION_KEYUP);
	}

	@Override
	public void keyTyped(KeyEvent event) {}

	@Override
	public void mouseClicked(MouseEvent event) {}

	@Override
	public void mouseEntered(MouseEvent event) {}

	@Override
	public void mouseExited(MouseEvent event) {}

	public void putPointerEvent(int action,MouseEvent event) {
		int button;
		if(action==YangPointerEvent.ACTION_POINTERMOVE) {
			button = YangPointerEvent.BUTTON_NONE;
		}else{
			if((event.getModifiers() & MouseEvent.BUTTON3_MASK) != 0)
				button = YangPointerEvent.BUTTON_RIGHT;
			else if((event.getModifiers() & MouseEvent.BUTTON2_MASK) != 0)
				button = YangPointerEvent.BUTTON_MIDDLE;
			else
				button = YangPointerEvent.BUTTON_LEFT;
		}
		mEventQueue.putPointerEvent(button, event.getX(), event.getY(), action, 0);
	}
	
	@Override
	public void mousePressed(MouseEvent event) {
		putPointerEvent(YangPointerEvent.ACTION_POINTERDOWN,event);
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		putPointerEvent(YangPointerEvent.ACTION_POINTERUP,event);
	}
	
	@Override
	public void mouseDragged(MouseEvent event) {
		putPointerEvent(YangPointerEvent.ACTION_POINTERDRAG,event);
	}

	@Override
	public void mouseMoved(MouseEvent event) {
		putPointerEvent(YangPointerEvent.ACTION_POINTERMOVE,event);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent event) {
		mEventQueue.putZoomEvent(event.getWheelRotation()*0.2f);
	}
	
}
