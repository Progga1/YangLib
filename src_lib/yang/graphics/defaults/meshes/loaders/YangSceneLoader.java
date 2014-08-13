package yang.graphics.defaults.meshes.loaders;

import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.model.material.YangMaterial;
import yang.graphics.model.material.YangMaterialSet;
import yang.graphics.textures.TextureProperties;
import yang.graphics.translator.AbstractGFXLoader;

public class YangSceneLoader {

	public static YangMaterial DEFAULT_MATERIAL = new YangMaterial();

	public static int MAX_VERTICES = Short.MAX_VALUE;
	protected static float[] workingPositions;
	protected static float[] workingNormals;
	protected static float[] workingTexCoords;
	protected static short[] workingIndices;
	protected static short[] edgeIndices;
	protected static int[] redirectIndices;
	protected static int[] positionIndices;
	protected static int[] texCoordIndices;
	protected static int[] normalIndices;
	protected static int[] smoothIndices;

	public String mName = null;

	//OBJECTS
	public TextureProperties mTextureProperties;
	public DefaultGraphics<?> mGraphics;
	public MeshMaterialHandles mHandles;
	public AbstractGFXLoader mGFXLoader;

	//STATE
	protected YangMesh mCurrentMesh;
	protected int mVertexCount;
	protected int mIndexId = 0;
	protected int mEdgeIndexId = 0;
	protected int posId;
	protected int texId;
	protected int normId;
	protected int curSmoothGroup;
	protected YangMaterialSection currentMatSec;
	private TextureProperties prevTexProps;

	public YangSceneLoader(DefaultGraphics<?> graphics,MeshMaterialHandles handles,TextureProperties textureProperties) {
		if(workingPositions==null) {
			workingPositions = new float[MAX_VERTICES*3];
			workingTexCoords = new float[MAX_VERTICES*2];
			workingNormals = new float[MAX_VERTICES*3];
			workingIndices = new short[MAX_VERTICES*4];
			edgeIndices = new short[MAX_VERTICES*4];
			redirectIndices = new int[workingIndices.length];
			positionIndices = new int[MAX_VERTICES];
			texCoordIndices = new int[MAX_VERTICES];
			normalIndices = new int[MAX_VERTICES];
			smoothIndices = new int[workingIndices.length];
		}

		mGraphics = graphics;
		mHandles = handles;
		mTextureProperties = textureProperties;
		mGFXLoader = graphics.mTranslator.mGFXLoader;
	}

	protected int copyVertex(int index) {
		redirectIndices[index] = mVertexCount;

		redirectIndices[mVertexCount] = -1;
		smoothIndices[mVertexCount] = curSmoothGroup;

		return mVertexCount++;
	}

	protected int addIndex(int posIndex,int texIndex,int normIndex) {
		int index = posIndex;
		while((smoothIndices[index]!=Integer.MIN_VALUE && (curSmoothGroup==-1 || curSmoothGroup!=smoothIndices[index])) || (texCoordIndices[index]>=0 && texCoordIndices[index]!=texIndex)) {
			final int redirect = redirectIndices[index];
			if(redirect<0) {
				copyVertex(index);
				index = mVertexCount-1;
				break;
			}
			index = redirect;
		}
		positionIndices[index] = posIndex;
		texCoordIndices[index] = texIndex;
		normalIndices[index] = normIndex;
		smoothIndices[index] = curSmoothGroup;
		workingIndices[mIndexId++] = (short)(index);
		return index;
	}

	public YangSceneLoader(DefaultGraphics<?> graphics,MeshMaterialHandles handles) {
		this(graphics,handles,null);
	}

	protected void startLoadingMesh(YangMesh currentMesh) {
		mVertexCount = 0;
		posId = 0;
		texId = 0;
		normId = 0;
		mIndexId = 0;
		positionIndices[0] = -1;
		normalIndices[0] = -1;
		curSmoothGroup = -1;

		mCurrentMesh = currentMesh;
		currentMatSec = new YangMaterialSection(0,0,new YangMaterial());
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

		currentMatSec.mEndIndex = mIndexId;
		currentMatSec.mEdgeEndIndex = mEdgeIndexId;

		mCurrentMesh.mVertexCount = mVertexCount;
		mCurrentMesh.mPositions = new float[posId];
		mCurrentMesh.mPosIndices = new int[mVertexCount];
		System.arraycopy(workingPositions, 0, mCurrentMesh.mPositions, 0, posId);
		if(positionIndices[0]!=-1)
			System.arraycopy(positionIndices, 0, mCurrentMesh.mPosIndices, 0, mVertexCount);
		else{
			for(int i=0;i<mVertexCount;i++) {
				mCurrentMesh.mPosIndices[i] = i;
			}
		}

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
			if(normalIndices[0]!=-1)
				System.arraycopy(normalIndices, 0, mCurrentMesh.mNormIndices, 0, mVertexCount);
			else{
				for(int i=0;i<mVertexCount;i++) {
					mCurrentMesh.mNormIndices[i] = i;
				}
			}
		}

		mCurrentMesh.mTriangleIndices = new short[mIndexId];
		System.arraycopy(workingIndices, 0, mCurrentMesh.mTriangleIndices, 0, mIndexId);
		if(mEdgeIndexId>0) {
			mCurrentMesh.mEdgeIndices = new short[mEdgeIndexId];
			System.arraycopy(edgeIndices, 0, mCurrentMesh.mEdgeIndices, 0, mEdgeIndexId);
		}
		mCurrentMesh.mSmoothIndices = new int[mCurrentMesh.mTriangleIndices.length];
		System.arraycopy(smoothIndices, 0, mCurrentMesh.mSmoothIndices, 0, mCurrentMesh.mSmoothIndices.length);
		mCurrentMesh.mRedirectIndices = new int[mCurrentMesh.mTriangleIndices.length];
		System.arraycopy(redirectIndices, 0, mCurrentMesh.mRedirectIndices, 0, mCurrentMesh.mRedirectIndices.length);

		mCurrentMesh.mIndexCount = mIndexId;
		mCurrentMesh.creationFinished();

		if(calcNormals)
			mCurrentMesh.calculateNormals();

		if(staticMesh) {
			mCurrentMesh.createDrawBatch(true);
		}

		YangMaterialSet.diffuseTextureProperties = prevTexProps;

		return mCurrentMesh;
	}

}
