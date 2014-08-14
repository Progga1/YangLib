package yang.graphics.defaults.meshes.loaders;

import java.util.Arrays;

import yang.graphics.buffers.DrawBatch;
import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.defaults.meshes.armature.LimbNeutralData;
import yang.graphics.defaults.meshes.armature.YangArmature;
import yang.graphics.defaults.meshes.armature.YangArmaturePosture;
import yang.graphics.defaults.programs.subshaders.EmissiveSubShader;
import yang.graphics.defaults.programs.subshaders.SpecularLightBasicSubShader;
import yang.graphics.model.FloatColor;
import yang.graphics.model.material.YangMaterial;
import yang.graphics.model.material.YangMaterialSet;
import yang.graphics.programs.GLProgram;
import yang.graphics.textures.TextureProperties;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.translator.glconsts.GLDrawModes;
import yang.math.objects.Point3f;
import yang.math.objects.Quadruple;
import yang.math.objects.YangMatrix;
import yang.util.YangList;

public class YangMesh {

	public static float PI = 3.1415926535f;

	public DefaultGraphics<?> mGraphics;
	protected GraphicsTranslator mTranslator;

	public int mVertexCount = 0;
	public int mIndexCount = 0;
	private int mUniqueVertexCount = 0;
	public float[] mPositions;
	public float[] mResultPositions = null;
	public float[] mTexCoords;
	public float[] mNormals;
	public float[] mResultNormals = null;
	public float[] mColors = null;
	public int[] mPosIndices;
	public int[] mTexCoordIndices;
	public int[] mNormIndices;
	public int[] mSmoothIndices;
	public int[] mRedirectIndices;
	public int[] mSkinIds;
	public float[] mSkinWeights;
	public short[] mTriangleIndices;
	public short[] mEdgeIndices;

	public FloatColor mDefaultColor = FloatColor.WHITE.clone();
	public Quadruple mSuppData = Quadruple.ZERO;
	public YangList<YangMaterialSet> mMaterialSets;
	public YangList<YangMaterialSection> mMaterialSections;

	//SETTINGS
	public boolean mDrawBackToFront = false;
	protected final MeshMaterialHandles mHandles;
	public TextureProperties mTextureProperties;
	public boolean mUseShaders = true;
	protected int mSkinJointsPerVertex = 4;
	public boolean mAutoSkinningUpdate = true;
	public boolean mBlockTextures = false;
	public boolean mNormalizeNormals = true;
	public YangArmaturePosture mCurArmature = null;
	public boolean mWireFrames = false;


	public DrawBatch mDrawBatch;

	//TEMP
	protected Point3f tempPoint = new Point3f();
	private YangMatrix tempMat = new YangMatrix();

	public YangMesh(DefaultGraphics<?> graphics,MeshMaterialHandles handles,TextureProperties textureProperties) {
		mGraphics = graphics;
		mTranslator = graphics.mTranslator;
		mHandles = handles;
		mTextureProperties = textureProperties;
		mMaterialSets = new YangList<YangMaterialSet>();
		mMaterialSections = new YangList<YangMaterialSection>();
	}

	protected void creationFinished() {
		mUniqueVertexCount = mPositions.length/3;
	}

	public void initColors() {
		if(mColors==null) {
			int l = mUniqueVertexCount*4;
			mColors = new float[l];
		}
	}

	public void fillColor(float r,float g,float b,float a) {
		initColors();
		int l = mUniqueVertexCount;
		for(int i=0;i<l;i++) {
			int index = i*4;
			mColors[index] = r;
			mColors[index+1] = g;
			mColors[index+2] = b;
			mColors[index+3] = a;
		}
	}

	public void fillColor(FloatColor color) {
		fillColor(color.mValues[0],color.mValues[1],color.mValues[2],color.mValues[3]);
	}

	public void fillWhite() {
		initColors();
		Arrays.fill(mColors,1);
	}

	public void initResultValues() {
		if(mResultPositions==null) {
			int l = getUniqueVertexCount()*3;
			mResultPositions = new float[l];
			mResultNormals = new float[l];
		}
	}

	public void initArmatureWeights() {
		if(mSkinIds==null) {
			int l = getUniqueVertexCount()*mSkinJointsPerVertex;
			mSkinIds = new int[l];
			mSkinWeights = new float[l];
			initResultValues();
		}
	}

