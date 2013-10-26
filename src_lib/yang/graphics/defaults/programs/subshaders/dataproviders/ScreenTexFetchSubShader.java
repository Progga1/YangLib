package yang.graphics.defaults.programs.subshaders.dataproviders;

public class ScreenTexFetchSubShader extends TextureFetchSubShader {

	public ScreenTexFetchSubShader() {
		super("vec2(screenPos.x/screenPos.w*0.5+0.5,screenPos.y/screenPos.w*0.5+0.5)");
	}



}
