package yang.util.filereader.exceptions;

import yang.util.filereader.TokenReader;

public class UnknownIdentifierException extends ParseException {

	private static final long serialVersionUID = 1L;

	public String mIdentifier;

	public UnknownIdentifierException(TokenReader reader,String identifier) {
		super(reader,"Unknown identifier","'"+identifier+"'");
		mIdentifier = identifier;
	}

}
