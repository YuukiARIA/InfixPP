package infixpp.gui;

import infixpp.ast.ASTNode;
import infixpp.ast.ASTNode.Binary;
import infixpp.ast.ASTNode.Literal;

public final class TreeGraphTransformer
{
	private static VisitorImpl visitor;

	private TreeGraphTransformer() { }

	public static TreeGraph toTreeGraph(ASTNode ast)
	{
		if (visitor == null)
		{
			visitor = new VisitorImpl();
		}
		return ast.accept(visitor, null);
	}

	private static class VisitorImpl implements ASTNode.Visitor<Void, TreeGraph>
	{
		public TreeGraph visit(Binary b, Void param)
		{
			TreeGraph l = b.x.accept(this, param);
			TreeGraph r = b.y.accept(this, param);
			return new TreeGraph.Node(b.operator.getNodeName(), l, r);
		}

		public TreeGraph visit(Literal l, Void param)
		{
			return new TreeGraph.Leaf(l.value);
		}
	}
}
