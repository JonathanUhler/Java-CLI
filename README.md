# Java-CLI
A light-weight command line parser library for Java


# Installation
To add to a project, download the project files and move the javacli/ directory into the project of choice


# Usage
To use the library, create a stand-alone class that defines the desired ```@Option```s.

```java
import javacli.Option;

// public class Options
//
// Stand-alone class containing command line options for the user to specify
//
public class Options {

        @Option (
               	name = "help",
               	abbreviation = 'h',
               	help = "prints useful information about the options and arguments"
        ) public String help;

        @Option (
               	name = "version",
               	abbreviation = 'V',
               	help = "prints the version and exits"
        ) public String version;

        @Option (
               	name = "output",
               	abbreviation = 'o',
               	help = "specifies an optional output file",
		numArgs = 1,
		argDescriptions = {"file"},
		argRegex = {"[a-zA-Z0-9]+"}
        ) public String output;

        @Option(
                	name = "verbose",
                	help = "prints verbose debug"
        ) public boolean verbose;

}

```

In a separate class, create a new ```OptionParser``` object and call its public ```parseAndExitUponError``` method with the ```String[] args``` from Java's main method.

```java
import javacli.OptionParser;

// public class Example
//
// Runner/entry class for the command line app
//
public class Example {
    
    public static void main(String[] args) {

	// Create a new parser object
	// Must specify: the stand-alone class with the option definitions (in this case "Options.class")
	// Can optionally specify: name of the app, version of the app, usage statement
        	OptionParser parser = new OptionParser(Options.class, "Example", "1.0.0", "java Example [-Vh] [-o <file>] [--verbose]");
	
	// Get parsed options
	Options options = parser.getOptions(Options.class);

	// Call the parse method with the String[] args from the main() method
        	parser.parseAndExitUponError(args);

	// Do stuff with parsed options here...
	System.out.println("output file: " + options.output + (options.verbose) ? " (this is the output file)" : "");

    }

}

```
