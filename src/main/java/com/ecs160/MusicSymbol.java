package com.ecs160;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public enum MusicSymbol {
    BASS (1, 1),
    TREBLE (2, 2),
    WHOLE (7, 2),
    HALF (5, 2),
    QUARTER (3, 2),
    EIGTH (10, 3),
    SIXTEENTH (9, 3);
    private final int g_width = 675;
    private final int g_height = g_width - 38;
    
    private static final class Helper {
        static BufferedImage master = null;
        public Helper() {
            if (master == null)
                try {
                    master = ImageIO.read(new File( "src\\main\\java\\com\\ecs160\\imgs\\notes.png"));
                } catch (IOException e) {
                    master = null;
                    e.printStackTrace();
                }
        }
    }
    private MusicSymbol(int tilex, int tiley) {
        new Helper();
        int x_offset = -84;
        int y_offset = 327;
        int buf = 30;
        int x = (tilex * g_width) + x_offset + buf;
        int y = (tiley * g_height) + buf + y_offset;
        // image = Helper.master.getSubimage(x, y, g_width - buf, g_height - buf);
        // width = image.getWidth();
        // height = image.getHeight();
        BufferedImage full_image = Helper.master.getSubimage(x, y, g_width - buf, g_height - buf);
        int x1 = g_width;
        int y1 = g_height;
        int x2 = 0;
        int y2 = 0;
        for (y = 0; y < full_image.getHeight(); y++) {
            for (x = 0; x < full_image.getWidth(); x++) {
                if (full_image.getRGB(x, y) < 0) {
                    x1 = Math.min(x1, x);
                    x2 = Math.max(x2, x);
                    y1 = Math.min(y1, y);
                    y2 = Math.max(y2, y);
                }
            }
        }
        System.out.println(name() + ": " + x1 + ", " + x2 + ", " + y1 + ", " + y2);
        width = x2 - x1;
        height = y2 - y1;
        image = full_image.getSubimage(x1, y1, width, height);
    }
    public final BufferedImage image;
    public final int width;
    public final int height;
}
