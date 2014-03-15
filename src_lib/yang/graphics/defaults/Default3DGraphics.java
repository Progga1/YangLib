package yang.graphics.defaults;



import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.geometrycreators.LineDrawer3D;
import yang.graphics.defaults.geometrycreators.SphereCreator;
import yang.graphics.model.FloatColor;
import yang.graphics.programs.Basic3DProgram;
import yang.graphics.textures.TextureCoordinatesQuad;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.util.Camera3D;
import yang.math.objects.Point3f;
import yang.math.objects.Quadruple;
import yang.math.objects.Vector3f;
import yang.math.objects.matrix.YangMatrix;
import yang.math.objects.matrix.YangMatrixCameraOps;


public class Default3DGraphics extends DefaultGraphics<Basic3DProgram> {

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
	public YangMatrixCameraOps mOriginalCameraMatrix;
	private final YangMatrix mInterMatrix;
	public YangMatrix mSavedCamera;
	public YangMatrix mSavedProjection;
	public float[] mSavedInvGameProjection;
	public float mDebugAxisWidthFactor = 1;
	private final Point3f mCameraPosition = new Point3f();

	private Basic3DProgram mDefaultProgram;
	private final SphereCreator mSphereCreator;
	public final LineDrawer3D mLineDrawer;

	protected boolean mCurIsPerspective;
	private float mCurNear,mCurFar,mCurFovy,mCurZoom;
	public float mSensorZ = 0.05f;


	//Temp vars
	private final float[] mTemp4f = new float[4];


	public static float[] swapCoords(float[] original,int x,int y,int z) {
		final float[] result = new float[12];
		for(int i=0;i<4;i++) {
			final int id = i*3;
			result[id] = original[id+Math.abs(x)-1]*Math.signum(x);
			result[id+1] = original[id+Math.abs(y)-1]*Math.signum(y);
			result[id+2] = original[id+Math.abs(z)-1]*Math.signum(z);
		}
		return result;
	}

	@Override
	public void shareBuffers(DefaultGraphics<?> graphics) {
		initDynamicBuffer();
		mDynamicVertexBuffer.linkBuffer(DefaultGraphics.ID_POSITIONS, graphics.mDynamicVertexBuffer, DefaultGraphics.ID_POSITIONS);
	}

