package yang.pc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JWindow;

import yang.math.objects.Quaternion;
import yang.systemdependent.YangSensor;

public class PCSensorFrame extends YangSensor implements MouseListener,MouseMotionListener,MouseWheelListener {

	public static boolean ENABLED = true;
	public static float GRAVITY = 9.81f;
	
	public Color CL_RUN = Color.LIGHT_GRAY;
	public Color CL_PAUSED = Color.GRAY;
	public JWindow mSensorFrame;
	public int mCurX,mCurY;
	private float mGravX,mGravY,mGravZ;
	public float mCurYaw,mCurPitch,mCurRoll;
	public float mSensitivity = 0.01f;
	public int mCurButton;
	
	private Quaternion tempQuat = new Quaternion();
	
	public PCSensorFrame() {
		
	}
	
	private void turn(float yaw,float pitch,float roll) {
		mCurYaw += yaw;
		mCurPitch += pitch;
		mCurRoll += roll;
		if(mSensorActive[YangSensor.TYPE_GYROSCOPE])
			mEvents.putSensorEvent(YangSensor.TYPE_GYROSCOPE, yaw,pitch,roll);
		if(mSensorActive[YangSensor.TYPE_ROTATION_VECTOR]) {
			tempQuat.setFromEuler(mCurYaw, mCurPitch, mCurRoll); //TODO: Local rotation!
			mEvents.putSensorEvent(YangSensor.TYPE_ROTATION_VECTOR,tempQuat.mX,tempQuat.mY,tempQuat.mZ);
		}
	}
	
	private void accelerate(float x,float y,float z) {
		if(mSensorActive[YangSensor.TYPE_ACCELEROMETER])
			mEvents.putSensorEvent(YangSensor.TYPE_ACCELEROMETER, x+mGravX,y+mGravY,z+mGravZ);
		if(mSensorActive[YangSensor.TYPE_LINEAR_ACCELERATION])
			mEvents.putSensorEvent(YangSensor.TYPE_LINEAR_ACCELERATION, x,y,z);
	}
	
	private void accUpdated() {
		
		
		mGravX = 0;
		mGravY = -GRAVITY;
		mGravZ = 0;
		if(mSensorActive[YangSensor.TYPE_GRAVITY])
			mEvents.putSensorEvent(YangSensor.TYPE_GRAVITY, mGravX,mGravY,mGravZ);
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
			accelerate(deltaX,deltaY,0);
		}
		if(mCurButton==MouseEvent.BUTTON2) {
			turn(deltaX,deltaY,0);
		}
		if(mCurButton==MouseEvent.BUTTON3) {
			turn(0,deltaY,deltaX);
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
		accelerate(0,0,ev.getWheelRotation()*mSensitivity);
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
