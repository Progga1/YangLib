package yang.graphics.defaults;

import javax.vecmath.Vector3f;

import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.meshcreators.SphereCreator;
import yang.graphics.defaults.meshcreators.TerrainCreator;
import yang.graphics.programs.Basic3DProgram;
import yang.graphics.textures.TextureCoordinatesQuad;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.util.Camera3D;
import yang.math.objects.matrix.YangMatrix;
import yang.math.objects.matrix.YangMatrixCameraOps;


public class Default3DGraphics extends DefaultGraphics<Basic3DProgram> {
	
	public static final float[][] NEUTRAL_ELEMENTS = {{0,0,0},{0,0},{1,1,1,1},{0,0,0,0},{0,1,0}};
	private static final int NORMAL_ELEM_SIZE = 3;
	private static final int POSITION_ELEM_SIZE = 3;
	public static final float[] ZERO_FLOAT_3 = {0,0,0};
	
	public static final float[] CUBE_FRONT = {
		-0.5f, -0.5f, 0.5f,
		0.5f, -0.5f, 0.5f,
		-0.5f, 0.5f, 0.5f,
		0.5f, 0.5f, 0.5f
	};
	
	public static final float[] CUBE_RIGHT = swapCoords(CUBE_FRONT,3,2,-1);
	public static final float[] CUBE_LEFT = swapCoords(CUBE_FRONT,-3,2,1);
	public static final float[] CUBE_BACK = swapCoords(CUBE_FRONT,-1,2,-3);
	public static final float[] CUBE_TOP = swapCoords(CUBE_FRONT,1,3,-2);
	public static final float[] CUBE_BOTTOM = swapCoords(CUBE_FRONT,1,-3,2);
	
	protected boolean mBillboardMode;
	public YangMatrixCameraOps mCameraMatrix;
	private YangMatrix mInterMatrix;
	public YangMatrix mSavedCamera;
	public YangMatrix mSavedProjection;
	public float[] mSavedInvGameProjection;
	
	private Basic3DProgram mDefaultProgram;
	private TerrainCreator mDefaultTerrainCreator;
	private SphereCreator mSphereCreator;
	
	protected boolean mCurIsPerspective;
	protected float mCurNear,mCurFar,mCurFovy,mCurZoom;
	
	public static float[] swapCoords(float[] original,int x,int y,int z) {
		float[] result = new float[12];
		for(int i=0;i<4;i++) {
			int id = i*3;
			result[id] = original[id+Math.abs(x)-1]*Math.signum(x);
			result[id+1] = original[id+Math.abs(y)-1]*Math.signum(y);
			result[id+2] = original[id+Math.abs(z)-1]*Math.signum(z);
		}
		return result;
	}
	
	public Default3DGraphics(GraphicsTranslator translator) {
		super(translator,3);
		mCurrentZ = 0;
		mCameraMatrix = new YangMatrixCameraOps();
		mInterMatrix = mTranslator.createTransformationMatrix();
		mCameraMatrix.set(3,3,1);
		mDefaultTerrainCreator = new TerrainCreator(this);
		mSphereCreator = new SphereCreator(this);
		mBillboardMode = false;
	}
	
	@Override
	public void bindBuffers() {
		super.bindBuffers();
		if (mCurrentProgram.mHasNormal)
			mTranslator.setAttributeBuffer(mCurrentProgram.mNormalHandle, DefaultGraphics.ID_NORMALS);
		assert mTranslator.checkErrorInst("Bind buffers 3D");
	}

	@Override
	public void enableBuffers() {
		super.enableBuffers();
		if (mCurrentProgram.mHasNormal)
			mTranslator.enableAttributePointer(mCurrentProgram.mNormalHandle);
		assert mTranslator.checkErrorInst("Enable buffers 3D");
	}

	@Override
	public void disableBuffers() {
		super.disableBuffers();
		if (mCurrentProgram.mHasNormal)
			mTranslator.disableAttributePointer(mCurrentProgram.mNormalHandle);
		assert mTranslator.checkErrorInst("Disable buffers 3D");
	}

	
	@Override
	public IndexedVertexBuffer createVertexBuffer(boolean dynamicVertices, boolean dynamicIndices, int maxIndices,int maxVertices) {
		IndexedVertexBuffer vertexBuffer = mTranslator.createVertexBuffer(dynamicVertices,dynamicIndices,maxIndices,maxVertices);
		vertexBuffer.init(new int[]{POSITION_ELEM_SIZE,2,4,4,NORMAL_ELEM_SIZE},NEUTRAL_ELEMENTS);
		return vertexBuffer;
	}
	
