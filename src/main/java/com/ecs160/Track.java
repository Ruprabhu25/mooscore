package com.ecs160;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

class Symbol extends JComponent {
    private MusicSymbol sym;
    public boolean selected = false;
    private int lastX, lastY;
    public Symbol(MusicSymbol sym, int x, int y) {
        super();
        this.sym = sym;
        setPreferredSize(new Dimension(sym.width, sym.height)); // Set default size
        // setBorder(BorderFactory.createLineBorder(Color.black));
        setBackground(Color.BLUE); // Set background color
        setOpaque(true); // Make sure the background color is visible
        setLocation(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        BufferedImage img;
        if (selected) img = sym.highlightImage;
        else img = sym.image;
        g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
    }

    public void resize(int size) {
        double largest = (double) Math.max(sym.width, sym.height);
        int w = (int) (size * sym.width / largest);
        int h = (int) (size * sym.height / largest);
        setBounds(getX(), getY(), w, h);
    }

    public void select() {
        // setBorder(BorderFactory.createLineBorder(Color.blue));
        selected = true;
    }

    public void deselect() {
        // setBorder(BorderFactory.createLineBorder(Color.black));
        selected =false;
    }
}

public class Track extends JPanel {
    private int gridSize = 20;
    private int gridHeight = 41;
    private int symbolSize = gridSize*4;
    private Point drag_p1, drag_p2;
    private int moust_lastX, mouse_lastY;

    private ArrayList<Symbol> selected;
    
    public Track() {
        setLayout(null); // Use absolute positioning
        selected = new ArrayList<Symbol>();
        // Add some draggable components
        MusicSymbol imgs[] = {MusicSymbol.QUARTER, MusicSymbol.BASS, MusicSymbol.HALF, MusicSymbol.EIGTH, MusicSymbol.WHOLE};
        for (int i = 0; i < 5; i++) {
            createNewSymbol(imgs[i]);
        }
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                // check against all components
                moust_lastX = e.getX();
                mouse_lastY = e.getY();
                boolean found = false;
                for (Component c :  getComponents()) {
                    Symbol s = (Symbol) (c);
                    if (s.getBounds().contains(e.getPoint())) {
                        found = true;
                        if (selected.isEmpty()) select(s);
                        else if (!selected.contains(s)) {
                            clearSelection();
                            select(s);
                        }
                    }
                }
                if (!found) {
                    clearSelection();
                }
                drag_p1 = e.getPoint();
                repaint();
            }
            public void mouseReleased(MouseEvent e) {
                if (drag_p1 == null || drag_p2 == null) return;
                int minx = Math.min(drag_p1.x, drag_p2.x);
                int maxx = Math.max(drag_p1.x, drag_p2.x);
                int miny = Math.min(drag_p1.y, drag_p2.y);
                int maxy = Math.max(drag_p1.y, drag_p2.y);
                for (Component c :  getComponents()) {
                    Symbol s = (Symbol) (c); 
                    int x = s.getX();
                    int y = s.getY();
                    if (x >= minx && x <= maxx
                     && y >= miny && y <= maxy) select(s);;
                    // else s.deselect();
                }
                System.out.println(selected);
                drag_p1 = null;
                drag_p2 = null;
        }});
        
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (!selected.isEmpty()) {
                    int dx = e.getX() - moust_lastX;
                    int dy = e.getY() - mouse_lastY;
                    for (Symbol s : selected) moveSymbol(s, s.getX() + dx, s.getY() + dy);
                    Point lastGridPoint = getGridPoint(moust_lastX + dx, mouse_lastY + dy);
                    moust_lastX = lastGridPoint.x;
                    mouse_lastY = lastGridPoint.y;
                    drag_p2 = null;
                }
                else {
                    drag_p2 = e.getPoint();
                }
                repaint();
            }
        });
    }

    private void select(Symbol s) {
        selected.add(s);
        s.select();
        repaint();
    }

    private void clearSelection() {
        for (Symbol s : selected) 
            s.deselect();
        selected.clear();
        repaint();
    }

    private Point getGridPoint(int x, int y) {
        Point out = new Point(x, y);
        out.x = (out.x / gridSize) * gridSize;
        out.y = (out.y / gridSize) * gridSize;    
        return out;
    }

    private void createNewSymbol(MusicSymbol newSym, int x, int y) {
        Symbol symbol = new Symbol(newSym, x, y);
        symbol.resize((int) (symbolSize * newSym.scale));
        add(symbol);
    }

    private void createNewSymbol(MusicSymbol newSym) {
        int x = (int) (Math.random() * 400); // Random x position
        int y = (int) (Math.random() * 400); // Random y position
        Point gridPoint = getGridPoint(x, y);
        createNewSymbol(newSym, gridPoint.x, gridPoint.y);
    }

    private void moveSymbol(Symbol s, int x, int y) {
        // System.out.println(dx + ", " + dy + ", " + (s.getX() + dx) + ", " + (s.getY() + dy));
        Point gridPoint = getGridPoint(x, y);
        s.setLocation(gridPoint);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Graphics2D g2 = (Graphics2D) g;
        // draw score lines
        int center = (gridHeight / 2) + 1;
        for (int line = -2; line <= 2; line++) 
            g.drawLine(100, (center + line) * gridSize, 1000, (center + line) * gridSize);
        // draw mouse drag box
        if (drag_p1 == null || drag_p2 == null) return;
        int minx = Math.min(drag_p1.x, drag_p2.x);
        int maxx = Math.max(drag_p1.x, drag_p2.x);
        int miny = Math.min(drag_p1.y, drag_p2.y);
        int maxy = Math.max(drag_p1.y, drag_p2.y);
        g.setColor(Color.BLUE);
        g.drawRect(minx, miny, maxx - minx, maxy - miny);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Draggable Container");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 500);
            Track draggableContainer = new Track();
            JScrollPane sp = new JScrollPane(draggableContainer);
            frame.setContentPane(sp);
            frame.setVisible(true);
        });
    }
}
