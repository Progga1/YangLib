package yang.pc.tools.runtimeinspectors.components;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;

import yang.graphics.textures.TextureRenderTarget;
import yang.graphics.translator.Texture;
import yang.graphics.translator.TextureDisplay;
import yang.pc.tools.runtimeinspectors.InspectorComponent;
import yang.pc.tools.runtimeinspectors.InspectorGUIDefinitions;
import yang.pc.tools.runtimeinspectors.subcomponents.InspectorButton;
import yang.pc.tools.runtimeinspectors.subcomponents.InspectorButtonListener;

public class PropertyTexture extends InspectorComponent implements InspectorButtonListener {

	protected Texture mTexture;
	protected JPanel mMainPanel;
	protected JLabel mDataText;
	protected InspectorButton mShowButton;
	protected TextureDisplay mTexDispl = null;
	protected boolean mFlipY = false;

	public PropertyTexture(boolean flipY) {
		mFlipY = flipY;
	}

	public PropertyTexture() {
		this(false);
	}

	@Override
	protected void postInit() {
		if(!isReferenced())
			throw new RuntimeException("Texture component must be referenced.");
		mShowButton = new InspectorButton("Show");
		mShowButton.setListener(this);
		mDataText = new JLabel();
		mDataText.setForeground(InspectorGUIDefinitions.CL_TEXT);
		mMainPanel = new JPanel();
		mMainPanel.setBackground(InspectorGUIDefinitions.CL_COMPONENT_DEFAULT_BACKGROUND);
		mMainPanel.setLayout(new BorderLayout());
		mMainPanel.add(mDataText,BorderLayout.WEST);
		mMainPanel.add(mShowButton,BorderLayout.EAST);
		mMainPanel.setBorder(InspectorGUIDefinitions.BORDER_COMPONENT_DEFAULT);
	}

	@Override
	public void setValueReference(Object reference) {
		if(reference instanceof TextureRenderTarget)
			mTexture = ((TextureRenderTarget)reference).mTargetTexture;
		else if(reference instanceof TextureDisplay) {
			mTexDispl = ((TextureDisplay)reference);
			mTexture = mTexDispl.getTexture();
		}else
			mTexture = (Texture)reference;
	}

	@Override
	protected void refreshInValue() {
		if(mTexture==null)
			return;
		String data = "<html><body>";
		final String DATA_SPLITTER = " | ";
		if(mTexture.mName!=null)
			data += mTexture.mName+": ";
		data += mTexture.getWidth()+"x"+mTexture.getHeight();
		data += DATA_SPLITTER;
		data += mTexture.mProperties.channelsToString();
		data += "<br>";
		data += mTexture.mProperties.wrapToString();
		data += DATA_SPLITTER;
		data += mTexture.mProperties.filterToString();
		data += "</body></html>";
		mDataText.setText(data);
	}

	@Override
	protected Component getComponent() {
		return mMainPanel;
	}

	@Override
	public void buttonPressed(InspectorButton sender, int button) {
		if(sender==mShowButton) {
			if(mTexDispl==null) {
				String title = mCurObject.getName()+" - ";
				if(mTexture.mName!=null)
					title += mName+" ("+mTexture.mName+")";
				else
					title += mName;
				mTexDispl = mTexture.mGraphics.createTextureDisplay(mTexture,title).show();
				mTexDispl.setFlipY(mFlipY);
			}else
				mTexDispl.getGLHolder().setVisible(true);
		}
	}

	@Override
	public PropertyTexture clone() {
		return new PropertyTexture(mFlipY);
	}

}
