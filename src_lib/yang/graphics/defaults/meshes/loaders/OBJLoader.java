package yang.graphics.defaults.meshes.loaders;

import java.io.IOException;
import java.io.InputStream;

import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.model.material.YangMaterial;
import yang.graphics.model.material.YangMaterialProvider;
import yang.graphics.model.material.YangMaterialSet;
import yang.graphics.textures.TextureProperties;
import yang.math.objects.matrix.YangMatrix;
import yang.util.filereader.TokenReader;

public class OBJLoader extends YangSceneLoader {

	private static final String[] KEYWORDS = {"mtllib","usemtl"};

	protected int mVertexCount;
	protected int mIndexId = 0;
	protected int curSmoothGroup;
	protected int posId;
	protected int texId;
	protected int normId;

	public YangMesh mMesh;
	public TextureProperties mTextureProperties;
	public DefaultGraphics<?> mGraphics;
	public MeshMaterialHandles mHandles;
	public YangMaterialProvider mMaterialProvider;

	public OBJLoader(DefaultGraphics<?> graphics,MeshMaterialHandles handles,TextureProperties textureProperties) {
		super();
		mGraphics = graphics;
		mHandles = handles;
		mTextureProperties = textureProperties;
		mMaterialProvider = graphics.mTranslator.mGFXLoader;
	}

	public OBJLoader(DefaultGraphics<?> graphics,MeshMaterialHandles handles) {
		this(graphics,handles,null);
	}

	private void copyVertex(int index,int posIndex,int texIndex) {
		redirectIndices[index] = mVertexCount;

		redirectIndices[mVertexCount] = -1;
		smoothIndices[mVertexCount] = curSmoothGroup;

		mVertexCount++;
	}

