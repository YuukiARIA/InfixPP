package infixpp.ast.parser.exception;

import infixpp.ast.parser.Location;

@SuppressWarnings("serial")
public class LexerException extends Exception
{
	private Location location;

	public LexerException(String message, Location location)
	{
		super(message);
		this.location = location;
	}

	public Location getLocation()
	{
		return location;
	}
}
