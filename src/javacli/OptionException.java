// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// OptionDefinitionException.java
// Java-CLI
//
// Created by Jonathan Uhler on 6/7/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package javacli;


import java.util.HashMap;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class OptionDefinitionException
//
// Exception for internal errors with option definitions
//
public class OptionException extends Exception {

    // Lookup table for error types and their descriptions
    final HashMap<javacli.Exceptions, String> ExceptionLookup = new HashMap<javacli.Exceptions, String>() {{
        // Option definition errors
        put(javacli.Exceptions.FlagWithNonZeroArgsException, "an option with the isFlag parameter has more than 0 expected arguments");
        put(javacli.Exceptions.NonFlagWithZeroArgsException, "an option without the isFlag parameter has 0 arguments");
        put(javacli.Exceptions.NoArgsOptionNotBooleanException, "an option with zero arguments is not type boolean");
        put(javacli.Exceptions.NoDefaultToShowException, "an option with the showDefault parameter has no defaultValue");
        put(javacli.Exceptions.DefaultForMultipleArgsException, "options with defaultValue are not allowed to have multiple arguments");
        put(javacli.Exceptions.DefaultForZeroArgsException, "options with defaultValue are not allowed to have zero arguments");
        put(javacli.Exceptions.DuplicateOptionNamesException, "two or more options have the same name");
        put(javacli.Exceptions.DuplicateOptionAbbreviationsException, "two or more options have the same abbreviation");
        put(javacli.Exceptions.DuplicateArgumentNamesException, "two or more arguments have the same name");
        put(javacli.Exceptions.CountingNonMultipleException, "an option with the count parameter is must be allowed to have multiple uses");
        put(javacli.Exceptions.VariableNameNotMatchingOptionException, "an option or argument annotation name does not match the variable name it is attached to");
        put(javacli.Exceptions.VariableTypeNotMatchingOptionException, "an option or argument annotation type does not match the variable type it is attached to");
        put(javacli.Exceptions.UncastableOptionTypeException, "the return data type could not be cast to the option or argument annotation type");
        // Option parse errors
        put(javacli.Exceptions.NoSuchOptionException, "a specified option does not exist");
        put(javacli.Exceptions.ExtraneousArgumentException, "more arguments than are required have been specified");
        put(javacli.Exceptions.MissingArgumentException, "less arguments than are required have been specified");
        put(javacli.Exceptions.MultipleNotAllowedException, "multiple uses of an option are not allowed");
    }};


    // ----------------------------------------------------------------------------------------------------
    // public OptionException
    //
    // OptionException constructor
    //
    // Arguments--
    //
    // classThrowingError:  the class that threw the error
    //
    // error:               the error being thrown
    //
    public OptionException(Class<?> classThrowingError, Exceptions error, String where) {
        System.out.println(classThrowingError.getName() + " ERROR: " + error.name() + ": " + ExceptionLookup.get(error) + "\n\tin " + where + "\n");
    }
    // end: public OptionException

}
// end: public class OptionDefinitionException