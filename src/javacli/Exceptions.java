// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// Exceptions.java
// Java-CLI
//
// Created by Jonathan Uhler on 6/25/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package javacli;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public enum Exceptions
//
// List of exception types
//
public enum Exceptions {
    // Option definition errors
    FlagWithNonZeroArgsException, // nargs > 0 and isFlag == true
    NonFlagWithZeroArgsException, // nargs == 0 and isFlag == false
    NoArgsOptionNotBooleanException, // nargs == 0 and type != boolean
    NoDefaultToShowException, // showDefault == true and defaultValue == ""
    DefaultForMultipleArgsException, // defaultValue != "" and nargs > 1
    DefaultForZeroArgsException, // defaultValue != "" and nargs == 0
    DuplicateOptionNamesException, // duplicate names
    DuplicateOptionAbbreviationsException, // duplicate abbrev
    DuplicateArgumentNamesException, // duplicate names
    CountingNonMultipleException, // count == true and multiple == false
    VariableNameNotMatchingOptionException, // Variable name with annotation != name
    VariableTypeNotMatchingOptionException, // Variable type with annotation != type
    UncastableOptionTypeException,
    // Option parse errors
    NoSuchOptionException,
    ExtraneousArgumentException,
    MissingArgumentException,
    MultipleNotAllowedException,
}
// end: public enum Exceptions
