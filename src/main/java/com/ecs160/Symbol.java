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
    private int accidentalWidth = 0;
    private int accidentalHeight = 0;
    private int size = 0;
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
        
        // set rendering hints to smooth out image drawing
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        
        // draw main symbol image 
        BufferedImage img;
        if (selected) img = sym.highlightImage;
        else img = sym.image;
        g2.drawImage(img, 0, 0, symbolWidth, symbolHeight, null);
        
        // draw accidental if applicable
        if (accidental != null) 
            if (selected) 
                g2.drawImage(accidental.highlightImage, symbolWidth, 
                             symbolHeight - accidentalHeight, null);
            else 
                g2.drawImage(accidental.highlightImage, symbolWidth, 
                symbolHeight - accidentalHeight, null);
    }

    public void resize(int size) {
        double largest = (double) Math.max(sym.width, sym.height);
        this.size = size;
        symbolWidth = (int) (size * sym.width / largest);
        symbolHeight = (int) (size * sym.height / largest);
        updateBounds();
    }

    public void setAccidental(MusicSymbol newAccidental) {
        accidental = newAccidental;
        // clear accidental if arg is null
        if (newAccidental == null) {
            accidentalWidth = 0;
            accidentalHeight = 0;
            updateBounds();
            return;
        } 
        double largest = (double) Math.max(accidental.width, accidental.height) * accidental.scale;
        accidentalWidth = (int) (size * accidental.width / largest);
        accidentalHeight = (int) (size * accidental.height / largest);
        updateBounds(); 
    }

    // update the bounds of the component to contain sub_images
    private void updateBounds() {
        setBounds(getX(), getY(), symbolWidth + accidentalWidth, symbolHeight);
    }

    public int getBottomY() {
        return getY() + getHeight();
    }
    public void select() {
        selected = true;
    }

    public void deselect() {
        selected = false;
    }
}