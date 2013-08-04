package infixpp.ast.parser;

public class Operator
{
	private final String notation;
	private final int prec;
	private final boolean rightAssoc;

	public Operator(String notation, int prec, boolean rightAssoc)
	{
		this.notation = notation;
		this.prec = prec;
		this.rightAssoc = rightAssoc;
	}

	public String getNotation()
	{
		return notation;
	}

	public int getPrecedence()
	{
		return prec;
	}

	public boolean isRightAssociative()
	{
		return rightAssoc;
	}

	public boolean isWeakerThan(Operator opdef)
	{
		return prec > opdef.prec;
	}

	public boolean isStrongerThan(Operator opdef)
	{
		return prec < opdef.prec;
	}

	public boolean shouldBeReducedBefore(Operator opdef)
	{
		return prec == opdef.prec ? !isRightAssociative() : isStrongerThan(opdef);
	}

	public String toString()
	{
		return "'" + notation + "'";
	}
}