	public boolean addArmatureWeight(int vertexId, int limbId, float weight) {
		float smallestWeight = Float.MAX_VALUE;
		int smallestWeightId = 0;
		int skinBaseId = vertexId*mSkinJointsPerVertex;
//		for(int k=0;k<mSkinJointsPerVertex;k++) {
//			if(mSkinWeights[skinBaseId+k]<smallestWeight) {
//				smallestWeight = mSkinWeights[skinBaseId+k];
//				smallestWeightId = k;
//			}
//		}
//		if(weight>smallestWeight) {
//			mSkinWeights[skinBaseId+smallestWeightId] = weight;
//			mSkinIds[skinBaseId+smallestWeightId] = limbId;
//		}

		//Delete previous weight
		for(int k=0;k<mSkinJointsPerVertex;k++) {
			if(mSkinIds[skinBaseId+k]==limbId) {
				weight += mSkinWeights[skinBaseId+k];
				for(int j=k;j<mSkinJointsPerVertex-1;j++) {
					mSkinWeights[skinBaseId+j] = mSkinWeights[skinBaseId+j+1];
					mSkinIds[skinBaseId+j] = mSkinIds[skinBaseId+j+1];
				}
				mSkinWeights[mSkinJointsPerVertex-1] = 0;
				mSkinIds[mSkinJointsPerVertex-1] = -1;
				break;
			}
		}

		for(int k=0;k<mSkinJointsPerVertex;k++) {
			if(weight>mSkinWeights[skinBaseId+k]) {
				for(int j=k+1;j<mSkinJointsPerVertex;j++) {
					if(mSkinWeights[skinBaseId+j]>0) {
						mSkinWeights[skinBaseId+j-1] = mSkinWeights[skinBaseId+j];
						mSkinIds[skinBaseId+j-1] = mSkinIds[skinBaseId+j];
					}
				}
				mSkinWeights[skinBaseId+k] = weight;
				mSkinIds[skinBaseId+k] = limbId;
				return true;
			}
		}
		return false;
	}

	public void generateArmatureWeights(YangArmature armature) {
		if(mPositions==null)
			throw new RuntimeException("Cannot use armature on static mesh");
		int weights = mSkinJointsPerVertex;
		int vCount = mPositions.length/3;
		int l = vCount*weights;
		initArmatureWeights();

		for(int i=0;i<vCount;i++) {

			int skinBaseId = i*weights;
			for(int k=0;k<weights;k++) {
				mSkinWeights[skinBaseId+k] = 0;
			}

			tempPoint.set(mPositions[i*3],mPositions[i*3+1],mPositions[i*3+2]);

			int j = 0;
			for(LimbNeutralData limbData:armature.mLimbData) {
				Point3f point = limbData.mPosition;
				float dist = point.getDistance(tempPoint);
				float resWeight = 0;
				if(dist<=0.00001f) {
					resWeight = 1000000;
				}else{
					resWeight = 1f/(dist*dist);
				}
//				float smallestWeight = Float.MAX_VALUE;
//				int smallestWeightId = 0;
//				for(int k=0;k<weights;k++) {
//					if(mSkinWeights[skinBaseId+k]<smallestWeight) {
//						smallestWeight = mSkinWeights[skinBaseId+k];
//						smallestWeightId = k;
//					}
//				}
//				if(resWeight>smallestWeight) {
//					mSkinWeights[skinBaseId+smallestWeightId] = resWeight;
//					mSkinIds[skinBaseId+smallestWeightId] = j;
//				}
				addArmatureWeight(i,j,resWeight);
				j++;
			}

			//Normalize
			float sum = 0;
			for(int k=0;k<weights;k++) {
				sum += mSkinWeights[skinBaseId+k];
			}
			sum = 1f/sum;
			for(int k=0;k<weights;k++) {
				mSkinWeights[skinBaseId+k] *= sum;
			}

		}
	}

