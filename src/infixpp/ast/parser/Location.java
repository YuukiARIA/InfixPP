package infixpp.ast.parser;

public final class Location
{
	public final int line;
	public final int column;

	public Location(int line, int column)
	{
		this.line = line;
		this.column = column;
	}

	public String toString()
	{
		return "(line " + line + ", column " + column + ")";
	}

	public static Location of(int line, int column)
	{
		return new Location(line, column);
	}
}
