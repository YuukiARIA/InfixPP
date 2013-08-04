package infixpp.ast.parser.exception;

@SuppressWarnings("serial")
public class LexerException extends Exception
{
	private int column;

	public LexerException(String message, int column)
	{
		super(message);
		this.column = column;
	}

	public int getColumn()
	{
		return column;
	}
}