	private void addIndex(int posIndex,int texIndex,int normIndex) {
		int index = posIndex;
		while((smoothIndices[index]!=Integer.MIN_VALUE && (curSmoothGroup==-1 || curSmoothGroup!=smoothIndices[index])) || (texCoordIndices[index]>=0 && texCoordIndices[index]!=texIndex)) {
			final int redirect = redirectIndices[index];
			if(redirect<0) {
				copyVertex(index,posIndex,texIndex);
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
	}

	public void loadOBJ(InputStream modelStream,YangMatrix transform,boolean useNormals,boolean staticMesh) throws IOException {
		if(modelStream==null)
			throw new RuntimeException("No stream given");
		final TextureProperties prevTexProps = YangMaterialSet.diffuseTextureProperties;
		if(mTextureProperties!=null)
			YangMaterialSet.diffuseTextureProperties = mTextureProperties;
		mModelReader = new TokenReader(modelStream);
		YangMaterialSection currentMatSec = new YangMaterialSection(0,DEFAULT_MATERIAL);

		//Reset mesh
		mMesh = new YangMesh(mGraphics,mHandles,mTextureProperties);
		mMesh.mMaterialSections.clear();
		mMesh.mMaterialSections.add(currentMatSec);
		mMesh.mDrawBatch = null;

		mVertexCount = 0;
		curSmoothGroup = -1;
		posId = 0;
		texId = 0;
		normId = 0;
		mIndexId = 0;

		final char[] chars = mModelReader.mCharBuffer;
		while(!mModelReader.eof()) {
			mModelReader.nextWord(true);
			final char fstC = chars[0];
			if(fstC=='#')
				mModelReader.toLineEnd();
			else{
				if(fstC=='\n') {

				}else{
					if(mModelReader.mWordLength==1) {
						//Single characters
						if(fstC=='v') {
							//Add vertex position
							final float posX = mModelReader.readFloat(false);
							final float posY = mModelReader.readFloat(false);
							final float posZ = mModelReader.readFloat(false);
							if(transform!=null) {
								transform.apply3D(posX, posY, posZ, workingPositions, posId);
								posId += 3;
							}else{
								workingPositions[posId++] = posX;
								workingPositions[posId++] = posY;
								workingPositions[posId++] = posZ;
							}
							redirectIndices[mVertexCount] = -1;
							positionIndices[mVertexCount] = -1;
							texCoordIndices[mVertexCount] = -1;
							normalIndices[mVertexCount] = -1;
							smoothIndices[mVertexCount] = Integer.MIN_VALUE;
							mVertexCount++;
						}else if(fstC=='f') {
							mModelReader.nextWord(false);
							final int baseInd = mModelReader.wordToInt(0,1)-1;
							final int baseTexInd = mModelReader.wordToInt(mModelReader.mNumberPos+1,1)-1;
							final int baseNormInd = mModelReader.wordToInt(mModelReader.mNumberPos+1, 1)-1;
							mModelReader.nextWord(false);
							int prevInd = mModelReader.wordToInt(0,1)-1;
							int prevTexInd = mModelReader.wordToInt(mModelReader.mNumberPos+1,1)-1;
							final int prevNormInd = mModelReader.wordToInt(mModelReader.mNumberPos+1, 1)-1;
							mModelReader.nextWord(false);
							int curInd = mModelReader.wordToInt(0,TokenReader.ERROR_INT);
							int curTexInd = mModelReader.wordToInt(mModelReader.mNumberPos+1,1)-1;
							int curNormInd = mModelReader.wordToInt(mModelReader.mNumberPos+1, 1)-1;
							while(curInd!=TokenReader.ERROR_INT) {
								curInd--;
								addIndex(baseInd,baseTexInd,baseNormInd);
								addIndex(prevInd,prevTexInd,prevNormInd);
								addIndex(curInd,curTexInd,curNormInd);

								//baseInd = prevInd;
								prevInd = curInd;
								prevTexInd = curTexInd;
								mModelReader.nextWord(false);
								curInd = mModelReader.wordToInt(0,TokenReader.ERROR_INT);
								curTexInd = mModelReader.wordToInt(mModelReader.mNumberPos+1,1)-1;
								curNormInd = mModelReader.wordToInt(mModelReader.mNumberPos+1,1)-1;
							}

						}else if(fstC=='s') {
							final int group = mModelReader.readInt(false);
							if(group==TokenReader.ERROR_INT)
								curSmoothGroup = -1;
							else
								curSmoothGroup = group;
						}
					}else if(mModelReader.mWordLength==2) {
						if(fstC=='v' && chars[1]=='t') {
							final float texU = mModelReader.readFloat(false);
							final float texV = mModelReader.readFloat(false);
							final float texW = mModelReader.readFloat(false);
							workingTexCoords[texId++] = texU;
							workingTexCoords[texId++] = -texV;
						}else if(fstC=='v' && chars[1]=='n') {
							final float normX = mModelReader.readFloat(false);
							final float normY = mModelReader.readFloat(false);
							final float normZ = mModelReader.readFloat(false);
							workingNormals[normId++] = normX;
							workingNormals[normId++] = normY;
							workingNormals[normId++] = normZ;
						}
					}else{
						//Keywords
						switch(mModelReader.pickWord(KEYWORDS)) {
						case 0:
							//mtllib
							final String filename = mModelReader.readString(false);
							final YangMaterialSet newMatSet = mMaterialProvider.getMaterialSet(filename);
							mMesh.mMaterialSets.add(newMatSet);
							break;
						case 1:
							//usemtl
							final String mtlKey = mModelReader.readString(false);
							final YangMaterial mat = mMesh.findMaterial(mtlKey);
							if(currentMatSec.mStartIndex==mIndexId) {
								currentMatSec.mMaterial = mat;
							}else{
								currentMatSec.mEndIndex = mIndexId;
								currentMatSec = new YangMaterialSection(mIndexId,mat);
								mMesh.mMaterialSections.add(currentMatSec);
							}
							break;
						}
					}
					mModelReader.toLineEnd();
				}
			}
		}

		currentMatSec.mEndIndex = mIndexId;

		mMesh.mVertexCount = mVertexCount;
		mMesh.mPositions = new float[posId];
		mMesh.mPosIndices = new int[mVertexCount];
		System.arraycopy(workingPositions, 0, mMesh.mPositions, 0, posId);
		System.arraycopy(positionIndices, 0, mMesh.mPosIndices, 0, mVertexCount);

		if(texId>0) {
			mMesh.mTexCoords = new float[texId];
			mMesh.mTexCoordIndices = new int[mVertexCount];
			System.arraycopy(workingTexCoords, 0, mMesh.mTexCoords, 0, texId);
			System.arraycopy(texCoordIndices, 0, mMesh.mTexCoordIndices, 0, mVertexCount);
		}
		if(normId>0) {
			mMesh.mNormals = new float[normId];
			mMesh.mNormIndices = new int[mVertexCount];
			System.arraycopy(workingNormals, 0, mMesh.mNormals, 0, normId);
			System.arraycopy(normalIndices, 0, mMesh.mNormIndices, 0, mVertexCount);
		}

		mMesh.mIndices = new short[mIndexId];
		System.arraycopy(workingIndices, 0, mMesh.mIndices, 0, mIndexId);
		mMesh.mSmoothIndices = new int[mMesh.mIndices.length];
		System.arraycopy(smoothIndices, 0, mMesh.mSmoothIndices, 0, mMesh.mSmoothIndices.length);
		mMesh.mRedirectIndices = new int[mMesh.mIndices.length];
		System.arraycopy(redirectIndices, 0, mMesh.mRedirectIndices, 0, mMesh.mRedirectIndices.length);

		mMesh.mIndexCount = mIndexId;

		if(useNormals)
			mMesh.calculateNormals();

		if(staticMesh) {
			mMesh.createDrawBatch(true);
		}

		YangMaterialSet.diffuseTextureProperties = prevTexProps;
	}

	public void draw() {
		mMesh.draw();
	}

	public YangMesh getMesh() {
		return mMesh;
	}

}