	public Default3DGraphics(GraphicsTranslator translator) {
		super(translator,3);
		mCurrentZ = 0;
		mCameraMatrix = new YangMatrixCameraOps();
		mOriginalCameraMatrix = new YangMatrixCameraOps();
		mInterMatrix = mTranslator.createTransformationMatrix();
		mCameraMatrix.set(3,3, 1);
		mSphereCreator = new SphereCreator(this);
		mBillboardMode = false;
		mLineDrawer = new LineDrawer3D(this);
		refreshCamera();
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
	protected int[] getBufferElementSizes() {
		return new int[]{POSITION_ELEM_SIZE,2,4,4,NORMAL_ELEM_SIZE};
	}

	@Override
	protected float[][] getNeutralBufferElements() {
		return new float[][]{{0,0,0},{0,0},{1,1,1,1},{0,0,0,0},{0,1,0}};
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

	public void setDefaultView() {
		resetCamera();
		resetProjection();
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
		setOrthogonalProjection(mTranslator.mCurrentSurface.getSurfaceRatioX()*zoom*2,mTranslator.mCurrentSurface.getSurfaceRatioY()*zoom*2,near,far);
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
		mProjectionTransform.setPerspectiveProjectionFovy(fovy, mTranslator.mCurrentSurface.getSurfaceRatioX()*stretchX,mTranslator.mCurrentSurface.getSurfaceRatioY(), near, far);
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
		mCurrentVertexBuffer.beginQuad();
		putPositionRect(worldX1, worldY1, worldX2, worldY2);
		putTextureArray(textureCoordinates.mAppliedCoordinates);
		putColorRect(mCurColor);
		putSuppDataRect(mCurSuppData);
	}

	public void drawRectZ(float x1, float y1, float x2, float y2, float z) {
		drawRectZ(x1,y1,x2,y2,z,mTexIdentity);
	}

	private void refreshCamera() {
		mOriginalCameraMatrix.set(mCameraMatrix);
		final boolean stereo = mTranslator.isStereo();
		if(mTranslator.getRenderTargetStackLevel()<=(stereo?0:-1)) {
			if(mTranslator.mSensorCameraEnabled) {
				mCameraMatrix.postTranslate(0, 0, mSensorZ);
				mCameraMatrix.multiplyLeft(mTranslator.mSensorCameraMatrix);
				mCameraMatrix.postTranslate(0, 0, -mSensorZ);

			}
			if(stereo) {
				mCameraMatrix.postTranslate(-mTranslator.mCameraShiftX, 0);
			}
		}
	}

	public void setCameraLookAt(float eyeX,float eyeY,float eyeZ, float lookAtX,float lookAtY,float lookAtZ, float upX,float upY,float upZ) {
		mTranslator.flush();
		mCameraPosition.set(eyeX,eyeY,eyeZ);
		mCameraMatrix.setLookAt(eyeX,eyeY,eyeZ, lookAtX,lookAtY,lookAtZ, upX,upY,upZ);
		refreshCamera();
	}

	public void setCameraLookAt(float eyeX,float eyeY, float eyeZ, float lookAtX,float lookAtY,float lookAtZ) {
		setCameraLookAt(eyeX,eyeY,eyeZ, lookAtX,lookAtY,lookAtZ, 0,1,0);
	}

	public void setCamera(Camera3D camera) {
		setCameraLookAt(camera.mEyeX,camera.mEyeY,camera.mEyeZ, camera.mLookAtX,camera.mLookAtY,camera.mLookAtZ, camera.mUpX,camera.mUpY,camera.mUpZ);
	}

	public void setCameraAlphaBeta(float lookAtX, float lookAtY, float lookAtZ, float alpha, float beta, float distance) {
		mTranslator.flush();
		mCameraMatrix.setLookAtAlphaBeta(lookAtX,lookAtY,lookAtZ, alpha,beta, distance, mCameraPosition);
		refreshCamera();
	}

	public void setCameraAlphaBeta(float alpha, float beta, float distance) {
		setCameraAlphaBeta(0,0,0, alpha,beta, distance);
	}

	public void setCameraByTransform(YangMatrix cameraTransform) {
		cameraTransform.getTranslation(mCameraPosition);
		cameraTransform.asInverted(mCameraMatrix.mValues);
		refreshCamera();
	}

	@Override
	public void refreshViewTransform() {
		if(mBillboardMode) {
			mCameraProjectionMatrix.set(mCameraMatrix);
			mCameraProjectionMatrix.setTranslationOnly();
			mCameraProjectionMatrix.multiplyLeft(mProjectionTransform);
		}else
			mCameraProjectionMatrix.multiply(mProjectionTransform,mCameraMatrix);
	}

	public void putCubePart(float[] array,Vector3f norm,YangMatrix transform) {
		mCurrentVertexBuffer.beginQuad();
		if(transform==null)
			mCurrentVertexBuffer.putArray(ID_POSITIONS,array);
		else
			mCurrentVertexBuffer.putTransformedArray3D(ID_POSITIONS,array,4,transform.mValues);
		putTextureArray(RECT_TEXTURECOORDS);
		putColorRect(mCurColor);
		putSuppDataRect(mCurSuppData);
		putNormal(norm.mX,norm.mY,norm.mZ);
		putNormal(norm.mX,norm.mY,norm.mZ);
		putNormal(norm.mX,norm.mY,norm.mZ);
		putNormal(norm.mX,norm.mY,norm.mZ);
	}

	public void drawCubeCentered(YangMatrix transform) {
		putCubePart(CUBE_FRONT,Vector3f.FORWARD, transform);
		putCubePart(CUBE_BACK,Vector3f.BACKWARD,transform);
		putCubePart(CUBE_LEFT,Vector3f.LEFT,transform);
		putCubePart(CUBE_RIGHT,Vector3f.RIGHT,transform);
		putCubePart(CUBE_TOP,Vector3f.UP,transform);
		putCubePart(CUBE_BOTTOM,Vector3f.DOWN,transform);
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
		mSphereCreator.putGridTextureRect(0,0,textureCoordFactorX,textureCoordFactorY);
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

	public void drawSphere(int verticesAlpha,int verticesBeta,Point3f position, float radius, float textureCoordFactorX,float textureCoordFactorY) {
		drawSphere(verticesAlpha,verticesBeta,position.mX,position.mY,position.mZ,radius,textureCoordFactorX,textureCoordFactorY);
	}

	private static Vector3f vec1 = new Vector3f();
	private static Vector3f vec2 = new Vector3f();
	private static Vector3f vec3 = new Vector3f();
	private static float[] arr1 = new float[3];
	private static float[] arr2 = new float[3];

	public static void fillNormals(ShortBuffer indexBuffer,FloatBuffer positions,FloatBuffer normalsTarget,int startIndex) {
		//Set zero
		final int endNormal = positions.position();
		int firstNormalId = normalsTarget.position();
		while(normalsTarget.position()<endNormal) {
			normalsTarget.put(ZERO_FLOAT_3);
		}

		//Compute cross products
		final int endIndex = indexBuffer.position()/3;
		int i=0;
		indexBuffer.rewind();
		while(i<endIndex) {
			final int v1 = indexBuffer.get()*POSITION_ELEM_SIZE;
			final int v2 = indexBuffer.get()*POSITION_ELEM_SIZE;
			final int v3 = indexBuffer.get()*POSITION_ELEM_SIZE;
			vec1.set(positions.get(v2)-positions.get(v1), positions.get(v2+1)-positions.get(v1+1),positions.get(v2+2)-positions.get(v1+2));
			vec2.set(positions.get(v3)-positions.get(v1), positions.get(v3+1)-positions.get(v1+1),positions.get(v3+2)-positions.get(v1+2));
			vec3.cross(vec1, vec2);
			vec3.normalize();
			normalsTarget.put(v1, normalsTarget.get(v1)+vec3.mX);
			normalsTarget.put(v1+1, normalsTarget.get(v1+1)+vec3.mY);
			normalsTarget.put(v1+2, normalsTarget.get(v1+2)+vec3.mZ);
			normalsTarget.put(v2, normalsTarget.get(v2)+vec3.mX);
			normalsTarget.put(v2+1, normalsTarget.get(v2+1)+vec3.mY);
			normalsTarget.put(v2+2, normalsTarget.get(v2+2)+vec3.mZ);
			normalsTarget.put(v3, normalsTarget.get(v3)+vec3.mX);
			normalsTarget.put(v3+1, normalsTarget.get(v3+1)+vec3.mY);
			normalsTarget.put(v3+2, normalsTarget.get(v3+2)+vec3.mZ);
			i++;
		}

		//Normalize
		normalsTarget.position(firstNormalId);
		while(normalsTarget.position()<endNormal) {
			normalsTarget.get(arr1);
			vec1.set(arr1);
			vec1.normalize();
			normalsTarget.position(normalsTarget.position()-3);
			normalsTarget.put(vec1.mX);
			normalsTarget.put(vec1.mY);
			normalsTarget.put(vec1.mZ);
		}
	}

	public static void fillNormals(IndexedVertexBuffer vertexBuffer,int startIndex) {
		fillNormals(vertexBuffer.mIndexBuffer,vertexBuffer.getFloatBuffer(ID_POSITIONS),vertexBuffer.getFloatBuffer(ID_NORMALS),startIndex);
	}

	public void fillNormals(int startIndex) {
		fillNormals(mIndexBuffer,mPositions,mNormals,startIndex);
	}

	public void fillNormals() {
		fillNormals(0);
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
		mNormals.put(vec1.mX);
		mNormals.put(vec1.mY);
		mNormals.put(vec1.mZ);
		mNormals.position(vertex2Id*POSITION_ELEM_SIZE);
		mNormals.put(vec1.mX);
		mNormals.put(vec1.mY);
		mNormals.put(vec1.mZ);
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

	public void getCameraRightVector(Vector3f target) {
		final float[] mat = this.mCameraMatrix.mValues;
		target.mX = mat[0];
		target.mY = mat[4];
		target.mZ = mat[8];
	}

	public void getCameraUpVector(Vector3f target) {
		final float[] mat = this.mCameraMatrix.mValues;
		target.mX = mat[1];
		target.mY = mat[5];
		target.mZ = mat[9];
	}

	public Point3f getCameraPosition() {
		return mCameraPosition;
	}

	public static float DEBUG_AXIS_WIDTH = 0.03f;

	private final Vector3f mTempVec1 = new Vector3f();
	private final Vector3f mTempVec2 = new Vector3f();

	public void drawDebugVector(float baseX,float baseY,float baseZ, float vecX,float vecY,float vecZ,FloatColor color,float alpha,YangMatrix transform) {
		color.copyToArray(mTemp4f);
		mTemp4f[3] = alpha;
		final int vertexCount = mLineDrawer.getLineVertexCount();
		if(transform==null) {
			mTempVec1.set(baseX,baseY,baseZ);
			mTempVec2.set(baseX+vecX,baseY+vecY,baseZ+vecZ);
		}else{
			transform.apply3D(baseX,baseY,baseZ, mTempVec1);
			transform.apply3D(baseX+vecX,baseY+vecY,baseZ+vecZ, mTempVec2);
		}
		mLineDrawer.drawLine(mTempVec1, mTempVec2, DEBUG_AXIS_WIDTH*mDebugAxisWidthFactor,0);
		mCurrentVertexBuffer.putArrayMultiple(ID_COLORS, mTemp4f, vertexCount);
		mCurrentVertexBuffer.putArrayMultiple(ID_SUPPDATA, Quadruple.ZERO.mValues, vertexCount);
	}

	public void drawDebugVector(float baseX,float baseY,float baseZ, Vector3f vector,FloatColor color,float scale,float alpha,YangMatrix transform) {
		drawDebugVector(baseX,baseY,baseZ,vector.mX*scale,vector.mY*scale,vector.mZ*scale,color,alpha,transform);
	}

	public void drawDebugVector(Point3f base, Vector3f vector,FloatColor color,float scale,float alpha,YangMatrix transform) {
		drawDebugVector(base.mX,base.mY,base.mZ,vector.mX*scale,vector.mY*scale,vector.mZ*scale,color,alpha,transform);
	}

	public void drawDebugCoordinateAxes(FloatColor xColor,FloatColor yColor,FloatColor zColor,float scale,float alpha,YangMatrix transform) {
		mTranslator.flush();
		mTranslator.switchCulling(true);
		drawDebugVector(0,0,0, scale,0,0, xColor,alpha,transform);
		drawDebugVector(0,0,0, 0,scale,0, yColor,alpha,transform);
		drawDebugVector(0,0,0, 0,0,scale, zColor,alpha,transform);
	}

	public void drawDebugCoordinateAxes(FloatColor xColor,FloatColor yColor,FloatColor zColor,float scale,float alpha) {
		drawDebugCoordinateAxes(xColor,yColor,zColor,scale,alpha,null);
	}

	public void drawDebugCoordinateAxes() {
		drawDebugCoordinateAxes(FloatColor.RED,FloatColor.GREEN,FloatColor.BLUE,1,1);
	}

	public void drawLine3D(float fromX,float fromY,float fromZ, float toX,float toY,float toZ, float startWidth,float endWidth) {
		final int indexId = mCurrentVertexBuffer.getCurrentIndexWriteCount();
		mLineDrawer.drawLine(fromX,fromY,fromZ, toX,toY,toZ, startWidth,endWidth);
		mLineDrawer.mCylinder.putColor(mCurColor);
		mLineDrawer.mCylinder.putSuppData(mCurSuppData);
		mLineDrawer.mCylinder.putTextureCoordinates();
		fillNormals(indexId);
	}

	public void drawLine3D(float fromX,float fromY,float fromZ, float toX,float toY,float toZ) {
		drawLine3D(fromX,fromY,fromZ, toX,toY,toZ, mLineDrawer.mLineWidth,mLineDrawer.mLineWidth);
	}

	public void drawLine3D(Point3f startPoint,Point3f endPoint, float startWidth,float endWidth) {
		drawLine3D(startPoint.mX,startPoint.mY,startPoint.mZ, endPoint.mX,endPoint.mY,endPoint.mZ, startWidth,endWidth);
	}

	public void drawLine3D(Point3f startPoint,Point3f endPoint) {
		drawLine3D(startPoint.mX,startPoint.mY,startPoint.mZ, endPoint.mX,endPoint.mY,endPoint.mZ, mLineDrawer.mLineWidth,mLineDrawer.mLineWidth);
	}

	public void drawLine3DRect(float worldX1, float worldY1, float worldX2, float worldY2, float z, float width) {
		drawLine3D(worldX1,worldY1,z, worldX2+width*0.5f,worldY1,z, width,width);
		drawLine3D(worldX2,worldY1,z, worldX2,worldY2+width*0.5f,z, width,width);
		drawLine3D(worldX2,worldY2,z, worldX1-width*0.5f,worldY2,z, width,width);
		drawLine3D(worldX1,worldY2,z, worldX1,worldY1-width*0.5f,z, width,width);
	}

	private final Vector3f tempVec1 = new Vector3f();
	private final Vector3f tempVec2 = new Vector3f();
	private final YangMatrixCameraOps mProjectionMatrix = new YangMatrixCameraOps();

	public void prepareProjection() {
		this.getToScreenTransform(mProjectionMatrix);
	}

	public float getProjectedPositionAndRadius(Vector3f target, float x, float y, float z, float radius) {
		//x *= mTranslator.mRatioX;
		mProjectionMatrix.apply3D(x,y,z,target);
		getCameraRightVector(tempVec1);
		tempVec1.scale(radius);
		tempVec1.add(x,y,z);
		mProjectionMatrix.apply3D(tempVec1.mX,tempVec1.mY,tempVec1.mZ,tempVec2);
//		target.mX *= mTranslator.mRatioX;
//		tempVec2.mX *= mTranslator.mRatioX;
		return target.getDistance(tempVec2);
	}

	public void getProjectedPosition(Vector3f target, float x,float y,float z) {
		mProjectionMatrix.apply3D(x,y,z,target);
	}

	public float getCurFovy() {
		return mCurFovy;
	}

	public float getCurNear() {
		return mCurNear;
	}

	public float getCurFar() {
		return mCurFar;
	}

}
