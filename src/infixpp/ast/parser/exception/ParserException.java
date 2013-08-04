package infixpp.ast.parser.exception;

import infixpp.ast.parser.Location;

@SuppressWarnings("serial")
public class ParserException extends Exception
{
	private Location location;

	public ParserException(String message, Location location)
	{
		super(message);
		this.location = location;
	}

	public ParserException(LexerException lexerEx)
	{
		this("(lexical error) " + lexerEx.getMessage(), lexerEx.getLocation());
	}

	public Location getLocation()
	{
		return location;
	}
}
