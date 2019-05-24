package com.syntheticentropy.ggpipe.task;

import com.syntheticentropy.ggpipe.ITaskHandler;
import com.syntheticentropy.ggpipe.Options;
import com.syntheticentropy.ggpipe.PipeCanvas;

import java.io.PrintStream;

public class PaletteTask implements ITaskHandler {
    @Override
    public PipeCanvas transform(PipeCanvas inputCanvas) {
        return inputCanvas;
    }

    @Override
    public void exportImage(PipeCanvas finalCanvas, PrintStream imageOut) {
        imageOut.println("IMAGE OUTPUT");
    }

    @Override
    public void exportPalette(PipeCanvas finalCanvas, PrintStream paletteOut) {
        paletteOut.println("PALETTE OUTPUT");
    }
}
