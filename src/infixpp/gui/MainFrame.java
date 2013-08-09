package infixpp.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
		mainPanel = new MainPanel();
		add(mainPanel);

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

		JTabbedPane tab = new JTabbedPane();

		JPanel panelEdit = new JPanel(new BorderLayout());
		textCode = new JTextArea();
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
		tab.addTab("Code", panelEdit);

		JPanel panelSliders = new JPanel();
		panelSliders.setLayout(new BoxLayout(panelSliders, BoxLayout.Y_AXIS));
		panelSliders.add(sliderHGap);
		panelSliders.add(sliderVGap);
		panelSliders.add(sliderNodeSize);
		tab.addTab("View Settings", panelSliders);

		add(tab, BorderLayout.SOUTH);

		pack();
	}

	private void parse(String code)
	{
	}
}
