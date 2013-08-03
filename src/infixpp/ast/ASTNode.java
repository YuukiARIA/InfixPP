package infixpp.ast;

public abstract class ASTNode
{
	public static class Binary extends ASTNode
	{
		public final String operator;
		public final ASTNode x;
		public final ASTNode y;

		public Binary(String operator, ASTNode x, ASTNode y)
		{
			this.operator = operator;
			this.x = x;
			this.y = y;
		}
	}

	public static class Literal extends ASTNode
	{
		public final String value;

		public Literal(String value)
		{
			this.value = value;
		}
	}
}
