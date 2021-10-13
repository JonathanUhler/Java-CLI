// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// ParsedData.java
// Java-CLI
//
// Created by Jonathan Uhler on 10/12/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package javacli.helper;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class ParsedData
//
// Data structure for parsed option and argument data
//
public class ParsedData {

    private HashMap<String, List<String>> parsedOptions; // Map of parsed options and their arguments
    private ArrayList<String> parsedArguments; // List of command line arguments


    // ----------------------------------------------------------------------------------------------------
    // public ParsedData
    //
    // Arguments--
    //
    // parsedOptions:   map of parsed options where keys are option names and values are lists of args to the options
    //
    // parsedArguments: list of arguments to the command line application
    //
    public ParsedData(HashMap<String, List<String>> parsedOptions, ArrayList<String> parsedArguments) {
        // Set instance variables
        this.parsedOptions = parsedOptions;
        this.parsedArguments = parsedArguments;
    }
    // end: public ParsedData


    // ====================================================================================================
    // GET methods
    public HashMap<String, List<String>> getParsedOptions() {
        return parsedOptions;
    }

    public ArrayList<String> getParsedArguments() {
        return parsedArguments;
    }
    // end: GET methods

}
// end: public class ParsedData