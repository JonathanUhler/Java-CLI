// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// OptionParser.java
// Java-CLI
//
// Created by Jonathan Uhler on 6/25/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package javacli;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.stream.Collectors;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class OptionParser
//
// Option parser and handler class
//
public class OptionParser {

    // App information
    Class<?> optionsDefinitionClass; // The class containing the option/argument definitions
    String version = "";
    String name;

    // Option information
    ArrayList<Option> options = new ArrayList<>();
    ArrayList<Argument> arguments = new ArrayList<>();
    ArrayList<Version> versions = new ArrayList<>();

    ArrayList<String> optionNames = new ArrayList<>();
    ArrayList<Character> optionAbbreviations = new ArrayList<>();
    ArrayList<String> argumentNames = new ArrayList<>();

    ArrayList<String> optionFieldNames = new ArrayList<>();
    ArrayList<Class<?>> optionFieldTypes = new ArrayList<>();
    ArrayList<String> argumentFieldNames = new ArrayList<>();
    ArrayList<Class<?>> argumentFieldTypes = new ArrayList<>();

    // Option and argument information
    ArrayList<String> inputArgs = new ArrayList<>(); // List of arguments to the program
    HashMap<String, List<String>> optionArgs = new HashMap<>(); // Map of arguments to options where the option name is the key and the arguments are the values


    // ----------------------------------------------------------------------------------------------------
    // public OptionParser
    //
    // OptionParser constructor
    //
    // Arguments--
    //
    // optionsDefinitionClass:  the class with the option, arguments, and version annotations
    //
    public OptionParser(Class<?> optionsDefinitionClass) {
        this.optionsDefinitionClass = optionsDefinitionClass;
        this.name = optionsDefinitionClass.getName();
    }
    // end: public OptionParser


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
    private String generateHelp() {
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

        // Return the finished help statement
        return help.toString();
    }
    // end: private String generateHelp


    // ====================================================================================================
    // private void init
    //
    // Initializes some information about the options and arguments
    //
    // Arguments--
    //
    // None
    //
    // Returns--
    //
    // None
    //
    private void init() {
        for (Field f : optionsDefinitionClass.getFields()) {
            Option o = f.getAnnotation(Option.class);
            Argument a = f.getAnnotation(Argument.class);
            Version v = f.getAnnotation(Version.class);

            if (o != null) {
                options.add(o);
                optionNames.add(o.name());
                optionAbbreviations.add(o.abbreviation());
                optionFieldNames.add(f.getName());
                optionFieldTypes.add(f.getType());
            }
            if (a != null) {
                arguments.add(a);
                argumentNames.add(a.name());
                argumentFieldNames.add(f.getName());
                argumentFieldTypes.add(f.getType());
            }
            if (v != null) { versions.add(v); version = v.version(); }
        }
    }
    // end: private void init


