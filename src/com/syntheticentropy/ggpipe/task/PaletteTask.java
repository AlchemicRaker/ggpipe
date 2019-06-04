package com.syntheticentropy.ggpipe.task;

import com.syntheticentropy.ggpipe.ITaskHandler;
import com.syntheticentropy.ggpipe.Main;
import com.syntheticentropy.ggpipe.Options;
import com.syntheticentropy.ggpipe.PipeCanvas;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PaletteTask implements ITaskHandler {
    @Override
    public void work(Options.ArgumentGroup args, PipeCanvas imageIn, PrintStream imageOut) {
//        imageOut.println("PALETTE OUTPUT");
        //NES palette: bg color + 4x 3-color palettes
        Integer paletteBackground = -16777216; //black, or provided

        //no palettes yet
        List<Set<Integer>> palettes = new ArrayList<>();

        //Loop through every tile, try to merge it into a palette or add a new palette
        int totalTiles = imageIn.tileTotal();
        for (int tileIndex = 0; tileIndex < totalTiles; tileIndex++) {
            //find the palette of the current tile, and see if it fits into the total palette
            try{
                Set<Integer> tilePalette = imageIn.tilePalette(tileIndex);
                tilePalette.remove(paletteBackground);
                if(tilePalette.size() > 3) {
                    Main.abort("Too many colors within a single tile.");
                }
                int bestDifference = -1;
                int bestPalette = -1;
                //find the closest palette index
                for (int paletteIndex = 0; paletteIndex < palettes.size(); paletteIndex++) {
                    Set<Integer> tmpPalette = palettes.get(paletteIndex);
                    Set<Integer> excessPalette = tilePalette.stream().filter(v->!tmpPalette.contains(v)).collect(Collectors.toSet());
                    int difference = excessPalette.size();
                    if(difference + tmpPalette.size() > 3) {
                        //too many colors, cannot fit in this palette
                        continue;
                    }
                    if(bestPalette == -1 || difference < bestDifference) {
                        bestPalette = paletteIndex;
                        bestDifference = difference;
                    }
                }
                if(bestDifference == -1 && palettes.size() >= 4) {
                    //couldn't fit it into an existing palette & no palettes left
                    Main.abort("Too many palettes detected");
                    return;
                } else if(bestDifference == -1 || bestPalette == -1) {
                    //couldn't fit it into an existing palette, insert new palette
                    palettes.add(tilePalette);
//                    System.out.println("Inserting a new palette");
                } else if(bestDifference >= 1) {
                    //found a palette to merge into, add all of this palette into the found palette
                    tilePalette.forEach(palettes.get(bestPalette)::add);
//                    System.out.println("Merging into an existing palette");
                } else {
                    //found an existing palette to fit within
//                    System.out.println("Found an existing palette");
                }
            }catch(Exception e){
//                System.out.println("Wot " + tileIndex);
                Main.abort("Problem encountered "+e.getMessage());
            }
        }
//        System.out.println("Palette["+tileIndex+"]="+colorsSet.stream().map(i->""+i).reduce((a,b)->a+","+b).orElse("n/a"));
        //Pad the palettes with bg
        while(palettes.size()<4){
            palettes.add(new HashSet<Integer>());
        }
        List<List<Integer>> paletteLists = new ArrayList<>();
        palettes.forEach(p->paletteLists.add(new ArrayList<>(p)));
        while(paletteLists.size()<4) paletteLists.add(new ArrayList<>());
        paletteLists.forEach(pl->{
            while(pl.size()<3)pl.add(paletteBackground);
        });
        List<Integer> output = Stream.concat(Stream.of(paletteBackground),paletteLists.stream().flatMap(List::stream)).collect(Collectors.toList());
        imageOut.println(output.stream().map(i->""+i).reduce((a,b)->a+","+b).orElse("NULL"));
    }
}
