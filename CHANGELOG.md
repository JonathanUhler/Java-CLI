# Java-CLI Changelog

Project created 6/5/21 -- Changelog begin:

FULL-RELEASES--

	version   date          changes
	------- --------    ----------------------------------------------------------------------------------------------------
	1.0.0   7/28/21     Refactored, first working version of Java-CLI

    1.0.1   7/29/21     Changes in this version:
                            -Added a jar file for easier use

    1.0.2   7/29/21     Changes in this version:
                            -Fixed a minor issue with the @Version annotation

    1.1.0   10/12/21    Changes in this version:
                            -Refactored package structure
                            -Improved documentation
                            -Improved legal and illegal POSIX use cases for options
                            -Temporarily deprecated the "doCount" parameter for options for JDK 9+

    2.0.0   10/24/21    Changes in this version:
                            -Added the @Command annotation
                            -Added support for sub-commands

    2.0.1   10/24/21    Changes in this version:
                            -Fixed a minor issue with sub-command formatting

    2.0.2   12/26/21    Changes in this version:
                            -Added support for hyphen characters ("-") in option names (ex: "test-opt" instead of "testopt")