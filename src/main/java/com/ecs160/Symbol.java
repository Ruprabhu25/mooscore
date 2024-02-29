package com.ecs160;

import java.awt.*;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

class Symbol extends JComponent {
    public final MusicSymbol sym;
    public boolean selected = false;
    private int symbolWidth = 0;
    private int symbolHeight = 0;
    private MusicSymbol accidental = null;

    public Symbol(MusicSymbol sym, int x, int y) {
        super();
        this.sym = sym;
        setPreferredSize(new Dimension(sym.width, sym.height)); // Set default size
        setLocation(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        BufferedImage img;
        if (selected) img = sym.highlightImage;
        // if (accidental != null) g2.drawImage(accidental.image, getWidth() + 10 )
        else img = sym.image;
        g2.drawImage(img, 0, 0, symbolWidth, symbolHeight, null);
    }

    public void resize(int size) {
        double largest = (double) Math.max(sym.width, sym.height);
        symbolWidth = (int) (size * sym.width / largest);
        symbolHeight = (int) (size * sym.height / largest);
        setBounds(getX(), getY(), symbolWidth, symbolHeight);
    }

    public void setAccidental(MusicSymbol accidental) {
        // widen symbol to allow for drawing 
    }

    public void select() {
        selected = true;
    }

    public void deselect() {
        selected =false;
    }
}