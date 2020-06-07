package pl.edu.pw.stud.bialek2.marcin.proz.views;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicScrollBarUI;


public class RoundedScrollView extends JScrollPane {
    private static final long serialVersionUID = 2480762626282775187L;
    private static final int SCROLL_BAR_SIZE = 12;
    private static final int THUMB_SIZE = 8;
    private static final Color THUMB_COLOR = new Color(200, 200, 200, 70);
    private static final Color THUMB_HOVER_COLOR = new Color(200, 200, 200, 100);

    public RoundedScrollView(Component component) {
        super(component);
        this.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        this.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        this.setBorder(null);

        JScrollBar verticalScrollBar = this.getVerticalScrollBar();
        verticalScrollBar.setOpaque(false);
        verticalScrollBar.setUI(new RoundedScrollBarUI(this));
        verticalScrollBar.setPreferredSize(new Dimension(SCROLL_BAR_SIZE, Integer.MAX_VALUE));

        JScrollBar horizontalScrollBar = this.getHorizontalScrollBar();
        horizontalScrollBar.setOpaque(false);
        horizontalScrollBar.setUI(new RoundedScrollBarUI(this));
        horizontalScrollBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, SCROLL_BAR_SIZE));

        this.setComponentZOrder(this.getVerticalScrollBar(), 0);
        this.setComponentZOrder(this.getHorizontalScrollBar(), 1);
        this.setComponentZOrder(this.getViewport(), 2);

        this.viewport.setView(component);
    }   

    private static class RoundedScrollBarButton extends JButton {
        private static final long serialVersionUID = 7806689909962377352L;

        public RoundedScrollBarButton() {
            this.setOpaque(false);
            this.setFocusable(false);
            this.setFocusPainted(false);
            this.setBorderPainted(false);
            this.setBorder(BorderFactory.createEmptyBorder());
        }
    }

    private static class RoundedScrollBarUI extends BasicScrollBarUI {
        private JScrollPane scrollPane;

        public RoundedScrollBarUI(JScrollPane scrollPane) {
            this.scrollPane = scrollPane;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return new RoundedScrollBarButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return new RoundedScrollBarButton();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {}

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            final int space = SCROLL_BAR_SIZE - THUMB_SIZE;
            final int x = thumbBounds.x + space / 2;
            final int y = thumbBounds.y + space / 2;
            final int orientation = scrollbar.getOrientation();

            int width = orientation == JScrollBar.VERTICAL ? THUMB_SIZE : thumbBounds.width - space;
            width = Math.max(width, THUMB_SIZE);

            int height = orientation == JScrollBar.VERTICAL ? thumbBounds.height - space : THUMB_SIZE;
            height = Math.max(height, THUMB_SIZE);

            Graphics2D graphics2D = (Graphics2D)g.create();
            graphics2D.setColor(this.isThumbRollover() ? THUMB_HOVER_COLOR : THUMB_COLOR);
            graphics2D.fillRoundRect(x, y, width, height, THUMB_SIZE, THUMB_SIZE);
            graphics2D.dispose();
        }

        @Override
        protected void setThumbBounds(int x, int y, int width, int height) {
            super.setThumbBounds(x, y, width, height);
            this.scrollPane.repaint();
        }
    }
}

