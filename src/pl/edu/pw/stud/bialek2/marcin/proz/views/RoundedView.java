package pl.edu.pw.stud.bialek2.marcin.proz.views;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;


public class RoundedView extends JPanel {
    private static final long serialVersionUID = -3639064706728525554L;
    private JPanel content = new JPanel();
    private JPanel safeArea = new JPanel();
    private int cornerRadius;

    public RoundedView() {
        this(10);
    }   
    
    public RoundedView(int cornerRadius) {
        this.cornerRadius = cornerRadius;
        this.setOpaque(false);
        this.setLayout(new BorderLayout());

        this.safeArea.setOpaque(false);
        this.content.setOpaque(false);
        this.content.setLayout(new BorderLayout());
        this.content.setBorder(BorderFactory.createEmptyBorder(cornerRadius / 2, cornerRadius / 2, cornerRadius / 2, cornerRadius / 2));
        this.content.add(this.safeArea, BorderLayout.CENTER);
        this.add(this.content, BorderLayout.CENTER);
    } 

    public JPanel getSafeArea() {
        return this.safeArea;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D)graphics;
        super.paintComponent(g2);	
        
        Insets insets = this.getInsets();
        int width = this.getWidth() - insets.left - insets.right;
        int height = this.getHeight() - insets.top - insets.bottom;
        g2.setColor(this.getBackground());
        g2.fillRoundRect(insets.left, insets.top, width, height, 2 * this.cornerRadius, 2 * this.cornerRadius);
    }
}
