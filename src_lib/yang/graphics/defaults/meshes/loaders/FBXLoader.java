package yang.graphics.defaults.meshes.loaders;

import java.io.IOException;
import java.io.InputStream;

import yang.graphics.defaults.meshes.armature.YangArmature;
import yang.graphics.translator.AbstractGFXLoader;
import yang.graphics.translator.AbstractGraphics;
import yang.util.YangList;
import yang.util.filereader.TokenReader;
import yang.util.filereader.UnexpectedTokenException;

public class FBXLoader {

	public YangList<YangMesh> mMeshes = new YangList<YangMesh>();
	public YangList<YangArmature> mArmatures = new YangList<YangArmature>();

	public AbstractGraphics<?> mGraphics;
	public MeshMaterialHandles mHandles;

	private TokenReader mReader;
	private char[] mChars;
	private AbstractGFXLoader mGFXLoader;

	public FBXLoader(AbstractGraphics<?> graphics, MeshMaterialHandles handles) {
		mGraphics = graphics;
		mHandles = handles;
	}

	public void readObjects() throws IOException {
		while(!mReader.eof()) {
			mReader.nextWord(true);
			if(mReader.isWord("Model")) {
				mReader.nextWord(true);
				System.out.println(mReader.wordToString());
			}

		}
	}

	public boolean load(String filename,AbstractGFXLoader gfxLoader) throws IOException, UnexpectedTokenException {
		mGFXLoader = gfxLoader;
		InputStream stream = gfxLoader.mResources.getAssetInputStream(filename);
		if(stream==null)
			return false;
		mReader = new TokenReader(stream);
		mChars = mReader.mCharBuffer;
		mReader.setLineCommentChars(";");
		mReader.mAutoSkipComments = true;
		mReader.mWordBreakers[':'] = true;
		mReader.mWordBreakers[','] = true;

		mMeshes.clear();
		mArmatures.clear();

		while(!mReader.eof()) {
			mReader.nextWord(true);
			if(mReader.isWord("Objects")) {
				mReader.expect("{");
				readObjects();
			}

		}

		return true;
	}

}
