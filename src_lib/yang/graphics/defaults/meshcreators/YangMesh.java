package yang.graphics.defaults.meshcreators;

import yang.graphics.buffers.DrawBatch;
import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.defaults.meshcreators.loaders.MeshMaterialHandles;
import yang.graphics.defaults.meshcreators.loaders.YangMaterialSection;
import yang.graphics.defaults.programs.subshaders.EmissiveSubShader;
import yang.graphics.defaults.programs.subshaders.SpecularLightBasicSubShader;
import yang.graphics.model.FloatColor;
import yang.graphics.model.material.YangMaterial;
import yang.graphics.model.material.YangMaterialSet;
import yang.graphics.programs.GLProgram;
import yang.graphics.textures.TextureProperties;
import yang.graphics.translator.AbstractGraphics;
import yang.graphics.translator.GraphicsTranslator;
import yang.math.objects.Point3f;
import yang.math.objects.Quadruple;
import yang.util.YangList;

public class YangMesh {

public static float PI = 3.1415926535f;

	public static YangMaterial DEFAULT_MATERIAL = new YangMaterial();

	public AbstractGraphics<?> mGraphics;
	protected GraphicsTranslator mTranslator;

	protected final MeshMaterialHandles mHandles;

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
	public int[] mSkinIds;
	public float[] mSkinWeights;
	public short[] mIndices;

	public FloatColor mColor = FloatColor.WHITE.clone();
	public Quadruple mSuppData = Quadruple.ZERO;
	public YangList<YangMaterialSet> mMaterialSets;
	public YangList<YangMaterialSection> mMaterialSections;

	public TextureProperties mTextureProperties;
	public boolean mUseShaders = true;
	protected int mSkinJointsPerVertex = 4;

	public YangArmature mCurArmature = null;

	public DrawBatch mDrawBatch;

	protected Point3f tempPoint = new Point3f();

	public YangMesh(AbstractGraphics<?> graphics,MeshMaterialHandles handles,TextureProperties textureProperties) {
		mGraphics = graphics;
		mTranslator = graphics.mTranslator;
		mHandles = handles;
		mTextureProperties = textureProperties;
	}

	public void createArmatureWeights(YangArmature armature) {
		if(mPositions==null)
			throw new RuntimeException("Cannot use armature on static mesh");
		int weights = mSkinJointsPerVertex;
		int vCount = mPositions.length/3;
		int l = vCount*weights;
		if(mSkinIds==null) {
			mSkinIds = new int[l];
			mSkinWeights = new float[l];
		}

		for(int i=0;i<vCount;i++) {

			int skinBaseId = i*weights;
			for(int k=0;k<weights;k++) {
				mSkinWeights[skinBaseId+k] = 0;
			}

			tempPoint.set(mPositions[i*3],mPositions[i*3+1],mPositions[i*3+2]);

			int j = 0;
			for(Point3f point:armature.mInitialPositions) {
				float dist = point.getDistance(tempPoint);
				float resWeight = 0;
				if(dist<=0.00001f) {
					resWeight = 1000000;
				}else{
					resWeight = 1f/(dist*dist);
				}
				float smallestWeight = Float.MAX_VALUE;
				int smallestWeightId = 0;
				for(int k=0;k<weights;k++) {
					if(mSkinWeights[skinBaseId+k]<smallestWeight) {
						smallestWeight = mSkinWeights[skinBaseId+k];
						smallestWeightId = k;
					}
				}
				if(resWeight>smallestWeight) {
					mSkinWeights[skinBaseId+smallestWeightId] = resWeight;
					mSkinIds[skinBaseId+smallestWeightId] = j;
				}
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

			mTranslator.drawVertices(matSec.mStartIndex, matSec.mEndIndex-matSec.mStartIndex, GraphicsTranslator.T_TRIANGLES);
		}
		mTranslator.mFlushDisabled = false;
		vertexBuffer.reset();
		mGraphics.resetVertexBuffer();
	}

	public void putBuffers(IndexedVertexBuffer vertexBuffer) {
		vertexBuffer.putIndexArray(mIndices);

		for(final int posInd:mPosIndices) {
			final int i = posInd*3;
			if(i<0)
				vertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS, 0,0,0);
			else{
				//Skinning
				if(mCurArmature!=null) {
					float x = mPositions[i];
					float y = mPositions[i+1];
					float z = mPositions[i+2];
					float resX = 0;
					float resY = 0;
					float resZ = 0;
					int weightBaseId = posInd*mSkinJointsPerVertex;
					float maxWeight = 0;
					for(int k=0;k<mSkinJointsPerVertex;k++) {
						float weight = mSkinWeights[weightBaseId+k];
						int jointId = mSkinIds[weightBaseId+k];
						float[] matrix = mCurArmature.mTransforms[jointId].mValues;
						//if(weight>maxWeight) {maxWeight = weight;weight = 1;

							resX += (matrix[0] * x + matrix[4] * y + matrix[8] * z + matrix[12])*weight;
							resY += (matrix[1] * x + matrix[5] * y + matrix[9] * z + matrix[13])*weight;
							resZ += (matrix[2] * x + matrix[6] * y + matrix[10] * z + matrix[14])*weight;
						//}
					}
					vertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS, resX,resY,resZ);
				}else
					vertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS, mPositions[i],mPositions[i+1],mPositions[i+2]);
			}
		}
//		vertexBuffer.putArray(DefaultGraphics.ID_POSITIONS, mPositions);
		if(mTexCoords!=null && mTexCoords.length>0)
			for(final int texInd:mTexCoordIndices) {
				final int i = texInd*2;
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
					for(final int normInd:mNormIndices) {
						final int i = normInd*3;
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
		final int polyCount = mIndices.length/3;
		for(int i=0;i<polyCount;i++) {
			int i1 = mPosIndices[mIndices[i*3]]*3;
			int i2 = mPosIndices[mIndices[i*3+1]]*3;
			int i3 = mPosIndices[mIndices[i*3+2]]*3;
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

	public boolean hasArmatureWeights() {
		return mSkinIds!=null;
	}

}
