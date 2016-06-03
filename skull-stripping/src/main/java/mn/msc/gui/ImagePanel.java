package mn.msc.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {
	private static final long serialVersionUID = -7214264518954900221L;
	
	private BufferedImage image;

	public ImagePanel(BufferedImage image) {
			this.image = image;
			setSize(image.getWidth(), image.getHeight());
			setMinimumSize(new Dimension(image.getWidth(), image.getHeight()));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		//g.drawImage(image, 0, 0, null);
		g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
	}

}