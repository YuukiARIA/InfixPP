package infixpp.gui;

public abstract class TreeGraph
{
	private String label;

	public String getLabel()
	{
		return label;
	}

	public abstract <P, R> R accept(Visitor<P, R> visitor, P param);

	public static class Node extends TreeGraph
	{
		private TreeGraph left;
		private TreeGraph right;

		public Node(TreeGraph left, TreeGraph right)
		{
			this.left = left;
			this.right = right;
		}

		public TreeGraph getLeft()
		{
			return left;
		}

		public TreeGraph getRight()
		{
			return right;
		}

		public <P, R> R accept(Visitor<P, R> visitor, P param)
		{
			return visitor.visit(this, param);
		}
	}

	public static class Leaf extends TreeGraph
	{
		public <P, R> R accept(Visitor<P, R> visitor, P param)
		{
			return visitor.visit(this, param);
		}
	}

	public static interface Visitor<P, R>
	{
		public R visit(Node node, P param);
		public R visit(Leaf leaf, P param);
	}
}
