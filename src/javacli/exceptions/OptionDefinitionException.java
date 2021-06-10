// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// OptionDefinitionException.java
// Java-CLI
//
// Created by Jonathan Uhler on 6/7/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package javacli.exceptions;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class OptionDefinitionException
//
// Exception for internal errors with option definitions
//
public class OptionDefinitionException extends Exception {

    public static final String argumentCount = "argument count -- "; // Format for argument count error
    public static final String nonUniqueName = "duplicate definition -- "; // Format for argument duplicate definition error

    String exception; // Exception statement


    // ----------------------------------------------------------------------------------------------------
    // public OptionDefinitionException
    //
    // OptionDefinitionException constructor
    //
    // Arguments--
    //
    // exception:   the exception statement
    //
    public OptionDefinitionException(String exception) {
        this.exception = exception;
    }
    // end: public OptionDefinitionException


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
        return "Exception exceptions.OptionDefinitionException: " + exception;
    }
    // end: public String getException

}
// end: public class OptionDefinitionException