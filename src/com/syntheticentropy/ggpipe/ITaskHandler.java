package com.syntheticentropy.ggpipe;

import java.io.PrintStream;

public interface ITaskHandler {
    public PipeCanvas transform(PipeCanvas inputCanvas);
    public void exportImage(PipeCanvas finalCanvas, PrintStream imageOut);
    public void exportPalette(PipeCanvas finalCanvas, PrintStream paletteOut);
}
