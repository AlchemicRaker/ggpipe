package com.syntheticentropy.ggpipe;

import java.io.PrintStream;

public interface ITaskHandler {
    public void work(Options.ArgumentGroup args, PipeCanvas imageIn, PrintStream imageOut);
}
