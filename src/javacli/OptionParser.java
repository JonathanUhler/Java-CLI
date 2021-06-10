// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// OptionParser.java
// Java-CLI
//
// Created by Jonathan Uhler on 6/5/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package javacli;


import javacli.exceptions.OptionDefinitionException;
import javacli.exceptions.OptionParseException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class OptionParser
//
// Parses options based on unix syntax conventions
// https://www.gnu.org/software/libc/manual/html_node/Argument-Syntax.html
//
public class OptionParser {

    // Helpful information about the command-line app using Java-CLI
    private String appName; // The name of the command line app
    private String version; // The version of the command line app
    private String usage; // The usage paragraph for the command line app

    Class<?> optionsClass; // The class file containing all the options

    // Information about the options
    List<Option> options = new ArrayList<>(); // List of all the options
    List<String> optionNames = new ArrayList<>(); // List of the option names
    List<Character> optionAbbrevs = new ArrayList<>(); // List of the option abbreviations


    // ----------------------------------------------------------------------------------------------------
    // public OptionParser
    //
    // OptionParser constructor 1
    //
    // Arguments--
    //
    // optionsClass:    the class made by the library user containing their desired options
    //
    public OptionParser(Class<?> optionsClass) {
        this.optionsClass = optionsClass;
        compileOptionInformation();
    }
    // end: public OptionParser


    // ----------------------------------------------------------------------------------------------------
    // public OptionParser
    //
    // OptionParser constructor 2
    //
    // Arguments--
    //
    // optionsClass:    the class made by the library user containing their desired options
    //
    // appName:         the name of the command line app the user is writing
    //
    // version:         the current version of the app
    //
    public OptionParser(Class<?> optionsClass, String appName, String version) {
        this.appName = appName;
        this.version = version;
        this.optionsClass = optionsClass;
        compileOptionInformation();
    }
    // end: public OptionParser


    // ----------------------------------------------------------------------------------------------------
    // public OptionParser
    //
    // OptionParser constructor 3
    //
    // Arguments--
    //
    // optionsClass:    the class made by the library user containing their desired options
    //
    // appName:         the name of the command line app the user is writing
    //
    // version:         the current version of the app
    //
    // usage:           the usage paragraph of the app
    //
    public OptionParser(Class<?> optionsClass, String appName, String version, String usage) {
        this.appName = appName;
        this.version = version;
        this.usage = usage;
        this.optionsClass = optionsClass;
        compileOptionInformation();
    }
    // end: public OptionParser


    // ====================================================================================================
    // public void parseAndExitUponError
    //
    // Begins the parse sequence
    //
    // Arguments--
    //
    // args:    the list of command line arguments
    //
    // Returns--
    //
    // None
    //
    public void parseAndExitUponError(String[] args) {
        for (String arg : args) {
            // Check for the help argument
            if (arg.equals("--help")) {
                describeOptions();
                System.exit(0);
            }
        }

        try { // Try to parse the command line arguments
            parse(Arrays.asList(args));
        }
        catch (OptionParseException e) { // Catch any errors and exit
            System.err.println(((appName != null) ? appName + ": " : "") + "argument parse issue -- " + e.getException());
            describeOptions();
            System.exit(2);
        }
    }
    // end: public void parseAndExitUponError


    public void getOptions() {
        
    }


    // ====================================================================================================
    // void compileOptionInformation
    //
    // Creates lists of information about the options in optionsClass (such as the names and abbreviations)
    //
    // Arguments--
    //
    // None
    //
    // Returns--
    //
    // None
    //
    void compileOptionInformation() {
        // Get all the fields from optionsClass
        for (Field f : optionsClass.getFields()) {
            Option option = f.getAnnotation(Option.class);

            try { // Validate the number of arguments in each option
                validateOption(option.name(), option.abbreviation(), option.numArgs(), option.argDescriptions(), option.argRegex());
            }
            catch (OptionDefinitionException e) { // Catch any errors about the number of arguments
                System.err.println("Class \"" + optionsClass.getName() + "\" option definition issue -- " + e.getException());
                System.exit(-2);
            }

            options.add(option); // Add the full option to a list
            optionNames.add(option.name()); // Add the option's name to a list
            optionAbbrevs.add(option.abbreviation()); // Add the option's abbreviation to a list
        }
    }
    // end: compileOptionInformation


