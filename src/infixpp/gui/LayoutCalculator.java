package infixpp.gui;

import infixpp.gui.TreeGraph.Leaf;
import infixpp.gui.TreeGraph.Node;

public class LayoutCalculator
{
	private int nodeSize;
	private int hgap;
	private int vgap;
	private int width;
	private int height;
	private VisitorImpl visitor;

	public LayoutCalculator(int nodeSize, int hgap, int vgap)
	{
		this.nodeSize = nodeSize;
		this.hgap = hgap;
		this.vgap = vgap;
		visitor = new VisitorImpl();
	}

	public int getNodeSize()
	{
		return nodeSize;
	}

	public void setNodeSize(int nodeSize)
	{
		this.nodeSize = nodeSize;
	}

	public void setHorizontalGap(int hgap)
	{
		this.hgap = hgap;
	}

	public void setVerticalGap(int vgap)
	{
		this.vgap = vgap;
	}

	public void layout(TreeGraph treeGraph)
	{
		width = height = 0;
		visitor.traverse(treeGraph);
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	private class VisitorImpl implements TreeGraph.Visitor<Integer, Void>
	{
		private int x0;

		private void traverse(TreeGraph treeGraph)
		{
			x0 = 0;
			treeGraph.accept(this, 0);
		}

		public Void visit(Node node, Integer y0)
		{
			node.getLeft().accept(this, y0 + nodeSize + vgap);

			x0 += nodeSize / 2 + hgap;
			node.setLocation(x0 + nodeSize / 2, y0 + nodeSize / 2);
			x0 += nodeSize / 2 + hgap;

			node.getRight().accept(this, y0 + nodeSize + vgap);
			return null;
		}

		public Void visit(Leaf leaf, Integer y0)
		{
			leaf.setLocation(x0 + nodeSize / 2, y0 + nodeSize / 2);
			width = Math.max(leaf.getX() + nodeSize / 2, width);
			height = Math.max(leaf.getY() + nodeSize / 2, height);
			return null;
		}
	}
}
