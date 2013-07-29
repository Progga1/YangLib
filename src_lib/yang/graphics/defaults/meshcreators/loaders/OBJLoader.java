package yang.graphics.defaults.meshcreators.loaders;

import java.io.IOException;
import java.io.InputStream;

import yang.graphics.buffers.DrawBatch;
import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.defaults.meshcreators.MeshCreator;
import yang.graphics.model.FloatColor;
import yang.graphics.model.material.YangMaterial;
import yang.graphics.model.material.YangMaterialProvider;
import yang.graphics.model.material.YangMaterialSet;
import yang.graphics.programs.GLProgram;
import yang.graphics.textures.TextureProperties;
import yang.graphics.translator.GraphicsTranslator;
import yang.math.objects.Quadruple;
import yang.math.objects.matrix.YangMatrix;
import yang.util.NonConcurrentList;
import yang.util.filereader.TokenReader;

public class OBJLoader extends MeshCreator<DefaultGraphics<?>>{

	public static YangMaterial DEFAULT_MATERIAL = new YangMaterial();
	
	public static int MAX_VERTICES = 200000;
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

	private ObjHandles mHandles;
	
	public int mVertexCount = 0;
	public int mIndexCount = 0;
	public float[] mPositions;
	public float[] mTexCoords;
	public float[] mNormals;
	public int[] mPosIndices;
	public int[] mTexCoordIndices;
	public int[] mNormIndices;
	public int[] mSmoothIndices;
	public int[] mRedirectIndices;
	public short[] mIndices;
	public FloatColor mColor = FloatColor.WHITE.clone();
	public Quadruple mSuppData = Quadruple.ZERO;
	public NonConcurrentList<YangMaterialSet> mMaterialSets;
	public NonConcurrentList<OBJMaterialSection> mMaterialSections;
	public TextureProperties mTextureProperties;
	
	public DrawBatch mDrawBatch;
	
	private TokenReader mModelReader;
	private int mIndexId = 0;
	private int curSmoothGroup;
	private int posId;
	private int texId;
	private int normId;
	
