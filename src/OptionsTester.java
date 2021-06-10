import javacli.OptionParser;

public class OptionsTester {
    
    public static void main(String[] args) {

        OptionParser parser = new OptionParser(OptionsTest.class, "OptionsTester", "1.0.0", "java OptionsTester [-Vp] [-o <file>] [--test <arg> <arg2>]"); // "OptionsTester", "1.0.0", "java OptionsTester [-V] [--test]"
        parser.parseAndExitUponError(args);

    }

}
