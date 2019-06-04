package com.syntheticentropy.ggpipe;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorModel {

    private static ColorModel singleton;
    public static ColorModel get() {
        return singleton;
    }

    private List<ColorEntry> entries;

    public List<ColorEntry> getEntries() {
        return entries;
    }

    public ColorModel(List<ColorEntry> entries){
        this.entries = entries;
    }

    public static boolean init(String filename) {
        if (singleton != null) return false;
        try{
            BufferedReader reader = new BufferedReader(new FileReader(filename));

            List<ColorEntry> entries = new ArrayList<>();
            Pattern colorLine = Pattern.compile("^\\s*(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s*$");

            while(true){
                String line = reader.readLine();
                if(line == null) break;

                Matcher m = colorLine.matcher(line);
                if(!m.matches()) continue;

                int red = Integer.parseInt(m.group(1));
                int green = Integer.parseInt(m.group(2));
                int blue = Integer.parseInt(m.group(3));
                byte nes = Byte.parseByte(m.group(4));

                entries.add(new ColorEntry(red, green, blue, nes));
            }

            singleton = new ColorModel(entries);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public ColorEntry getEntryForNes(int nes) {
        return entries.stream().filter(e->e.nes==nes).findFirst().orElse(entries.get(0));
    }

    public Byte getNearestNesColorFromRgb(int rgb) {
        int red =   (rgb >> 16) & 0xFF;
        int green = (rgb >>  8) & 0xFF;
        int blue =  (rgb      ) & 0xFF;
        int nearestIndex = 0;
        int nearestDistance = entries.get(0).distanceTo(red, green, blue);
        for (int i = 1; i < entries.size(); i++) {
            int distance = entries.get(i).distanceTo(red, green, blue);
            if(distance < nearestDistance){
                nearestDistance = distance;
                nearestIndex = i;
            }
        }
        return entries.get(nearestIndex).nes;
    }

    public Byte getNearestIndexColorFromRgb(int rgb) {
        int red =   (rgb >> 16) & 0xFF;
        int green = (rgb >>  8) & 0xFF;
        int blue =  (rgb      ) & 0xFF;
        byte nearestIndex = 0;
        int nearestDistance = entries.get(0).distanceTo(red, green, blue);
        for (byte i = 1; i < 4; i++) {
            int distance = entries.get(i).distanceTo(red, green, blue);
            if(distance < nearestDistance){
                nearestDistance = distance;
                nearestIndex = i;
            }
        }
        return nearestIndex;
    }

    public static class ColorEntry {
        int red;
        int green;
        int blue;
        byte nes;
        public ColorEntry(int red, int green, int blue, byte nes) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.nes = nes;
        }

        public int distanceTo(int red, int green, int blue) {
            BiFunction<Integer, Integer, Integer> sq = (a,b)->(a-b)*(a-b);
            return sq.apply(this.red, red) + sq.apply(this.green, green) + sq.apply(this.blue, blue);
        }

        public int distanceTo(Integer rgb) {
            int red =   (rgb >> 16) & 0xFF;
            int green = (rgb >>  8) & 0xFF;
            int blue =  (rgb      ) & 0xFF;
            BiFunction<Integer, Integer, Integer> sq = (a,b)->(a-b)*(a-b);
            return sq.apply(this.red, red) + sq.apply(this.green, green) + sq.apply(this.blue, blue);
        }

        public int getRGB() {
            return (red << 16) | (green << 8) | blue;
        }

        public int getRed() {
            return red;
        }

        public int getGreen() {
            return green;
        }

        public int getBlue() {
            return blue;
        }

        public byte getNes() {
            return nes;
        }
    }
}
