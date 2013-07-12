package yang.graphics.defaults.meshcreators.loaders;

import java.io.IOException;
import java.io.InputStream;

import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.defaults.meshcreators.MeshCreator;
import yang.graphics.model.FloatColor;
import yang.math.objects.Quadruple;
import yang.math.objects.matrix.YangMatrix;
import yang.util.Util;
import yang.util.filereader.TokenReader;

public class OBJLoader extends MeshCreator<DefaultGraphics<?>>{

	public static int MAX_VERTICES = 100000;
	private static String[] keyWords = {"mtllib","usemtl"};
	private static float[] workingPositions;
	private static short[] workingIndices;
	private static int[] redirectIndices;
	private static int[] texCoordIndices;
	private static int[] normalIndices;
	private static int[] smoothIndices;
	private static int redirectId;
	
	public int mVertexCount = 0;
	public int mIndexCount = 0;
	public float[] mPositions;
	public short[] mIndices;
	public float[] mNormals;
	public FloatColor mColor = FloatColor.WHITE.clone();
	public Quadruple mSuppData = Quadruple.ZERO;
	
	private TokenReader mModelReader;
	private TokenReader mMatReader;
	private int mIndexId = 0;
	
	public OBJLoader(DefaultGraphics<?> graphics) {
		super(graphics);
		if(workingPositions==null) {
			workingPositions = new float[MAX_VERTICES*3];
			workingIndices = new short[MAX_VERTICES];
			redirectIndices = new int[MAX_VERTICES];
			texCoordIndices = new int[MAX_VERTICES];
			normalIndices = new int[MAX_VERTICES];
			smoothIndices = new int[MAX_VERTICES];
		}
	}
	
	private void addIndex(int index) {
		workingIndices[mIndexId++] = (short)(index-1);
	}

	public void loadOBJ(InputStream modelStream,InputStream materialStream,YangMatrix transform) throws IOException {
		mModelReader = new TokenReader(modelStream);
		mMatReader = new TokenReader(materialStream);
		
		redirectId = 0;
		int curSmoothGroup = -1;
		int workingId = 0;
		
		boolean lineBeginning = true;
		
		char[] chars = mModelReader.mCharBuffer;
		while(!mModelReader.eof()) {
			mModelReader.nextWord();
			
			if(chars[0]=='#')
				mModelReader.skipLine();
			else{
				if(chars[0]=='\n') {
					lineBeginning = true;
				}else{
					if(lineBeginning) {
						lineBeginning = false;
						if(mModelReader.mWordLength==1) {
							if(chars[0]=='v') {
								//Add vertex position
								float posX = mModelReader.readFloat();
								float posY = mModelReader.readFloat();
								float posZ = mModelReader.readFloat();
								if(transform!=null) {
									transform.apply3D(posX, posY, posZ, workingPositions, workingId);
									workingId += 3;
								}else{
									workingPositions[workingId++] = posX;
									workingPositions[workingId++] = posY;
									workingPositions[workingId++] = posZ;
								}
								redirectIndices[redirectId] = -1;
								texCoordIndices[redirectId] = 0;
								normalIndices[redirectId] = 0;
								smoothIndices[redirectId] = curSmoothGroup;
								redirectId++;
							}
							
							if(chars[0]=='f') {
								int baseInd = mModelReader.readInt();
								int prevInd = mModelReader.readInt();
//								workingIndices[indexId++] = baseInd;
//								workingIndices[indexId++] = prevInd;
								int curInd = mModelReader.readInt();
								while(curInd!=TokenReader.ERROR_INT) {
									addIndex(baseInd);
									addIndex(prevInd);
									addIndex(curInd);
									
									//baseInd = prevInd;
									prevInd = curInd;
									curInd = mModelReader.readInt();
								}
								lineBeginning = true;
							}
						}
						
					}
				}
			}
		}
		
		mPositions = new float[workingId];
		mIndices = new short[mIndexId];
		System.arraycopy(workingPositions, 0, mPositions, 0, workingId);
		System.arraycopy(workingIndices, 0, mIndices, 0, mIndexId);
//		short[] ar = new short[5000];
//		System.arraycopy(workingIndices, 0, ar, 0, 5000);
//		System.out.println(Util.arrayToString(ar, ",", 3));
		mVertexCount = workingId/3;
		mIndexCount = mIndexId;
	}
	
	public void draw() {
		IndexedVertexBuffer vertexBuffer = mGraphics.getCurrentVertexBuffer();
		vertexBuffer.putIndexArray(mIndices);
		vertexBuffer.putArray(DefaultGraphics.ID_POSITIONS, mPositions);
		vertexBuffer.putArrayMultiple(DefaultGraphics.ID_COLORS, mColor.mValues, mVertexCount);
		vertexBuffer.putArrayMultiple(DefaultGraphics.ID_SUPPDATA, mSuppData.mValues, mVertexCount);
	}
	
}
