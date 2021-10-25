# Java-CLI
A light-weight command line parser library for Java


# Installation
To add Java-CLI to a project, download the project files and move the javacli.jar file into the project of choice.\
The project can now be compiled by specifying the path to the jar like so: ```javac -cp /path/to/javacli.jar source-files.java```\
Alternatively, a shebang line can be used in the main .java file to allow the project to be run directly. An example shebang looks like: ```#!/usr/bin/java --source 17 --class-path /path/to/javacli.jar```


# Features
## Overview
| Name     | Creator        | First Release | Latest Stable Version |
| -------- | -------------- | ------------- | --------------------- |
| Java-CLI | Jonathan Uhler | 7/28/2021     | full-1.0.0            |

## General Features
| Generates Help/Usage | Customizable Help/Usage | Dependencies | User Docs |
| -------------------- | ----------------------- | ------------ | --------- |
| yes                  | no                      | -            | readme    |

## POSIX Conventions Adherence
| Short Option Groups (-abc) | No Spaces (-oarg) | Equals Support (--opt=val) | Counting/Multiple Support | "--" Special Option |
|--------------------------- | ----------------- | -------------------------- | ------------------------- | ------------------- |
| yes                        | yes               | yes                        | only for JDK 8 and below  | yes                 |

## Valid Short Option Syntax
| Number of Arguments | Valid Usage Cases
| ------------------- | ------------------------------
| 0                   | -o
| 1                   | -o1<br>-o 1<br>-o=1
| more than 1; fixed  | -o 1 2 3<br>-o 1,2,3<br>-o=1,2,3
| variable            | -o1<br>-o 1<br>-o=1<br>-o 1,2,3<br>-o=1,2,3

## Valid Long Option Syntax
| Number of Arguments | Valid Usage Cases
| ------------------- | ------------------------------
| 0                   | --opt
| 1                   | --opt 1<br>--opt=1
| more than 1; fixed  | --opt 1 2 3<br>--opt 1,2,3<br>--opt=1,2,3
| variable            | --opt 1<br>--opt=1<br>--opt 1,2,3<br>--opt=1,2,3


# Usage
## Annotations
Java-CLI contains four types to annotations that can be used. An ```@Option``` annotation, ```@Argument``` annotation, ```@Command``` annotation, and a ```@Version``` annotation.

### @Option
Specifies an option the user can include when running the script.