	public OBJLoader(DefaultGraphics<?> graphics,ObjHandles handles,TextureProperties textureProperties) {
		super(graphics);
		mHandles = handles;
		mTextureProperties = textureProperties;
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
	
	public OBJLoader(DefaultGraphics<?> graphics,ObjHandles handles) {
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
			int redirect = redirectIndices[index];
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
	
	public YangMaterial findMaterial(String materialName) {
		for(YangMaterialSet matSet:mMaterialSets) {
			if(matSet==null)
				continue;
			YangMaterial mat = matSet.getMaterial(materialName);
			if(mat!=null)
				return mat;
		}
		return null;
	}

	public void loadOBJ(InputStream modelStream,YangMaterialProvider materialProvider,YangMatrix transform,boolean useNormals,boolean staticMesh) throws IOException {
		TextureProperties prevTexProps = YangMaterialSet.diffuseTextureProperties;
		if(mTextureProperties!=null)
			YangMaterialSet.diffuseTextureProperties = mTextureProperties;
		mDrawBatch = null;
		mModelReader = new TokenReader(modelStream);
		OBJMaterialSection currentMatSec = new OBJMaterialSection(0,DEFAULT_MATERIAL);
		mMaterialSections.clear();
		mMaterialSections.add(currentMatSec);
		
		mVertexCount = 0;
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
							redirectIndices[mVertexCount] = -1;
							positionIndices[mVertexCount] = -1;
							texCoordIndices[mVertexCount] = -1;
							normalIndices[mVertexCount] = -1;
							smoothIndices[mVertexCount] = Integer.MIN_VALUE;
							mVertexCount++;
						}else if(fstC=='f') {
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

						}else if(fstC=='s') {
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
						}else if(fstC=='v' && chars[1]=='n') {
							float normX = mModelReader.readFloat(false);
							float normY = mModelReader.readFloat(false);
							float normZ = mModelReader.readFloat(false);
							workingNormals[normId++] = normX;
							workingNormals[normId++] = normY;
							workingNormals[normId++] = normZ;
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
	
	private void drawBuffer(IndexedVertexBuffer vertexBuffer) {
		mTranslator.setVertexBuffer(vertexBuffer);
		mTranslator.prepareDraw();
		mTranslator.mFlushDisabled = true;
		GLProgram program = mGraphics.mCurrentProgram.mProgram;
		for(OBJMaterialSection matSec:mMaterialSections) {
			YangMaterial mat = matSec.mMaterial;
			mTranslator.bindTexture(mat.mDiffuseTexture);
			//mGraphics.setAmbientColor(matSec.mMaterial.mDiffuseColor);
			if(mat.mSpecularTexture!=null) {
				program.setUniformInt(mHandles.mSpecTexSampler, 2);
				program.setUniformInt(mHandles.mSpecUseTexHandle, 1);
				mGraphics.bindTexture(mat.mSpecularTexture,2);
			}else{
				program.setUniform4f(mHandles.mSpecColorHandle, mat.mSpecularColor.mValues);
				program.setUniformInt(mHandles.mSpecUseTexHandle, 0);
			}
			program.setUniform4f(mHandles.mDiffuseColorHandle, mat.mDiffuseColor.mValues);
			
			program.setUniformFloat(mHandles.mSpecExponentHandle, mat.mSpecularCoefficient);
			mTranslator.drawVertices(matSec.mStartIndex, matSec.mEndIndex-matSec.mStartIndex, GraphicsTranslator.T_TRIANGLES);
		}
		mTranslator.mFlushDisabled = false;
		vertexBuffer.reset();
		mGraphics.resetVertexBuffer();
	}
	
	public void putBuffers(IndexedVertexBuffer vertexBuffer) {
		vertexBuffer.putIndexArray(mIndices);

		for(int posInd:mPosIndices) {
			int i = posInd*3;
			if(i<0)
				vertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS, 0,0,0);
			else
				vertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS, mPositions[i],mPositions[i+1],mPositions[i+2]);
		}
//		vertexBuffer.putArray(DefaultGraphics.ID_POSITIONS, mPositions);
		if(mTexCoords!=null && mTexCoords.length>0)
			for(int texInd:mTexCoordIndices) {
				int i = texInd*2;
				if(i<0)
					vertexBuffer.putVec2(DefaultGraphics.ID_TEXTURES, 0,0);
				else
					vertexBuffer.putVec2(DefaultGraphics.ID_TEXTURES, mTexCoords[i], mTexCoords[i+1]);
			}
//		vertexBuffer.putArray(DefaultGraphics.ID_TEXTURES, mTexCoords);
		
		vertexBuffer.putArrayMultiple(DefaultGraphics.ID_COLORS, mColor.mValues, mVertexCount);
		vertexBuffer.putArrayMultiple(DefaultGraphics.ID_SUPPDATA, mSuppData.mValues,mVertexCount);
		
		if(mGraphics instanceof Default3DGraphics) {
			if(mNormals!=null && mNormals.length>0) {
				if(mNormIndices==null)
					vertexBuffer.putArray(DefaultGraphics.ID_NORMALS, mNormals);
				else	
					for(int normInd:mNormIndices) {
						int i = normInd*3;
						if(i<0)
							vertexBuffer.putVec3(Default3DGraphics.ID_NORMALS, 0,0,0);
						else
							vertexBuffer.putVec3(Default3DGraphics.ID_NORMALS, mNormals[i],mNormals[i+1],mNormals[i+2]);
					}
			}else{
				Default3DGraphics.fillNormals(vertexBuffer,0);
			}
		}
	}
	
	public void drawDynamic() {
		IndexedVertexBuffer vertexBuffer = mGraphics.getCurrentVertexBuffer();
		
		putBuffers(vertexBuffer);

		drawBuffer(vertexBuffer);
	}
	
	public void updateDrawBatch() {
		if(mDrawBatch==null)
			mDrawBatch = new DrawBatch(mGraphics,mGraphics.createVertexBuffer(false, false, mIndexCount, mVertexCount));
		putBuffers(mDrawBatch.mVertexBuffer);
		mDrawBatch.mVertexBuffer.finishUpdate();
		mDrawBatch.mVertexBuffer.reset();
	}
	
	public void freeDynamicData() {
		mPositions = null;
		mNormals = null;
		mIndices = null;
		mTexCoords = null;
		mRedirectIndices = null;
		mSmoothIndices = null;
		mPosIndices = null;
		mNormIndices = null;
		mTexCoordIndices = null;
	}
	
	public void createDrawBatch(boolean completelyStatic) {
		mDrawBatch = new DrawBatch(mGraphics,mGraphics.createVertexBuffer(!completelyStatic, false, mIndexCount, mVertexCount));
		updateDrawBatch();
		if(completelyStatic)
			freeDynamicData();
	}
	
	public void drawStatic() {
		if(mDrawBatch==null)
			createDrawBatch(true);
		mDrawBatch.mVertexBuffer.reset();

		drawBuffer(mDrawBatch.mVertexBuffer);
	}
	
	public void draw() {
		if(mDrawBatch==null)
			drawDynamic();
		else
			drawStatic();
	}
	
//	public void setShader(Basic3DProgram shader) {
//		if(mShader==shader)
//			return;
//		mShader = shader;
//		if(mHandles==null)
//			mHandles = new ObjHandles();
//		mHandles.setHandles(shader);
//	}
	
	public void createNormIndices() {
		mNormIndices = new int[mVertexCount];
		for(int i=0;i<mVertexCount;i++) {
			mNormIndices[i] = -1;
		}
		int c = 0;
		for(int i=0;i<mVertexCount;i++) {
			if(mNormIndices[i]!=-1)
				continue;
			int smoothGroup = mSmoothIndices[i];
			if(smoothGroup>=0) {
				int redirect = mRedirectIndices[i];
				while(redirect>=0) {
					if(mSmoothIndices[redirect]==smoothGroup) {
						mNormIndices[redirect] = c;
					}
					redirect = mRedirectIndices[redirect];
				}
			}
			mNormIndices[i] = c;
			c++;
		}
		if(mNormals==null || mNormals.length<c*3) {
			mNormals = new float[c*3];
		}
	}
	
	protected void addToNormal(int i,float normX,float normY,float normZ) {
		mNormals[i] += normX;
		mNormals[i+1] += normY;
		mNormals[i+2] += normZ;
	}
	
	public void calculateNormals() {
		if(mNormIndices==null)
			createNormIndices();
//		if(mNormals==null || mNormals.length<mVertexCount*3) {
//			mNormals = new float[mVertexCount*3];
//		}else{
			for(int i=0;i<mNormals.length;i++)
				mNormals[i] = 0;
//		}
		int polyCount = mIndices.length/3;
		for(int i=0;i<polyCount;i++) {
			int i1 = mPosIndices[mIndices[i*3]]*3;
			int i2 = mPosIndices[mIndices[i*3+1]]*3;
			int i3 = mPosIndices[mIndices[i*3+2]]*3;
			float dx1 = mPositions[i2]-mPositions[i1];
			float dy1 = mPositions[i2+1]-mPositions[i1+1];
			float dz1 = mPositions[i2+2]-mPositions[i1+2];
			float dx2 = mPositions[i3]-mPositions[i1];
			float dy2 = mPositions[i3+1]-mPositions[i1+1];
			float dz2 = mPositions[i3+2]-mPositions[i1+2];
			float crossX = dy1*dz2 - dz1*dy2;
			float crossY = dz1*dx2 - dx1*dz2;
			float crossZ = dx1*dy2 - dy1*dx2;
			float crossMagn = (float)Math.sqrt(crossX*crossX+crossY*crossY+crossZ*crossZ);
			if(crossMagn==0)
				continue;
			crossMagn = 1/crossMagn;
			crossX *= crossMagn;
			crossY *= crossMagn;
			crossZ *= crossMagn;
			i1 = mNormIndices[mIndices[i*3]]*3;
			i2 = mNormIndices[mIndices[i*3+1]]*3;
			i3 = mNormIndices[mIndices[i*3+2]]*3;
			addToNormal(i1,crossX,crossY,crossZ);
			addToNormal(i2,crossX,crossY,crossZ);
			addToNormal(i3,crossX,crossY,crossZ);
		}

		for(int n=0;n<mNormals.length;n+=3) {
			float normMagn = (float)Math.sqrt(mNormals[n]*mNormals[n] + mNormals[n+1]*mNormals[n+1] + mNormals[n+2]*mNormals[n+2]);
			if(normMagn==0)
				continue;
			normMagn = 1/normMagn;
			mNormals[n] *= normMagn;
			mNormals[n+1] *= normMagn;
			mNormals[n+2] *= normMagn;
		}
	}
	
	public boolean hasStaticNormals() {
		return mNormals!=null;
	}
	
}
