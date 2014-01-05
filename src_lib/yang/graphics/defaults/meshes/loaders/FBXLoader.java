package yang.graphics.defaults.meshes.loaders;

import java.io.IOException;
import java.io.InputStream;

import yang.graphics.defaults.meshes.armature.YangArmature;
import yang.graphics.translator.AbstractGFXLoader;
import yang.graphics.translator.AbstractGraphics;
import yang.util.YangList;
import yang.util.filereader.TokenReader;

public class FBXLoader {

	public YangList<YangMesh> mMeshes = new YangList<YangMesh>();
	public YangList<YangArmature> mArmatures = new YangList<YangArmature>();

	public AbstractGraphics<?> mGraphics;
	public MeshMaterialHandles mHandles;

	private TokenReader mReader;
	private AbstractGFXLoader mGFXLoader;

	public FBXLoader(AbstractGraphics<?> graphics, MeshMaterialHandles handles) {
		mGraphics = graphics;
		mHandles = handles;
	}

	public void readObjects() throws IOException {
		final char[] chars = mReader.mCharBuffer;
		while(!mReader.eof()) {
			mReader.nextWord(false);
			if(mReader.isWord("Model")) {

			}

		}
	}

	public boolean load(String filename,AbstractGFXLoader gfxLoader) throws IOException {
		mGFXLoader = gfxLoader;
		InputStream stream = gfxLoader.mResources.getAssetInputStream(filename);
		if(stream==null)
			return false;
		mReader = new TokenReader(stream);
		mReader.setLineCommentChars(";");
		mReader.mAutoSkipComments = true;

		mMeshes.clear();
		mArmatures.clear();

		final char[] chars = mReader.mCharBuffer;
		while(!mReader.eof()) {
			mReader.nextWord(false);
			if(mReader.isWord("Object")) {
				readObjects();
			}

		}

		return true;
	}

}