| Parameter    | Description                                                        | Required | Default      | Prerequisites
| ------------ | ------------------------------------------------------------------ | -------- | ------------ | ---------------------------------------------------------------------
| name         | The long name of the option                                        | yes      | -            | Must be the same as the variable name the annotation is attached to
| abbreviation | The short name of the option                                       | no       | \u000        | -
| help         | The statement describing the option                                | no       | ""           | -
| nargs        | The number of expected arguments                                   | no       | 0            | Must be >= -1 (-1 = variable # of args). If > 0, isFlag must be false
| type         | The expected type of the arguments                                 | no       | String.class | Must be the same as the variable type the annotation is attached to
| multiple     | Whether multiple uses of the option are allowed                    | no       | false        | -
| defaultValue | The default value for the arguments if the option is not specified | no       | ""           | nargs == 1
| showDefault  | Whether the default value should be shown in --help                | no       | false        | defaultValue has been specified
| isFlag       | If the option is a flag (has no arguments)                         | no       | false        | nargs == 0, variable type of the annotation must be boolean
| ~~doCount~~  | ~~Whether the number of times the option is used should be counted~~| no       | false        | ~~multiple == true~~

**Note**: the ```doCount``` parameter is deprecated in JDK 9 and above as the ```setAccessible()``` method no longer works.\
A solution to this problem is currently being worked on

### @Argument
| Parameter | Description                                     | Required | Default      | Prerequisites
| --------- | ----------------------------------------------- | -------- | ------------ | -------------------------------------------------------------------
| name      | The name of the argument (to display on --help) | yes      | -            | -
| type      | The excepted type of the argument               | no       | String.class | Must be the same as the variable type the annotation is attached to

### @Command
| Parameter | Description                                     | Required | Default      | Prerequisites
| --------- | ----------------------------------------------- | -------- | ------------ | -------------------------------------------------------------------
| name      | The name of the command (to display on --help)  | yes      | -            | -
| help      | The statement describing the command            | no       | ""           | -

### @Version
| Parameter    | Description                                       | Required | Default      | Prerequisites |
| ------------ | ------------------------------------------------- | -------- | ------------ | ------------- |
| version      | The version number to print at --version          | yes      | -            | -             |
| abbreviation | The abbreviation for the --version special option | no       | \u000        | -             |

To create options, add an annotation to a variable with the same type as the expected argument type (or boolean if no argument is expected) and the same name as the option name:

```java
@Option (name = "number", abbreviation = 'n', help = "Specifies a number.", nargs = 1,  type = Integer.class) public int number;
```


## Example

```java
import javacli.annotations.Argument;
import javacli.annotations.Option;
import javacli.annotations.Version;
import javacli.OptionParser;

import java.util.List;


// Example class
public class Example {
    
    // Define options
    // ------------------
    // foo -> a boolean flag with no arguments
    // output -> an example option with 1 argument and a default value
    // many -> an option with 3 arguments
    //
    @Option(name = "foo", abbreviation = 'f', help = "example flag option option", isFlag = true, type = boolean.class) public static boolean foo;
    @Option(name = "output", abbreviation = 'o', help = "Set output file.", nargs = 1, defaultValue = "/path/") public static String output;
    @Option(name = "many", abbreviation = 'm', help = "An option with many arguments", nargs = 3, type = double.class) public static List<Double> many;
    
    // Define version option
    @Version(version = "1.0.0", abbreviation = 'v') public static String version;
    
    // Define command line arguments
    // ------------------
    // input -> a required string
    // number -> a required integer
    //
    @Argument(name = "input") public static String input;
    @Argument(name = "number", type = Integer.class) public static Integer number;

    // main method
    public static void main(String[] args) throws Exception {
        // Create a new parser, passing in a reference to the class with the annotations
        OptionParser parser = new OptionParser(Example.class);
        parser.parse(args); // Parse with the String[] args from java

        
        // Do things with the option and argument values
        System.out.println("foo has been specified: " + foo); // True if specified, false otherwise
        System.out.println("output's value is: " + output); // If an option with args isn't specified it will be null if a defaultValue also hasn't been specified
        System.out.println("input is: " + input + ", number is: " + number + " and number is type " + number.getClass());
        
        double sum = 0;
        for (double i : many) {
            sum += i;
        }
        System.out.println("The sum of many's elements is: " + sum);
        

    }

}
```


# Possible Questions
* [How do I specify an option with a variable number of arguments?](#How-do-I-specify-an-option-with-a-variable-number-of-arguments?)
* [How do I specify sub commands?](#How-do-I-specify-sub-commands?)

## How do I specify an option with a variable number of arguments?
Java-CLI supports options with a variable number of arguments (>=1 args).\
To define an option as being able to take a variable number of arguments, use "-1" for the "nargs" parameter.
```java
    @Option(name = "variable", abbreviation = 'v', help = "An option with a variable number of arguments", nargs = -1, type = double.class) public static List<Double> variable;
```

## How do I specify sub commands?
Java-CLI supports commands of commands, or sub-commands. \
Each sub-command must be defined as an independent class with its own option and argument annotations as desired. \
In order to parse sub-commands, create your instance of the ```OptionParser``` class with an ```ArrayList<Class<?>>``` of option definition classes rather than just one (note the first element of this array is treated as the "top level" command).
```java
ArrayList<Class<?>> commands = new ArrayList<>();
commands.add(Example.class);
commands.add(Example2.class);
        
OptionParser parser = new OptionParser(commands);
```