package com.ecs160;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

class ImageComponent extends JPanel {
    private static BufferedImage image;
    private static int grid_width = 666;
    private static double x_off = 0;
    private static double y_off = 0;
    private static int bonus_height = 0;
    
    public ImageComponent(String imagePath) {
        try {
            image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setPreferredSize(new Dimension(500, 500));
        addKeyListener(new MyKeyListener());
        this.setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            int w = Math.min(getWidth(), getHeight());
            // System.out.println();
            int gridSize = (int)((double) w / (double)((double)image.getWidth() / grid_width));
            int x_offset = (int) ((double)w * x_off);
            int y_offset = (int) ((double)w * y_off);
            // System.out.println(x_offset + ", " + y_offset);
            // System.out.println(gridSize);
            System.out.println(grid_width + ", " + x_off + ", " + y_off + ", " + bonus_height);
            g2.setColor(Color.red);
            for (int i = 0; i < w / gridSize; i++) {
                for (int j = 0; j < w / gridSize; j++) {
                    int x = i * gridSize + x_offset;
                    int y = j * gridSize + y_offset + bonus_height * j;
                    Line2D hor = new Line2D.Float(0,y, gridSize * 20, y);
                    g2.draw(hor);
                    Line2D vet = new Line2D.Float(x, 0, x, gridSize * 20);            
                    g2.draw(vet);
                    // g2.drawLine()
                    // g2.drawRect();
                }
            }
            g2.drawImage(image, 0, 0, w, w, this);
            g2.dispose();
        }
    }

    class MyKeyListener implements KeyListener {
        @Override
        public void keyPressed(KeyEvent e) {
        }
        @Override
        public void keyReleased(KeyEvent e) {

        }
        @Override
        public void keyTyped(KeyEvent e) {
            char keyCode = e.getKeyChar();
            // System.out.println(e);
            switch (keyCode) {
                case 'i':
                    grid_width += 3;
                    break;
                case 'k':
                    grid_width -= 3;
                    break;
                case 'j':
                    x_off -= 0.001;
                    break;
                case 'l':
                    x_off += 0.001;
                    break;
                case 'w':
                    y_off -= 0.001;
                    break;
                case 's':
                    y_off += 0.001;
                    break;
                case 'x':
                    bonus_height += 1;
                    break;
                case 'c':
                    bonus_height -= 1;
                    break;
                default:
            }
            repaint();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        if (image != null) {
            return new Dimension(500, 500);
        } else {
            return super.getPreferredSize();
        }
    }

x    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Image Component");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            // Path to your image file
            String imagePath = "src\\main\\java\\com\\ecs160\\imgs\\notes.png";

            ImageComponent imageComponent = new ImageComponent(imagePath);
            frame.add(imageComponent, BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
