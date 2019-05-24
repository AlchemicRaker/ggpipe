package com.syntheticentropy.ggpipe;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class MainTest {

    @Test
    public void showsHelp() {
        List<String> output = callMain("-h");
        Assert.assertTrue("Should contain usage instructions",
                output.stream().map(String::toLowerCase).anyMatch(s->s.contains("usage")));
    }

    public List<String> callMain(String ... args) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream ops = System.out;
        System.setOut(ps);
        try {
            Main.testMode = true;
            Class<?> mainClass = Class.forName("com.syntheticentropy.ggpipe.Main");
            Method mainMethod = mainClass.getMethod("main",String[].class);
            mainMethod.invoke(null, (Object) args);
        }catch(Exception e){
            System.out.flush();
            System.setOut(ops);
            Assert.fail(e.toString());
        }
        System.out.flush();
        System.setOut(ops);
        return Arrays.asList(baos.toString().split("\n"));
    }

}