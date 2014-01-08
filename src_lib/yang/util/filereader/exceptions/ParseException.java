package yang.util.filereader.exceptions;

import yang.util.filereader.TokenReader;

public class ParseException extends Exception {

	private static final long serialVersionUID = 1L;

	public int mLine;
	public int mColumn;

	public ParseException(int line,int column) {
		 mLine = line;
		 mColumn = column;
	}

	public ParseException(TokenReader reader) {
		this(reader.getCurrentLine(),reader.getCurrentColumn());
	}

	public String getMessage(String messageType,String subMessage) {
		return mLine+"-"+mColumn+" "+messageType+ ": " + subMessage;
	}

}
