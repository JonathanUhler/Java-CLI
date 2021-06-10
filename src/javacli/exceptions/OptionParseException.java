// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// OptionParseException.java
// Java-CLI
//
// Created by Jonathan Uhler on 6/6/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package javacli.exceptions;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class OptionParseException
//
// Exception for option parsing issues
//
public class OptionParseException extends Exception {

    public static final String invalidOption = "invalid option -- "; // Format for invalid option error
    public static final String invalidArguments = "invalid argument(s) -- "; // Format for invalid argument error
    public static final String missingArguments = "missing argument(s)"; // Format for the missing argument error

    String exception; // Exception statement


    // ----------------------------------------------------------------------------------------------------
    // public OptionParseException
    //
    // OptionParseException constructor
    //
    // exception:   the exception statement
    //
    public OptionParseException(String exception) {
        this.exception = exception;
    }
    // end: public OptionParseException


    // ====================================================================================================
    // public String getException
    //
    // Get the exception statement
    //
    // Arguments--
    //
    // None
    //
    // Returns--
    //
    // exception:   the exception statement
    //
    public String getException() {
        return "Exception exceptions.OptionParseException: " + exception;
    }
    // end: public String getException

}
// end: public class OptionParseException