	@Override
	public void derivedInit() {
		super.derivedInit();
		setOrthogonalProjection();
		resetCamera();
		mDefaultProgram = new Basic3DProgram();
		mTranslator.addProgram(mDefaultProgram);
	}
	
	public void resetCamera() {
		setCameraLookAt(0,0,0, 0,0,-1);
	}
	
	public void resetProjection() {
		setOrthogonalProjection();
	}

	public void setOrthogonalProjection(float width,float height,float near,float far) {
		mCurIsPerspective = false;
		mCurNear = near;
		mCurFar = far;
		mCurZoom = 1;
		mProjectionTransform.setOrthogonalProjection(-width*0.5f,width*0.5f,height*0.5f,-height*0.5f,near,far);
		mProjectionTransform.asInverted(invGameProjection);
	}
		
	public void setOrthogonalProjection(float near,float far,float zoom) {
		setOrthogonalProjection(mTranslator.mCurrentScreen.getSurfaceRatioX()*zoom*2,mTranslator.mCurrentScreen.getSurfaceRatioY()*zoom*2,near,far);
		mCurZoom = zoom;
	}

		
	public void setOrthogonalProjection(float near,float far) {
		setOrthogonalProjection(near,far,1);
	}
	
	public void setOrthogonalProjection() {
		setOrthogonalProjection(YangMatrix.DEFAULT_NEAR,YangMatrix.DEFAULT_FAR);
	}

	public void setPerspectiveProjection(float fovy, float near, float far,float stretchX) {
		mCurIsPerspective = true;
		mCurNear = near;
		mCurFar = far;
		mCurFovy = fovy;
		mProjectionTransform.setPerspectiveProjectionFovy(fovy, mTranslator.mCurrentScreen.getSurfaceRatioX()*stretchX,mTranslator.mCurrentScreen.getSurfaceRatioY(), near, far);
	}
	
	public void setPerspectiveProjection(float fovy, float near, float far) {
		setPerspectiveProjection(fovy,near,far,1);
	}
	
	public void setPerspectiveProjection(float range) {
		setPerspectiveProjection(0.6f,0.02f,range);
	}
	
//	public void drawRectZ(float worldX1, float worldY1, float worldX2, float worldY2, float z, TransformationMatrix textureTransform) {
//		mCurrentVertexBuffer.beginQuad(mTranslator.mWireFrames);
//		putPositionRect(worldX1, worldY1, worldX2, worldY2);
//		putTextureArray(textureTransform.mAppliedRect);
//		putColorRect(mCurColor);
//		putSuppDataRect(mCurSuppData);
//	}
	
	public void drawRectZ(float worldX1, float worldY1, float worldX2, float worldY2, float z, TextureCoordinatesQuad textureCoordinates) {
		mCurrentVertexBuffer.beginQuad(mTranslator.mWireFrames);
		putPositionRect(worldX1, worldY1, worldX2, worldY2);
		putTextureArray(textureCoordinates.mAppliedCoordinates);
		putColorRect(mCurColor);
		putSuppDataRect(mCurSuppData);
	}
	
	public void drawRectZ(float x1, float y1, float x2, float y2, float z) {
		drawRectZ(x1,y1,x2,y2,z,mTexIdentity);
	}
	
	public void setCameraLookAt(float eyeX,float eyeY,float eyeZ, float lookAtX,float lookAtY,float lookAtZ, float upX,float upY,float upZ) {
		mTranslator.flush();
		mCameraMatrix.setLookAt(eyeX,eyeY,eyeZ, lookAtX,lookAtY,lookAtZ, upX,upY,upZ);
	}

	public void setCameraLookAt(float eyeX,float eyeY, float eyeZ, float lookAtX,float lookAtY,float lookAtZ) {
		setCameraLookAt(eyeX,eyeY,eyeZ, lookAtX,lookAtY,lookAtZ, 0,1,0);
	}
	
	public void setCamera(Camera3D camera) {
		setCameraLookAt(camera.mEyeX,camera.mEyeY,camera.mEyeZ, camera.mLookAtX,camera.mLookAtY,camera.mLookAtZ, camera.mUpX,camera.mUpY,camera.mUpZ);
	}
	
