import javacli.Option;


public class OptionsTest {

        @Option (
                name = "test",
                abbreviation = 't',
                help = "test option",
                numArgs = 2,
                argDescriptions = {"arg", "arg2"},
                argRegex = {"[a-zA-Z0-9]+", "[a-zA-Z0-9]+"}
        ) public String[] test;

        @Option (
                name = "version",
                abbreviation = 'V',
                help = "prints the version and exits"
        ) public String version;

        @Option (
                name = "opt",
                abbreviation = 'p',
                help = "second test option"
        ) public String opt;

        @Option(
                name = "output",
                help = "set output file",
                abbreviation = 'o',
                numArgs = 1,
                argDescriptions = {"file"},
                argRegex = {"[a-zA-Z0-9]+"}
        ) public String[] output;

}
