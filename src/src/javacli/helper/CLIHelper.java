package javacli.helper;


import javacli.annotations.Argument;
import javacli.annotations.Command;
import javacli.annotations.Option;
import javacli.annotations.Version;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;


public class CLIHelper {

    public static void cliAssert(boolean assertion, String failureMessage, String... extraArgs) throws Exception {
        if (!assertion) {
            String err = "ERROR: javacli Assertion Failed; " + failureMessage + ((extraArgs.length > 0) ? " -\n\t" + String.join("\n\t", extraArgs) : "");

            System.out.println(err);
            throw new Exception("Assertion Failed: " + failureMessage);
        }
    }


    // ====================================================================================================
    // private String generateHelp
    //
    // Generates a help message to print
    //
    // Arguments--
    //
    // None
    //
    // Returns--
    //
    // help:    the built help message
    //
    public String generateHelp(Class<?> optionsDefinitionClass, String name, String version) {
        StringBuilder help = new StringBuilder();

        // Add the app name and version if able
        help.append(name)
                .append((!version.equals("")) ? ", version " + version + "\n\n" : "\n\n");

        // Add the usage statement
        help.append("usage: ")
                .append(name)
                .append(" [OPTIONS] ");

        for (Field f : optionsDefinitionClass.getFields()) {
            try {
                Argument argument = f.getAnnotation(Argument.class);

                help.append(argument.name().toUpperCase())
                        .append(" ");
            } catch (NullPointerException ignored) {}
        }

        // Add a newline character
        help.append("\n\n");

        // Go through all the fields in the options class
        for (Field f : optionsDefinitionClass.getFields()) {
            // Get the annotation for each field
            Option option = f.getAnnotation(Option.class);

            // Append the help information for that option
            if (option != null) {
                help.append("\t")
                        .append((option.abbreviation() != Character.MIN_VALUE) ? option.abbreviation() + ", " + option.name() : option.name())
                        .append(" ")
                        .append((!option.isFlag()) ? ((option.type() != String.class) ? option.type().getSimpleName().toUpperCase() : "TEXT") : "")
                        .append((!option.defaultValue().equals("")) ? ((option.showDefault()) ? " (" + option.defaultValue() + ")" : "") : "")
                        .append((!option.help().equals("")) ? "\t: " + option.help() : "")
                        .append("\n");
            }

            // Get and append the version option if one exists
            Version v = f.getAnnotation(Version.class);

            if (v != null) {
                help.append("\t")
                        .append((v.abbreviation() != Character.MIN_VALUE) ? v.abbreviation() + ", version" : "version")
                        .append("\t: Print the version and exit\n");
            }
        }

        // Add the help option to the end
        help.append("\thelp\t: Print this message and exit");

        // Go through all the sub commands
        StringBuilder subCommands = new StringBuilder("");
        for (Field f : optionsDefinitionClass.getFields()) {
            // Get the command annotation
            Command command = f.getAnnotation(Command.class);

            if (command != null) {
                subCommands.append("\n\t")
                        .append(command.name())
                        .append(" [OPTIONS]\t")
                        .append((!command.help().equals("")) ? ": " + command.help() : "");
            }
        }

        // Add sub commands
        if (!subCommands.toString().equals("")) help.append("\n\n").append(subCommands);

        // Return the finished help statement
        return help.toString();
    }
    // end: private String generateHelp


    // ====================================================================================================
    // private void changeAnnotationValue
    //
    // Changes the value of an annotation parameter based on a parameter key
    //
    // Arguments--
    //
    // annotation:  the annotation to change
    //
    // key:         the name of the parameter to change
    //
    // newValue:    the new value to insert
    //
    // Returns--
    //
    // None
    //
    @SuppressWarnings("unchecked")
    public void changeAnnotationValue(Annotation annotation, String key, Object newValue) {
        try {
            disableAccessWarnings();
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
            Field f = invocationHandler.getClass().getDeclaredField("memberValues");
            f.setAccessible(true); // MARK: This does not, and will never, work in JDK 9+
            Map<String, Object> memberValues = (Map<String, Object>) f.get(invocationHandler);
            memberValues.put(key, newValue);
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    // end: private void changeAnnotationValue


    // ====================================================================================================
    // private void disableAccessWarnings
    //
    // HACK: Disables illegal access warning when changing annotation values, because I know what I'm doing
    //
    // Arguments--
    //
    // None
    //
    // Returns--
    //
    // None
    //
    @SuppressWarnings("unchecked")
    private void disableAccessWarnings() {
        try {
            Class unsafeClass = Class.forName("sun.misc.Unsafe");
            Field field = unsafeClass.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Object unsafe = field.get(null);

            Method putObjectVolatile = unsafeClass.getDeclaredMethod("putObjectVolatile", Object.class, long.class, Object.class);
            Method staticFieldOffset = unsafeClass.getDeclaredMethod("staticFieldOffset", Field.class);

            Class loggerClass = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field loggerField = loggerClass.getDeclaredField("logger");
            Long offset = (Long) staticFieldOffset.invoke(unsafe, loggerField);
            putObjectVolatile.invoke(unsafe, loggerClass, offset, null);
        } catch (Exception ignored) {
        }
    }
    // end: private void disableAccessWarnings

}