    // ====================================================================================================
    // private void castAndAdd
    //
    // Try casting data to a certain type and add it to the correct field
    //
    // Arguments--
    //
    // f:           the field to add it to
    //
    // castType:    the type to cast to
    //
    // data:        the data to cast
    //
    // Returns--
    //
    // None
    //
    private void castAndAdd(Field f, Class<?> castType, Object data, boolean dataIsCollection) throws IllegalAccessException {
        // Check for casting for multiple types
        if (!dataIsCollection) {
            if (Integer.class.equals(castType) || int.class.equals(castType)) { f.set(optionsDefinitionClass, (int) Integer.parseInt((String) data)); }
            else if (Boolean.class.equals(castType) || boolean.class.equals(castType)) { f.set(optionsDefinitionClass, (boolean) Boolean.parseBoolean((String) data)); }
            else if (Byte.class.equals(castType) || byte.class.equals(castType)) { f.set(optionsDefinitionClass, (byte) Byte.parseByte((String) data)); }
            else if (Short.class.equals(castType) || short.class.equals(castType)) { f.set(optionsDefinitionClass, (short) Short.parseShort((String) data)); }
            else if (Long.class.equals(castType) || long.class.equals(castType)) { f.set(optionsDefinitionClass, (long) Long.parseLong((String) data)); }
            else if (Float.class.equals(castType) || float.class.equals(castType)) { f.set(optionsDefinitionClass, (float) Float.parseFloat((String) data)); }
            else if (Double.class.equals(castType) || double.class.equals(castType)) { f.set(optionsDefinitionClass, (double) Double.parseDouble((String) data)); }
            else { f.set(optionsDefinitionClass, (String) data); }
        }
        // Check for casting for multiple types
        else {
            @SuppressWarnings("unchecked")
            List<String> dataList = (List<String>) data;
            if (Integer.class.equals(castType)) { List<Integer> ls = dataList.stream().map(Integer::parseInt).collect(Collectors.toList()); f.set(optionsDefinitionClass, ls); }
            else if (Boolean.class.equals(castType)) { List<Boolean> ls = dataList.stream().map(Boolean::parseBoolean).collect(Collectors.toList()); f.set(optionsDefinitionClass, ls); }
            else if (Byte.class.equals(castType)) { List<Byte> ls = dataList.stream().map(Byte::parseByte).collect(Collectors.toList()); f.set(optionsDefinitionClass, ls); }
            else if (Short.class.equals(castType)) { List<Short> ls = dataList.stream().map(Short::parseShort).collect(Collectors.toList()); f.set(optionsDefinitionClass, ls); }
            else if (Long.class.equals(castType)) { List<Long> ls = dataList.stream().map(Long::parseLong).collect(Collectors.toList()); f.set(optionsDefinitionClass, ls); }
            else if (Float.class.equals(castType)) { List<Float> ls = dataList.stream().map(Float::parseFloat).collect(Collectors.toList()); f.set(optionsDefinitionClass, ls); }
            else if (Double.class.equals(castType)) { List<Double> ls = dataList.stream().map(Double::parseDouble).collect(Collectors.toList()); f.set(optionsDefinitionClass, ls); }
            else { f.set(optionsDefinitionClass, dataList); }
        }
    }
    // end: private void castAndAdd


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
    private void changeAnnotationValue(Annotation annotation, String key, Object newValue) {
        try {
            disableAccessWarnings();
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
            Field f = invocationHandler.getClass().getDeclaredField("memberValues");
            f.setAccessible(true);
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


    // ====================================================================================================
    // public int getCount
    //
    // Gets the count parameter from an annotation
    //
    // Arguments--
    //
    // key:     the name of the variable/annotation name to get
    //
    // Returns--
    //
    // o.count: the count parameter
    public int getCount(String key) {
        for (Field f : optionsDefinitionClass.getFields()) {
            Option o = f.getAnnotation(Option.class);

            if (o != null && o.name().equals(key)) { return o._countValue(); }
        }
        return 0;
    }
    // end: public int getCount


    // ====================================================================================================
    // public void parse
    //
    // Begins the parse process
    //
    // Arguments--
    //
    // args:    the list of command line args from the main method
    //
    // Returns--
    //
    // None
    //
    public void parse(String[] args) {
        // Set up information
        init();

        // Check for correct option definitions
        try {
            parseOptionDefinitions();
        } catch (OptionException e) { // Catch and print any errors and exit
            System.exit(1);
        }

        // Check for special options
        for (String arg : args) {
            // Check for --help
            if (arg.equals("--help")) {
                System.out.println(generateHelp());
                System.exit(0);
            }

            // Check for --version
            if ((arg.equals("--version") || (arg.startsWith("-") && arg.contains("" + versions.get(0).abbreviation()) && !version.equals("")))) {
                System.out.println(name + ", version " + version);
                System.exit(0);
            }
        }

        // Information from command line parse
        HashMap<HashMap<String, List<String>>, ArrayList<String>> parsedArgs = null;
        // Try to parse the command line arguments
        try {
            parsedArgs = parseAndExitUponError(Arrays.asList(args));
        } catch (OptionException e) { // Catch and print any errors and exit
            System.out.println(generateHelp());
            System.exit(2);
        }

        // Set parse information
        @SuppressWarnings("unchecked")
        ArrayList<String> inputArgs = (ArrayList<String>) parsedArgs.values().toArray()[0];
        @SuppressWarnings("unchecked")
        HashMap<String, List<String>> optionArgs = (HashMap<String, List<String>>) parsedArgs.keySet().toArray()[0];
        Class<?> castType = null;
        List<String> data = null;

        // Try setting variables in the option definition class
        try {
            int argCount = 0;
            for (Field f : optionsDefinitionClass.getFields()) {
                // The field is an option
                if (f.getAnnotation(Option.class) != null) {
                    Option o = f.getAnnotation(Option.class);
                    // The option was specified by the user
                    if (optionArgs.containsKey(f.getName())) {
                        castType = options.get(optionNames.indexOf(f.getName())).type();
                        data = optionArgs.get(f.getName());

                        // Cast and add single argument options
                        if (optionArgs.get(f.getName()).size() == 1 && (o.nargs() == 1 || o.nargs() == 0)) { castAndAdd(f, castType, data.get(0), false); }
                        // Cast and add multiple argument options
                        else { castAndAdd(f, castType, data, true); }
                    }
                    // The option wasn't specified
                    else {
                        // If the option is a flag, set it to false
                        if (f.getAnnotation(Option.class).nargs() == 0) {
                            f.set(optionsDefinitionClass, false);
                        }
                        // If the option has arguments and a default value, set it to the default value
                        else if (f.getAnnotation(Option.class).nargs() == 1 && !f.getAnnotation(Option.class).defaultValue().equals("")) {
                            castAndAdd(f, f.getAnnotation(Option.class).type(), f.getAnnotation(Option.class).defaultValue(), false);
                        }
                    }
                }

                // The field is an argument
                if (f.getAnnotation(Argument.class) != null) {
                    Class<?> argCastType = arguments.get(argumentNames.indexOf(f.getName())).type();
                    // Cast the value and set the variable
                    castAndAdd(f, argCastType, inputArgs.get(argCount), false);
                    argCount++;
                }
            }
        } catch (Exception e) {
            try {
                throw new OptionException(optionsDefinitionClass, Exceptions.UncastableOptionTypeException, ((data != null) ? ((data.size() == 1) ? String.class : List.class) : "") + " -> " + castType);
            } catch (OptionException e2) {
                System.exit(1);
            }
        }
    }
    // end: public void parse


    // ====================================================================================================
    // private void parseOptionDefinitions
    //
    // Parses option definitions for syntax mistakes
    //
    // Arguments--
    //
    // None
    //
    // Return--
    //
    // None
    //
    private void parseOptionDefinitions() throws OptionException {
        // Parse option definitions
        for (int i = 0; i < options.size(); i++) {
            Option o = options.get(i);
            if (!optionFieldNames.get(i).equals(o.name())) { throw new OptionException(optionsDefinitionClass, Exceptions.VariableNameNotMatchingOptionException, o.name() + ", " + optionFieldNames.get(i)); }
            if (!optionFieldTypes.get(i).equals(o.type()) && options.get(optionFieldTypes.indexOf(optionFieldTypes.get(i))).nargs() <= 1) { throw new OptionException(optionsDefinitionClass, Exceptions.VariableTypeNotMatchingOptionException, o.type() + ", " + optionFieldTypes.get(i)); }
            if (o.isFlag() && o.nargs() > 0) { throw new OptionException(optionsDefinitionClass, Exceptions.FlagWithNonZeroArgsException, o.name()); }
            if (!o.isFlag() && o.nargs() == 0) { throw new OptionException(optionsDefinitionClass, Exceptions.NonFlagWithZeroArgsException, o.name()); }
            if (o.showDefault() && o.defaultValue().equals("")) { throw new OptionException(optionsDefinitionClass, Exceptions.NoDefaultToShowException, o.name()); }
            if (!o.defaultValue().equals("") && o.nargs() > 1) { throw new OptionException(optionsDefinitionClass, Exceptions.DefaultForMultipleArgsException, o.name()); }
            if (!o.defaultValue().equals("") && o.nargs() == 0) { throw new OptionException(optionsDefinitionClass, Exceptions.DefaultForZeroArgsException, o.name()); }
            if (o.doCount() && !o.multiple()) { throw new OptionException(optionsDefinitionClass, Exceptions.CountingNonMultipleException, o.name()); }
            if (Collections.frequency(optionNames, o.name()) > 1) { throw new OptionException(optionsDefinitionClass, Exceptions.DuplicateOptionNamesException, o.name()); }
            if (Collections.frequency(optionAbbreviations, o.abbreviation()) > 1) { throw new OptionException(optionsDefinitionClass, Exceptions.DuplicateOptionAbbreviationsException, o.name()); }
            if (o.nargs() == 0 && o.type() != boolean.class && o.type() != Boolean.class) { throw new OptionException(optionsDefinitionClass, Exceptions.NoArgsOptionNotBooleanException, o.name()); }
        }

        // Parse argument definitions
        for (int i = 0; i < arguments.size(); i++) {
            Argument a = arguments.get(i);
            if (!argumentFieldNames.get(i).equals(a.name())) { throw new OptionException(optionsDefinitionClass, Exceptions.VariableNameNotMatchingOptionException, a.name() + ", " + argumentFieldNames.get(i)); }
            if (!argumentFieldTypes.get(i).equals(a.type())) { throw new OptionException(optionsDefinitionClass, Exceptions.VariableTypeNotMatchingOptionException, a.type() + ", " + argumentFieldTypes.get(i)); }
            if (Collections.frequency(argumentNames, a.name()) > 1) { throw new OptionException(optionsDefinitionClass, Exceptions.DuplicateArgumentNamesException, a.name()); }
        }
    }
    // end: private void parseOptionDefinitions


    // ====================================================================================================
    // private void parseAndExitUponError
    //
    // Parses options and throws errors
    //
    // Arguments--
    //
    // inputList:   the String[] args list from java main as a List<String>
    //
    // Returns--
    //
    // Hashmap of optionArgs and inputArgs
    //
    private HashMap<HashMap<String, List<String>>, ArrayList<String>> parseAndExitUponError(List<String> inputList) throws OptionException {
        // Loop through each string in the list
        int i = 0;
        while (i < inputList.size()) {
            String inputStr = inputList.get(i); // Note the string currently being parsed

            // Check if the string is an argument to the program
            if (!inputStr.startsWith("-") && !inputStr.startsWith("--")) {
                inputArgs.add(inputStr);
                i++;
                continue;
            }

            // Check if there is an "--" signifying all following strings are arguments
            if (inputStr.equals("--")) {
                ArrayList<String> allArgs = new ArrayList<>();

                // Add everything after the -- to the argument list
                for (int a = inputList.indexOf(inputStr) + 1; a < inputList.size(); a++) {
                    allArgs.add(inputList.get(a));
                }

                inputArgs.addAll(allArgs);
                break;
            }

            // Parse long options
            if (inputStr.startsWith("--")) {
                String longOption = inputStr.substring(2); // Note the long option name
                Option longOptionAnnotation;

                if (inputStr.contains("=")) {
                    longOption = longOption.split("=")[0];
                } // Split by = if found

                try {
                    longOptionAnnotation = options.get(optionNames.indexOf(longOption)); // Note the annotation belonging to the long option being parsed
                } catch (IndexOutOfBoundsException e) {
                    throw new OptionException(optionsDefinitionClass, Exceptions.NoSuchOptionException, longOption);
                }

                // Check if the long option exists
                if (optionNames.contains(longOption)) {
                    if (longOptionAnnotation.doCount()) { changeAnnotationValue(longOptionAnnotation, "_countValue", longOptionAnnotation._countValue() + 1); }

                    // Parse no arguments
                    if (longOptionAnnotation.nargs() == 0) {
                        longZero(inputList, inputStr, longOptionAnnotation, longOption);
                        i++;
                        continue;
                    }
                    // Parse only one argument
                    else if (longOptionAnnotation.nargs() == 1) {
                        longOne(inputList, inputStr, longOptionAnnotation, longOption, i);
                        i += (inputStr.contains("=")) ? 1 : 2;
                        continue;
                    }
                    // Parse for more than one argument
                    else if (longOptionAnnotation.nargs() > 1) {
                        longMany(inputList, inputStr, longOptionAnnotation, longOption, i);
                        i += (inputStr.contains("=")) ? longOptionAnnotation.nargs() : longOptionAnnotation.nargs() + 1;
                        continue;
                    }
                    // Parse for variable number of arguments
                    else if (longOptionAnnotation.nargs() == -1) {
                        longVariable(inputList, inputStr, longOptionAnnotation, longOption, i);
                        i += (inputStr.contains("=")) ? 1 : 2;
                        continue;
                    }
                }
                // The long option name wasn't found, throw an error
                else {
                    throw new OptionException(optionsDefinitionClass, Exceptions.NoSuchOptionException, longOption);
                }
            }

            // Parse short options
            if (inputStr.startsWith("-")) {
                // Loop through each character after the first hyphen
                for (char shortOption : inputStr.substring(1).toCharArray()) {
                    Option shortOptionAnnotation;
                    try {
                        // Try getting the annotation for the character
                        shortOptionAnnotation = options.get(optionAbbreviations.indexOf(shortOption));
                    } catch (IndexOutOfBoundsException e) {
                        // Annotation could not be gotten, character is an invalid option
                        throw new OptionException(optionsDefinitionClass, Exceptions.NoSuchOptionException, shortOption + "");
                    }

                    // Check if the option exists
                    if (optionAbbreviations.contains(shortOption)) {
                        // Update count if specified
                        if (shortOptionAnnotation.doCount()) { changeAnnotationValue(shortOptionAnnotation, "_countValue", shortOptionAnnotation._countValue() + 1); }

                        if (shortOptionAnnotation.nargs() == 0) {
                            shortZero(shortOptionAnnotation, shortOption);
                        }
                        // The option has 1 argument
                        else if (shortOptionAnnotation.nargs() == 1) {
                            i = shortOne(inputList, inputStr, shortOptionAnnotation, shortOption, i);
                            break;
                        }
                        // The option has more than 1 argument
                        else if (shortOptionAnnotation.nargs() > 1) {
                            i += shortMany(inputList, inputStr, shortOptionAnnotation, shortOption, i);
                            i += options.get(optionAbbreviations.indexOf(shortOption)).nargs();
                            break;
                        }
                        // Parse for variable number of arguments
                        else if (shortOptionAnnotation.nargs() == -1) {
                            shortVariable(inputList, inputStr, shortOptionAnnotation, shortOption, i);
                            i++;
                            break;
                        }

                    }
                    // The option did not exist
                    else {
                        throw new OptionException(optionsDefinitionClass, Exceptions.NoSuchOptionException, shortOption + "");
                    }
                }
            }

            // Update i each time through the while loop
            i++;
        }

        // Parse arguments (check for correct number)
        if (inputArgs.size() > arguments.size()) {
            throw new OptionException(optionsDefinitionClass, Exceptions.ExtraneousArgumentException, inputArgs.toString());
        }
        else if (inputArgs.size() < arguments.size()) {
            throw new OptionException(optionsDefinitionClass, Exceptions.MissingArgumentException, inputArgs.toString());
        }

        // Return optionArgs and inputArgs as one hashmap
        HashMap<HashMap<String, List<String>>, ArrayList<String>> parseInfo = new HashMap<>();
        parseInfo.put(optionArgs, inputArgs);
        return parseInfo;

    }
    // end: private void parseAndExitUponError


    // ====================================================================================================
    // private void longZero
    //
    // Parse long options with zero arguments
    //
    // Arguments--
    //
    // inputList:               the input list array from the command line
    //
    // inputStr:                the current element of inputList being processed
    //
    // longOptionAnnotation:    the annotation for the option being processed
    //
    // longOption:              the name of the option being processed
    //
    // Returns--
    //
    // None
    //
    private void longZero(List<String> inputList, String inputStr, Option longOptionAnnotation, String longOption) throws OptionException {
        // Make sure the option name is in the right place, meaning there are no extra arguments
        if (inputList.indexOf(inputStr) + 1 != (inputList.size() - arguments.size()) && inputList.size() > inputList.indexOf(inputStr) + 1 && !inputList.get(inputList.indexOf(inputStr) + 1).startsWith("-")) {
            throw new OptionException(optionsDefinitionClass, Exceptions.ExtraneousArgumentException, inputStr);
        }

        // Check if this option already has an entry in the map and save the new argument
        if (optionArgs.containsKey(longOptionAnnotation.name())) {
            if (!longOptionAnnotation.multiple() && optionArgs.get(longOptionAnnotation.name()).size() >= longOptionAnnotation.nargs()) {
                throw new OptionException(optionsDefinitionClass, Exceptions.MultipleNotAllowedException, longOption + "");
            }

            ArrayList<String> optExistingArgs = new ArrayList<>(optionArgs.get(longOptionAnnotation.name()));
            optExistingArgs.add("true");
            optionArgs.put(longOptionAnnotation.name(), optExistingArgs);
        } else { // Create an entry in the map if one was not found
            optionArgs.put(longOptionAnnotation.name(), Collections.singletonList("true"));
        }
    }
    // end: private void longZero


    // ====================================================================================================
    // private void longOne
    //
    // Parse long options with one argument
    //
    // Arguments--
    //
    // inputList:               the input list array from the command line
    //
    // inputStr:                the current element of inputList being processed
    //
    // longOptionAnnotation:    the annotation for the option being processed
    //
    // longOption:              the name of the option being processed
    //
    // i:                       the counter to keep track of inputList
    //
    // Returns--
    //
    // None
    //
    private void longOne(List<String> inputList, String inputStr, Option longOptionAnnotation, String longOption, int i) throws OptionException {
        // Note the name of the argument
        String longOptionArg = (inputList.size() > i + 1) ? inputList.get(i + 1) : "";
        if (inputStr.contains("=")) {
            longOptionArg = inputStr.split("=")[1];
        }

        // Check for the presence of the argument
        if (longOptionArg.length() == 0) {
            throw new OptionException(optionsDefinitionClass, Exceptions.MissingArgumentException, inputStr);
        }

        // Check if this option already has an entry in the map and save the new argument
        if (optionArgs.containsKey(longOption)) {
            if (!longOptionAnnotation.multiple() && optionArgs.get(longOption).size() >= longOptionAnnotation.nargs()) {
                throw new OptionException(optionsDefinitionClass, Exceptions.MultipleNotAllowedException, longOption);
            }

            ArrayList<String> optExistingArgs = new ArrayList<>(optionArgs.get(longOption));
            optExistingArgs.add(longOptionArg);
            optionArgs.put(longOption, optExistingArgs);
        } else { // Create an entry in the map if one was not found
            optionArgs.put(longOption, Collections.singletonList(longOptionArg));
        }
    }
    // end: private void longOne


    // ====================================================================================================
    // private void longMany
    //
    // Parse long options with many arguments
    //
    // Arguments--
    //
    // inputList:               the input list array from the command line
    //
    // inputStr:                the current element of inputList being processed
    //
    // longOptionAnnotation:    the annotation for the option being processed
    //
    // longOption:              the name of the option being processed
    //
    // i:                       the counter to keep track of inputList
    //
    // Returns--
    //
    // None
    //
    private void longMany(List<String> inputList, String inputStr, Option longOptionAnnotation, String longOption, int i) throws OptionException {
        // Loop through each argument
        for (int j = 0; j < longOptionAnnotation.nargs(); j++) {
            // Note the name of the argument
            String longOptionArg = (inputList.size() > i + j + 1) ? inputList.get(i + j + 1) : "";
            // If there was an = and the first of many arguments is currently being parsed, need to split
            if (inputStr.contains("=")) {
                longOptionArg = (inputList.size() > i + j) ? inputList.get(i + j) : "";
                if (j == 0) {
                    longOptionArg = inputStr.split("=")[1];
                }
            }

            // Check for the presence of the argument
            if (longOptionArg.length() == 0) {
                throw new OptionException(optionsDefinitionClass, Exceptions.MissingArgumentException, inputStr);
            }

            // Check if this option already has an entry in the map and save the new argument
            if (optionArgs.containsKey(longOption)) {
                if (!longOptionAnnotation.multiple() && optionArgs.get(longOption).size() >= longOptionAnnotation.nargs()) {
                    throw new OptionException(optionsDefinitionClass, Exceptions.MultipleNotAllowedException, longOption);
                }

                ArrayList<String> optExistingArgs = new ArrayList<>(optionArgs.get(longOption));
                optExistingArgs.add(longOptionArg);
                optionArgs.put(longOption, optExistingArgs);
            } else { // Create an entry in the map if one was not found
                optionArgs.put(longOption, Collections.singletonList(longOptionArg));
            }
        }
    }
    // end: private void longMany


    // ====================================================================================================
    // private void longVariable
    //
    // Parse long options with variable arguments
    //
    // Arguments--
    //
    // inputList:               the input list array from the command line
    //
    // inputStr:                the current element of inputList being processed
    //
    // longOptionAnnotation:    the annotation for the option being processed
    //
    // longOption:              the name of the option being processed
    //
    // i:                       the counter to keep track of inputList
    //
    // Returns--
    //
    // None
    //
    private void longVariable(List<String> inputList, String inputStr, Option longOptionAnnotation, String longOption, int i) throws OptionException {
        // User specified >1 args in either format "--opt=1,2,3" or "--opt 1,2,3"
        if ((inputStr.contains("=") && inputStr.matches("(.)*(,)(.)*")) || (inputList.size() > i + 1 && !inputStr.contains("=") && inputList.get(i + 1).matches("(.)*(,)(.)*"))) {
            String unsplitLongOptionArgs = (inputList.size() > i + 1) ? inputList.get(i + 1) : "";
            if (inputStr.contains("=")) {
                unsplitLongOptionArgs = inputStr.split("=")[1].replaceAll(",\\s+", ",");
            }

            String cleanedLongOptionArgs = unsplitLongOptionArgs.replaceAll(",\\s+", ",");
            String[] longOptionArgs = cleanedLongOptionArgs.split(",");

            // Check if this option already has an entry in the map and save the new argument
            if (optionArgs.containsKey(longOption)) {
                if (!longOptionAnnotation.multiple()) {
                    throw new OptionException(optionsDefinitionClass, Exceptions.MultipleNotAllowedException, longOption);
                }

                ArrayList<String> optExistingArgs = new ArrayList<>(optionArgs.get(longOption));
                optExistingArgs.addAll(Arrays.asList(longOptionArgs));
                optionArgs.put(longOption, optExistingArgs);
            } else { // Create an entry in the map if one was not found
                optionArgs.put(longOption, Arrays.asList(longOptionArgs));
            }
        }

        // User specified 1 arg in either format "--opt=1" or "--opt 1"
        else if ((inputStr.contains("=") && !inputStr.contains(",")) || (inputList.size() > i + 1 && !inputStr.contains("=") && !inputList.get(i + 1).contains(","))) {
            longOne(inputList, inputStr, longOptionAnnotation, longOption, i);
        }
    }
    // end: private void longVariable


    // ====================================================================================================
    // private void shortZero
    //
    // Parse short options with zero arguments
    //
    // Arguments--
    //
    // shortOptionAnnotation:   the annotation for the option being processed
    //
    // shortOption:             the name of the option being processed
    //
    // Returns--
    //
    // None
    //
    private void shortZero(Option shortOptionAnnotation, char shortOption) throws OptionException {
        // Check if this option already has an entry in the map and save the new argument
        if (optionArgs.containsKey(shortOptionAnnotation.name()) && !shortOptionAnnotation.isFlag()) {
            if (!shortOptionAnnotation.multiple() && optionArgs.get(shortOptionAnnotation.name()).size() >= shortOptionAnnotation.nargs()) {
                throw new OptionException(optionsDefinitionClass, Exceptions.MultipleNotAllowedException, shortOption + "");
            }

            ArrayList<String> optExistingArgs = new ArrayList<>(optionArgs.get(shortOptionAnnotation.name()));
            optExistingArgs.add("true");
            optionArgs.put(shortOptionAnnotation.name(), optExistingArgs);
        } else { // Create an entry in the map if one was not found
            optionArgs.put(shortOptionAnnotation.name(), Collections.singletonList("true"));
        }
    }
    // end: private void shortZero


    // ====================================================================================================
    // private int shortOne
    //
    // Parse short options with one argument
    //
    // Arguments--
    //
    // inputList:               the input list array from the command line
    //
    // inputStr:                the current element of inputList being processed
    //
    // shortOptionAnnotation:   the annotation for the option being processed
    //
    // shortOption:             the name of the option being processed
    //
    // i:                       the counter to keep track of inputList
    //
    // Returns--
    //
    // i:                       the updated counter to keep track of inputList
    //
    private int shortOne(List<String> inputList, String inputStr, Option shortOptionAnnotation, char shortOption, int i) throws OptionException {
        String shortOptionArg = inputStr.substring(inputStr.indexOf(shortOption) + 1);

        // Check if the argument has been specified as part of the initial string (ex: -ofile)
        if (shortOptionArg.length() == 0) {
            shortOptionArg = (inputList.size() > i + 1) ? inputList.get(i + 1) : "";
            i++;
            // Check if the argument has been specified after the initial string (ex: -o file)
            if (shortOptionArg.length() == 0) {
                // If neither, missing argument
                throw new OptionException(optionsDefinitionClass, Exceptions.MissingArgumentException, shortOption + "");
            }
        }

        // Check if this option already has an entry in the map and save the new argument
        if (optionArgs.containsKey(shortOptionAnnotation.name())) {
            if (!shortOptionAnnotation.multiple() && optionArgs.get(shortOptionAnnotation.name()).size() >= shortOptionAnnotation.nargs()) {
                throw new OptionException(optionsDefinitionClass, Exceptions.MultipleNotAllowedException, shortOption + "");
            }

            ArrayList<String> optExistingArgs = new ArrayList<>(optionArgs.get(shortOptionAnnotation.name()));
            optExistingArgs.add(shortOptionArg);
            optionArgs.put(shortOptionAnnotation.name(), optExistingArgs);
        } else { // Create an entry in the map if one was not found
            optionArgs.put(shortOptionAnnotation.name(), Collections.singletonList(shortOptionArg));
        }

        return i;
    }
    // end: private int shortOne


    // ====================================================================================================
    // private int shortMany
    //
    // Parse short options with many arguments
    //
    // Arguments--
    //
    // inputList:               the input list array from the command line
    //
    // inputStr:                the current element of inputList being processed
    //
    // shortOptionAnnotation:   the annotation for the option being processed
    //
    // shortOption:             the name of the option being processed
    //
    // i:                       the counter to keep track of inputList
    //
    // Returns--
    //
    // increment:               the amount to change i by (depending if format "-o1 2 3" or "-o 1 2 3" was used)
    //
    private int shortMany(List<String> inputList, String inputStr, Option shortOptionAnnotation, char shortOption, int i) throws OptionException {
        int increment = 0;

        // Loop through all the arguments
        for (int j = 0; j < options.get(optionAbbreviations.indexOf(shortOption)).nargs(); j++) {
            String shortOptionArg = (j == 0) ? inputStr.substring(inputStr.indexOf(shortOption) + 1) : "";
            if (shortOptionArg.length() == 0 && j == 0) { i++; }
            else if (shortOptionArg.length() != 0) { increment = -1; }

            // Check if the argument has been specified as part of the initial string (ex: -ofile)
            if (shortOptionArg.length() == 0) {
                shortOptionArg = (inputList.size() > i + j) ? inputList.get(i + j) : "";
                // Check if the argument has been specified after the initial string (ex: -o file)
                if (shortOptionArg.length() == 0) {
                    // If neither, missing argument
                    throw new OptionException(optionsDefinitionClass, Exceptions.MissingArgumentException, shortOption + "");
                }
            }

            // Check if this option already has an entry in the map and save the new argument
            if (optionArgs.containsKey(shortOptionAnnotation.name())) {
                if (!shortOptionAnnotation.multiple() && optionArgs.get(shortOptionAnnotation.name()).size() >= shortOptionAnnotation.nargs()) {
                    throw new OptionException(optionsDefinitionClass, Exceptions.MultipleNotAllowedException, shortOption + "");
                }

                ArrayList<String> optExistingArgs = new ArrayList<>(optionArgs.get(shortOptionAnnotation.name()));
                optExistingArgs.add(shortOptionArg);
                optionArgs.put(shortOptionAnnotation.name(), optExistingArgs);
            } else { // Create an entry in the map if one was not found
                optionArgs.put(shortOptionAnnotation.name(), Collections.singletonList(shortOptionArg));
            }
        }

        return increment;
    }
    // end: private int shortMany


    // ====================================================================================================
    // private void shortVariable
    //
    // Parse short options with variable arguments
    //
    // Arguments--
    //
    // inputList:               the input list array from the command line
    //
    // inputStr:                the current element of inputList being processed
    //
    // shortOptionAnnotation:   the annotation for the option being processed
    //
    // shortOption:             the name of the option being processed
    //
    // i:                       the counter to keep track of inputList
    //
    // Returns--
    //
    // None
    //
    private void shortVariable(List<String> inputList, String inputStr, Option shortOptionAnnotation, char shortOption, int i) throws OptionException {
        // User specified >1 args in format "-o 1,2,3"
        if (inputList.size() > i + 1 && !inputStr.contains("=") && inputList.get(i + 1).matches("(.)*(,)(.)*")) {
            String unsplitShortOptionArgs = (inputList.size() > i + 1) ? inputList.get(i + 1) : "";

            String cleanedShortOptionArgs = unsplitShortOptionArgs.replaceAll(",\\s+", ",");
            String[] shortOptionArgs = cleanedShortOptionArgs.split(",");

            // Check if this option already has an entry in the map and save the new argument
            if (optionArgs.containsKey(shortOptionAnnotation.name())) {
                if (!shortOptionAnnotation.multiple()) {
                    throw new OptionException(optionsDefinitionClass, Exceptions.MultipleNotAllowedException, shortOption + "");
                }

                ArrayList<String> optExistingArgs = new ArrayList<>(optionArgs.get(shortOptionAnnotation.name()));
                optExistingArgs.addAll(Arrays.asList(shortOptionArgs));
                optionArgs.put(shortOptionAnnotation.name(), optExistingArgs);
            } else { // Create an entry in the map if one was not found
                optionArgs.put(shortOptionAnnotation.name(), Arrays.asList(shortOptionArgs));
            }
        }

        // User specified 1 arg in format "-o 1"
        else if (inputList.size() > i + 1 && !inputStr.contains("=") && !inputList.get(i + 1).contains(",")) {
            shortOne(inputList, inputStr, shortOptionAnnotation, shortOption, i);
        }
    }
    // end: private void shortVariable

}
// end: public class OptionParser
