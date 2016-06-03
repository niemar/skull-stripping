package mn.msc.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import ij.ImagePlus;
import ij.gui.HistogramWindow;
import ij.gui.ImageCanvas;

public class MultiplyImagesWindow extends JFrame {
	private static final long serialVersionUID = 4213516032634215802L;

	private JPanel imagesPanel = new JPanel();
	private JScrollPane pane;

	private int rows, cols;
	private int x = 0, y = 0;
	private Dimension maxImageDim;
	
	public MultiplyImagesWindow(int rows, int cols, Dimension maxImageDim) throws HeadlessException {
		this.rows = rows;
		this.cols = cols;
		this.maxImageDim = maxImageDim;
		pane = new JScrollPane(imagesPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		pane.getVerticalScrollBar().setUnitIncrement(30);
		imagesPanel.setLayout(new GridBagLayout());
		getContentPane().add(pane);
		setLocation();
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		onEscKey();
	}

	private GridBagConstraints createConstraints() {
		GridBagConstraints gbc = new GridBagConstraints();
		if(x == cols) {
			x = 0;
			y++;
		}
		gbc.gridx = x++;
		gbc.gridy = y;
		gbc.insets = new Insets(5, 5, 5, 5);
		return gbc;
	}

	public void addImage(ImagePlus image) {
		BufferedImage bufferedImage = image.getBufferedImage();
		JLabel picLabel;
		// scale if needed
		if(bufferedImage.getWidth() > maxImageDim.getWidth() || bufferedImage.getHeight() > maxImageDim.getHeight()) {
			Image scaledImage = bufferedImage.getScaledInstance((int) maxImageDim.getWidth(), (int) maxImageDim.getHeight(), Image.SCALE_SMOOTH);		
			picLabel = new JLabel(new ImageIcon(scaledImage));
		}
		else
			picLabel = new JLabel(new ImageIcon(bufferedImage));
		imagesPanel.add(picLabel, createConstraints());
		pack();
		setSize();
	}
	
	public void addHistogram(HistogramWindow histogramWindow) {
		imagesPanel.add(histogramWindow, createConstraints());
		pack();
		setSize();
	}

	private void setSize() {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		setSize((int) (screenWidth / 1.5), screenHeight - screenHeight / 20);
	}

	private void setLocation() {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int screenWidth = screenSize.width;
		setLocation(screenWidth / 4, 0);
	}

	@SuppressWarnings("serial")
	public void onEscKey() {
		final JFrame thisFrame = this;
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				"Cancel");
		getRootPane().getActionMap().put("Cancel", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.setVisible(false);
				thisFrame.dispose();
			}
		});
		// on close window the close method is called
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				thisFrame.setVisible(false);
				thisFrame.dispose();
			}
		});
	}

}
