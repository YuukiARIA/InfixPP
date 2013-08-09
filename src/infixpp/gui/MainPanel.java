package infixpp.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class MainPanel extends JPanel
{
	private TreeGraph treeGraph;
	private LayoutCalculator layoutCalculator;
	private TreeGraphDrawer drawer;

	public MainPanel()
	{
		setPreferredSize(new Dimension(500, 500));

		treeGraph =
			new TreeGraph.Node("+",
				new TreeGraph.Node("*",
					new TreeGraph.Leaf("1"),
					new TreeGraph.Leaf("2")),
				new TreeGraph.Leaf("3"));

		layoutCalculator = new LayoutCalculator(32, 80, 80);
		drawer = new TreeGraphDrawer(32);
	}

	private void draw(Graphics2D g)
	{
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		layoutCalculator.layout(treeGraph, 0, 0);
		drawer.draw((Graphics2D)g, treeGraph);
	}

	protected void paintComponent(Graphics g)
	{
		draw((Graphics2D)g);
	}
}
