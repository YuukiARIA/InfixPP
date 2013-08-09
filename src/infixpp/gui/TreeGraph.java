package infixpp.gui;

public abstract class TreeGraph
{
	private String label;
	private int x;
	private int y;

	public TreeGraph(String label)
	{
		this.label = label;
	}

	public void setLocation(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public String getLabel()
	{
		return label;
	}

	public abstract <P, R> R accept(Visitor<P, R> visitor, P param);

	public static class Node extends TreeGraph
	{
		private TreeGraph left;
		private TreeGraph right;

		public Node(String label, TreeGraph left, TreeGraph right)
		{
			super(label);
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
		public Leaf(String label)
		{
			super(label);
		}

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
