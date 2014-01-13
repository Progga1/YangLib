package yang.util.filereader.exceptions;

import yang.util.filereader.TokenReader;


public class UnexpectedTokenException extends ParseException {

	private static final long serialVersionUID = 1L;

	public String mExpected;
	public String mGot;

	public UnexpectedTokenException(int line,int column,String expected,String got) {
		super(line,column,"Unexpected token","expected "+expected+", got "+got);
		mExpected = expected;
		mGot = got;
	}

	public UnexpectedTokenException(TokenReader reader, String expected) {
		this(reader.getCurrentLine(),reader.getCurrentColumn(),expected,reader.wordToString());
	}

}
