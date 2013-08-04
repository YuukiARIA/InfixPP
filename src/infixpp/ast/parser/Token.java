package infixpp.ast.parser;

public class Token
{
	public final Kind kind;
	public final String text;
	public final int intValue;
	public final Location location;

	public Token(Kind kind, String text, Location location)
	{
		this(kind, text, 0, location);
	}

	public Token(Kind kind, int intValue, Location location)
	{
		this(kind, "", intValue, location);
	}

	private Token(Kind kind, String text, int intValue, Location location)
	{
		this.kind = kind;
		this.text = text;
		this.intValue = intValue;
		this.location = location;
	}

	public String toString()
	{
		return kind + "'" + text + "'";
	}
}
