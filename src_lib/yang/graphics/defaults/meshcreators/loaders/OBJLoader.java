package yang.graphics.defaults.meshcreators.loaders;

import java.io.IOException;
import java.io.InputStream;

import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.defaults.meshcreators.MeshCreator;
import yang.graphics.model.FloatColor;
import yang.graphics.model.material.YangMaterial;
import yang.graphics.model.material.YangMaterialProvider;
import yang.graphics.model.material.YangMaterialSet;
import yang.graphics.translator.GraphicsTranslator;
import yang.math.objects.Quadruple;
import yang.math.objects.matrix.YangMatrix;
import yang.util.NonConcurrentList;
import yang.util.filereader.TokenReader;

public class OBJLoader extends MeshCreator<DefaultGraphics<?>>{

	public static YangMaterial DEFAULT_MATERIAL = new YangMaterial();
	
	public static int MAX_VERTICES = 100000;
	private static final String[] KEYWORDS = {"mtllib","usemtl"};
	private static float[] workingPositions;
	private static float[] workingNormals;
	private static float[] workingTexCoords;
	private static short[] workingIndices;
	private static int[] redirectIndices;
	private static int[] positionIndices;
	private static int[] texCoordIndices;
	private static int[] normalIndices;
	private static int[] smoothIndices;
	private static int curVertexCount;
	
	public int mVertexCount = 0;
	public int mIndexCount = 0;
	public float[] mPositions;
	public float[] mTexCoords;
	public float[] mNormals;
	public int[] mPosIndices;
	public int[] mTexCoordIndices;
	public int[] mNormIndices;
	public short[] mIndices;
	public FloatColor mColor = FloatColor.WHITE.clone();
	public Quadruple mSuppData = Quadruple.ZERO;
	public NonConcurrentList<YangMaterialSet> mMaterialSets;
	public NonConcurrentList<OBJMaterialSection> mMaterialSections;
	
	private TokenReader mModelReader;
	private int mIndexId = 0;
	private int curSmoothGroup;
	private int posId;
	private int texId;
	private int normId;
	
	public OBJLoader(DefaultGraphics<?> graphics) {
		super(graphics);
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

		mMaterialSets = new NonConcurrentList<YangMaterialSet>();
		mMaterialSections = new NonConcurrentList<OBJMaterialSection>();
	}
	
	private void copyVertex(int index,int posIndex,int texIndex) {
		redirectIndices[index] = curVertexCount;

		redirectIndices[curVertexCount] = -1;
		smoothIndices[curVertexCount] = curSmoothGroup;

		curVertexCount++;
	}
	
