package com.syntheticentropy.ggpipe;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

//Takes one or more image files and handles it as a stream of 8x8 tiles
public class PipeCanvas {
    public static final int TILE_WIDTH = 8;
    public static final int TILE_HEIGHT = 8;
    int tileHeight = 8;
    List<BufferedImage> images;
    public PipeCanvas(List<String> inputFiles) {
        Function<String, BufferedImage> strToImage = fn -> {
            try{
                return ImageIO.read(new File(fn));
            }catch(Exception e){
                Main.abort("Unable to import image "+fn+", exception: "+e.getMessage());
                return null;
            }
        };
        images = inputFiles.stream().map(strToImage).collect(Collectors.toList());
    }

    public int tileTotal() {
        int count = 0;
        for (int i = 0; i < images.size(); i++) {
            count += tileCount(i);
        }
        return count;
    }

    public int tileCount(int imageIndex) {
        BufferedImage image = images.get(imageIndex);
        int width = (image.getWidth() - (image.getWidth() % TILE_WIDTH)) / TILE_WIDTH;
        int height = (image.getHeight() - (image.getHeight() % 8)) / 8;
        return width * height;
    }

    public int getXFromTileIndex(int tileIndex) {
        int count = 0;
        for (int i = 0; i < images.size(); i++) {
            int tileCount = tileCount(i);
            if(count + tileCount > tileIndex){
                int width = (images.get(i).getWidth() - (images.get(i).getWidth() % TILE_WIDTH)) / TILE_WIDTH;
                int tileIndexInImage = tileIndex - count;
                int x = tileIndexInImage % width;
                return x * TILE_WIDTH;
            }
            count += tileCount;
        }
        return -1;
    }

    public int getYFromTileIndex(int tileIndex) {
        int count = 0;
        for (int i = 0; i < images.size(); i++) {
            int tileCount = tileCount(i);
            if(count + tileCount > tileIndex){
                int width = (images.get(i).getWidth() - (images.get(i).getWidth() % TILE_WIDTH)) / TILE_WIDTH;
                int tileIndexInImage = tileIndex - count;
                int x = tileIndexInImage % width;
                int y = (tileIndexInImage - x) / width;
                return y * TILE_HEIGHT;
            }
            count += tileCount;
        }
        return -1;
    }

    public int getImageIndexFromTileIndex(int tileIndex) {
        int count = 0;
        for (int i = 0; i < images.size(); i++) {
            int tileCount = tileCount(i);
            if(count + tileCount > tileIndex){
                return i;
            }
            count += tileCount;
        }
        return -1;
    }

    public Set<Integer> tilePalette(int tileIndex) {
        BufferedImage image = images.get(getImageIndexFromTileIndex(tileIndex));
        int topLeftX = getXFromTileIndex(tileIndex);
        int topLeftY = getYFromTileIndex(tileIndex);
//        System.out.println("Checking "+topLeftX+", "+topLeftY);
        Set<Integer> colorsSet = new HashSet<Integer>();
        for (int x = 0; x < TILE_WIDTH; x++) {
            for (int y = 0; y < TILE_HEIGHT; y++) {
                colorsSet.add(image.getRGB(topLeftX+x,topLeftY+y));
//                System.out.println("Palette["+tileIndex+"]="+colorsSet.stream().map(i->""+i).reduce((a,b)->a+","+b).orElse("n/a"));
            }
        }
//        System.out.println("Palette["+tileIndex+"]="+colorsSet.stream().map(i->""+i).reduce((a,b)->a+","+b).orElse("n/a"));
        return colorsSet;
    }

    public List<BufferedImage> getImages() {
        return images;
    }
}
