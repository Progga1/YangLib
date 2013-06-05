package yang.model;

public class Pair<FirstType, SecondType> {

	public FirstType mFirst;
	public SecondType mSecond;
	
	public Pair(FirstType first,SecondType second) {
		mFirst = first;
		mSecond = second;
	}
	
	@Override
	public String toString() {
		return "("+mFirst+","+mSecond+")";
	}
	
}
