package yang.oculusheadtracking.samples;

import de.fruitfly.ovr.HMDInfo;

import de.fruitfly.ovr.OculusRift;

public class TrackerMain {

	public static void main(String[] args) {
		OculusRift or = new OculusRift();
		or.init();
		
		HMDInfo hmdInfo = or.getHMDInfo();
		System.out.println(hmdInfo);
		
		while (or.isInitialized()) {
			or.poll();
			
			System.out.println("Yaw: " + or.getYaw() + " Pitch: " + or.getPitch() + " Roll: " + or.getRoll());
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		or.destroy();
	}
	
}
