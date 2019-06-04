package com.syntheticentropy.ggpipe.task;

import com.syntheticentropy.ggpipe.ColorModel;
import com.syntheticentropy.ggpipe.ITaskHandler;
import com.syntheticentropy.ggpipe.Options;
import com.syntheticentropy.ggpipe.PipeCanvas;

import java.awt.image.BufferedImage;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class BinaryTask implements ITaskHandler {

    @Override
    public void work(Options.ArgumentGroup args, PipeCanvas imageIn, PrintStream imageOut) {
        //Expects reduced images
        //Converts them into binary data 1 tile at a time, all into one contiguous result

        //getNearestIndexColorFromRgb
        int totalTiles = imageIn.tileTotal();
        for (int tileIndex = 0; tileIndex < totalTiles; tileIndex++) {
            int imageIndex = imageIn.getImageIndexFromTileIndex(tileIndex);
            BufferedImage image = imageIn.getImages().get(imageIndex);
            int topLeftX = imageIn.getXFromTileIndex(tileIndex);
            int topLeftY = imageIn.getYFromTileIndex(tileIndex);

            byte[] lowBytes = new byte[8];
            byte[] highBytes = new byte[8];
            for (int yOff = 0; yOff < PipeCanvas.TILE_HEIGHT; yOff++) {
                int y = topLeftY + yOff;
                byte lowByte = 0;
                byte highByte = 0;
                for (int xOff = 0; xOff < PipeCanvas.TILE_WIDTH; xOff++) {
                    int x = topLeftX + xOff;

                    //Identify the color (2-bit result)
                    byte color = ColorModel.get().getNearestIndexColorFromRgb(image.getRGB(x,y));
                    byte lowBit = (color & 1)>0?(byte)1:(byte)0;
                    byte highBit = (color & 2)>0?(byte)1:(byte)0;
                    lowByte |= lowBit << (7-xOff);
                    highByte |= highBit << (7-xOff);
                }
                lowBytes[yOff] = lowByte;
                highBytes[yOff] = highByte;
            }
            //output 8 low bytes and 8 high bytes
            try{
                imageOut.write(lowBytes);
                imageOut.write(highBytes);
//            String encodeL = "";
//            String encodeH = "";
//                encodeL = new String(lowBytes, "UTF-8");
//                encodeH = new String(highBytes, "UTF-8");
            }catch(Exception e){

            }
//            imageOut.print(encodeL);
//            imageOut.print(encodeH);
        }
    }
}
