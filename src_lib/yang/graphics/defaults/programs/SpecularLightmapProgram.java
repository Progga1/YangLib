package yang.graphics.defaults.programs;

import yang.graphics.AbstractGFXLoader;

public class SpecularLightmapProgram extends LightmapProgram{
	
	@Override
	protected String getVertexShader(AbstractGFXLoader gfxLoader) {
		return gfxLoader.getShader("specular_lightmap_vertex");
	}

	@Override
	protected String getFragmentShader(AbstractGFXLoader gfxLoader) {
		return gfxLoader.getShader("specular_lightmap_fragment");
	}
	
}