	private void drawBuffer(IndexedVertexBuffer vertexBuffer) {
		mTranslator.setVertexBuffer(vertexBuffer);
		if(mDrawBackToFront && !mWireFrames) {
			mGraphics.sort();
			mTranslator.switchZBuffer(true);
			mTranslator.switchZWriting(false);
		}else{
			mTranslator.switchZBuffer(true);
			mTranslator.switchZWriting(true);
		}
		mTranslator.prepareDraw();
		mTranslator.mFlushDisabled = true;
		final GLProgram program = mGraphics.mCurrentProgram.mProgram;
		final EmissiveSubShader emisShader = mHandles.mEmisShader;
		final SpecularLightBasicSubShader specShader = mHandles.mSpecShader;

		for(final YangMaterialSection matSec:mMaterialSections) {
			if(mUseShaders) {
				YangMaterial mat = matSec.mMaterial;
				if(mat==null) {
					matSec.mMaterial = new YangMaterial();
					mat = matSec.mMaterial;
				}
				if(!mBlockTextures)
					mTranslator.bindTexture(mat.mDiffuseTexture);
				if(specShader!=null) {
					if(mat.mSpecularProps.mTexture!=null) {
						program.setUniformInt(specShader.mSpecTexSampler,specShader.mTextureLevel);
						mTranslator.bindTextureNoFlush(mat.mSpecularProps.mTexture,specShader.mTextureLevel);
						program.setUniformInt(specShader.mSpecUseTexHandle, 1);
					}else{
						program.setUniform4f(specShader.mSpecColorHandle, mat.mSpecularProps.mColor.mValues);
						program.setUniformInt(specShader.mSpecUseTexHandle, 0);
					}
					program.setUniformFloat(specShader.mSpecExponentHandle, mat.mSpecularProps.mExponent);
				}

				if(emisShader!=null) {
					program.setUniform4f(emisShader.mEmisColorHandle, mat.mEmissiveProps.mColor.mValues);
					if(mat.mEmissiveProps.mTexture!=null) {
						program.setUniformInt(emisShader.mEmisTexSampler,emisShader.mTextureLevel);
						mTranslator.bindTextureNoFlush(mat.mEmissiveProps.mTexture,emisShader.mTextureLevel);
						program.setUniformInt(emisShader.mEmisUseTexHandle, 1);
					}else{
						program.setUniformInt(emisShader.mEmisUseTexHandle, 0);
					}
				}

				program.setUniform4f(mHandles.mDiffuseColorHandle, mat.mDiffuseColor.mValues);
			}

			if(mWireFrames) {
				if(mEdgeIndices==null) {
					boolean preWire = mTranslator.mForceWireFrames;
					mTranslator.mForceWireFrames = true;
					mTranslator.drawVertices(matSec.mStartIndex, matSec.mEndIndex-matSec.mStartIndex, GLDrawModes.TRIANGLES);
					mTranslator.mForceWireFrames = preWire;
				}else{
					mTranslator.drawVertices(matSec.mEdgeStartIndex, matSec.mEdgeEndIndex-matSec.mEdgeStartIndex, GLDrawModes.LINELIST);
				}
			}else
				mTranslator.drawVertices(matSec.mStartIndex, matSec.mEndIndex-matSec.mStartIndex, GLDrawModes.TRIANGLES);
		}
		mTranslator.mFlushDisabled = false;
		vertexBuffer.reset();
		mGraphics.resetVertexBuffer();
	}

