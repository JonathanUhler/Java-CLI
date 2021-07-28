# Java-CLI
A light-weight command line parser library for Java


# Installation
To add Java-CLI to a project, download the project files and move the javacli/ directory into the project of choice


# Features
## Overview
| Name     | Creator        | First Release | Latest Stable Version |
| -------- | -------------- | ------------- | --------------------- |
| Java-CLI | Jonathan Uhler | -             | -                     |

## General Features
| Generates Help/Usage | Customizable Help/Usage | Dependencies | User Docs |
| -------------------- | ----------------------- | ------------ | --------- |
| yes                  | no                      | -            | readme    |

## POSIX Conventions Adherence
| Short Option Groups (-abc) | No Spaces (-oarg) | Equals Support (--opt=val) | Counting/Multiple Support | "--" Special Option |
|--------------------------- | ----------------- | -------------------------- | ------------------------- | ------------------- |
| yes                        | yes               | yes                        | yes                       | yes                 |


# Usage
## Annotations
Java-CLI contains three types to annotations that can be used. An ```@Option``` annotation, ```@Argument``` annotation, and a ```@Version``` annotation.

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
| isFlag       | If the option is a flag (has no arguments)                         | no       | false        | nargs == 0
| doCount      | Whether the number of times the option is used should be counted   | no       | false        | multiple == true

### @Argument
| Parameter | Description                                     | Required | Default      | Prerequisites
| --------- | ----------------------------------------------- | -------- | ------------ | -------------------------------------------------------------------
| name      | The name of the argument (to display on --help) | yes      | -            | -
| type      | The excepted type of the argument               | no       | String.class | Must be the same as the variable type the annotation is attached to

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
import javacli.Argument;
import javacli.Option;
import javacli.OptionParser;
import javacli.Version;
import java.util.List;

public class Example {
    
    // Define options
    @Option(name = "foo", abbreviation = 'f', help = "foo option", isFlag = true) public static boolean foo;
    @Option(name = "output", abbreviation = 'o', help = "Set output file.", nargs = 1, defaultValue = "/path/") public static String output;
    @Option(name = "many", abbreviation = 'm', help = "An option with many arguments", nargs = 3, type = double.class) public static List<Double> many;
    
    // Define version special option
    @Version(version = "1.0.0", abbreviation = 'v') public static String version;
    
    // Define arguments
    @Argument(name = "input") public static String input;
    @Argument(name = "number", type = Integer.class) public static Integer number;

    // main class
    public static void main(String[] args) {

        // Create a new parser
        OptionParser parser = new OptionParser(javaclitest.class);
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