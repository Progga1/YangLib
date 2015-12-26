package yang.graphics.defaults.meshes.loaders;

import java.io.IOException;
import java.io.InputStream;

import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.model.material.YangMaterial;
import yang.graphics.model.material.YangMaterialSet;
import yang.graphics.textures.TextureProperties;
import yang.math.objects.YangMatrix;
import yang.systemdependent.AbstractResourceManager;
import yang.util.filereader.TokenReader;

public class OBJLoader extends YangSceneLoader {

	private static final String[] KEYWORDS = {"mtllib","usemtl"};

	protected TokenReader mModelReader;
	public YangMesh mMesh;

	public OBJLoader(DefaultGraphics<?> graphics,MeshMaterialHandles handles,TextureProperties textureProperties) {
		super(graphics,handles,textureProperties);
	}

	public OBJLoader(DefaultGraphics<?> graphics,MeshMaterialHandles handles) {
		this(graphics,handles,null);
	}

	public YangMesh loadOBJ(InputStream modelStream,YangMatrix transform,boolean useNormals,boolean staticMesh) throws IOException {
		if(modelStream==null)
			throw new RuntimeException("No stream given");

		mModelReader = new TokenReader(modelStream);

		//Reset mesh
		mMesh = startLoadingMesh();

		curSmoothGroup = -1;

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
							int baseInd = mModelReader.wordToInt(0,1)-1;
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
							final YangMaterialSet newMatSet = mGFXLoader.getMaterialSet(filename);
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
								currentMatSec.mEdgeEndIndex = mEdgeIndexId;
								currentMatSec = new YangMaterialSection(mIndexId,mEdgeIndexId, mat);
								mMesh.mMaterialSections.add(currentMatSec);
							}
							break;
						}
					}
					mModelReader.toLineEnd();
				}
			}
		}

		finishLoadingMesh(useNormals,staticMesh);

		return mMesh;
	}

	public YangMesh loadOBJ(String filename,YangMatrix transform,boolean useNormals,boolean staticMesh) throws IOException {
		YangMesh result = loadOBJ(mGraphics.mTranslator.mGFXLoader.mResources.getAssetInputStream(filename),transform,useNormals,staticMesh);
		result.mName = AbstractResourceManager.extractNameFromFilename(filename);
		return result;
	}

	public void draw() {
		mMesh.draw();
	}

	public YangMesh getMesh() {
		return mMesh;
	}

}