	public void updateSkinningVertices(YangArmaturePosture armaturePose) {
		if(mResultPositions==null)
			initResultValues();
		int weightBaseId = 0;
		for(int i=0;i<mPositions.length;i+=3) {
			float x = mPositions[i];
			float y = mPositions[i+1];
			float z = mPositions[i+2];

			float normX = mNormals[i];
			float normY = mNormals[i+1];
			float normZ = mNormals[i+2];

			float resX = 0;
			float resY = 0;
			float resZ = 0;
			float normResX = 0;
			float normResY = 0;
			float normResZ = 0;

			if(mSkinIds[weightBaseId]<0) {
				resX = x;
				resY = y;
				resZ = z;
				normResX = normX;
				normResY = normY;
				normResZ = normZ;
			}else{
				for(int k=0;k<mSkinJointsPerVertex;k++) {
					int jointId = mSkinIds[weightBaseId+k];
					if(jointId<0) {
						break;
					}
					float weight = mSkinWeights[weightBaseId+k];

					float[] matrix = armaturePose.mTransforms[jointId].mValues;

					normResX += (matrix[0] * normX + matrix[4] * normY + matrix[8] * normZ)*weight;
					normResY += (matrix[1] * normX + matrix[5] * normY + matrix[9] * normZ)*weight;
					normResZ += (matrix[2] * normX + matrix[6] * normY + matrix[10] * normZ)*weight;

					resX += (matrix[0] * x + matrix[4] * y + matrix[8] * z + matrix[12])*weight;
					resY += (matrix[1] * x + matrix[5] * y + matrix[9] * z + matrix[13])*weight;
					resZ += (matrix[2] * x + matrix[6] * y + matrix[10] * z + matrix[14])*weight;
				}
			}

			if(mNormalizeNormals) {
				float dist = (float)Math.sqrt(normResX*normResX + normResY*normResY + normResZ*normResZ);
				if(dist!=0) {
					dist = 1/dist;
					normResX *= dist;
					normResY *= dist;
					normResZ *= dist;
				}
			}

			mResultPositions[i] = resX;
			mResultPositions[i+1] = resY;
			mResultPositions[i+2] = resZ;
			mResultNormals[i] = normResX;
			mResultNormals[i+1] = normResY;
			mResultNormals[i+2] = normResZ;

			weightBaseId += mSkinJointsPerVertex;
		}
	}

