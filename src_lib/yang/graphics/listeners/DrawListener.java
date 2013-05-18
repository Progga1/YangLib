package yang.graphics.listeners;

public interface DrawListener {

	public void onRestartGraphics();
	public void onPreDraw();
	public void bindBuffers();
	public void enableBuffers();
	public void disableBuffers();
	
}
