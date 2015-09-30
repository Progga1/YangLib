package yang.graphics.translator;

public interface GLHolder {

	public void setProperties(String title,boolean undecorated,boolean alwaysOnTop);
	public void setBounds(int x,int y,int width,int height);
	public void setFullscreen(int screenId);
	public void setVisible(boolean visible);
	public void setFramed();
	public void run();

}
