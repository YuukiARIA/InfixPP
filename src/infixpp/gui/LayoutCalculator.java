package infixpp.gui;

import infixpp.gui.TreeGraph.Leaf;
import infixpp.gui.TreeGraph.Node;

public class LayoutCalculator
{
	private int nodeSize;
	private int hgap;
	private int vgap;
	private VisitorImpl visitor;

	public LayoutCalculator(int nodeSize, int hgap, int vgap)
	{
		this.nodeSize = nodeSize;
		this.hgap = hgap;
		this.vgap = vgap;
		visitor = new VisitorImpl();
	}

	public void layout(TreeGraph treeGraph, int x0, int y0)
	{
		visitor.x0 = x0;
		treeGraph.accept(visitor, y0);
	}

	private class VisitorImpl implements TreeGraph.Visitor<Integer, Void>
	{
		private int x0;

		public Void visit(Node node, Integer y0)
		{
			node.getLeft().accept(this, y0 + vgap);

			x0 += hgap;
			node.setLocation(x0 + nodeSize / 2, y0 + nodeSize / 2);
			x0 += hgap;

			node.getRight().accept(this, y0 + vgap);
			return null;
		}

		public Void visit(Leaf leaf, Integer y0)
		{
			leaf.setLocation(x0 + nodeSize / 2, y0 + nodeSize / 2);
			return null;
		}
	}
}
