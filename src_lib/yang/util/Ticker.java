package yang.util;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Ticker {
	long lastTick;
	long[] buffer;
	int index;
	int counter;
	int bufferSize;
	
	static DecimalFormat f = new DecimalFormat("0.0000");
	
	static final int defaultBufferSize = 30;
	static final boolean active = true;
	static HashMap<String, Ticker> profiles = new HashMap<String, Ticker>();
	static Stack<String> profileStack = new Stack<String>();
	
	// static functions (profile management)
	
	public static void tickProfile(String profile) {
		Ticker ticker = profiles.get(profile);
		profileStack.push(profile);
		
		if (ticker == null) {
			ticker = new Ticker(defaultBufferSize);
			profiles.put(profile, ticker);
		}
		
		ticker.tick();
	}
	
	public static double tickTockProfile(String profile) {
		Ticker ticker = profiles.get(profile);
		profileStack.push(profile);
		
		if (ticker == null) {
			ticker = new Ticker(defaultBufferSize);
			profiles.put(profile, ticker);
		}
		
		return ticker.tickTock();		
	}
	
	public static double tockProfile() {
		String profile = profileStack.pop();
		Ticker ticker = profiles.get(profile);
		
		if (ticker == null) {
			ticker = new Ticker(defaultBufferSize);
			profiles.put(profile, ticker);
		}
		
		return ticker.tock();
	}	
	
	public static String report() {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Ticker> profile : profiles.entrySet() ) {
			double val = profile.getValue().get();
			sb.append(profile.getKey()).append(" : ").append(f.format(val)).append("s \t ").append(f.format(1/val)).append("Hz\n");
		}
		return sb.toString();
	}
	
	public static String[] reportArray() {
		String[] strings = new String[profiles.entrySet().size()];
		int i = 0;
		for (Map.Entry<String, Ticker> profile : profiles.entrySet() ) {
			double val = profile.getValue().get();
			strings[i++] = profile.getKey()+" : "+f.format(val)+"s \t "+f.format(1/val)+"Hz";
		}
		
		return strings;
	}
	
	// constructors
	
	public Ticker(int bufferSize) {
		this.bufferSize = bufferSize;
		buffer = new long[bufferSize];
		index = 0;
		counter = 0;
		lastTick = -1;
	}
	
	public Ticker() {
		this(defaultBufferSize);
	}
	
	// ticker
	
	/**
	 * does not affect buffer
	 * @return current (averaged) ticker value
	 */
	public double get() {
		double dTmean = 1.0 / bufferSize;
		
		if (lastTick > 0) {
			int n = Math.min(bufferSize, counter);
			double acc = 0;
			for (int i=0; i<n; i++) {
				acc += buffer[i];
			}
			dTmean = acc / n;
		}
		
		return dTmean * 10e-10;
	}
	
	/**
	 * starts time measurement 
	 */
	public void tick() {
		lastTick = System.nanoTime();		
	}
	
	/**
	 * stops time measurement
	 * @return current (averaged) ticker value
	 */
	public double tock() {
		if (lastTick > 0) {
			long dT = (System.nanoTime() - lastTick);
			buffer[index] = dT;
			index = (index + 1) % bufferSize;
			counter++;
		}
		return get();
	}
	
	/**
	 * measures time between consecutive calls
	 * @return (average) time between tick calls
	 */
	public double tickTock() {
		double ticktock = tock();
		tick();
		return ticktock;
	}
}
