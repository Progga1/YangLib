package yang.graphics.translator;

import yang.graphics.programs.BasicProgram;
import yang.math.objects.YangMatrix;

public interface TextureDisplay {

	public GLHolder getGLHolder();
	public Texture getTexture();
	public void setTexture(Texture texture);
	public void setFlipY(boolean flipY);
	public void setShader(BasicProgram shader);
	public void setDefaultShader();
	public void setProjectionMatrix(YangMatrix matrix);
	public TextureDisplay show();

}
