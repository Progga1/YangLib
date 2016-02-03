package yang.graphics.translator;

public abstract class GLHolder {

	protected DisplayListener mDisplayListener = null;
	protected DisplayMouseListener mMouseListener = null;

	public abstract void setTitle(String title);
	public abstract void setAlwaysOnTop(boolean alwaysOnTop);
	public abstract void setSize(int width,int height);
	public abstract void setLocation(int x,int y);
	public abstract void setCentered();
	public abstract void requestFocus();
	public abstract void setFullscreen(int screenId);
	public abstract void setVisible(boolean visible);
	public abstract void setFramed(boolean undecorated);
	public abstract boolean isFramed();
	public abstract void run();

	public void setDisplayListener(DisplayListener listener) {
		mDisplayListener = listener;
	}

	public void setMouseListener(DisplayMouseListener listener) {
		mMouseListener = listener;
	}

	public void setBounds(int x,int y,int width,int height) {
		setLocation(x,y);
		setSize(width,height);
	}
}
