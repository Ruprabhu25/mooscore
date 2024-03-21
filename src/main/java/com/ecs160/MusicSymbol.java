package com.ecs160;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public enum MusicSymbol {
    // Note images are created using their tiled position inside 
    // the master image, notes.png
    BASS (1, 1, 1, 0),
    REPEAT_END (3, 1, 1, 0),
    REPEAT_START (4, 1, 1, 0),
    END_MEASURE_BAR (5, 1, 1, 0),
    DOUBLE_MEASURE_BAR (7, 1, 1, 0),
    TREBLE (2, 2, 1, 0),
    WHOLE (7, 2, 0.4, 16),
    HALF (5, 2, 1, 8),
    QUARTER (3, 2, 1, 4),
    EIGHTH (10, 3, 1, 2),
    SIXTEENTH (9, 3, 1, 1),
    
    // rests 
    WHOLE_REST (6, 2, 0.5, -16),
    HALF_REST (8, 2, 0.4, -8),
    QUARTER_REST (3, 9, 0.5, -4),
    EIGTH_REST (6, 6, 0.5, -2),
    SIXTEENTH_REST (5, 6, 0.7, -1),
    // accidentals / other
    DOUBLE_SHARP (1,4,0.7,0),
    NATURAL (2,4,1,0),
    DOUBLE_FLAT (3, 4, 1, 0),
    FLAT (4, 4, 1, 0),
    SHARP (6, 5, 1, 0),
    QUARTER_NOTE_EQUALS (2, 5, 1, 0),
    BREATH_MARK (6, 4, 0.5, 0);
    
    /* These fields are made public so that they can be accessed by other classes, but
     * they are final so that modifications can't be made. The images are references
     * however so they could be modified, but that would be very rude.
    */
    BufferedImage image; // main image taken from master image
    BufferedImage highlightImage; // recolored image to show when selected
    // the width of the image, shrunken to fit the contents
    final int width; 
    // the height of the image, shrunken to fit the contents
    final int height; 
    // the scale relative to other symbols that this symbol should be drawn at 
    double scale; 
    // this is the smallest note possible, a quarter note is RESOLUTION ticks long
    static final int RESOLUTION = 4; 
    // 0 indicates it is not a playable note, negative indicates it is a rest
    final int noteDuration; 
    
    private MusicSymbol(int tilex, int tiley, double scale, int duration) {
        new ImageLoader(); // load the main png file if it isnt loaded already
        this.scale = scale;
        noteDuration = duration;

        int x = (tilex * g_width) + x_offset + buf;
        int y = (tiley * g_height) + buf + y_offset;
        BufferedImage full_image = ImageLoader.master.getSubimage(x, y, g_width - buf, g_height - buf);
        BufferedImage highlight = new BufferedImage(g_width-buf, g_height-buf, BufferedImage.TYPE_INT_ARGB);
        
        // Find the edges of the image, and set the width and height accordingly
        // Also, initialize the highlight image by copying black pixels as highlightRGB
        int x1 = g_width;
        int y1 = g_height;
        int x2 = 0;
        int y2 = 0;
        for (y = 0; y < full_image.getHeight(); y++) {
            for (x = 0; x < full_image.getWidth(); x++) {
                // negative pixels means it is an opaque black pixel = 0xFF000000 = -211...
                if (full_image.getRGB(x, y) < 0) {
                    // Store the lowest and highest found pixels
                    // to have the corners of the image well described 
                    x1 = Math.min(x1, x);
                    x2 = Math.max(x2, x);
                    y1 = Math.min(y1, y);
                    y2 = Math.max(y2, y);
                    // copy black pixels as blue in highlight image
                    highlight.setRGB(x, y, highlightRGB);
                }
                // copy empty pixels as empty in highlight image
                else highlight.setRGB(x, y, 0); // set to empty 
            }
        }
        width = (x2 - x1);
        height = (y2 - y1);
        
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) image.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        // draw subimage into final result image
        g2.drawImage(full_image, 0, 0, width, height, x1, y1, x1 + width, y1 + height, null);

        highlightImage = highlight.getSubimage(x1, y1, width, height);
    }

    /* This class exists so that the master image can be a static variable :p */ 
    private static final class ImageLoader {
        static BufferedImage master = null;
        public ImageLoader() {
            if (master == null)
                try {
                    String osName = System.getProperty("os.name").toLowerCase();
                    if (osName.contains("windows"))
                        master = ImageIO.read(new File( "src\\main\\java\\com\\ecs160\\imgs\\notes.png"));
                    else
                        master = ImageIO.read(new File("src/main/java/com/ecs160/imgs/notes.png"));
                } catch (IOException e) {
                    master = null;
                    e.printStackTrace();
                }
        }
    }  

    // return a smaller or bigger image of the music symbol provided
    public static Image getScaledImage(MusicSymbol sym, int size, boolean highlight) {
        double largest = (double) Math.max(sym.width, sym.height);
        int w = (int) (size * sym.width * sym.scale / largest);
        int h = (int) (size * sym.height * sym.scale / largest);
        if (highlight) return sym.highlightImage.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        else return sym.image.getScaledInstance(w, h, Image.SCALE_SMOOTH);
    }

    // return a smaller or bigger image of the music symbol provided
    public static Image getScaledImage(MusicSymbol sym, int size) {
        return getScaledImage(sym, size, false);
    }

    public int getDuration() {
        return this.noteDuration;
    }

    /* These are relevant inside the constructor, but not outside of this file */
    private final int g_width = 675; // the width of each tile in the main image
    private final int g_height = g_width - 42; // the grid's height is shorter than the width
    private final int x_offset = -84; // constant offset of the tile grid
    private final int y_offset = 327; // constant offset of the tile grid
    private final int buf = 30; // buffer around each image tile to clean up edges
    private final int highlightRGB = 0xFF5da4e3; // the color that will be drawn while notes are selected
}