	public void setCameraAlphaBeta(float lookAtX, float lookAtY, float lookAtZ, float alpha, float beta, float distance) {
		mTranslator.flush();
		mCameraMatrix.setLookAtAlphaBeta(lookAtX,lookAtY,lookAtZ, alpha,beta, distance);
	}
	
	public void setCameraAlphaBeta(float alpha, float beta, float distance) {
		setCameraAlphaBeta(0,0,0, alpha,beta, distance);
	}
	
	@Override
	public void refreshResultTransform() {
		if(mBillboardMode) {
			mCameraProjectionMatrix.set(mCameraMatrix);
			mCameraProjectionMatrix.setTranslationOnly();
			mCameraProjectionMatrix.multiplyLeft(mProjectionTransform);
		}else
			mCameraProjectionMatrix.multiply(mProjectionTransform,mCameraMatrix);
	}
	
	public void putCubePart(float[] array,YangMatrix transform) {
		mCurrentVertexBuffer.beginQuad(mTranslator.mWireFrames);
		if(transform==null)
			mCurrentVertexBuffer.putArray(ID_POSITIONS,array);
		else
			mCurrentVertexBuffer.putTransformedArray3D(ID_POSITIONS,array,4,transform.mMatrix);
		putTextureArray(RECT_TEXTURECOORDS);
		putColorRect(mCurColor);
		putSuppDataRect(mCurSuppData);
	}
	
	public void drawCubeCentered(YangMatrix transform) {
		putCubePart(CUBE_FRONT,transform);
		putCubePart(CUBE_BACK,transform);
		putCubePart(CUBE_LEFT,transform);
		putCubePart(CUBE_RIGHT,transform);
		putCubePart(CUBE_TOP,transform);
		putCubePart(CUBE_BOTTOM,transform);
	}

	public void drawCubeCentered(float x,float y,float z,float size) {
		mInterMatrix.loadIdentity();
		mInterMatrix.translate(x, y, z);
		mInterMatrix.scale(size,size,size);
		drawCubeCentered(mInterMatrix);
	}
	
	public void drawCuboidCentered(float x,float y,float z,float sizeX,float sizeY,float sizeZ) {
		mInterMatrix.loadIdentity();
		mInterMatrix.translate(x, y, z);
		mInterMatrix.scale(sizeX,sizeY,sizeZ);
		drawCubeCentered(mInterMatrix);
	}
	
	public void drawCuboidCentered(float x,float y,float z,float sizeX,float sizeY,float sizeZ,YangMatrix transform) {
		mInterMatrix.loadIdentity();
		mInterMatrix.translate(x, y, z);
		mInterMatrix.scale(sizeX,sizeY,sizeZ);
		if(transform!=null)
			mInterMatrix.multiplyLeft(transform);
		drawCubeCentered(mInterMatrix);
	}
	
	public void drawSphere(int verticesAlpha,int verticesBeta,YangMatrix transform,float textureCoordFactorX,float textureCoordFactorY) {
		mSphereCreator.begin(verticesAlpha,verticesBeta,1,1,1);
		mSphereCreator.putPositions(transform,true);
		mSphereCreator.putTerrainTextureRect(0,0,textureCoordFactorX,textureCoordFactorY);
		mSphereCreator.putGridColor(mCurColor);
		mSphereCreator.putGridSuppData(mCurSuppData);
		mSphereCreator.finish();
	}
	
	public void drawSphere(int verticesAlpha,int verticesBeta,float centerX,float centerY,float centerZ, float radius, float textureCoordFactorX,float textureCoordFactorY) {
		mInterMatrix.loadIdentity();
		mInterMatrix.translate(centerX, centerY, centerZ);
		mInterMatrix.scale(radius);
		drawSphere(verticesAlpha,verticesBeta,mInterMatrix,textureCoordFactorX,textureCoordFactorY);
	}

	private Vector3f vec1 = new Vector3f();
	private Vector3f vec2 = new Vector3f();
	private Vector3f vec3 = new Vector3f();
	private float[] arr1 = new float[3];
	private float[] arr2 = new float[3];
	
