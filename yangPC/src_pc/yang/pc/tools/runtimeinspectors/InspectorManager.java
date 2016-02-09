package yang.pc.tools.runtimeinspectors;

import java.util.HashMap;

import javax.swing.JFrame;

import yang.graphics.camera.CameraIntrinsics;
import yang.graphics.defaults.programs.helpers.ShadowHelper;
import yang.graphics.model.FloatColor;
import yang.graphics.model.TransformationData;
import yang.graphics.textures.TextureRenderTarget;
import yang.graphics.translator.Texture;
import yang.graphics.translator.TextureDisplay;
import yang.graphics.util.cameracontrol.Camera3DControl;
import yang.math.objects.EulerAngles;
import yang.math.objects.Point3f;
import yang.math.objects.Quaternion;
import yang.math.objects.Vector3f;
import yang.math.objects.YangMatrix;
import yang.model.Boundaries3D;
import yang.pc.tools.runtimeinspectors.components.PropertyBooleanCheckBox;
import yang.pc.tools.runtimeinspectors.components.PropertyBoundaries3D;
import yang.pc.tools.runtimeinspectors.components.PropertyColorNums;
import yang.pc.tools.runtimeinspectors.components.PropertyTextField;
import yang.pc.tools.runtimeinspectors.components.PropertyTexture;
import yang.pc.tools.runtimeinspectors.components.PropertyTransform;
import yang.pc.tools.runtimeinspectors.components.PropertyVector3;
import yang.pc.tools.runtimeinspectors.components.camera.PropertyCameraControl;
import yang.pc.tools.runtimeinspectors.components.camera.PropertyCameraIntrinsics;
import yang.pc.tools.runtimeinspectors.components.numbers.PropertyFloatNum;
import yang.pc.tools.runtimeinspectors.components.numbers.PropertyInteger;
import yang.pc.tools.runtimeinspectors.components.rotation.PropertyEulerAngles;
import yang.pc.tools.runtimeinspectors.components.rotation.PropertyQuaternion;
import yang.pc.tools.runtimeinspectors.components.utilobjects.PropertyShadowHelper;
import yang.util.YangList;

public class InspectorManager {

	protected HashMap<Class<?>,Class<? extends InspectorComponent>> mTypes = new HashMap<Class<?>,Class<? extends InspectorComponent>>();
	protected YangList<InspectorFrame> mFrames = new YangList<InspectorFrame>();

	public InspectorManager() {
		registerType(Boolean.class,PropertyBooleanCheckBox.class);
		registerType(Integer.class,PropertyInteger.class);
		registerType(Float.class,PropertyFloatNum.class);
		registerType(Double.class,PropertyFloatNum.class);
		registerType(String.class,PropertyTextField.class);
		registerType(Point3f.class,PropertyVector3.class);
		registerType(Vector3f.class,PropertyVector3.class);
		registerType(EulerAngles.class,PropertyEulerAngles.class);
		registerType(FloatColor.class,PropertyColorNums.class);
		registerType(Quaternion.class,PropertyQuaternion.class);
		registerType(TransformationData.class,PropertyTransform.class);
		registerType(YangMatrix.class,PropertyTransform.class);
		registerType(Camera3DControl.class,PropertyCameraControl.class);
		registerType(Texture.class,PropertyTexture.class);
		registerType(TextureRenderTarget.class,PropertyTexture.class);
		registerType(TextureDisplay.class,PropertyTexture.class);
		registerType(Boundaries3D.class,PropertyBoundaries3D.class);
		registerType(CameraIntrinsics.class,PropertyCameraIntrinsics.class);
		registerType(ShadowHelper.class,PropertyShadowHelper.class);
	}

	public void registerType(Class<?> type,Class<? extends InspectorComponent> component) {
		mTypes.put(type,component);
	}

	public Class<? extends InspectorComponent> getDefaultComponent(Class<?> type) {
		return mTypes.get(type);
	}

	public InspectorComponent createDefaultComponentInstance(Class<?> type) {
		Class<? extends InspectorComponent> compClass = getDefaultComponent(type);
		if(compClass==null)
			return null;
		InspectorComponent component;
		try {
			component = compClass.newInstance();
			component.setPreferredOutputType(type);
			return component;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public InspectorFrame createInspectionFrame() {
		InspectorFrame frame = new InspectorFrame(this);
		frame.setFramed();
		mFrames.add(frame);
		return frame;
	}

	public InspectorPanel createInspector(InspectorFrame frame) {
		return new InspectorPanel(frame);
	}

	public void handleShortcut(boolean ctrlDown,int keyCode) {
		for(InspectorFrame frame:mFrames) {
			if(frame.handleShortCut(ctrlDown,keyCode)!=null)
				break;
		}
	}

	public void arrangeFrames(int totalHeight,InspectorFrame... frames) {
		int count = frames.length;
		int ADD = 4;
		int frameHeight = totalHeight/count;
		int i=0;
		for(InspectorFrame frame:frames) {
			JFrame jFrame = frame.mFrame;
			jFrame.setSize(jFrame.getWidth(),frameHeight+ADD*2);
			jFrame.setLocation(0,i*frameHeight-ADD);
			i++;
		}
	}

}
