package yang.util.filereader;

public class UnexpectedTokenException extends Exception {

	private static final long serialVersionUID = 1L;

	public int mLine;
	public int mColumn;
	public String mExpected;
	public String mGot;

	public UnexpectedTokenException(int line,int column,String expected,String got) {
		super();
		mLine = line;
		mColumn = column;
		mExpected = expected;
		mGot = got;
	}

	@Override
	public String getMessage() {
		return mLine+"-"+mColumn+" Unexpected token: expected "+mExpected+", got "+mGot;
	}

}
