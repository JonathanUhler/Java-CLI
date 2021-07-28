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


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public interface Option
//
// Command-line option
//
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Option {

    String name(); // Specifies the full name

    char abbreviation() default Character.MIN_VALUE; // Specifies the abbreviated name

    String help() default ""; // Specifies the help message to print for the option

    int nargs() default 0; // Specifies the expected number of arguments

    Class<?> type() default String.class; // Specifies the expected type of the arguments

    boolean multiple() default false; // Specifies if multiple instances of the option are allowed

    String defaultValue() default ""; // Specifies the default value for the option

    boolean showDefault() default false; // Specifies if the default value should be shown in the help message

    boolean isFlag() default false; // Specifies if the option is a flag (one that has 0 arguments. Obvious case being --version)

    boolean doCount() default false; // Specifies if the number of times the option shows up should be counted (obvious case being --verbose)

    int _countValue() default 0; // The number of times the option has been included, if doCount == true

}
// end: public interface option