	public void fillNormals(int firstNormalId) {
		//Set zero
		int endNormal = mPositions.position();
		mNormals.position(firstNormalId*POSITION_ELEM_SIZE);
		while(mNormals.position()<endNormal) {
			mNormals.put(ZERO_FLOAT_3);
		}
		
		//Compute cross products
		int endIndex = mIndexBuffer.position()/3;
		mIndexBuffer.rewind();
		int i=0;
		while(i<endIndex) {
			int v1 = mIndexBuffer.get()*POSITION_ELEM_SIZE;
			int v2 = mIndexBuffer.get()*POSITION_ELEM_SIZE;
			int v3 = mIndexBuffer.get()*POSITION_ELEM_SIZE;
			vec1.set(mPositions.get(v2)-mPositions.get(v1), mPositions.get(v2+1)-mPositions.get(v1+1),mPositions.get(v2+2)-mPositions.get(v1+2));
			vec2.set(mPositions.get(v3)-mPositions.get(v1), mPositions.get(v3+1)-mPositions.get(v1+1),mPositions.get(v3+2)-mPositions.get(v1+2));
			vec3.cross(vec1, vec2);
			vec3.normalize();
			mNormals.put(v1, mNormals.get(v1)+vec3.x);
			mNormals.put(v1+1, mNormals.get(v1+1)+vec3.y);
			mNormals.put(v1+2, mNormals.get(v1+2)+vec3.z);
			mNormals.put(v2, mNormals.get(v2)+vec3.x);
			mNormals.put(v2+1, mNormals.get(v2+1)+vec3.y);
			mNormals.put(v2+2, mNormals.get(v2+2)+vec3.z);
			mNormals.put(v3, mNormals.get(v3)+vec3.x);
			mNormals.put(v3+1, mNormals.get(v3+1)+vec3.y);
			mNormals.put(v3+2, mNormals.get(v3+2)+vec3.z);
			i++;
		}
		
		//Normalize
		mNormals.position(firstNormalId*POSITION_ELEM_SIZE);
		while(mNormals.position()<endNormal) {
			mNormals.get(arr1);
			vec1.set(arr1);
			vec1.normalize();
			mNormals.position(mNormals.position()-3);
			mNormals.put(vec1.x);
			mNormals.put(vec1.y);
			mNormals.put(vec1.z);
		}
	}

	public void mergeNormals(int vertex1Id, int vertex2Id) {
		mNormals.position(vertex1Id*POSITION_ELEM_SIZE);
		mNormals.get(arr1);
		vec1.set(arr1);
		mNormals.position(vertex2Id*POSITION_ELEM_SIZE);
		mNormals.get(arr2);
		vec2.set(arr2);
		vec1.add(vec1);
		vec1.normalize();
		mNormals.position(vertex1Id*POSITION_ELEM_SIZE);
		mNormals.put(vec1.x);
		mNormals.put(vec1.y);
		mNormals.put(vec1.z);
		mNormals.position(vertex2Id*POSITION_ELEM_SIZE);
		mNormals.put(vec1.x);
		mNormals.put(vec1.y);
		mNormals.put(vec1.z);
	}

	@Override
	public Basic3DProgram getDefaultProgram() {
		return mDefaultProgram;
	}

	public void switchBillboardMode(boolean enabled) {
		mTranslator.flush();
		mBillboardMode = enabled;
	}

//	public void setCameraProjection(TransformationMatrix camera, TransformationMatrix projection,float[] invProjection) {
//		mTranslator.flush();
//		mSavedCamera = mCameraMatrix;
//		mSavedProjection = mCurProjTransform;
//		mSavedInvGameProjection = invGameProjection;
//		
//		mCameraMatrix = camera;
//		mProjectionTransform = projection;
//		mCurProjTransform = projection;
//		invGameProjection = invProjection;
//	}
//	
//	public void restoreCameraProjection() {
//		mTranslator.flush();
//		mCameraMatrix = mSavedCamera;
//		mCurProjTransform = mSavedProjection;
//		mProjectionTransform = mSavedProjection;
//		invGameProjection = mSavedInvGameProjection;
//	}
	
	@Override
	public void onSurfaceSizeChanged(int width,int height) {
		if(mCurIsPerspective) {
			setPerspectiveProjection(mCurFovy,mCurNear,mCurFar);
		}else{
			setOrthogonalProjection(mCurNear,mCurFar,mCurZoom);
		}
	}
	
}
