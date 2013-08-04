package infixpp.ast.parser;

public class OperatorDefinition
{
	private final String notation;
	private final int prec;
	private final boolean rightAssoc;

	public OperatorDefinition(String notation, int prec, boolean rightAssoc)
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

	public boolean isWeakerThan(OperatorDefinition opdef)
	{
		return prec == opdef.prec ? isRightAssociative() : prec > opdef.prec;
	}
}
