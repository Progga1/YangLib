package yang.graphics.defaults.meshes.loaders;

import yang.graphics.model.material.YangMaterial;
import yang.graphics.model.material.YangMaterialProvider;
import yang.graphics.model.material.YangMaterialSet;
import yang.graphics.textures.TextureProperties;
import yang.graphics.translator.AbstractGraphics;

public class YangSceneLoader {

	public static YangMaterial DEFAULT_MATERIAL = new YangMaterial();

	public static int MAX_VERTICES = 200000;
	protected static float[] workingPositions;
	protected static float[] workingNormals;
	protected static float[] workingTexCoords;
	protected static short[] workingIndices;
	protected static int[] redirectIndices;
	protected static int[] positionIndices;
	protected static int[] texCoordIndices;
	protected static int[] normalIndices;
	protected static int[] smoothIndices;

	//OBJECTS
	public TextureProperties mTextureProperties;
	public AbstractGraphics<?> mGraphics;
	public MeshMaterialHandles mHandles;
	public YangMaterialProvider mMaterialProvider;

	//STATE
	protected YangMesh mCurrentMesh;
	protected int mVertexCount;
	protected int mIndexId = 0;
	protected int posId;
	protected int texId;
	protected int normId;
	protected YangMaterialSection currentMatSec;
	private TextureProperties prevTexProps;

	public YangSceneLoader(AbstractGraphics<?> graphics,MeshMaterialHandles handles,TextureProperties textureProperties) {
		if(workingPositions==null) {
			workingPositions = new float[MAX_VERTICES*3];
			workingTexCoords = new float[MAX_VERTICES*2];
			workingNormals = new float[MAX_VERTICES*3];
			workingIndices = new short[MAX_VERTICES*2];
			redirectIndices = new int[MAX_VERTICES];
			positionIndices = new int[MAX_VERTICES];
			texCoordIndices = new int[MAX_VERTICES];
			normalIndices = new int[MAX_VERTICES];
			smoothIndices = new int[MAX_VERTICES];
		}

		mGraphics = graphics;
		mHandles = handles;
		mTextureProperties = textureProperties;
		mMaterialProvider = graphics.mTranslator.mGFXLoader;
	}

	public YangSceneLoader(AbstractGraphics<?> graphics,MeshMaterialHandles handles) {
		this(graphics,handles,null);
	}

	protected void startLoadingMesh(YangMesh currentMesh) {
		mVertexCount = 0;
		posId = 0;
		texId = 0;
		normId = 0;
		mIndexId = 0;

		mCurrentMesh = currentMesh;
		currentMatSec = new YangMaterialSection(0,DEFAULT_MATERIAL);
		currentMesh.mMaterialSections.clear();
		currentMesh.mMaterialSections.add(currentMatSec);
		currentMesh.mDrawBatch = null;
	}

	protected YangMesh startLoadingMesh() {

		prevTexProps = YangMaterialSet.diffuseTextureProperties;
		if(mTextureProperties!=null)
			YangMaterialSet.diffuseTextureProperties = mTextureProperties;

		YangMesh mesh = new YangMesh(mGraphics,mHandles,mTextureProperties);
		startLoadingMesh(mesh);
		return mesh;
	}

	protected YangMesh finishLoadingMesh(boolean calcNormals,boolean staticMesh) {

		mCurrentMesh.mVertexCount = mVertexCount;
		mCurrentMesh.mPositions = new float[posId];
		mCurrentMesh.mPosIndices = new int[mVertexCount];
		System.arraycopy(workingPositions, 0, mCurrentMesh.mPositions, 0, posId);
		System.arraycopy(positionIndices, 0, mCurrentMesh.mPosIndices, 0, mVertexCount);

		if(texId>0) {
			mCurrentMesh.mTexCoords = new float[texId];
			mCurrentMesh.mTexCoordIndices = new int[mVertexCount];
			System.arraycopy(workingTexCoords, 0, mCurrentMesh.mTexCoords, 0, texId);
			System.arraycopy(texCoordIndices, 0, mCurrentMesh.mTexCoordIndices, 0, mVertexCount);
		}
		if(normId>0) {
			mCurrentMesh.mNormals = new float[normId];
			mCurrentMesh.mNormIndices = new int[mVertexCount];
			System.arraycopy(workingNormals, 0, mCurrentMesh.mNormals, 0, normId);
			System.arraycopy(normalIndices, 0, mCurrentMesh.mNormIndices, 0, mVertexCount);
		}

		mCurrentMesh.mIndices = new short[mIndexId];
		System.arraycopy(workingIndices, 0, mCurrentMesh.mIndices, 0, mIndexId);
		mCurrentMesh.mSmoothIndices = new int[mCurrentMesh.mIndices.length];
		System.arraycopy(smoothIndices, 0, mCurrentMesh.mSmoothIndices, 0, mCurrentMesh.mSmoothIndices.length);
		mCurrentMesh.mRedirectIndices = new int[mCurrentMesh.mIndices.length];
		System.arraycopy(redirectIndices, 0, mCurrentMesh.mRedirectIndices, 0, mCurrentMesh.mRedirectIndices.length);

		mCurrentMesh.mIndexCount = mIndexId;

		if(calcNormals)
			mCurrentMesh.calculateNormals();

		if(staticMesh) {
			mCurrentMesh.createDrawBatch(true);
		}

		YangMaterialSet.diffuseTextureProperties = prevTexProps;

		return mCurrentMesh;
	}

}
