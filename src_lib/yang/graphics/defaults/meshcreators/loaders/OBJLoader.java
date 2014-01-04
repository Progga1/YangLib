package yang.graphics.defaults.meshcreators.loaders;

import java.io.IOException;
import java.io.InputStream;

import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.defaults.meshcreators.YangMesh;
import yang.graphics.model.material.YangMaterial;
import yang.graphics.model.material.YangMaterialProvider;
import yang.graphics.model.material.YangMaterialSet;
import yang.graphics.textures.TextureProperties;
import yang.math.objects.matrix.YangMatrix;
import yang.util.YangList;
import yang.util.filereader.TokenReader;

public class OBJLoader extends YangMesh {

	private static final String[] KEYWORDS = {"mtllib","usemtl"};

	public static int MAX_VERTICES = 200000;
	private static float[] workingPositions;
	private static float[] workingNormals;
	private static float[] workingTexCoords;
	private static short[] workingIndices;
	private static int[] redirectIndices;
	private static int[] positionIndices;
	private static int[] texCoordIndices;
	private static int[] normalIndices;
	private static int[] smoothIndices;

	private TokenReader mModelReader;
	private int mIndexId = 0;
	private int curSmoothGroup;
	private int posId;
	private int texId;
	private int normId;

	public OBJLoader(DefaultGraphics<?> graphics,MeshMaterialHandles handles,TextureProperties textureProperties) {
		super(graphics,handles,textureProperties);
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

		mMaterialSets = new YangList<YangMaterialSet>();
		mMaterialSections = new YangList<YangMaterialSection>();
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

	public void loadOBJ(InputStream modelStream,YangMaterialProvider materialProvider,YangMatrix transform,boolean useNormals,boolean staticMesh) throws IOException {
		if(modelStream==null)
			throw new RuntimeException("No stream given");
		final TextureProperties prevTexProps = YangMaterialSet.diffuseTextureProperties;
		if(mTextureProperties!=null)
			YangMaterialSet.diffuseTextureProperties = mTextureProperties;
		mDrawBatch = null;
		mModelReader = new TokenReader(modelStream);
		YangMaterialSection currentMatSec = new YangMaterialSection(0,DEFAULT_MATERIAL);
		mMaterialSections.clear();
		mMaterialSections.add(currentMatSec);

		mVertexCount = 0;
		curSmoothGroup = -1;
		posId = 0;
		texId = 0;

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
							final YangMaterialSet newMatSet = materialProvider.getMaterialSet(filename);
							mMaterialSets.add(newMatSet);
							break;
						case 1:
							//usemtl
							final String mtlKey = mModelReader.readString(false);
							final YangMaterial mat = findMaterial(mtlKey);
							if(currentMatSec.mStartIndex==mIndexId) {
								currentMatSec.mMaterial = mat;
							}else{
								currentMatSec.mEndIndex = mIndexId;
								currentMatSec = new YangMaterialSection(mIndexId,mat);
								mMaterialSections.add(currentMatSec);
							}
							break;
						}
					}
					mModelReader.toLineEnd();
				}
			}
		}

		currentMatSec.mEndIndex = mIndexId;

		mPositions = new float[posId];
		mPosIndices = new int[mVertexCount];
		System.arraycopy(workingPositions, 0, mPositions, 0, posId);
		System.arraycopy(positionIndices, 0, mPosIndices, 0, mVertexCount);

		if(texId>0) {
			mTexCoords = new float[texId];
			mTexCoordIndices = new int[mVertexCount];
			System.arraycopy(workingTexCoords, 0, mTexCoords, 0, texId);
			System.arraycopy(texCoordIndices, 0, mTexCoordIndices, 0, mVertexCount);
		}
		if(normId>0) {
			mNormals = new float[normId];
			mNormIndices = new int[mVertexCount];
			System.arraycopy(workingNormals, 0, mNormals, 0, normId);
			System.arraycopy(normalIndices, 0, mNormIndices, 0, mVertexCount);
		}

		mIndices = new short[mIndexId];
		System.arraycopy(workingIndices, 0, mIndices, 0, mIndexId);
		mSmoothIndices = new int[mIndices.length];
		System.arraycopy(smoothIndices, 0, mSmoothIndices, 0, mSmoothIndices.length);
		mRedirectIndices = new int[mIndices.length];
		System.arraycopy(redirectIndices, 0, mRedirectIndices, 0, mRedirectIndices.length);

		mIndexCount = mIndexId;

		if(useNormals)
			calculateNormals();

		if(staticMesh) {
			createDrawBatch(true);
		}

		YangMaterialSet.diffuseTextureProperties = prevTexProps;
	}

}
