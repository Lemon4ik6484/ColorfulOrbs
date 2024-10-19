package dev.lemonnik.colorfulorbs;

public class HexToRGB {
    public static int[] convert(String hex) {

        int rgb = Integer.parseInt(hex.substring(1), 16);

        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;

        return new int[] { red, green, blue };
    }
}