package com.syntheticentropy.ggpipe;

import com.syntheticentropy.ggpipe.task.PaletteTask;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class Main {

    public static boolean testMode = false;
    private static List<TaskLink> taskLinks = Arrays.asList(
            new TaskLink("palette", PaletteTask::new)
    );

    public static void main(String[] args) {
        Options.init(args);
	    if(Options.get().hasInvalidArguments()){
            abort("Unable to parse arguments.");
	        return;
        }
        Options.ArgumentGroup arguments = Options.get().getArguments();

	    //Route to a task
        if(arguments.hasFlag("-h") || arguments.hasFlag("--help")){
            Help.printHelp();
            exit(0);
            return;
        }

        ColorModel.init(arguments.getArgumentValue("--color-model").orElse("colormodel.txt"));

        //If -o is provided, output to that file
        Optional<String> outputImageFile = Optional.ofNullable(Options.get().getOutputImageFile());
        PrintStream imageOut = outputImageFile.map(Main::createPrintStream).orElse(System.out);
        //Inputs are always provided
        PipeCanvas pipeCanvas = new PipeCanvas(Options.get().getInputFiles());

        //Tasks:
        //Palette - find palettes from all of the input images
        //Reduce Palette - convert all palettes in the inputs to a single palette
        //Tileset - reduce the input images into a tileset (best to apply to reduced palette input)
        String task = arguments.getArgumentValue("-t").get();
        Optional<TaskLink> link = taskLinks.stream().filter(l->l.getFlag().equalsIgnoreCase(task)).findFirst();
        if(link.isPresent()){
            link.get().getHandlerFactory().get().work(arguments, pipeCanvas, imageOut);
            return;
        }else{
            abort("Unknown task "+task);
            return;
        }
    }

    public static PrintStream createPrintStream(String filename) {
        try{
            return new PrintStream(filename);
        }catch(FileNotFoundException e){
            abort("Cannot open file " + filename);
            return System.out;
        }
    }

    private static class TaskLink {
        private String flag;
        private Supplier<ITaskHandler> handlerFactory;
        public TaskLink(String flag, Supplier<ITaskHandler> handlerFactory) {
            this.flag = flag;
            this.handlerFactory = handlerFactory;
        }

        public String getFlag() {
            return flag;
        }

        public Supplier<ITaskHandler> getHandlerFactory() {
            return handlerFactory;
        }
    }

    public static void abort(String message) {
        System.out.println(message);
        System.out.println("Aborted");
        exit(-1);
    }
    public static void exit(int code) {
        if(!testMode){
            System.exit(code);
        }else{
            System.out.println(code);
        }
    }
}