    // ====================================================================================================
    // void validateOption
    //
    // Validates information about an option
    //
    // Arguments--
    //
    // argName:         the name of the option
    //
    // argAbbrev:       the abbreviated name of the option
    //
    // numArgs:         the number of arguments for that option
    //
    // argDescriptions: the list of descriptions of the arguments
    //
    // argRegex:        the list of regular expressions for the arguments
    //
    // Returns--
    //
    // None
    //
    void validateOption(String argName, char argAbbrev, int numArgs, String[] argDescriptions, String[] argRegex) throws OptionDefinitionException {
        // Get the number of regular expressions and descriptions
        int numDescriptions = argDescriptions.length;
        int numRegex = argRegex.length;

        // Confirm the numbers line up
        if (numArgs != numDescriptions || numArgs != numRegex) {
            throw new OptionDefinitionException(OptionDefinitionException.argumentCount + argName);
        }

        // Confirm no two options share the same name or abbreviation
        if (optionNames.contains(argName)) {
            throw new OptionDefinitionException(OptionDefinitionException.nonUniqueName + argName);
        }
        else if (optionAbbrevs.contains(argAbbrev)) {
            throw new OptionDefinitionException(OptionDefinitionException.nonUniqueName + argAbbrev);
        }
    }
    // end: void validateOptions


    // ====================================================================================================
    // void describeOptions
    //
    // Prints help information about the command line app and the options
    //
    // Arguments--
    //
    // None
    //
    // Returns--
    //
    // None
    //
    void describeOptions() {
        StringBuilder help = new StringBuilder();

        // Add in the app name and version if that information is available
        if (appName != null && version != null) { help.append(appName).append(", version ").append(version).append("\n\n"); }
        // Add in the usage statement if one is available
        help.append((usage != null) ? "usage: " + usage + "\n\n" : "");

        // Get all the options from optionsClass
        for (Field f : optionsClass.getFields()) {
            Option option = f.getAnnotation(Option.class);

            // Add each option's help statement
            // Option help expression: "\t<abbrev> [args]\t: <help msg>"
            help.append("\t").append((option.abbreviation() != Character.MIN_VALUE) ? option.abbreviation() : option.name()).append(" ").append(Arrays.toString(option.argDescriptions()).replace("[", "").replace("]", "")).append("\t: ").append(option.help()).append("\n");
        }

        // Print out the help information
        System.out.println(help);
    }
    // end: describeOptions


