// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// OptionParser.java
// Java-CLI
//
// Created by Jonathan Uhler on 6/25/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package javacli;


import javacli.annotations.Argument;
import javacli.annotations.Option;
import javacli.annotations.Version;
import javacli.helper.CLIHelper;
import javacli.helper.ParsedData;
import javacli.parser.AnnotationSyntax;
import javacli.parser.LongOption;
import javacli.parser.ShortOption;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Field;
import java.util.*;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class OptionParser
//
// Option parser and handler class
//
public class OptionParser {

    // App information
    private final ArrayList<Class<?>> optionsDefinitionClasses; // The class containing the option/argument definitions
    private final ArrayList<String> definitionClassNames;
    private String version = "";

    // Current command information
    private Class<?> optionsDefinitionClass;
    private String definitionClassName;

    // Option information
    private ArrayList<Option> options;
    private ArrayList<Argument> arguments;
    private ArrayList<Version> versions;

    private ArrayList<String> optionNames;
    private ArrayList<Character> optionAbbreviations;
    private ArrayList<String> argumentNames;

    private ArrayList<String> optionFieldNames;
    private ArrayList<Class<?>> optionFieldTypes;
    private ArrayList<String> argumentFieldNames;
    private ArrayList<Class<?>> argumentFieldTypes;

    // Option and argument information
    private final ArrayList<String> inputArgs = new ArrayList<>(); // List of arguments to the program
    private final HashMap<String, List<String>> optionArgs = new HashMap<>(); // Map of arguments to options where the option name is the key and the arguments are the values


    // ----------------------------------------------------------------------------------------------------
    // public OptionParser
    //
    // OptionParser constructor
    //
    // Arguments--
    //
    // optionsDefinitionClasses:  the class with the option, arguments, and version annotations
    //
    public OptionParser(Class<?> optionsDefinitionClass) {
        this.optionsDefinitionClasses = new ArrayList<>(Collections.singletonList(optionsDefinitionClass));
        this.definitionClassNames = new ArrayList<>(Collections.singletonList(optionsDefinitionClass.getSimpleName()));

        this.optionsDefinitionClass = this.optionsDefinitionClasses.get(0);
        this.definitionClassName = this.definitionClassNames.get(0);
    }
    // end: public OptionParser


    public OptionParser(ArrayList<Class<?>> optionsDefinitionClasses) {
        this.optionsDefinitionClasses = optionsDefinitionClasses;
        this.definitionClassNames = new ArrayList<>();
        for (Class<?> optionClass : this.optionsDefinitionClasses) this.definitionClassNames.add(optionClass.getSimpleName());

        this.optionsDefinitionClass = this.optionsDefinitionClasses.get(0);
        this.definitionClassName = this.definitionClassNames.get(0);
    }


    // ====================================================================================================
    // GET methods
    public ArrayList<Option> getOptions() {
        return options;
    }

    public ArrayList<Argument> getArguments() {
        return arguments;
    }

    public ArrayList<String> getOptionNames() {
        return optionNames;
    }

    public ArrayList<Character> getOptionAbbreviations() {
        return optionAbbreviations;
    }

    public ArrayList<String> getArgumentNames() {
        return argumentNames;
    }

    public ArrayList<String> getOptionFieldNames() {
        return optionFieldNames;
    }

    public ArrayList<Class<?>> getOptionFieldTypes() {
        return optionFieldTypes;
    }

    public ArrayList<String> getArgumentFieldNames() {
        return argumentFieldNames;
    }

    public ArrayList<Class<?>> getArgumentFieldTypes() {
        return argumentFieldTypes;
    }
    // end: GET methods


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
    //
    public int getCount(String key) {
        for (Field f : this.optionsDefinitionClass.getFields()) {
            Option o = f.getAnnotation(Option.class);
            if (o != null && o.name().equals(key)) return o._countValue();
        }

        return 0;
    }
    // end: public int getCount


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
        this.options = new ArrayList<>();
        this.arguments = new ArrayList<>();
        this.versions = new ArrayList<>();
        this.optionNames = new ArrayList<>();
        this.optionAbbreviations = new ArrayList<>();
        this.argumentNames = new ArrayList<>();
        this.optionFieldNames = new ArrayList<>();
        this.optionFieldTypes = new ArrayList<>();
        this.argumentFieldNames = new ArrayList<>();
        this.argumentFieldTypes = new ArrayList<>();
        this.version = "";
        this.definitionClassName = this.optionsDefinitionClass.getSimpleName();

