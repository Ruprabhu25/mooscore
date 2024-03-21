package com.ecs160;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

class Symbol extends JComponent {
    // The underlying music symbol this component draws and represents
    public final MusicSymbol sym;

    // this determins the overall scale of the symbol and is updated 
    // through resize
    private int size = 0;

    // these are determined by the underlying MusicSymbol and are 
    // scaled to fit the size. The largest between the sym's
    // width and height are set to be equal to the size. 
    private int symbolWidth;
    private int symbolHeight;
    
    // these are set dynamically as the accidental is updateded by user
    private int accidentalWidth = 0;
    private int accidentalHeight = 0;
    // gaps between note and its accidental
    private final int accidentalXbuffer = 5;
    // private final int accidentalYbuffer = 5;
    private MusicSymbol accidental = null;
    
    // these control the size of the actual component,
    // and are meant to fit both the symbol any accidental
    private int boundWidth;
    private int boundHeight;

    // controls whether the symbol draws as highlighted or not
    public boolean selected = false;

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
        g2.drawImage(img, getWidth() - symbolWidth, getHeight() - symbolHeight, symbolWidth, symbolHeight, null);
        
        // draw accidental if applicable
        if (accidental != null) { 
            if (selected) img = accidental.highlightImage;
            else img = accidental.image;
            g2.drawImage(img, (size / 3) - accidentalWidth, getHeight() - accidentalHeight, 
            accidentalWidth, accidentalHeight, null);
        }
    }

    public void resize(int size) {
        // update the size of the component
        this.size = size;
        double largest = (double) Math.max(sym.width, sym.height);
        symbolWidth = (int) (size * sym.width / largest);
        symbolHeight = (int) (size * sym.height / largest);
        int flatHeight = (int) ((size / sym.scale) / 2);

        // update the width and height of the symbols bounding box 
        boundWidth = symbolWidth + (size / 3) + accidentalXbuffer;
        boundHeight = Math.max(symbolHeight, flatHeight);

        // update the bounds of the symbol as a component
        setBounds(getX(), getY(), boundWidth, boundHeight);
    }

    public void setAccidental(MusicSymbol newAccidental) {
        accidental = newAccidental;
        // clear accidental if arg is null
        if (newAccidental == null) {
            accidentalWidth = 0;
            accidentalHeight = 0;
            // updateBounds();
            return;
        }

        // set relevant accidental fields for drawing to screen 
        double largest = (double) Math.max(accidental.width, accidental.height) * accidental.scale;
        // divide by sym.scale so that accidentals are always full size
        double accidentalSize = (size / sym.scale) * 0.5;
        accidentalWidth = (int) (accidentalSize * accidental.width / largest);
        accidentalHeight = (int) (accidentalSize * accidental.height / largest);        
    }

    public MusicSymbol getAccidental() {
        return accidental;
    }

    public int getBottomY() {
        return getY() + getHeight();
    }
    public void select() {
        selected = true;
    }

    public int getSymbolX() {
        // ignore the added width of the accidental
        // System.out.println("x: " + getX() + ", sym x: " + (getX() + (boundWidth - symbolWidth)));
        return getX() + (boundWidth - symbolWidth);
    }

    public void deselect() {
        selected = false;
    }
}