    // ====================================================================================================
    // void parse
    //
    // Parses command line arguments
    //
    // Arguments--
    //
    // args:    the arguments to parse
    //
    // Returns--
    //
    // None
    //
    void parse(List<String> args) throws OptionParseException {
        List<String> programArguments = new ArrayList<>(); // List of arguments for the command line app
        List<String> optionArguments = new ArrayList<>(); // List of arguments for options

        // Parse each command line argument
        for (int i = 0; i < args.size(); i++) {
            String arg = args.get(i);

            // If the argument does not start with hyphens it is an argument, not an option
            if (!arg.startsWith("-") && !arg.startsWith("--") && !optionArguments.contains(arg)) {
                programArguments.add(arg);
                continue;
            }

            // Process lone double hyphens
            if (arg.equals("--")) { // "--" means all following args are not options
                List<String> argsToParse = new ArrayList<>();

                // Gather all remaining command line arguments and mark them as non options
                for (int currentArg = args.indexOf(arg) + 1; currentArg < args.size(); currentArg++) {
                    argsToParse.add(args.get(currentArg));
                }

                programArguments.addAll(argsToParse);
                break;
            }

            // Check to see if the argument is an option that starts with 2 hyphens
            if (arg.startsWith("--")) {
                String a = arg.substring(2);
                if (arg.contains("=")) { a = a.split("=")[0]; }

                // Check to see if the option exists
                if (optionNames.contains(a)) {
                    // The option has 1 argument
                    if (options.get(optionNames.indexOf(a)).numArgs() == 1) {
                        String optionArg = (args.size() > i + 1) ? args.get(i + 1) : "";
                        if (arg.contains("=")) { optionArg = arg.split("=")[1]; }

                        // Check to see if the needed arguments are supplied
                        if (optionArg.length() == 0) {
                            throw new OptionParseException(OptionParseException.missingArguments);
                        }

                        // Check to see if the argument matches the regular expression or not
                        if (!optionArg.matches(options.get(optionNames.indexOf(a)).argRegex()[0])) {
                            throw new OptionParseException(OptionParseException.invalidArguments + optionArg);
                        }

                        optionArguments.add(optionArg);
                        continue;
                    }
                    // The option has multiple arguments
                    else if (options.get(optionNames.indexOf(a)).numArgs() != 1 && options.get(optionNames.indexOf(a)).numArgs() != 0) {
                        for (int j = 0; j < options.get(optionNames.indexOf(a)).numArgs(); j++) {
                            String optionArg = (args.size() > i + j + 1) ? args.get(i + j + 1) : "";

                            // Check to see if the needed arguments are supplied
                            if (optionArg.length() == 0) {
                                throw new OptionParseException(OptionParseException.missingArguments);
                            }

                            // Check to see if the argument matches the regular expression or not
                            if (!optionArg.matches(options.get(optionNames.indexOf(a)).argRegex()[j])) {
                                throw new OptionParseException(OptionParseException.invalidArguments + optionArg);
                            }

                            optionArguments.add(optionArg);
                        }

                        continue;
                    }
                    // The option has no arguments, the loop will continue to the next option
                }
                // The option was invalid/does not exist
                else {
                    throw new OptionParseException(OptionParseException.invalidOption + a);
                }
            }


            // Check to see if the argument is an option abbreviation that starts with 1 hyphen
            if (arg.startsWith("-")) {
                // Step through each letter (after discarding the leading hyphen)
                for (char a : arg.substring(1).toCharArray()) {
                    // Check to see if the option exists
                    if (optionAbbrevs.contains(a)) {
                        // The option has 1 argument
                        if (options.get(optionAbbrevs.indexOf(a)).numArgs() == 1) {
                            // Assume there is no whitespace between the option and the argument
                            String optionArg = arg.substring(arg.indexOf(a) + 1);

                            // Check to see if the needed arguments are supplied
                            if (optionArg.length() == 0) {
                                // If the non whitespace assumption fails, assume there is whitespace and check again
                                optionArg = (args.size() > i + 1) ? args.get(i + 1) : "";
                                // There was no provided argument
                                if (optionArg.length() == 0) {
                                    throw new OptionParseException(OptionParseException.missingArguments);
                                }
                            }

                            // Check to see if the argument matches the regular expression or not
                            if (!optionArg.matches(options.get(optionAbbrevs.indexOf(a)).argRegex()[0])) {
                                throw new OptionParseException(OptionParseException.invalidArguments + optionArg);
                            }

                            optionArguments.add(optionArg);
                            break;
                        }
                        // The option has multiple arguments
                        else if (options.get(optionAbbrevs.indexOf(a)).numArgs() > 1) {
                            for (int j = 0; j < options.get(optionAbbrevs.indexOf(a)).numArgs(); j++) {
                                String optionArg = (args.size() > i + j + 1) ? args.get(i + j + 1) : "";

                                // Check to see if the needed arguments are supplied
                                if (optionArg.length() == 0) {
                                    throw new OptionParseException(OptionParseException.missingArguments);
                                }

                                // Check to see if the argument matches the regular expression or not
                                if (!optionArg.matches(options.get(optionAbbrevs.indexOf(a)).argRegex()[j])) {
                                    throw new OptionParseException(OptionParseException.invalidArguments + optionArg);
                                }

                                optionArguments.add(optionArg);
                            }
                        }
                        // The option has no arguments, the loop will continue to the next option
                    }
                    // The option was invalid/does not exist
                    else {
                        throw new OptionParseException(OptionParseException.invalidOption + a);
                    }
                }
            }

        }

        System.out.println("Program args: " + programArguments + "\nOption args: " + optionArguments);

    }
    // end: void parse

}
// end: public class OptionParser