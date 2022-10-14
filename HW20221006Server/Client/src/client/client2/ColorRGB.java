package client.client2;

import java.awt.*;

public enum ColorRGB {
    RED(255, 0, 0, new Color(255,0,0)),
    YELLOW(255, 255, 0, new Color(255,255,0)),
    GREEN(0, 214, 120, new Color(0,214,120)),
    GREY(238, 238, 238, new Color(238,238,238));

    private int r;
    private int g;
    private int b;
    private Color color;

    ColorRGB(int r, int g, int b, Color color) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.color = color;
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }

    public Color getColor() {
        return color;
    }
}
