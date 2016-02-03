package yang.graphics.translator;

public interface DisplayMouseListener {

	public void displayMouseDown(GLHolder sender,float x,float y,int button);
	public void displayMouseMove(GLHolder sender,float x,float y);
	public void displayMouseDrag(GLHolder sender,float x,float y,int button);
	public void displayMouseUp(GLHolder sender,float x,float y,int button);
	public void displayMouseWheel(GLHolder sender,int scrollAmount);
	public void displayMouseEnter(GLHolder sender);
	public void displayMouseExit(GLHolder sender);

}
