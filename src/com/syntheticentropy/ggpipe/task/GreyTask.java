package com.syntheticentropy.ggpipe.task;

import com.syntheticentropy.ggpipe.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class GreyTask implements ITaskHandler {

    @Override
    public void work(Options.ArgumentGroup args, PipeCanvas imageIn, PrintStream imageOut) {
        if(imageIn.getImages().size() != 1){
            Main.abort("Must have exactly 1 input image");
            return;
        }

        String paletteString = args.getArgumentValue("-palette").orElse("3F,3F,3F,3F,3F,3F,3F,3F,3F,3F,3F,3F,3F");
        Function<String, Optional<Byte>> parseByteString = pc->{
            try{
                return Optional.of(Byte.parseByte(pc, 16));
            }catch(Exception e){
                return Optional.empty();
            }
        };

        List<Optional<Byte>> fullPaletteBytes = Arrays.asList(paletteString.split(","))
                .stream().map(parseByteString).collect(Collectors.toList());

        if(!fullPaletteBytes.stream().allMatch(Optional::isPresent)){
            Main.abort("Couldn't parse all palette colors");
            return;
        }

        List<Byte> fullPalette = fullPaletteBytes.stream().map(Optional::get).collect(Collectors.toList());
        BufferedImage inputImage = imageIn.getImages().get(0);
        BufferedImage outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), TYPE_INT_RGB);
        //Loop through each tile
        //Detect the tile's palette, map it to 0-3
        //Rewrite the tile through the map
        BiFunction<Set<Byte>, Integer, Boolean> isPaletteMatch =
                (p, i) -> {
                    List<Byte> subset = fullPalette.subList(i*3+1,i*3+4);
                    return p.stream().allMatch(subset::contains);
                };
        int totalTiles = imageIn.tileCount(0);
        for (int tileIndex = 0; tileIndex < totalTiles; tileIndex++) {
            int topLeftX = imageIn.getXFromTileIndex(tileIndex);
            int topLeftY = imageIn.getYFromTileIndex(tileIndex);

            Set<Byte> tileColors = imageIn.tilePalette(tileIndex); //this may be a subset of a palette
            tileColors.remove(fullPalette.get(0));
            //detect the palette 0-3
            int usePalette = -1;
            for (int i = 0; i < 4; i++) {
                if(isPaletteMatch.apply(tileColors,i)){
                    usePalette = i;
                    break;
                }
            }
            if(usePalette == -1){
                Main.abort("Cannot match a palette with tile "+tileIndex+" at "+topLeftX+","+topLeftY);
                return;
            }

            for (int xOff = 0; xOff < PipeCanvas.TILE_WIDTH; xOff++) {
                for (int yOff = 0; yOff < PipeCanvas.TILE_HEIGHT; yOff++) {
                    int x = topLeftX + xOff;
                    int y = topLeftY + yOff;
                    Byte originalColor = ColorModel.get().getNearestNesColorFromRgb(inputImage.getRGB(x,y));

                    int mapColor = -1;
                    //find pixel color index
                    if(originalColor == fullPalette.get(0)) {
                        mapColor = 0;
                    }else if(originalColor == fullPalette.get(1 + (3*usePalette))){
                        mapColor = 1;
                    }else if(originalColor == fullPalette.get(2 + (3*usePalette))){
                        mapColor = 2;
                    }else if(originalColor == fullPalette.get(3 + (3*usePalette))){
                        mapColor = 3;
                    }

                    if(mapColor < 0 || mapColor > 3){
                        Main.abort("Unable to map color in palette "+usePalette);
                        return;
                    }

                    int newColor = mapColor;

                    outputImage.setRGB(x,y, ColorModel.get().getEntries().get(newColor).getRGB());
                }
            }
        }
        try{
            ImageIO.write(outputImage,"PNG",imageOut);
        }catch(Exception e){
            Main.abort("Unable to output the image "+e.getMessage());
            return;
        }
    }
}
