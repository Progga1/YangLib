package yang.graphics.util;

import yang.graphics.interfaces.ClockInterface;

public class DefaultClock implements ClockInterface {

	@Override
	public double getTime() {
		return (System.nanoTime()*0.000000001);
	}

}
