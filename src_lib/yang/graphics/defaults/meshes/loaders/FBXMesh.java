package yang.graphics.defaults.meshes.loaders;

import yang.graphics.textures.TextureProperties;
import yang.graphics.translator.AbstractGraphics;
import yang.util.filereader.TokenReader;

public class FBXMesh extends YangMesh {

	public FBXMesh(AbstractGraphics<?> graphics, MeshMaterialHandles handles, TextureProperties textureProperties) {
		super(graphics, handles, textureProperties);
	}

	protected void startLoading() {

	}

	protected boolean keyword(TokenReader reader) {

		return false;
	}

	protected void finishLoading() {

	}

}
