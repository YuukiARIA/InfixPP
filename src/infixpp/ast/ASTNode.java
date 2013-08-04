package infixpp.ast;

import infixpp.ast.parser.Operator;

public abstract class ASTNode
{
	public static class Binary extends ASTNode
	{
		public final Operator operator;
		public final ASTNode x;
		public final ASTNode y;

		public Binary(Operator operator, ASTNode x, ASTNode y)
		{
			this.operator = operator;
			this.x = x;
			this.y = y;
		}

		public String toString()
		{
			return operator.getNodeName() + "(" + x + ", " + y  + ")";
		}
	}

	public static class Literal extends ASTNode
	{
		public final String value;

		public Literal(String value)
		{
			this.value = value;
		}

		public String toString()
		{
			return value;
		}
	}
}
