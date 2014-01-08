package yang.util.filereader.exceptions;


public class UnexpectedTokenException extends ParseException {

	private static final long serialVersionUID = 1L;

	public String mExpected;
	public String mGot;

	public UnexpectedTokenException(int line,int column,String expected,String got) {
		super(line,column);
		mExpected = expected;
		mGot = got;
	}

	@Override
	public String getMessage() {
		return super.getMessage("Unexpected token","expected "+mExpected+", got "+mGot);
	}

}
