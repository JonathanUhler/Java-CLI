// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// LongOption.java
// Java-CLI
//
// Created by Jonathan Uhler on 10/10/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package javacli.parser;


import javacli.annotations.Option;
import javacli.helper.CLIHelper;
import java.util.*;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class LongOption
//
// Parser for long/full options
//
public class LongOption {

    private final ArrayList<Option> options; // List of all option annotations defined in optionsDefinitionClass
    private final ArrayList<String> optionNames; // List of all full names for all the options

    private final HashMap<String, List<String>> optionArgs; // Map of options and their arguments. The key is the long name of the option and the value is a list of its args


    // ----------------------------------------------------------------------------------------------------
    // public LongOption
    //
    // Constructor for LongOption class
    //
    // Arguments--
    //
    // options:         list of option annotations defined in the optionsDefinitionClass
    //
    // optionNames:     list of full option names
    //
    // optionArgs:      hashmap to store option names and their arguments
    //
    public LongOption(ArrayList<Option> options, ArrayList<String> optionNames, HashMap<String, List<String>> optionArgs) {
        // Set instance variable
        this.options = options;
        this.optionNames = optionNames;
        this.optionArgs = optionArgs;
    }
    // end: public LongOption


    // ====================================================================================================
    // public int parseLongOption
    //
    // Parse a long option and its arguments
    //
    // Arguments--
    //
    // inputList:       the list of all arguments passed into the main() method as String[] args
    //
    // inputStr:        the specific short option to be parsed
    //
    // parseCounter:    the counter that points to which element in inputList is being parsed
    //
    // Returns--
    //
    // parseCounter:    the updated counter to use in reference with inputList
    //
    public int parseLongOption(List<String> inputList, String inputStr, int parseCounter) throws Exception {
        String longOptionName = inputStr.substring(2); // Get the name of the long option so the annotation can be found
        if (inputStr.contains("=")) longOptionName = longOptionName.split("=")[0]; // Remove any equals and arguments to isolate just the name of the option

        // Check that the option exists with a long option name
        CLIHelper.cliAssert((this.optionNames.contains(longOptionName)),
                "a specified option does not exist",
                "in option " + longOptionName);

        // Check that the option exists as an annotation
        CLIHelper.cliAssert((this.options.size() > this.optionNames.indexOf(longOptionName)),
                "a specified option does not exist",
                "in option " + longOptionName);

        Option longOptionAnnotation = this.options.get(this.optionNames.indexOf(longOptionName)); // Get the annotation belonging to the long option being parsed

        // If the option has the count parameter specified, then update its count value
        if (longOptionAnnotation.doCount())
            new CLIHelper().changeAnnotationValue(longOptionAnnotation, "_countValue", longOptionAnnotation._countValue() + 1);

        // Check if the option has no arguments and parse as a boolean flag
        if (longOptionAnnotation.nargs() == 0) {
            this.parseZeroArguments(longOptionAnnotation);
            parseCounter++;
            return parseCounter;
        }
        // Check if the option has 1 argument only
        else if (longOptionAnnotation.nargs() == 1) {
            parseCounter = this.parseOneArgument(longOptionAnnotation, inputList, inputStr, parseCounter);
            return parseCounter;
        }
        // Check if the option has a constant number of arguments that is greater than 1
        else if (longOptionAnnotation.nargs() > 1) {
            parseCounter = this.parseManyArguments(longOptionAnnotation, inputList, parseCounter);
            return parseCounter;
        }
        // Check if the option can take any number of arguments (besides 0)
        else if (longOptionAnnotation.nargs() == -1) {
            parseCounter = this.parseVariableArguments(longOptionAnnotation, inputList, inputStr, parseCounter);
            return parseCounter;
        }

        return parseCounter;
    }
    // end: public int parseLongOption


    // ====================================================================================================
    // private void parseZeroArguments
    //
    // Parse an option with no argument
    //
    // Arguments--
    //
    // longOptionAnnotation:    the annotation that defines the option
    //
    // Returns--
    //
    // parseCounter:            the updated counter after parsing an option and its arguments
    //
    private void parseZeroArguments(Option longOptionAnnotation) throws Exception {
        this.addArgs(longOptionAnnotation, new ArrayList<>(Collections.singletonList("true")));
    }
    // end: private void parseZeroArguments


