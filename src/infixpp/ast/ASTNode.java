package infixpp.ast;

import infixpp.ast.parser.Operator;

public abstract class ASTNode
{
	public ASTNode bind(Operator op, ASTNode y)
	{
		return new Binary(op, this, y);
	}

	public abstract <P, R> R accept(Visitor<P, R> visitor, P param);

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

		public <P, R> R accept(Visitor<P, R> visitor, P param)
		{
			return visitor.visit(this, param);
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

		public <P, R> R accept(Visitor<P, R> visitor, P param)
		{
			return visitor.visit(this, param);
		}

		public String toString()
		{
			return value;
		}
	}

	public static interface Visitor<P, R>
	{
		public R visit(Binary b, P param);
		public R visit(Literal l, P param);
	}
}
