package infixpp.gui;

import infixpp.gui.TreeGraph.Leaf;
import infixpp.gui.TreeGraph.Node;

import java.awt.Color;
import java.awt.Graphics2D;

public class TreeGraphDrawer
{
	private int nodeSize;
	private int x0;
	private int y0;
	private Color nodeColor = new Color(230, 230, 255);
	private Color leafColor = new Color(255, 230, 230);
	private VisitorImpl visitor;

	public TreeGraphDrawer(int nodeSize)
	{
		this.nodeSize = nodeSize;
		visitor = new VisitorImpl();
	}

	public void draw(Graphics2D g, int x0, int y0, TreeGraph treeGraph)
	{
		this.x0 = x0;
		this.y0 = y0;
		visitor.traverse(treeGraph, g);
	}

	private void drawEdge(Graphics2D g, TreeGraph a, TreeGraph b)
	{
		g.setColor(Color.BLACK);
		g.drawLine(x0 + a.getX(), y0 + a.getY(), x0 + b.getX(), y0 + b.getY());
	}

	private void drawNode(Graphics2D g, TreeGraph node, Color fillColor)
	{
		int nodeX = x0 + node.getX() - nodeSize / 2;
		int nodeY = y0 + node.getY() - nodeSize / 2;
		g.setColor(fillColor);
		g.fillOval(nodeX, nodeY, nodeSize, nodeSize);
		g.setColor(Color.BLACK);
		g.drawOval(nodeX, nodeY, nodeSize, nodeSize);

		g.drawString(node.getLabel(), x0 + node.getX(), y0 + node.getY());
	}

	private class VisitorImpl implements TreeGraph.Visitor<Graphics2D, Void>
	{
		public void traverse(TreeGraph treeGraph, Graphics2D g)
		{
			treeGraph.accept(this, g);
		}

		public Void visit(Node node, Graphics2D g)
		{
			drawEdge(g, node, node.getLeft());
			drawEdge(g, node, node.getRight());

			drawNode(g, node, nodeColor);
			node.getLeft().accept(this, g);
			node.getRight().accept(this, g);
			return null;
		}

		public Void visit(Leaf leaf, Graphics2D g)
		{
			drawNode(g, leaf, leafColor);
			return null;
		}
	}
}
