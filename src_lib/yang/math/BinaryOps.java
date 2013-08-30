package yang.math;

public class BinaryOps {

	public static String intToBin(int value,int digits) {
		int c = 0;
		value = Math.abs(value);
		if(digits>31)
			digits = 31;
		StringBuilder result = new StringBuilder(digits+4);
		for(int i=0;i<digits;i++) {
			result.append("0");
			c++;
			if(c%8==0)
				result.append(" ");
		}
		c = result.length()-1;
		for(int i=0;i<digits;i++) {
			if(result.charAt(c)==' ')
				c--;
			result.setCharAt(c,value%2==0?'0':'1');
			value /= 2;
			c--;
		}
		
		return result.toString();
	}
	
}
