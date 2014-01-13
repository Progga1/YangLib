package yang.util.filereader.exceptions;

import yang.util.filereader.TokenReader;

public class ParseException extends Exception {

	private static final long serialVersionUID = 1L;

	public String mMessageType,mMessage;
	public int mLine;
	public int mColumn;

	public ParseException(int line,int column,String messageType,String message) {
		 mLine = line;
		 mColumn = column;
		 mMessageType = messageType;
		 mMessage = message;
	}

	public ParseException(TokenReader reader,String messageType,String message) {
		this(reader.getCurrentLine(),reader.getCurrentColumn(),messageType,message);
	}

	public ParseException(TokenReader reader,String message) {
		this(reader,"Parse exception",message);
	}

	public String getMessage(String messageType,String subMessage) {
		return mLine+"-"+mColumn+" "+messageType+ ": " + subMessage;
	}

	@Override
	public String getMessage() {
		return getMessage(mMessageType,mMessage);
	}

}
