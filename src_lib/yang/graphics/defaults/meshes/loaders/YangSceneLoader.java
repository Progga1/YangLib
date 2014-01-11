package yang.graphics.defaults.meshes.loaders;

import yang.graphics.model.material.YangMaterial;
import yang.util.filereader.TokenReader;

public class YangSceneLoader {

	public static YangMaterial DEFAULT_MATERIAL = new YangMaterial();

	public static int MAX_VERTICES = 200000;
	protected static float[] workingPositions;
	protected static float[] workingNormals;
	protected static float[] workingTexCoords;
	protected static short[] workingIndices;
	protected static int[] redirectIndices;
	protected static int[] positionIndices;
	protected static int[] texCoordIndices;
	protected static int[] normalIndices;
	protected static int[] smoothIndices;

	protected TokenReader mModelReader;

	public YangSceneLoader() {
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
	}

}
