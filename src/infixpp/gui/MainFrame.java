package infixpp.gui;

import infixpp.ast.ASTNode;
import infixpp.ast.Context;
import infixpp.ast.parser.Parser;
import infixpp.ast.parser.exception.ParserException;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import util.TextFileIO;

@SuppressWarnings("serial")
public class MainFrame extends JFrame
{
	private MainPanel mainPanel;
	private JTextArea textCode;
	private JButton buttonUpdate;
	private JSlider sliderHGap;
	private JSlider sliderVGap;
	private JSlider sliderNodeSize;

	public MainFrame()
	{
		setTitle("InfixPP GUI");

		JPanel panelRight = new JPanel(new BorderLayout());
		mainPanel = new MainPanel();
		panelRight.add(mainPanel, BorderLayout.CENTER);

		sliderHGap = new JSlider(0, 100, 20);
		sliderHGap.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				mainPanel.setHorizontalGap(sliderHGap.getValue());
				mainPanel.repaint();
			}
		});
		sliderVGap = new JSlider(0, 100, 80);
		sliderVGap.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				mainPanel.setVerticalGap(sliderVGap.getValue());
				mainPanel.repaint();
			}
		});
		sliderNodeSize = new JSlider(1, 100, 32);
		sliderNodeSize.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				mainPanel.setNodeSize(sliderNodeSize.getValue());
				mainPanel.repaint();
			}
		});

		JPanel panelEdit = new JPanel(new BorderLayout());
		textCode = new JTextArea(20, 40);
		textCode.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		panelEdit.add(new JScrollPane(textCode), BorderLayout.CENTER);
		buttonUpdate = new JButton("parse");
		buttonUpdate.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				parse(textCode.getText());
			}
		});
		panelEdit.add(buttonUpdate, BorderLayout.SOUTH);

		JPanel panelSliders = new JPanel();
		panelSliders.setLayout(new BoxLayout(panelSliders, BoxLayout.Y_AXIS));
		panelSliders.add(sliderHGap);
		panelSliders.add(sliderVGap);
		panelSliders.add(sliderNodeSize);
		panelRight.add(panelSliders, BorderLayout.SOUTH);

		JSplitPane split = new JSplitPane();
		split.setLeftComponent(panelEdit);
		split.setRightComponent(panelRight);
		add(split, BorderLayout.CENTER);

		setJMenuBar(new MainMenuBar());

		pack();
	}

	private void parse(String code)
	{
		Context ctx = new Context();
		Parser parser = new Parser(code);
		try
		{
			List<ASTNode> list = parser.parse(ctx);
			if (!list.isEmpty())
			{
				mainPanel.setTreeGraph(TreeGraphTransformer.toTreeGraph(list.get(0)));
				mainPanel.repaint();
			}
		}
		catch (ParserException e)
		{
			System.err.println(e.getMessage());
		}
	}

	private void openFile()
	{
		JFileChooser chooser = new JFileChooser(new File("."));
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			File file = chooser.getSelectedFile();
			try
			{
				textCode.setText(TextFileIO.read(file));
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void saveAs()
	{
		JFileChooser chooser = new JFileChooser(new File("."));
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			File file = chooser.getSelectedFile();
			try
			{
				TextFileIO.write(file, textCode.getText());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void exportImage()
	{
		JFileChooser chooser = new JFileChooser(new File("."));
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			File file = chooser.getSelectedFile();
			try
			{
				mainPanel.saveImage(file);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void close()
	{
		dispose();
	}

	private class MainMenuBar extends JMenuBar
	{
		public MainMenuBar()
		{
			JMenu menuFile = new JMenu("File");
			JMenuItem itemOpen = new JMenuItem("Open File...");
			itemOpen.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					openFile();
				}
			});
			menuFile.add(itemOpen);

			menuFile.addSeparator();

			JMenuItem itemSaveAs = new JMenuItem("Save As...");
			itemSaveAs.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					saveAs();
				}
			});
			menuFile.add(itemSaveAs);

			final JMenuItem itemSaveImage = new JMenuItem("Export Image...");
			itemSaveImage.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					exportImage();
				}
			});
			menuFile.add(itemSaveImage);

			menuFile.addSeparator();

			JMenuItem itemExit = new JMenuItem("Exit");
			itemExit.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					close();
				}
			});
			menuFile.add(itemExit);

			menuFile.addMenuListener(new MenuListener()
			{
				public void menuSelected(MenuEvent e)
				{
					itemSaveImage.setEnabled(mainPanel.hasTreeGraph());
				}

				public void menuDeselected(MenuEvent e)
				{
				}

				public void menuCanceled(MenuEvent e)
				{
				}
			});
			add(menuFile);
		}
	}
}
