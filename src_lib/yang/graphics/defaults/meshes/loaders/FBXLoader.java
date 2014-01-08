package yang.graphics.defaults.meshes.loaders;

import java.io.IOException;
import java.io.InputStream;

import yang.graphics.defaults.meshes.armature.YangArmature;
import yang.graphics.defaults.meshes.scenes.LimbObject;
import yang.graphics.defaults.meshes.scenes.SceneObject;
import yang.graphics.translator.AbstractGFXLoader;
import yang.graphics.translator.AbstractGraphics;
import yang.physics.massaggregation.MassAggregation;
import yang.physics.massaggregation.elements.Joint;
import yang.util.YangList;
import yang.util.filereader.TokenReader;
import yang.util.filereader.exceptions.ParseException;
import yang.util.filereader.exceptions.UnknownIdentifierException;

public class FBXLoader {

	public YangList<YangMesh> mMeshes = new YangList<YangMesh>();
	public YangList<YangArmature> mArmatures = new YangList<YangArmature>();
//	public YangList<MassAggregation> mSkeletons = new YangList<MassAggregation>();
	public MassAggregation mMassAggregation;
	public YangList<SceneObject> mObjects = new YangList<SceneObject>();
	public YangList<LimbObject> mLimbObjects = new YangList<LimbObject>();

	public AbstractGraphics<?> mGraphics;
	public MeshMaterialHandles mHandles;

	private TokenReader mReader;
	private float mDefaultJointRadius = Joint.DEFAULT_RADIUS;

	//private targetObject mTempProperties = new targetObject();

	public FBXLoader(AbstractGraphics<?> graphics, MeshMaterialHandles handles) {
		mGraphics = graphics;
		mHandles = handles;
	}

	public void setTargetMassAggregation(MassAggregation target) {
		mMassAggregation = target;
	}

	private void readProperties(SceneObject targetObject) throws IOException {
		while(!mReader.eof()) {
			mReader.nextWord(true);
			if(mReader.isChar('}'))
				break;
			if(mReader.isWord("Lcl Translation")) {
				mReader.skipWord(true);
				mReader.skipWord(true);
				mReader.readPoint3f(targetObject.mTranslation);
			}
		}
	}

	private void skipBracketContent() throws IOException {
		mReader.toLineEnd();
		int depth = 1;
		while(!mReader.eof()) {
			mReader.nextWord(true);
			if(mReader.isChar('{')) {
				depth++;
			}else if(mReader.isChar('}')) {
				depth--;
				if(depth<=0)
					return;
			}
		}
	}

	private void readObjects() throws IOException, ParseException {
		while(!mReader.eof()) {
			mReader.nextWord(true);
			if(mReader.isWord("}"))
				break;
			else if(mReader.isWord("Model")) {
				//Read single model
				mReader.nextWord(true);
				String name = mReader.wordToString();
				//mReader.expect(",");
				mReader.nextWord(true);
				boolean isLimb = mReader.isWord("Limb");
				//mReader.expect("{");

				SceneObject newObj;
				if(isLimb) {
//					Joint joint = new Joint(name.split("::")[1],null,mTempProperties.mTranslation,mDefaultJointRadius,mMassAggregation);
//					mMassAggregation.addJoint(joint);
//					System.out.println(joint);
					newObj = new LimbObject();
					mLimbObjects.add((LimbObject)newObj);
				}else
					newObj = new SceneObject();
				mObjects.add(newObj);
				newObj.mName = name.substring(7);

				while(!mReader.eof()) {
					mReader.nextWord(true);
					if(mReader.isWord("}")) {
						break;
					}else if(mReader.isWord("Properties60")) {
						mReader.expect("{");

						readProperties(newObj);
					}else if(mReader.startsWith("Layer")){
						skipBracketContent();
					}else
						mReader.toLineEnd();
				}
			}else if(mReader.isWord("Materials")) {
				skipBracketContent();
			}else if(mReader.isWord("Pose")) {
				skipBracketContent();
			}else if(mReader.isWord("GlobalSettings")) {
				skipBracketContent();
			}

		}
	}

	private SceneObject findModel() {
		for(SceneObject obj:mObjects) {
			if(mReader.endsWith(obj.mName)) {
				return obj;
			}
		}
		return null;
	}

	private void readConnections() throws IOException, UnknownIdentifierException {
		while(!mReader.eof()) {
			mReader.nextWord(true);
			if(mReader.isWord("}"))
				break;
			if(mReader.isWord("Connect")) {
				mReader.nextWord(true); //"OO"
				mReader.nextWord(true);
				if(mReader.startsWith("Model")) {
					SceneObject obj1 = findModel();
					if(obj1==null)
						throw new UnknownIdentifierException(mReader,mReader.wordToString().substring(7));
					mReader.nextWord(true);
					if(mReader.startsWith("Model")) {
						SceneObject obj2 = findModel();
						if(obj2==null)
							throw new UnknownIdentifierException(mReader,mReader.wordToString().substring(7));
						obj1.setParent(obj2);
						System.out.println(obj1+" "+obj2);
					}
				}

			}
		}
	}

	public boolean load(String filename,AbstractGFXLoader gfxLoader) throws IOException, ParseException {
	//	mGFXLoader = gfxLoader;
		InputStream stream = gfxLoader.mResources.getAssetInputStream(filename);
		if(stream==null)
			return false;
		if(mMassAggregation==null)
			mMassAggregation = new MassAggregation();
		mReader = new TokenReader(stream);
	//	mChars = mReader.mCharBuffer;
		mReader.setLineCommentChars(";");
		mReader.mAutoSkipComments = true;
		mReader.mWordBreakers[':'] = true;
		mReader.mWordBreakers[','] = true;

		mMeshes.clear();
		mArmatures.clear();
		mMassAggregation.clear();

		SceneObject scene = new SceneObject();
		scene.mName = "Scene";
		mObjects.add(scene);

		while(!mReader.eof()) {
			mReader.nextWord(true);
			if(mReader.isWord("Objects")) {
				mReader.expect("{");
				readObjects();
			}else if(mReader.isWord("Connections")) {
				mReader.expect("{");
				readConnections();
			}else if(mReader.isWord("Takes")) {
				skipBracketContent();
			}else if(mReader.startsWith("Version")) {
				skipBracketContent();
			}else if(mReader.startsWith("Relations")) {
				skipBracketContent();
			}

		}

		return true;
	}

	public void createSkeleton(MassAggregation targetSkeleton) {
		for(LimbObject limbObject:mLimbObjects) {
			Joint joint = new Joint(limbObject.mName,null,limbObject.mTranslation,mDefaultJointRadius);
			targetSkeleton.addJoint(joint);
		}
		for(LimbObject limbObject:mLimbObjects) {
			if(limbObject.mParent!=null) {
				targetSkeleton.getJointByName(limbObject.mName).setParent(targetSkeleton.getJointByName(limbObject.mParent.mName));
			}
		}
	}

	public MassAggregation createSkeleton() {
		MassAggregation skel = new MassAggregation();
		createSkeleton(skel);
		return skel;
	}

}
