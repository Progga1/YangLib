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
	
	private TokenReader mModelReader;
	private TokenReader mMatReader;
	public int mVertexCount = 0;
	public int mIndexCount = 0;
	public float[] mPositions;
	public short[] mIndices;
	public float[] mNormals;
	public FloatColor mColor = FloatColor.WHITE.clone();
	public Quadruple mSuppData = Quadruple.ZERO;
	
	public OBJLoader(DefaultGraphics<?> graphics) {
		super(graphics);
		if(workingPositions==null) {
			workingPositions = new float[MAX_VERTICES*3];
			workingIndices = new short[MAX_VERTICES];
		}
	}

	public void loadOBJ(InputStream modelStream,InputStream materialStream,YangMatrix transform) throws IOException {
		mModelReader = new TokenReader(modelStream);
		mMatReader = new TokenReader(materialStream);
		
		int workingId = 0;
		int indexId = 0;
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
							}
							
							if(chars[0]=='f') {
								int baseInd = mModelReader.readInt();
								int prevInd = mModelReader.readInt();
//								workingIndices[indexId++] = baseInd;
//								workingIndices[indexId++] = prevInd;
								int curInd = mModelReader.readInt();
								while(curInd!=TokenReader.ERROR_INT) {
									workingIndices[indexId++] = (short)(baseInd-1);
									workingIndices[indexId++] = (short)(prevInd-1);
									workingIndices[indexId++] = (short)(curInd-1);
									
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
		mIndices = new short[indexId];
		System.arraycopy(workingPositions, 0, mPositions, 0, workingId);
		System.arraycopy(workingIndices, 0, mIndices, 0, indexId);
//		short[] ar = new short[5000];
//		System.arraycopy(workingIndices, 0, ar, 0, 5000);
//		System.out.println(Util.arrayToString(ar, ",", 3));
		mVertexCount = workingId/3;
		mIndexCount = indexId;
	}
	
	public void draw() {
		IndexedVertexBuffer vertexBuffer = mGraphics.getCurrentVertexBuffer();
		vertexBuffer.putIndexArray(mIndices);
		vertexBuffer.putArray(DefaultGraphics.ID_POSITIONS, mPositions);
		vertexBuffer.putArrayMultiple(DefaultGraphics.ID_COLORS, mColor.mValues, mVertexCount);
		vertexBuffer.putArrayMultiple(DefaultGraphics.ID_SUPPDATA, mSuppData.mValues, mVertexCount);
	}
	
}
