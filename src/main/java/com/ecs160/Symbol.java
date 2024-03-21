package com.ecs160;

import java.awt.*;
import java.awt.RenderingHints;
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
    private int symbolWidth = 0;
    private int symbolHeight = 0;
    
    // these are set dynamically as the accidental is updateded by user
    private int accidentalWidth = 0;
    private int accidentalHeight = 0;
    // gaps between note and its accidental
    private int accidentalXbuffer = 5;
    private int accidentalYbuffer = 5;
    private MusicSymbol accidental = null;
    
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
        g2.drawImage(img, accidentalWidth, 0, symbolWidth, symbolHeight, null);
        
        // draw accidental if applicable
        if (accidental != null) { 
            if (selected) img = accidental.highlightImage;
            else img = accidental.image;
            g2.drawImage(img, 0, symbolHeight - accidentalHeight, 
            accidentalWidth - accidentalXbuffer, accidentalHeight - accidentalYbuffer, null);
        }
    }

    public void resize(int size) {
        // update the size of the component and its children
        double largest = (double) Math.max(sym.width, sym.height);
        this.size = size;
        symbolWidth = (int) (size * sym.width / largest);
        symbolHeight = (int) (size * sym.height / largest);
        updateBounds();
    }

    public MusicSymbol getAccidental() {
        return this.accidental;
    }

    public void setAccidental(MusicSymbol newAccidental) {
        accidental = newAccidental;
        // clear accidental if arg is null or if new accidental is the same as old one
        if (newAccidental == null) {
            accidentalWidth = 0;
            accidentalHeight = 0;
            updateBounds();
            return;
        }
        // set relevant accidental fields for drawing to screen 
        double largest = (double) Math.max(accidental.width, accidental.height) * accidental.scale;
        // divide by sym.scale so that accidentals are always full size
        double accidentalSize = (size / sym.scale) * 0.5;
        accidentalWidth = (int) (accidentalSize * accidental.width / largest) + accidentalXbuffer;
        accidentalHeight = (int) (accidentalSize * accidental.height / largest) + accidentalYbuffer;
        updateBounds(); 
    }

    // update the bounds of the component to fit both the symbol
    // and accidental
    private void updateBounds() {
        setBounds(getX() - accidentalWidth, getY(), 
            symbolWidth + accidentalWidth, 
            Math.max(symbolHeight, accidentalHeight));
    }

    public int getBottomY() {
        return getY() + getHeight();
    }
    public void select() {
        selected = true;
    }

    public int getSymbolX() {
        // ignore the added width of the accidental
        int x = getX();
        return x + accidentalWidth;
    }

    public int getSymbolY() {
        // ignore the added height of the accidental
        int y = getY();
        if (accidentalHeight > symbolHeight) 
            y += accidentalHeight - symbolHeight;
        return y;
    }
    public void deselect() {
        selected = false;
    }
}