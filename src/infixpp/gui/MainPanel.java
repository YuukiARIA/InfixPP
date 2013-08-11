package infixpp.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class MainPanel extends JPanel
{
	private TreeGraph treeGraph;
	private LayoutCalculator layoutCalculator;
	private TreeGraphDrawer drawer;

	public MainPanel()
	{
		setPreferredSize(new Dimension(300, 300));

		layoutCalculator = new LayoutCalculator(32, 50, 50);
		drawer = new TreeGraphDrawer();
	}

	public boolean hasTreeGraph()
	{
		return treeGraph != null;
	}

	public void setTreeGraph(TreeGraph treeGraph)
	{
		this.treeGraph = treeGraph;
	}

	public void setNodeSize(int nodeSize)
	{
		layoutCalculator.setNodeSize(nodeSize);
	}

	public void setHorizontalGap(int hgap)
	{
		layoutCalculator.setHorizontalGap(hgap);
	}

	public void setVerticalGap(int vgap)
	{
		layoutCalculator.setVerticalGap(vgap);
	}

	public void saveImage(File file) throws IOException
	{
		if (treeGraph != null)
		{
			layoutCalculator.layout(treeGraph);
			int width = layoutCalculator.getWidth() + 40;
			int height = layoutCalculator.getHeight() + 40;
			BufferedImage image = new BufferedImage(width, height, Transparency.OPAQUE);
			Graphics2D g = (Graphics2D)image.getGraphics();
			fillBackground(g, width, height);
			draw(g, width, height);
			g.dispose();
			ImageIO.write(image, "png", file);
		}
	}

	private void draw(Graphics2D g, int width, int height)
	{
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int x0 = (width - layoutCalculator.getWidth()) / 2;
		int y0 = (height - layoutCalculator.getHeight()) / 2;
		drawer.draw(g, x0, y0, layoutCalculator.getNodeSize(), treeGraph);
	}

	protected void paintComponent(Graphics g)
	{
		fillBackground(g, getWidth(), getHeight());
		if (treeGraph != null)
		{
			layoutCalculator.layout(treeGraph);
			draw((Graphics2D)g, getWidth(), getHeight());
		}
	}

	private static void fillBackground(Graphics g, int width, int height)
	{
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
	}
}
