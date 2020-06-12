package pl.edu.pw.stud.bialek2.marcin.proz.views;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;


public class RoundedImageView extends JPanel {
    private static final long serialVersionUID = 3557801757471949130L;
    
    private BufferedImage image;
    private BufferedImage scaledImage;
    private int cornerRadius;
    private int currentWidth = 0;
    private int currentHeight = 0;
    private Timer timer;

    public RoundedImageView(BufferedImage image) {
        this(image, 10);
    }

    public RoundedImageView(BufferedImage image, int cornerRadius) {
        super();
        this.setOpaque(false);
        this.image = image;
        this.cornerRadius = cornerRadius;
    }

    private static BufferedImage scaleAndRoundCorners(BufferedImage image, int width, int height, int cornerRadius) {
        final BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphics = output.createGraphics();

        graphics.setComposite(AlphaComposite.Src);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(Color.WHITE);
        graphics.fillRoundRect(0, 0, width, height, 2 * cornerRadius, 2 * cornerRadius);

        graphics.setComposite(AlphaComposite.SrcIn);
        //final Image scaled = image.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);
        graphics.drawImage(image, 0, 0, width, height, null);

        graphics.dispose();
        return output;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(this.image.getWidth(), this.image.getHeight());
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        final Insets insets = this.getInsets();
        final int width = this.getWidth() - insets.left - insets.right;
        final int height = this.getHeight() - insets.top - insets.bottom;

        if(this.scaledImage == null) {
            this.currentWidth = width;
            this.currentHeight = height;
            this.scaledImage = scaleAndRoundCorners(image, currentWidth, currentHeight, cornerRadius);
        }

        if(this.currentWidth != width) {
            this.currentWidth = width;
            this.currentHeight = height;

            if(this.timer == null) {
                this.timer = new Timer(200, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        timer.stop();
                        timer = null;

                        scaledImage = scaleAndRoundCorners(image, currentWidth, currentHeight, cornerRadius);

                        SwingUtilities.invokeLater(() -> {
                            repaint();
                            revalidate();
                        });
                    }
                });

                this.timer.start();
            }
            else {
                this.timer.restart();
            }
        }
        
        graphics.drawImage(this.scaledImage, insets.left, insets.top, this);
    }
}

