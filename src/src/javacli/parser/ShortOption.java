// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// ShortOption.java
// Java-CLI
//
// Created by Jonathan Uhler on 10/10/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package javacli.parser;


import javacli.annotations.Option;
import javacli.helper.CLIHelper;
import java.util.*;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class ShortOption
//
// Parser for short/abbreviated options
//
public class ShortOption {

    private final ArrayList<Option> options; // List of all the option annotations defined in optionsDefinitionClass
    private final ArrayList<Character> optionAbbreviations; // List of the abbreviations (short names) of all the options

    private final HashMap<String, List<String>> optionArgs; // Map of options and their arguments. The key is the long name of the option and the value is a list of its specified args


    // ----------------------------------------------------------------------------------------------------
    // public ShortOption
    //
    // Constructor for ShortOption class
    //
    // Arguments--
    //
    // options:                 list of option annotations defined in the optionsDefinitionClass
    //
    // optionAbbreviations:     list of short option names (single characters)
    //
    // optionArgs:              hashmap to store option names and their arguments
    //
    public ShortOption(ArrayList<Option> options, ArrayList<Character> optionAbbreviations, HashMap<String, List<String>> optionArgs) {
        // Set instance variables
        this.options = options;
        this.optionAbbreviations = optionAbbreviations;
        this.optionArgs = optionArgs;
    }
    // end: public ShortOption


    // ====================================================================================================
    // public int parseShortOption
    //
    // Parse a short option and its arguments
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
    public int parseShortOption(List<String> inputList, String inputStr, int parseCounter) throws Exception {
        // Loop through each character after the first hyphen in the input string
        // This takes into account multiple short options put next to each other and their arguments put without any equals to whitespace
        for (char shortOption : inputStr.substring(1).toCharArray()) {
            // Check that the option exists with a short option abbreviation
            CLIHelper.cliAssert((this.optionAbbreviations.contains(shortOption)),
                    "a specified option does not exist",
                    "in option " + shortOption);

            // Check that the option exists as an annotation
            CLIHelper.cliAssert((this.options.size() > this.optionAbbreviations.indexOf(shortOption)),
                    "a specified option does not exist",
                    "in option " + shortOption);

            Option shortOptionAnnotation = this.options.get(this.optionAbbreviations.indexOf(shortOption)); // Get the annotation for the option to be parsed

            // If the option has the count parameter specified, then update its count value
            if (shortOptionAnnotation.doCount())
                new CLIHelper().changeAnnotationValue(shortOptionAnnotation, "_countValue", shortOptionAnnotation._countValue() + 1);

            // Check if the option has no arguments and parse as a boolean flag
            if (shortOptionAnnotation.nargs() == 0) {
                this.parseZeroArguments(shortOptionAnnotation);
            }
            // Check if the option has 1 argument only
            else if (shortOptionAnnotation.nargs() == 1) {
                parseCounter = this.parseOneArgument(shortOptionAnnotation, inputList, inputStr, parseCounter); // Parse the option and its args and update the parse counter
                break; // Break out of the loop
            }
            // Check if the option has a constant number of arguments that is greater than 1
            else if (shortOptionAnnotation.nargs() > 1) {
                parseCounter = this.parseManyArguments(shortOptionAnnotation, inputList, parseCounter); // Parse the option and its args and update the parse counter
                break; // Break out of the loop
            }
            // Check if the option can take any number of arguments (besides 0)
            else if (shortOptionAnnotation.nargs() == -1) {
                parseCounter = this.parseVariableArguments(shortOptionAnnotation, inputList, inputStr, parseCounter); // Parse the option and its args and update the parse counter
                break; // Break out of the loop
            }
        }

        return parseCounter; // Return the parse counter now that some options have been parsed
    }
    // end: public int parseShortOption


    // ====================================================================================================
    // private void parseZeroArguments
    //
    // Parse an option with no arguments
    //
    // Arguments--
    //
    // shortOptionAnnotation:   the annotation that defines the option
    //
    // Returns--
    //
    // None
    //
    private void parseZeroArguments(Option shortOptionAnnotation) throws Exception {
        // Add "true" to the list of arguments for this option
        this.addArgs(shortOptionAnnotation, new ArrayList<>(Collections.singletonList("true")));
    }
    // end: private void parseZeroArguments


    // ====================================================================================================
    // private int parseOneArguments
    //
    // Parse an option with exactly 1 argument
    //
    // Arguments--
    //
    // shortOptionAnnotation:   the annotation that defines the option
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
    private int parseOneArgument(Option shortOptionAnnotation, List<String> inputList, String inputStr, int parseCounter) throws Exception {
        char shortOptionAbbrev = shortOptionAnnotation.abbreviation(); // Get the abbreviation (short name) for the option
        int optionNumArgs = shortOptionAnnotation.nargs(); // Get the number of arguments the option has

        // Initialize the argument to the next part of the input string
        // This starts by assuming the format of the option is -o1 where the argument is right next to the option name
        String argument = inputStr.substring(inputStr.indexOf(shortOptionAbbrev) + 1);

        // Check for an equals and split to get the option (ex, if the format was -o=1)
        if (argument.contains("=")) {
            argument = argument.split("=")[1];
        }

        // Check if the length of the argument is 0
        // If it is 0, then that means either the argument is missing entirely or there is whitespace like in "-o 1"
        if (argument.length() == 0) {
            // Check that there are more elements to be parsed in the input list. If there are not that means the argument is missing
            CLIHelper.cliAssert((inputList.size() > parseCounter + 1),
                    "an option is missing an argument",
                    "in option " + shortOptionAbbrev + ", expected " + optionNumArgs + " arguments");

            argument = inputList.get(parseCounter + 1); // If the arg is not missing, get it by indexing the next element in the input list
            parseCounter++; // Increment the parse counter since we picked up en element in the next index
        }

        this.addArgs(shortOptionAnnotation, new ArrayList<>(Collections.singletonList(argument))); // Add the argument to the list of parsed arguments and their options
        return parseCounter; // Return the parse counter
    }
    // end: private int parseOneArgument


