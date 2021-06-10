// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// Option.java
// Java-CLI
//
// Created by Jonathan Uhler on 6/5/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package javacli;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public interface Option
//
// Command-line option skeleton
public @interface Option {

    // ====================================================================================================
    // String name
    //
    // Name parameter for an option
    //
    String name();
    // end: String name


    // ====================================================================================================
    // char abbreviation
    //
    // Name abbreviation for an option
    //
    char abbreviation() default Character.MIN_VALUE;
    // end: char abbreviation


    // ====================================================================================================
    // String help
    //
    // Help message for an option
    //
    String help();
    // end: String help


    // ====================================================================================================
    // int numArgs
    //
    // Number of required arguments for an option
    //
    int numArgs() default 0;
    // end: int numArgs


    // ====================================================================================================
    // String[] argDescriptions
    //
    // Descriptions for required arguments for an option
    //
    String[] argDescriptions() default {};
    // end: String[] argDescriptions


    // ====================================================================================================
    // String[] argRegex
    //
    // Regex for required arguments for an option
    //
    String[] argRegex() default {};
    // end: String[] argRegex


    // ====================================================================================================
    //
    // Default values for required arguments for an option
    //
    String[] defaultValue() default {};
    // end: String[] defaultValue

}
// end: public interface option