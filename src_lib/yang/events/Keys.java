package yang.events;

public class Keys {

	private static final int OFFSET = 300;

	public static final int F1			= 1+OFFSET;
	public static final int F2			= 2+OFFSET;
	public static final int F3			= 3+OFFSET;
	public static final int F4			= 4+OFFSET;
	public static final int F5			= 5+OFFSET;
	public static final int F6			= 6+OFFSET;
	public static final int F7			= 7+OFFSET;
	public static final int F8			= 8+OFFSET;
	public static final int F9			= 9+OFFSET;
	public static final int F10			= 10+OFFSET;
	public static final int F11			= 11+OFFSET;
	public static final int F12			= 12+OFFSET;

	public static final int ESC			= 13+OFFSET;
	public static final int CTRL		= 14+OFFSET;

	public static final int LEFT		= 15+OFFSET;
	public static final int RIGHT		= 16+OFFSET;
	public static final int DOWN		= 17+OFFSET;
	public static final int UP			= 18+OFFSET;

	public static final int TAB			= 19+OFFSET;
	public static final int ENTER		= 20+OFFSET;
	public static final int BACKSPACE	= 21+OFFSET;

	public static final int ALT			= 22+OFFSET;

	public static final int SHIFT       = 65535;

	public static String toString(int keyCode) {
		if(keyCode>=F1 && keyCode<=F12) {
			return "F"+(keyCode-OFFSET);
		}
		switch(keyCode) {
		case Keys.LEFT: return "Left";
		case Keys.RIGHT: return "Right";
		case Keys.UP: return "Up";
		case Keys.DOWN: return "Down";
		case Keys.ESC: return "Esc";
		case Keys.CTRL: return "Ctrl";
		case Keys.SHIFT: return "Shift";
		case Keys.TAB: return "Tab";
		case Keys.ENTER: return "Enter";
		case Keys.BACKSPACE: return "Backspace";
		case Keys.ALT: return "Alt";
		default: return Character.toString((char)keyCode);
		}
	}
}
