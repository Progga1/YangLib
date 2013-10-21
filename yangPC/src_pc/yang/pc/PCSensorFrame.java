package yang.pc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JDialog;

import yang.math.objects.Quaternion;
import yang.systemdependent.YangSensor;

public class PCSensorFrame extends YangSensor implements MouseListener,MouseMotionListener,MouseWheelListener {

	public static boolean ENABLED = true;
	public static float GRAVITY = 9.81f;

	public Color CL_RUN = Color.LIGHT_GRAY;
	public Color CL_PAUSED = Color.GRAY;
	public JDialog mSensorFrame;
	public int mCurX,mCurY;
	private float mGravX,mGravY,mGravZ;
	public float mCurYaw,mCurPitch,mCurRoll;
	public float mSensitivity = 0.01f;
	public int mCurButton;

	private final Quaternion tempQuat = new Quaternion();

	public PCSensorFrame() {

	}

	private void turn(float yaw,float pitch,float roll) {
		mCurYaw += yaw;
		mCurPitch += pitch;
		mCurRoll += roll;
		if(mSensorActive[YangSensor.TYPE_GYROSCOPE])
			mEvents.putSensorEvent(YangSensor.TYPE_GYROSCOPE, yaw,pitch,roll); //TODO: no proper axis rotations!
		if(mSensorActive[YangSensor.TYPE_ROTATION_VECTOR]) {
			tempQuat.setFromEuler(mCurYaw, mCurPitch, mCurRoll); //TODO: Local rotation!
			mEvents.putSensorEvent(YangSensor.TYPE_ROTATION_VECTOR,tempQuat.mX,tempQuat.mY,tempQuat.mZ);
		}
		if(mSensorActive[YangSensor.TYPE_EULER_ANGLES]) {
			mEvents.putSensorEvent(YangSensor.TYPE_EULER_ANGLES, mCurYaw,mCurPitch,mCurRoll);
		}
	}

	private void accelerate(float x,float y,float z) {
		if(mSensorActive[YangSensor.TYPE_ACCELEROMETER])
			mEvents.putSensorEvent(YangSensor.TYPE_ACCELEROMETER, x+mGravX,y+mGravY,z+mGravZ);
		if(mSensorActive[YangSensor.TYPE_LINEAR_ACCELERATION])
			mEvents.putSensorEvent(YangSensor.TYPE_LINEAR_ACCELERATION, x,y,z);
	}

	@Override
	public void derivedStartSensor(int type,int speed) {
		if(!ENABLED)
			return;
		if(mSensorFrame==null) {
			mSensorFrame = new JDialog();
			mSensorFrame.setTitle("Fake sensor");
			mSensorFrame.setUndecorated(false);
			mSensorFrame.setFocusable(false);
			mSensorFrame.setSize(new Dimension(160,160));
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
		final float deltaX = (ev.getX()-mCurX)*mSensitivity;
		final float deltaY = (ev.getY()-mCurY)*mSensitivity;
		mCurX = ev.getX();
		mCurY = ev.getY();
		if(mCurButton==MouseEvent.BUTTON1) {
			accelerate(deltaX,deltaY,0);
		}
		if(mCurButton==MouseEvent.BUTTON2) {
			turn(deltaX,-deltaY,0);
		}
		if(mCurButton==MouseEvent.BUTTON3) {
			turn(0,-deltaY,-deltaX);
		}
	}

	@Override
	public void mouseMoved(MouseEvent ev) {

	}

	@Override
	public void mouseClicked(MouseEvent ev) {
		if(ev.getClickCount()==2) {
			turn(-mCurYaw,-mCurPitch,-mCurRoll);
		}
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
