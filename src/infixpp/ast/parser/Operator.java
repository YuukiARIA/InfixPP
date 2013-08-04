package infixpp.ast.parser;

public class Operator
{
	private String notation;
	private String nodeName;
	private int prec;
	private boolean rightAssoc;

	public Operator(String notation, String nodeName, boolean rightAssoc)
	{
		this.notation = notation;
		this.nodeName = nodeName;
		this.rightAssoc = rightAssoc;
	}

	public String getNotation()
	{
		return notation;
	}

	public String getNodeName()
	{
		return nodeName;
	}

	public int getPrecedence()
	{
		return prec;
	}

	public void setPrecedence(int prec)
	{
		this.prec = prec;
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
