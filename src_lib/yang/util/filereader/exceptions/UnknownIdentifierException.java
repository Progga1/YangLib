package yang.util.filereader.exceptions;

import yang.util.filereader.TokenReader;

public class UnknownIdentifierException extends ParseException {

	private static final long serialVersionUID = 1L;

	private String mIdentifier;

	public UnknownIdentifierException(TokenReader reader,String identifier) {
		super(reader);
		mIdentifier = identifier;
	}

	@Override
	public String getMessage() {
		return super.getMessage("Unknown identifier","'"+mIdentifier+"'");
	}

}
