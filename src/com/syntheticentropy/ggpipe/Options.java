package com.syntheticentropy.ggpipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

// Singleton to decode and track the command line arguments
// Failure to parse the arguments will abort the program
public class Options {

    private static Options singleton;
    public static Options get() {
        return singleton;
    }

    private ArgumentGroup arguments;
    public ArgumentGroup getArguments() {
        return arguments;
    }

    private List<String> inputFiles;
    private String outputImageFile;
    private String outputPaletteFile;
    private String outputFormat;
    private String task;

    public List<String> getInputFiles() {
        return inputFiles;
    }

    public String getOutputImageFile() {
        return outputImageFile;
    }

    public String getOutputPaletteFile() {
        return outputPaletteFile;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public String getTask() {
        return task;
    }

    private Options(ArgumentGroup args, boolean invalid,
                    List<String> inputFiles,
                    String outputImageFile, String task) {
        arguments = args;
        invalidArguments = invalid;
        this.inputFiles = inputFiles;
        this.outputImageFile = outputImageFile;
        this.task = task;
    }
    public static boolean init(String args[]) {
        if(singleton != null) return false;
        //parse it and create Options
        ArgumentGroup group = new ArgumentGroup(args);
        boolean invalid = false;

        if(group.hasFlag("-d")){
            for (Argument argument:group.getArguments()) {
                System.out.println(argument.getFlag()+": "+argument.getValues().stream().reduce((a,b)->a+", "+b).orElse("N/A"));
            }
        }

        if(group.hasFlag("-h") || group.hasFlag("--help")) {
            singleton = new Options(group, false, null, null, null);
        }else{
            invalid|= group.assertArgumentHasOneValue("-o");

            invalid|= group.assertArgumentHasOneValue("-t");

            invalid|= group.assertArgumentHasOneValue("-bg");

            invalid|= group.assertArgumentHasOneValue("--color-model");

            invalid|= group.assertArgumentHasAtLeastOneValue("--");

            List<String> inputFiles = group.getArgumentValues("--").orElseGet(ArrayList::new);
            String outputImageFile = group.getArgumentValue("-o").orElseGet(()->null);
            String task = group.getArgumentValue("-t").orElseGet(()->"");

            //failure to parse: return false;
            singleton = new Options(group, invalid, inputFiles, outputImageFile, task);
        }

        return invalid;
    }

    private boolean invalidArguments = false;
    public boolean hasInvalidArguments() {
        return invalidArguments;
    }

    public static class ArgumentGroup {
        private List<Argument> arguments;
        public ArgumentGroup(String args[]) {
            List<Argument> result = new ArrayList<>();
            Argument selectedGroup = new Argument("--", new ArrayList<>());
            result.add(selectedGroup);
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                if(arg.length() == 0) continue;
                if(arg.startsWith("--")){
                    String flag = null;
                    String value = null;
                    // add this as value for another flag
                    if(arg.equalsIgnoreCase("--palette")){
                        flag = "-t";
                        value = "palette";
                    }
                    if(arg.equalsIgnoreCase("--map")){
                        flag = "-t";
                        value = "map";
                    }
                    if(flag != null){
                        Function<String,Predicate<Argument>> matchFlag = f -> ag->ag.getFlag().equalsIgnoreCase(f);
                        Optional<Argument> argument = result.stream().filter(matchFlag.apply(flag)).findFirst();
                        if(!argument.isPresent()){
                            argument = Optional.of(new Argument(flag, new ArrayList<>()));
                        }
                        argument.get().values.add(value);
                        result.add(argument.get());
                        selectedGroup = result.get(0);
                        continue;
                    }
                }
                if(arg.startsWith("-")){
                    Optional<Argument> tempArgument = result.stream().filter(ag->ag.getFlag().equalsIgnoreCase(arg)).findFirst();
                    if(!tempArgument.isPresent()){
                        tempArgument = Optional.of(new Argument(arg, new ArrayList<>()));
                        selectedGroup = tempArgument.get();
                        result.add(selectedGroup);
                    }else{
                        selectedGroup = tempArgument.get();
                    }
                }else{
                    selectedGroup.getValues().add(arg);
                }
            }
            arguments = result;
        }
        public ArgumentGroup(List<Argument> arguments) {
            this.arguments = arguments;
        }
        public List<Argument> getArguments() {
            return arguments;
        }
        public Optional<Argument> getArgument(String flag) {
            return arguments.stream().filter(a->a.flag.equalsIgnoreCase(flag)).findFirst();
        }
        public Optional<List<String>> getArgumentValues(String flag) {
            return getArgument(flag).map(Argument::getValues);
        }
        public Optional<String> getArgumentValue(String flag) {
            return getArgumentValues(flag).map(l->l.get(0));
        }
        public boolean hasFlag(String flag) {
            return getArgument(flag).isPresent();
        }
        private boolean assertArgumentExists(String flag) {
            if(!getArgument(flag).isPresent()){
                return abort("Required argument " + flag + " not found");
            }
            return false;
        }
        private boolean assertArgumentHasNoValues(String flag) {
            Optional<Integer> size = getArgument(flag).map(Argument::getValues).map(List::size);
            if(size.map(s->s!=0).orElse(false)){
                return abort(flag + " requires 0 arguments, " + size.get() + " provided");
            }
            return false;
        }
        private boolean assertArgumentHasOneValue(String flag) {
            Optional<Integer> size = getArgument(flag).map(Argument::getValues).map(List::size);
            if(size.map(s->s!=1).orElse(false)){
                return abort(flag + " requires 1 argument, " + size.get() + " provided");
            }
            return false;
        }
        private boolean assertArgumentHasAtLeastOneValue(String flag) {
            Optional<Integer> size = getArgument(flag).map(Argument::getValues).map(List::size);
            if(size.map(s->s<1).orElse(false)){
                return abort(flag + " requires at least 1 argument, " + size.get() + " provided");
            }
            return false;
        }
        private boolean abort(String message) {
            System.out.println(message);
            return true;
        }
    }

    public static class Argument {
        private String flag;
        private List<String> values;
        public Argument(String flag, List<String> values) {
            this.flag = flag;
            this.values = values;
        }
        public String getFlag(){
            return flag;
        }
        public List<String> getValues(){
            return values;
        }
    }

}
