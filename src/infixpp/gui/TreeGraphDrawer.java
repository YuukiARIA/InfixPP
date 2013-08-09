package infixpp.gui;

import infixpp.gui.TreeGraph.Leaf;
import infixpp.gui.TreeGraph.Node;

import java.awt.Color;
import java.awt.Graphics2D;

public class TreeGraphDrawer
{
	private int nodeSize;
	private VisitorImpl visitor;

	public TreeGraphDrawer(int nodeSize)
	{
		this.nodeSize = nodeSize;
		visitor = new VisitorImpl();
	}

	public void draw(Graphics2D g, TreeGraph treeGraph)
	{
		treeGraph.accept(visitor, g);
	}

	private void drawEdge(Graphics2D g, TreeGraph a, TreeGraph b)
	{
		g.setColor(Color.BLACK);
		g.drawLine(a.getX(), a.getY(), b.getX(), b.getY());
	}

	private void drawNode(Graphics2D g, TreeGraph node)
	{
		g.setColor(Color.WHITE);
		g.fillOval(node.getX() - nodeSize / 2, node.getY() - nodeSize / 2, nodeSize, nodeSize);
		g.setColor(Color.BLACK);
		g.drawOval(node.getX() - nodeSize / 2, node.getY() - nodeSize / 2, nodeSize, nodeSize);

		g.drawString(node.getLabel(), node.getX(), node.getY());
	}

	private class VisitorImpl implements TreeGraph.Visitor<Graphics2D, Void>
	{
		public Void visit(Node node, Graphics2D g)
		{
			drawEdge(g, node, node.getLeft());
			drawEdge(g, node, node.getRight());

			drawNode(g, node);
			node.getLeft().accept(this, g);
			node.getRight().accept(this, g);
			return null;
		}

		public Void visit(Leaf leaf, Graphics2D g)
		{
			drawNode(g, leaf);
			return null;
		}
	}
}
