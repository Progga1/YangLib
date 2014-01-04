package yang.graphics.defaults.meshes.loaders;

import java.io.InputStream;

import yang.graphics.translator.AbstractGFXLoader;
import yang.util.YangList;
import yang.util.filereader.TokenReader;

public class FBXLoader {

	public YangList<YangMesh> mMeshes = new YangList<YangMesh>();

	private TokenReader mReader;
	private AbstractGFXLoader mGFXLoader;

	public FBXLoader() {

	}

	public boolean load(String filename,AbstractGFXLoader gfxLoader) {
		mGFXLoader = gfxLoader;
		InputStream stream = gfxLoader.mResources.getAssetInputStream(filename);
		if(stream==null)
			return false;
		mReader = new TokenReader(stream);



		return true;
	}

}