    // ====================================================================================================
    // private int parseOneArgument
    //
    // Parse an option with exactly 1 argument
    //
    // Arguments--
    //
    // longOptionAnnotation:    the annotation that defines the option
    //
    // inputList:               the list of all command line arguments from the main() method
    //
    // inputStr:                the specific part of inputList (the option) to be parsed
    //
    // parseCounter:            a counter variable that keeps track of which element in inputList is being parsed
    //
    // Returns--
    //
    // parseCounter:            the updated counter after parsing an option and its arguments
    //
    private int parseOneArgument(Option longOptionAnnotation, List<String> inputList, String inputStr, int parseCounter) throws Exception {
        String longOptionName = longOptionAnnotation.name(); // Get the name of the long option
        int optionNumArgs = longOptionAnnotation.nargs(); // Get the number of arguments the option has

        String argument = ""; // Initialize a variable to store the argument for the option
        if (inputStr.contains("=")) argument = inputStr.split("=")[1]; // If the input string has an equals, that means the arg is part of inputStr. Split by the equals and get the arg

        // If the length of argument is still 0, that means there was no equals dividing it
        if (argument.length() == 0) {
            // Check that the inputList array can be safely indexed to get the next element
            CLIHelper.cliAssert((inputList.size() > parseCounter + 1),
                    "an option is missing an argument",
                    "in option " + longOptionName + ", expected " + optionNumArgs + " arguments");

            // Get the argument and increment the parse counter
            argument = inputList.get(parseCounter + 1);
            parseCounter++;
        }

        // Increment the parse counter, add the arg, and return the parse counter
        parseCounter++;
        this.addArgs(longOptionAnnotation, new ArrayList<>(Collections.singletonList(argument)));
        return parseCounter;
    }
    // end: private int parseOneArgument


    // ====================================================================================================
    // private int parseManyArguments
    //
    // Parse an option with a constant number of arguments greater than 1
    //
    // Arguments--
    //
    // longOptionAnnotation:    the annotation that defines the option
    //
    // inputList:               the list of all command line arguments from the main() method
    //
    // parseCounter:            a counter variable that keeps track of which element in inputList is being parsed
    //
    // Returns--
    //
    // parseCounter:            the updated counter after parsing an option and its arguments
    //
    private int parseManyArguments(Option longOptionAnnotation, List<String> inputList, int parseCounter) throws Exception {
        String longOptionName = longOptionAnnotation.name(); // Get the name of the long option
        int optionNumArgs = longOptionAnnotation.nargs(); // Get the number of arguments the option has

        String inputStr = inputList.get(parseCounter); // Initialize the input string to be the index of the parse counter at the total input list
        if (inputStr.contains("=")) inputStr = inputStr.split("=")[1];
        else if (!inputStr.contains("=") && inputList.size() > parseCounter + 1) inputStr = inputList.get(parseCounter + 1); // If no equals, increment the parse counter}

        ArrayList<String> arguments = new ArrayList<>(Arrays.asList(inputStr.split(","))); // Get a list of the arguments if they are separated by commas
        parseCounter++; // Increment the parse counter

        // Check if there is only 1 argument in the array
        // If there is only 1 arg, then there are no commas (or there are missing arguments) and the format is "-o 1 2 3"
        if (arguments.size() <= 1) {
            // Loop through each of the argument (but 1 less than the total because the first arg is already in the "arguments" array)
            for (int a = 0; a < optionNumArgs - 1; a++) {
                // Check that there are more elements to be parsed in the input list. If there are not that means the argument is missing
                CLIHelper.cliAssert((inputList.size() > parseCounter + 1),
                        "an option is missing an argument",
                        "in option " + longOptionName + ", expected " + optionNumArgs + " arguments");

                arguments.add(inputList.get(parseCounter + 1)); // If the arg is not missing, get it by indexing the next element in the input list
                parseCounter++; // Increment the parse counter since we picked up en element in the next index
            }
        }

        // Check that the correct number of arguments are present
        CLIHelper.cliAssert((arguments.size() == optionNumArgs) || // Check that there are the correct # of args
                        (optionNumArgs == -1), // Or if the args differ, the option has variable number of args
                "an option has an incorrect number of arguments",
                "in option " + longOptionName + ", expected " + optionNumArgs + " arguments");

        if (!inputList.get(parseCounter - 1).contains("=")) parseCounter++;

        this.addArgs(longOptionAnnotation, arguments); // Add all the arguments to the option/arg hashmap
        return parseCounter; // Return the new parse counter
    }
    // end: private int parseManyArguments


