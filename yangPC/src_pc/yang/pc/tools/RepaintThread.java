package yang.pc.tools;

import java.awt.Component;

public class RepaintThread extends Thread {

	private Component repaintComponent;
	private long periodMillis;
	
	public RepaintThread(Component repaintComponent,long periodMillis) {
		this.repaintComponent = repaintComponent;
		this.periodMillis = periodMillis;
	}
	
	@Override
	public void run() {
		while (true) {
			repaintComponent.repaint();
			if(periodMillis>0) {
				try {
					Thread.sleep(periodMillis);
				} catch (InterruptedException e) {
					return;
				}
			}
		}
	}
	
}
