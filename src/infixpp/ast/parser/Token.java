package infixpp.ast.parser;

public class Token
{
	public final Kind kind;
	public final String text;
	public final int intValue;
	public final int column;

	public Token(Kind kind, String text, int column)
	{
		this(kind, text, 0, column);
	}

	public Token(Kind kind, int intValue, int column)
	{
		this(kind, "", intValue, column);
	}

	private Token(Kind kind, String text, int intValue, int column)
	{
		this.kind = kind;
		this.text = text;
		this.intValue = intValue;
		this.column = column;
	}
}