        for (Field f : this.optionsDefinitionClass.getFields()) {
            Option o = f.getAnnotation(Option.class);
            Argument a = f.getAnnotation(Argument.class);
            Version v = f.getAnnotation(Version.class);

            if (o != null) {
                this.options.add(o);
                this.optionNames.add(o.name());
                this.optionAbbreviations.add(o.abbreviation());
                this.optionFieldNames.add(f.getName());
                this.optionFieldTypes.add(f.getType());
            }
            if (a != null) {
                this.arguments.add(a);
                this.argumentNames.add(a.name());
                this.argumentFieldNames.add(f.getName());
                this.argumentFieldTypes.add(f.getType());
            }
            if (v != null) {
                this.versions.add(v);
                this.version = v.version();
            }
        }
    }
    // end: private void init


    // ====================================================================================================
    // private void parseSpecialOptions
    //
    // Parse and handle special options such as --version and --help
    //
    // Arguments--
    //
    // args:    the string array of command line arguments
    //
    // Returns--
    //
    // None
    //
    private void parseSpecialOptions(String[] args) {
        // Check for special options
        for (String arg : args) {
            // Check for --help
            if (arg.equals("--help")) {
                System.out.println(new CLIHelper().generateHelp(this.optionsDefinitionClass, this.definitionClassName, this.version));
                System.exit(0);
            }

            // Check for --version
            if (this.versions.size() > 0 && (arg.equals("--version") || (arg.startsWith("-") && arg.contains("" + this.versions.get(0).abbreviation()) && !this.version.equals("")))) {
                System.out.println(this.definitionClassNames + ", version " + this.version);
                System.exit(0);
            }
        }
    }
    // end: private void parseSpecialOptions


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
    private void castAndAdd(Field f, Class<?> castType, Object data, boolean dataIsCollection) throws Exception {
        // Check for casting for multiple types
        try {
            if (!dataIsCollection) {
                PropertyEditor editor = PropertyEditorManager.findEditor(castType);
                editor.setAsText(data.toString());
                f.set(this.optionsDefinitionClasses, editor.getValue());
            }
            // Check for casting for multiple types
            else {
                List<?> uncastedDataList = (List<?>) data; // List of uncasted data of any type
                List<Object> castedDataList = new ArrayList<>(); // Initialize a second arraylist to hold the correct type of data

                // Loop through each element of the uncasted data and cast it using the PropertyEditor
                for (Object dataElem : uncastedDataList) {
                    PropertyEditor editor = PropertyEditorManager.findEditor(castType);
                    editor.setAsText(dataElem.toString());
                    castedDataList.add(editor.getValue()); // Add the casted data to the second arraylist
                }

                // Set the data
                f.set(this.optionsDefinitionClasses, castedDataList);
            }
        } catch (Exception e) {
            CLIHelper.cliAssert(false,
                    "invalid argument type",
                    "data is " + data,
                    "expected type " + f.getType().getSimpleName());
        }
    }
    // end: private void castAndAdd


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
    public void parse(String[] args) throws Exception {
        this.init(); // Set up information about the options
        new AnnotationSyntax().parseOptionDefinitions(this); // Parse the option definitions in the optionDefinitionsClass

        ParsedData parsedData = null; // Initialize a data structure to hold the parsed information
        try { parsedData = this.parseAndExitUponError(Arrays.asList(args)); } // Try parsing the options and args and catch any errors by printing the help menu
        catch (Exception e) {
            System.out.println(new CLIHelper().generateHelp(this.optionsDefinitionClass, this.definitionClassName, this.version));
            System.exit(2);
        }

        // Set parse information
        ArrayList<String> inputArgs = parsedData.getParsedArguments();
        HashMap<String, List<String>> optionArgs = parsedData.getParsedOptions();

        // Try setting variables in the option definition class
        int argCount = 0;

        for (Field f : this.optionsDefinitionClass.getFields()) {
            // The field is an option
            if (f.getAnnotation(Option.class) != null) {
                Option o = f.getAnnotation(Option.class);
                // The option was specified by the user
                if (optionArgs.containsKey(f.getName())) {
                    Class<?> castType = this.options.get(this.optionNames.indexOf(f.getName())).type();
                    List<String> data = optionArgs.get(f.getName());

                    // Cast and add single argument options
                    if (optionArgs.get(f.getName()).size() == 1 && (o.nargs() == 1 || o.nargs() == 0)) { this.castAndAdd(f, castType, data.get(0), false); }
                    // Cast and add multiple argument options
                    else { this.castAndAdd(f, castType, data, true); }
                }
                // The option wasn't specified
                else {
                    // If the option is a flag, set it to false
                    if (f.getAnnotation(Option.class).nargs() == 0) {
                        f.set(this.optionsDefinitionClasses, false);
                    }
                    // If the option has arguments and a default value, set it to the default value
                    else if (f.getAnnotation(Option.class).nargs() == 1 && !f.getAnnotation(Option.class).defaultValue().equals("")) {
                        this.castAndAdd(f, f.getAnnotation(Option.class).type(), f.getAnnotation(Option.class).defaultValue(), false);
                    }
                }
            }

            // The field is an argument
            if (f.getAnnotation(Argument.class) != null) {
                Class<?> argCastType = this.arguments.get(this.argumentNames.indexOf(f.getName())).type();
                // Cast the value and set the variable
                this.castAndAdd(f, argCastType, inputArgs.get(argCount), false);
                argCount++;
            }
        }
    }
    // end: public void parse


    // ====================================================================================================
    // private ParsedData parseAndExitUponError
    //
    // Parses options and throws errors
    //
    // Arguments--
    //
    // inputList:   the String[] args list from java main as a List<String>
    //
    // Returns--
    //
    // ParsedData object containing the parsed args and options
    //
    private ParsedData parseAndExitUponError(List<String> inputList) throws Exception {
        int parseCounter = 0; // Initialize a variable to keep track of which element in inputList is being parsed

        // Loop through each of the elements in inputList and parse them
        while (parseCounter < inputList.size()) {
            String inputStr = inputList.get(parseCounter); // Get the element in inputList that is going to be parsed currently

            // Check if the string is an argument to the program
            // The parseCounter in incremented automatically when parsing options so arguments to options will not be added to inputArgs
            if (!inputStr.startsWith("-") && !inputStr.startsWith("--")) {
                if (this.definitionClassNames.contains(inputStr)) {
                    this.optionsDefinitionClass = this.optionsDefinitionClasses.get(
                            this.definitionClassNames.indexOf(inputStr)
                    );
                    this.definitionClassName = this.optionsDefinitionClass.getName();
                    this.init();
                    parseCounter++;
                    continue;
                }

                this.inputArgs.add(inputStr);
                parseCounter++;
                continue;
            }

            this.parseSpecialOptions((String[]) inputList.toArray()); // Parse for and handle special options like --help and --version

            // Check if there is an "--" signifying all following strings are arguments
            if (inputStr.equals("--")) {
                ArrayList<String> allArgs = new ArrayList<>();

                // Add everything after the -- to the argument list
                for (int a = inputList.indexOf(inputStr) + 1; a < inputList.size(); a++) {
                    allArgs.add(inputList.get(a));
                }

                this.inputArgs.addAll(allArgs);
                break; // End the parsing as there will be nothing left to parse
            }

            // Parse long options
            if (inputStr.startsWith("--")) {
                LongOption longOptionParser = new LongOption(this.options, this.optionNames, this.optionArgs);
                parseCounter = longOptionParser.parseLongOption(inputList, inputStr, parseCounter);
                continue;
            }

            // Parse short options
            if (inputStr.startsWith("-")) {
                ShortOption shortOptionParser = new ShortOption(this.options, this.optionAbbreviations, this.optionArgs);
                parseCounter = shortOptionParser.parseShortOption(inputList, inputStr, parseCounter);
            }

            // Update parseCounter each time through the while loop
            parseCounter++;
        }

        // Check that the correct number of arguments have been specified
        CLIHelper.cliAssert((this.inputArgs.size() == this.arguments.size()), // Check that the number of input args is the same as the number of argument annotations
                "incorrect number of command line arguments specified",
                "expected " + this.arguments.size() + " args, but got " + this.inputArgs.size(),
                "specified arguments are: " + this.inputArgs);

        // Return optionArgs and inputArgs as one object
        return new ParsedData(this.optionArgs, this.inputArgs);
    }
    // end: private void parseAndExitUponError

}
// end: public class OptionParser
