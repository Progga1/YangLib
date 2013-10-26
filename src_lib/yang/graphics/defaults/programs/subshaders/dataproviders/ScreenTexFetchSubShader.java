package yang.graphics.defaults.programs.subshaders.dataproviders;

public class ScreenTexFetchSubShader extends TextureFetchSubShader {

	public ScreenTexFetchSubShader(float blur,String shift) {
		super("screenTexCl","screenTexCoords"+(shift!=null?" + "+shift:""),blur);
	}



}