	public void putBuffers(IndexedVertexBuffer vertexBuffer) {
		if(mWireFrames && mEdgeIndices!=null)
			vertexBuffer.putIndexArray(mEdgeIndices);
		else
			vertexBuffer.putIndexArray(mTriangleIndices);

		boolean skinningActive = mCurArmature!=null && mCurArmature.mTransforms.length>0;

		if(skinningActive && mAutoSkinningUpdate) {
			updateSkinningVertices(mCurArmature);
		}

		float[] positions = mResultPositions==null?mPositions:mResultPositions;
		float[] normals = mResultNormals==null?mNormals:mResultNormals;

		int i=0;
		if(mPosIndices!=null) {
			for(final int posInd:mPosIndices) {
				if(posInd>=0) {
					final int posId = posInd*3;

					vertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS, positions[posId],positions[posId+1],positions[posId+2]);

					if(mColors!=null)
						vertexBuffer.putArray(DefaultGraphics.ID_COLORS, mColors, posInd*4, 4);
				}else{
					vertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS, 0,0,0);
					if(mColors!=null)
						vertexBuffer.putArray(DefaultGraphics.ID_COLORS, mColors, 0, 4);
				}
				i++;
			}
		}else
			vertexBuffer.putArray(DefaultGraphics.ID_POSITIONS, mPositions);
		if(mTexCoords!=null && mTexCoords.length>0) {
			if(mTexCoordIndices!=null) {
				i = 0;
				for(final int texInd:mTexCoordIndices) {
					if(texInd<0)
						vertexBuffer.putVec2(DefaultGraphics.ID_TEXTURES, mTexCoords[i], mTexCoords[i+1]);
					else
						vertexBuffer.putVec2(DefaultGraphics.ID_TEXTURES, mTexCoords[texInd*2], mTexCoords[texInd*2+1]);
					i += 2;
				}
			}else
				vertexBuffer.putArray(DefaultGraphics.ID_TEXTURES, mTexCoords);
		}

		if(mColors==null)
			vertexBuffer.putArrayMultiple(DefaultGraphics.ID_COLORS, mDefaultColor.mValues, mVertexCount);
		vertexBuffer.putArrayMultiple(DefaultGraphics.ID_SUPPDATA, mSuppData.mValues,mVertexCount);

		if(normals!=null && normals.length>0) {
			if(mNormIndices==null)
				vertexBuffer.putArray(DefaultGraphics.ID_NORMALS, normals);
			else
				for(final int normInd:mNormIndices) {
					i = normInd*3;

					if(i<0)
						vertexBuffer.putVec3(Default3DGraphics.ID_NORMALS, 0,0,0);
					else
						vertexBuffer.putVec3(Default3DGraphics.ID_NORMALS, normals[i],normals[i+1],normals[i+2]);
				}
		}else{
			Default3DGraphics.fillNormals(vertexBuffer,0); //TODO not per draw call !
		}
	}

	public void drawDynamic() {
		if(mPositions==null)
			throw new RuntimeException("Cannot draw dynamic mesh after setting to completely static");
		final IndexedVertexBuffer vertexBuffer = mGraphics.getCurrentVertexBuffer();

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
		mTriangleIndices = null;
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

	public void createNormIndices() {
		mNormIndices = new int[mVertexCount];
		for(int i=0;i<mVertexCount;i++) {
			mNormIndices[i] = -1;
		}
		int c = 0;
		for(int i=0;i<mVertexCount;i++) {
			if(mNormIndices[i]!=-1)
				continue;
			final int smoothGroup = mSmoothIndices[i];
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

//	public void setShader(Basic3DProgram shader) {
//	if(mShader==shader)
//		return;
//	mShader = shader;
//	if(mHandles==null)
//		mHandles = new ObjHandles();
//	mHandles.setHandles(shader);
//}

	public YangMaterial findMaterial(String materialName) {
		for(final YangMaterialSet matSet:mMaterialSets) {
			if(matSet==null)
				continue;
			final YangMaterial mat = matSet.getMaterial(materialName);
			if(mat!=null)
				return mat;
		}
		return null;
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
		final int polyCount = mTriangleIndices.length/3;
		for(int i=0;i<polyCount;i++) {
			int i1 = mPosIndices[mTriangleIndices[i*3]]*3;
			int i2 = mPosIndices[mTriangleIndices[i*3+1]]*3;
			int i3 = mPosIndices[mTriangleIndices[i*3+2]]*3;
			final float dx1 = mPositions[i2]-mPositions[i1];
			final float dy1 = mPositions[i2+1]-mPositions[i1+1];
			final float dz1 = mPositions[i2+2]-mPositions[i1+2];
			final float dx2 = mPositions[i3]-mPositions[i1];
			final float dy2 = mPositions[i3+1]-mPositions[i1+1];
			final float dz2 = mPositions[i3+2]-mPositions[i1+2];
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
			i1 = mNormIndices[mTriangleIndices[i*3]]*3;
			i2 = mNormIndices[mTriangleIndices[i*3+1]]*3;
			i3 = mNormIndices[mTriangleIndices[i*3+2]]*3;
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

	public boolean hasArmatureWeights() {
		return mSkinIds!=null;
	}

	public void applyTransform(YangMatrix transform) {
		transform.applyToArray(mPositions, mPositions.length/3, true, 0,0, 0,0, mPositions, 0);
		if(mNormals!=null) {
			transform.asNormalTransform4f(tempMat.mValues);
			tempMat.applyToArray(mNormals, mNormals.length/3, true, 0,0, 0,0, mNormals, 0);
		}
	}

	public void createNeutralArmatureWeights() {
		initArmatureWeights();
		for(int i=0;i<mSkinIds.length;i++) {
			if(i%mSkinJointsPerVertex==0) {
				mSkinIds[i] = 0;
				mSkinWeights[i] = 1;
			}else{
				mSkinIds[i] = -1;
				mSkinWeights[i] = 0;
			}
		}
	}

	public void setZeroArmatureWeights() {
		initArmatureWeights();
		for(int i=0;i<mSkinIds.length;i++) {
			mSkinIds[i] = -1;
			mSkinWeights[i] = 0;
		}
	}

	public void normalizeArmatureWeights() {
		if(mSkinIds!=null) {
			int l = mSkinIds.length/mSkinJointsPerVertex;
			for(int i=0;i<l;i++) {
				float sum = 0;
				int baseId = i*mSkinJointsPerVertex;
				for(int j=0;j<mSkinJointsPerVertex;j++) {
					sum += mSkinWeights[baseId+j];
				}
				if(sum>0) {
//					if(Math.abs(sum-1)>0.001f)
//						System.out.println(sum);
					sum = 1/sum;
					for(int j=0;j<mSkinJointsPerVertex;j++) {
						mSkinWeights[baseId+j] *= sum;
					}
				}
			}
		}
	}

	public int getUniqueVertexCount() {
		return mUniqueVertexCount;
	}

	public int getSkinJointsPerVertex() {
		return mSkinJointsPerVertex;
	}

}