	private void addIndex(int posIndex,int texIndex,int normIndex) {
		int index = posIndex;
		while((smoothIndices[index]!=Integer.MIN_VALUE && (curSmoothGroup==-1 || curSmoothGroup!=smoothIndices[index])) || (texCoordIndices[index]>=0 && texCoordIndices[index]!=texIndex)) {
			int redirect = redirectIndices[index];
			if(redirect<0) {
				copyVertex(index,posIndex,texIndex);
				index = curVertexCount-1;
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
	
	public YangMaterial findMaterial(String materialName) {
		for(YangMaterialSet matSet:mMaterialSets) {
			YangMaterial mat = matSet.getMaterial(materialName);
			if(mat!=null)
				return mat;
		}
		return null;
	}

	public void loadOBJ(InputStream modelStream,YangMaterialProvider materialProvider,YangMatrix transform) throws IOException {
		mModelReader = new TokenReader(modelStream);
		OBJMaterialSection currentMatSec = new OBJMaterialSection(0,DEFAULT_MATERIAL);
		mMaterialSections.clear();
		mMaterialSections.add(currentMatSec);
		
		curVertexCount = 0;
		curSmoothGroup = -1;
		posId = 0;
		texId = 0;
		
		char[] chars = mModelReader.mCharBuffer;
		while(!mModelReader.eof()) {
			mModelReader.nextWord(true);
			char fstC = chars[0];
			if(fstC=='#')
				mModelReader.toLineEnd();
			else{
				if(fstC=='\n') {
					
				}else{
					if(mModelReader.mWordLength==1) {
						//Single characters
						if(fstC=='v') {
							//Add vertex position
							float posX = mModelReader.readFloat(false);
							float posY = mModelReader.readFloat(false);
							float posZ = mModelReader.readFloat(false);
							if(transform!=null) {
								transform.apply3D(posX, posY, posZ, workingPositions, posId);
								posId += 3;
							}else{
								workingPositions[posId++] = posX;
								workingPositions[posId++] = posY;
								workingPositions[posId++] = posZ;
							}
							redirectIndices[curVertexCount] = -1;
							positionIndices[curVertexCount] = -1;
							texCoordIndices[curVertexCount] = -1;
							normalIndices[curVertexCount] = -1;
							smoothIndices[curVertexCount] = Integer.MIN_VALUE;
							curVertexCount++;
						}
						
						if(fstC=='f') {
							mModelReader.nextWord(false);
							int baseInd = mModelReader.wordToInt(0,1)-1;
							int baseTexInd = mModelReader.wordToInt(mModelReader.mNumberPos+1,1)-1;
							int baseNormInd = mModelReader.wordToInt(mModelReader.mNumberPos+1, 1)-1;
							mModelReader.nextWord(false);
							int prevInd = mModelReader.wordToInt(0,1)-1;
							int prevTexInd = mModelReader.wordToInt(mModelReader.mNumberPos+1,1)-1;
							int prevNormInd = mModelReader.wordToInt(mModelReader.mNumberPos+1, 1)-1;
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

						}
						
						if(fstC=='s') {
							int group = mModelReader.readInt(false);
							if(group==TokenReader.ERROR_INT)
								curSmoothGroup = -1;
							else
								curSmoothGroup = group;
						}
					}else if(mModelReader.mWordLength==2) {
						if(fstC=='v' && chars[1]=='t') {
							float texU = mModelReader.readFloat(false);
							float texV = mModelReader.readFloat(false);
							float texW = mModelReader.readFloat(false);
							workingTexCoords[texId++] = texU;
							workingTexCoords[texId++] = -texV;
						}
					}else{
						//Keywords
						switch(mModelReader.pickWord(KEYWORDS)) {
						case 0:
							//mtllib
							String filename = mModelReader.readString(false);
							YangMaterialSet newMatSet = materialProvider.getMaterialSet(filename);
							mMaterialSets.add(newMatSet);
							break;
						case 1:
							//usemtl
							String mtlKey = mModelReader.readString(false);
							YangMaterial mat = findMaterial(mtlKey);
							if(currentMatSec.mStartIndex==mIndexId) {
								currentMatSec.mMaterial = mat;
							}else{
								currentMatSec.mEndIndex = mIndexId;
								currentMatSec = new OBJMaterialSection(mIndexId,mat);
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
		mTexCoords = new float[texId];
		mNormals = new float[normId];
		mPosIndices = new int[curVertexCount];
		mTexCoordIndices = new int[curVertexCount];
		mNormIndices = new int[curVertexCount];
		mIndices = new short[mIndexId];
		System.arraycopy(workingPositions, 0, mPositions, 0, posId);
		System.arraycopy(workingTexCoords, 0, mTexCoords, 0, texId);
		System.arraycopy(workingNormals, 0, mNormals, 0, normId);
		System.arraycopy(positionIndices, 0, mPosIndices, 0, curVertexCount);
		System.arraycopy(texCoordIndices, 0, mTexCoordIndices, 0, curVertexCount);
		System.arraycopy(normalIndices, 0, mNormIndices, 0, curVertexCount);
		System.arraycopy(workingIndices, 0, mIndices, 0, mIndexId);
		mVertexCount = curVertexCount;
		mIndexCount = mIndexId;
	}
	
	public void draw() {
		IndexedVertexBuffer vertexBuffer = mGraphics.getCurrentVertexBuffer();
		for(int posInd:mPosIndices) {
			int i = posInd*3;
			vertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS, mPositions[i], mPositions[i+1], mPositions[i+2]);
		}
//		vertexBuffer.putArray(DefaultGraphics.ID_POSITIONS, mPositions);
		if(mTexCoords.length>0)
			for(int texInd:mTexCoordIndices) {
				int i = texInd*2;
				vertexBuffer.putVec2(DefaultGraphics.ID_TEXTURES, mTexCoords[i], mTexCoords[i+1]);
			}
//		vertexBuffer.putArray(DefaultGraphics.ID_TEXTURES, mTexCoords);
		vertexBuffer.putArrayMultiple(DefaultGraphics.ID_COLORS, mColor.mValues, mVertexCount);
		vertexBuffer.putArrayMultiple(DefaultGraphics.ID_SUPPDATA, mSuppData.mValues, mVertexCount);
		vertexBuffer.putIndexArray(mIndices);
		if(mGraphics instanceof Default3DGraphics) {
			((Default3DGraphics)mGraphics).fillNormals(0);
		}
		
		mTranslator.prepareDraw();
		for(OBJMaterialSection matSec:mMaterialSections) {
			mTranslator.bindTexture(matSec.mMaterial.mDiffuseTexture);
			mGraphics.setAmbientColor(matSec.mMaterial.mDiffuseColor);
			mTranslator.drawVertices(matSec.mStartIndex, matSec.mEndIndex-matSec.mStartIndex, GraphicsTranslator.T_TRIANGLES);
		}
	}
	
}
