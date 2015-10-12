package yang.graphics.translator;

public interface TextureDisplay {

	public GLHolder getGLHolder();
	public Texture getTexture();
	public void setTexture(Texture texture);
	public void setFlipY(boolean flipY);

}
