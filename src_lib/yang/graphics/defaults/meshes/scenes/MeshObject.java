package yang.graphics.defaults.meshes.scenes;

import yang.graphics.defaults.meshes.loaders.YangMesh;


public class MeshObject extends SceneObject {

	public YangMesh mMesh;

	public void draw() {
		mMesh.draw();
	}

}