    // ====================================================================================================
    // private int parseManyArguments
    //
    // Parse an option with a constant number of arguments greater than 1
    //
    // Arguments--
    //
    // shortOptionAnnotation:   the annotation that defines the option
    //
    // inputList:               the list of all command line arguments from the main() method
    //
    // parseCounter:            a counter variable that keeps track of which element in inputList is being parsed
    //
    // Returns--
    //
    // parseCounter:            the updated counter after parsing an option and its arguments
    //
    private int parseManyArguments(Option shortOptionAnnotation, List<String> inputList, int parseCounter) throws Exception {
        char shortOptionAbbrev = shortOptionAnnotation.abbreviation(); // Get the abbreviation (short name) for the option
        int optionNumArgs = shortOptionAnnotation.nargs(); // Get the number of arguments the option has

        String inputStr = inputList.get(parseCounter); // Initialize the input string to be the index of the parse counter at the total input list
        if (inputStr.contains("=")) { // If the input string contains an equals, it needs to be divided
            inputStr = inputStr.split("=")[1]; // Divide the input string and take the data to the right of the equals
            parseCounter--; // Decrement the parse counter since we are not advancing because of the equals
        }
        else if (!inputStr.contains("=") && inputList.size() > parseCounter + 1) inputStr = inputList.get(parseCounter + 1); // If no equals, increment the parse counter

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
                        "in option " + shortOptionAbbrev + ", expected " + optionNumArgs + " arguments");

                arguments.add(inputList.get(parseCounter + 1)); // If the arg is not missing, get it by indexing the next element in the input list
                parseCounter++; // Increment the parse counter since we picked up en element in the next index
            }
        }

        // Check that the correct number of arguments are present
        CLIHelper.cliAssert((arguments.size() == optionNumArgs) || // Check that there are the correct # of args
                (optionNumArgs == -1), // Or if the args differ, the option has variable number of args
                "an option has an incorrect number of arguments",
                "in option " + shortOptionAbbrev + ", expected " + optionNumArgs + " arguments");

        this.addArgs(shortOptionAnnotation, arguments); // Add all the arguments to the option/arg hashmap
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
    // shortOptionAnnotation:   the annotation that defines the option
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
    private int parseVariableArguments(Option shortOptionAnnotation, List<String> inputList, String inputStr, int parseCounter) throws Exception {
        // Check if the input string has an equals
        if (inputStr.contains("=")) {
            parseCounter = (inputStr.split("=")[1].contains(",")) ?
                    this.parseManyArguments(shortOptionAnnotation, inputList, parseCounter) : // If it has an equals and a comma-separated list, parse as many args
                    this.parseOneArgument(shortOptionAnnotation, inputList, inputStr, parseCounter); // If it has an equals but no comma-separated list, parse as 1 arg
        }
        // If no equals, then parse differently
        else {
            // Check that we can safely index the next value in the input list to find the args
            CLIHelper.cliAssert((inputList.size() > parseCounter + 1),
                    "an option is missing an argument",
                    "in option " + shortOptionAnnotation.abbreviation() + ", expected " + shortOptionAnnotation.nargs() + " arguments");

            parseCounter = (inputList.get(parseCounter + 1).contains(",")) ?
                    this.parseManyArguments(shortOptionAnnotation, inputList, parseCounter) : // If it has a comma-separated list, parse as many args
                    this.parseOneArgument(shortOptionAnnotation, inputList, inputStr, parseCounter); // If it has no comma-separated list, parse as 1 arg
        }

        return parseCounter; // Return the parse counter
    }


    // ====================================================================================================
    // private void addArgs
    //
    // Add arguments to the optionArgs hashmap for a given option
    //
    // Arguments--
    //
    // shortOptionAnnotation:   the annotation that defines the option
    //
    // args:                    a list of arguments that go with the option to add to the hashmap
    //
    // Returns--
    //
    // None
    //
    private void addArgs(Option shortOptionAnnotation, ArrayList<String> args) throws Exception {
        String optionFullName = shortOptionAnnotation.name(); // Get the full name of the short option
        int optionNumArgs = shortOptionAnnotation.nargs(); // Get the number of arguments the option has

        // Check if the optionArgs hashmap already has an entry for this option
        if (this.optionArgs.containsKey(optionFullName) && !shortOptionAnnotation.isFlag()) {
            // If it does have an entry, check that multiple entries are allowed
            CLIHelper.cliAssert((shortOptionAnnotation.multiple()) || // Check that multiple = true or
                            (this.optionArgs.get(optionFullName).size() <= optionNumArgs), // check that the number of arguments <= the number of argument the option can have
                    "multiple uses of an option are forbidden",
                    "in option " + optionFullName + " was used more than once");

            ArrayList<String> specifiedArguments = new ArrayList<>(this.optionArgs.get(optionFullName)); // Get the list of already specified arguments for the option
            specifiedArguments.addAll(args); // Append to the existing list with the value "true" since this option must be a boolean flag
            this.optionArgs.put(optionFullName, specifiedArguments); // Push the new list of options back into the hashmap
        }
        // If there is no entry in the hashmap, create one with the starting value
        else {
            this.optionArgs.put(optionFullName, args);
        }
    }
    // end: private void addArgs

}
