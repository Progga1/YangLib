package yang.pc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JWindow;

import yang.systemdependent.YangSensor;

public class PCSensorFrame extends YangSensor implements MouseListener,MouseMotionListener,MouseWheelListener {

	public static boolean ENABLED = true;
	
	public Color CL_RUN = Color.LIGHT_GRAY;
	public Color CL_PAUSED = Color.GRAY;
	public JWindow mSensorFrame;
	public int mCurX,mCurY;
	public float mLinAccX,mLinAccY,mLinAccZ;
	public float mRotX,mRotY,mRotZ;
	public float mSensitivity = 0.01f;
	public int mCurButton;
	
	public PCSensorFrame() {
		
	}
	
	private void accUpdated() {
		final float GRAVITY = 9.81f;
		float gravX = (float)(Math.sin(mRotX)*Math.sin(mRotY))*GRAVITY;
		float gravY = -GRAVITY;
		float gravZ = 0;
		if(mSensorActive[YangSensor.TYPE_ACCELEROMETER])
			mEvents.putSensorEvent(YangSensor.TYPE_ACCELEROMETER, mLinAccX+gravX,mLinAccY+gravY,mLinAccZ+gravZ);
		if(mSensorActive[YangSensor.TYPE_LINEAR_ACCELERATION])
			mEvents.putSensorEvent(YangSensor.TYPE_LINEAR_ACCELERATION, mLinAccX,mLinAccY,mLinAccZ);
		if(mSensorActive[YangSensor.TYPE_GRAVITY])
			mEvents.putSensorEvent(YangSensor.TYPE_GRAVITY, gravX,gravY,gravZ);
		if(mSensorActive[YangSensor.TYPE_GYROSCOPE])
			mEvents.putSensorEvent(YangSensor.TYPE_GYROSCOPE, mRotX,mRotY,mRotZ);
		mLinAccX = 0;
		mLinAccY = 0;
		mLinAccZ = 0;
	}
	
	@Override
	public void derivedStartSensor(int type,int speed) {
		if(!ENABLED)
			return;
		if(mSensorFrame==null) {
			mSensorFrame = new JWindow();
			mSensorFrame.setFocusable(false);
			mSensorFrame.setSize(new Dimension(320,240));
			mSensorFrame.addMouseListener(this);
			mSensorFrame.addMouseMotionListener(this);
			mSensorFrame.addMouseWheelListener(this);
			mSensorFrame.setBackground(CL_RUN);
			mSensorFrame.setAlwaysOnTop(true);
		}
		mSensorFrame.setVisible(true);
	}

	@Override
	public void derivedStopSensor(int type) {
		if(!isAnySensorActive())
			mSensorFrame.setVisible(false);
	}
	
	@Override
	public void mouseDragged(MouseEvent ev) {
		float deltaX = (ev.getX()-mCurX)*mSensitivity;
		float deltaY = (ev.getY()-mCurY)*mSensitivity;
		mCurX = ev.getX();
		mCurY = ev.getY();
		if(mCurButton==MouseEvent.BUTTON1) {
			mLinAccX = deltaX;
			mLinAccY = deltaY;
			accUpdated();
		}
		if(mCurButton==MouseEvent.BUTTON2) {
			mRotX += deltaX;
			mRotY += deltaY;
			accUpdated();
		}
		if(mCurButton==MouseEvent.BUTTON3) {
			mRotZ += deltaX;
			mRotY += deltaY;
			accUpdated();
		}
	}

	@Override
	public void mouseMoved(MouseEvent ev) {
		
	}

	@Override
	public void mouseClicked(MouseEvent ev) {
		
	}

	@Override
	public void mouseEntered(MouseEvent ev) {
		
	}

	@Override
	public void mouseExited(MouseEvent ev) {
		
	}

	@Override
	public void mousePressed(MouseEvent ev) {
		mCurX = ev.getX();
		mCurY = ev.getY();
		mCurButton = ev.getButton();
	}

	@Override
	public void mouseReleased(MouseEvent ev) {
		
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent ev) {
		mLinAccZ = ev.getWheelRotation()*mSensitivity;
		accUpdated();
	}

	@Override
	protected void derivedPause() {
		if(mSensorFrame!=null)
			mSensorFrame.setBackground(CL_PAUSED);
	}

	@Override
	protected void derivedResume() {
		if(mSensorFrame!=null)
			mSensorFrame.setBackground(CL_RUN);
	}
	
}
