package com.ecs160;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class Symbol extends JComponent {
    private int lastX, lastY;
    private MusicSymbol sym;
    private int grid = 10;

    public Symbol(MusicSymbol sym) {
        super();
        this.sym = sym;
        setPreferredSize(new Dimension(sym.width, sym.height)); // Set default size
        setBorder(BorderFactory.createLineBorder(Color.black));
        setBackground(Color.BLUE); // Set background color
        setOpaque(true); // Make sure the background color is visible
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                lastX = e.getX();
                lastY = e.getY();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int dx = (e.getX() - lastX) / grid;
                int dy = (e.getY() - lastY) / grid;
                setLocation(getX() + dx * grid, getY() + dy * grid);
            }
        });
        System.out.println(getLocation());
        
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // largest dimension
        // int largest = Math.max(sym.width, sym.height);
        g.drawImage(sym.image, 0, 0, getWidth(), getHeight(), null);
    }
}

public class DraggableContainer extends JPanel {
    public DraggableContainer() {
        setLayout(null); // Use absolute positioning

        // Add some draggable components
        MusicSymbol imgs[] = {MusicSymbol.QUARTER, MusicSymbol.BASS, MusicSymbol.HALF, MusicSymbol.EIGTH, MusicSymbol.WHOLE};
        for (int i = 0; i < 5; i++) {
            Symbol draggableComponent = new Symbol(imgs[i]);
            int x = (int) (Math.random() * 400); // Random x position
            int y = (int) (Math.random() * 400); // Random y position
            int largest = Math.max(imgs[i].width, imgs[i].height);
            draggableComponent.setBounds(x, y, 50 * imgs[i].width / largest, 50 * imgs[i].height / largest); // Set bounds
            add(draggableComponent);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Draggable Container");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 500);
            DraggableContainer draggableContainer = new DraggableContainer();
            frame.setContentPane(draggableContainer);
            frame.setVisible(true);
        });
    }
}
