package javacli.parser;


import javacli.OptionParser;
import javacli.annotations.Argument;
import javacli.annotations.Option;
import javacli.helper.CLIHelper;

import java.util.ArrayList;
import java.util.Collections;


public class AnnotationSyntax {

    // ====================================================================================================
    // private void parseOptionDefinitions
    //
    // Parses option definitions for syntax mistakes
    //
    // Arguments--
    //
    // None
    //
    // Return--
    //
    // None
    //
    public void parseOptionDefinitions(OptionParser optionParser) throws Exception {
        ArrayList<Option> opts = optionParser.getOptions();
        ArrayList<Argument> args = optionParser.getArguments();
        ArrayList<String> optNames = optionParser.getOptionNames();
        ArrayList<Character> optAbbrevs = optionParser.getOptionAbbreviations();
        ArrayList<String> argNames = optionParser.getArgumentNames();
        ArrayList<String> optFldNames = optionParser.getOptionFieldNames();
        ArrayList<Class<?>> optFldTypes = optionParser.getOptionFieldTypes();
        ArrayList<String> argFldNames = optionParser.getArgumentFieldNames();
        ArrayList<Class<?>> argFldTypes = optionParser.getArgumentFieldTypes();

        // Parse option definitions
        for (int i = 0; i < opts.size(); i++) {
            Option o = opts.get(i);
            String name = o.name();
            char abbrev = o.abbreviation();
            Class<?> type = o.type();
            String oFldName = optFldNames.get(i);
            Class<?> oFldType = optFldTypes.get(i);

            CLIHelper.cliAssert((oFldName.equals(name)),
                    "variable name does not match option name",
                    "in variable " + oFldName + " does not match option " + name);

            CLIHelper.cliAssert((oFldType.equals(type) ||
                    (o.nargs() > 1)),
                    "variable type does not match option type",
                    "variable " + oFldName + " has type " + oFldType,
                    "option " + name + " expects type " + type);

            CLIHelper.cliAssert((o.nargs() != 0) ||
                    (o.isFlag()),
                    "a non-boolean flag option was declared with 0 arguments",
                    "in option " + name);

            CLIHelper.cliAssert((!o.isFlag()) ||
                    (o.nargs() == 0),
                    "a boolean flag option was declared with other than 0 arguments",
                    "in option " + name + " is declared with " + o.nargs() + " arguments");

            CLIHelper.cliAssert((!o.showDefault()) ||
                    (!o.defaultValue().equals("")),
                    "showDefault is true but no defaultValue is specified",
                    "for option " + name);

            CLIHelper.cliAssert((o.defaultValue().equals("")) ||
                    (o.nargs() == 1),
                    "a defaultValue was specified for an option with other than 1 argument",
                    "in option " + name);

            CLIHelper.cliAssert((!o.multiple()) ||
                    (o.doCount()),
                    "doCount was true for an option that does not allow multiple occurrences",
                    "in option " + name);

            CLIHelper.cliAssert((Collections.frequency(optNames, name) == 1),
                    "an option name was used more than once",
                    "for option name " + name);

            CLIHelper.cliAssert((Collections.frequency(optAbbrevs, abbrev) == 1),
                    "an option abbreviation (short name) was used more than once",
                    "for short option " + abbrev);

            CLIHelper.cliAssert((o.nargs() != 0) ||
                    (type == boolean.class || type == Boolean.class),
                    "an option with 0 arguments is not boolean type",
                    "in option " + name + ", given type was " + type);
        }

        // Parse argument definitions
        for (int i = 0; i < args.size(); i++) {
            Argument a = args.get(i);
            String name = a.name();
            Class<?> type = a.type();
            String aFldName = argFldNames.get(i);
            Class<?> aFldType = argFldTypes.get(i);

            CLIHelper.cliAssert((aFldName.equals(name)),
                    "variable name does not match argument name",
                    "in variable " + aFldName + " does not match argument " + name);

            CLIHelper.cliAssert((aFldType.equals(type)),
                    "variable type does not match argument type",
                    "variable " + aFldName + " has type " + aFldType,
                    "argument " + name + " expects type " + type);

            CLIHelper.cliAssert((Collections.frequency(argNames, name) == 1),
                    "an argument name was used more than once",
                    "in argument " + name);
        }
    }
    // end: private void parseOptionDefinitions

}