    // ====================================================================================================
    // private int parseVariableArguments
    //
    // Parse an option with a variable number of arguments
    //
    // Arguments--
    //
    // longOptionAnnotation:    the annotation that defines the option
    //
    // inputList:               the list of all command line arguments from the main() method
    //
    // inputStr:                the specific part of inputList (the option) to be parsed
    //
    // parseCounter:            a counter variable that keeps track of which element in inputList is being parsed
    //
    // Returns--
    //
    // parseCounter:            the updated counter after parsing an option and its arguments
    //
    private int parseVariableArguments(Option longOptionAnnotation, List<String> inputList, String inputStr, int parseCounter) throws Exception {
        // Check if the input string has an equals
        if (inputStr.contains("=")) {
            parseCounter = (inputStr.split("=")[1].contains(",")) ?
                    this.parseManyArguments(longOptionAnnotation, inputList, parseCounter) : // If it has an equals and a comma-separated list, parse as many args
                    this.parseOneArgument(longOptionAnnotation, inputList, inputStr, parseCounter); // If it has an equals but no comma-separated list, parse as 1 arg
        }
        // If no equals, then parse differently
        else {
            // Check that we can safely index the next value in the input list to find the args
            CLIHelper.cliAssert((inputList.size() > parseCounter + 1),
                    "an option is missing an argument",
                    "in option " + longOptionAnnotation.name() + ", expected " + longOptionAnnotation.nargs() + " arguments");

            parseCounter = (inputList.get(parseCounter + 1).contains(",")) ?
                    this.parseManyArguments(longOptionAnnotation, inputList, parseCounter) : // If it has a comma-separated list, parse as many args
                    this.parseOneArgument(longOptionAnnotation, inputList, inputStr, parseCounter); // If it has no comma-separated list, parse as 1 arg
        }

        return parseCounter; // Return the parse counter
    }
    // end: private int parseVariableArguments


    // ====================================================================================================
    // private void addArgs
    //
    // Add arguments to the optionArgs hashmap for a given option
    //
    // Arguments--
    //
    // longOptionAnnotation:    the annotation that defines the option
    //
    // args:                    a list of arguments that go with the option to add to the hashmap
    //
    // Returns--
    //
    // None
    //
    private void addArgs(Option longOptionAnnotation, ArrayList<String> args) throws Exception {
        String optionFullName = longOptionAnnotation.name(); // Get the full name of the short option
        int optionNumArgs = longOptionAnnotation.nargs(); // Get the number of arguments the option has

        if (this.optionArgs.containsKey(longOptionAnnotation.name())) {
            // If it does have an entry, check that multiple entries are allowed
            CLIHelper.cliAssert((longOptionAnnotation.multiple()) || // Check that multiple = true or
                            (this.optionArgs.get(optionFullName).size() <= optionNumArgs), // check that the number of arguments <= the number of argument the option can have
                    "multiple uses of an option are forbidden",
                    "in option " + optionFullName + " was used more than once");

            ArrayList<String> specifiedArguments = new ArrayList<>(this.optionArgs.get(optionFullName));
            specifiedArguments.addAll(args);
            this.optionArgs.put(optionFullName, specifiedArguments);
        } else { // Create an entry in the map if one was not found
            this.optionArgs.put(optionFullName, args);
        }
    }
    // end: private void addArgs

}
