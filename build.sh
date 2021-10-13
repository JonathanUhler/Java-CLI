javac -d ./src/jv $(find ./src/ -name '*.java')
cd src/jv
jar -cf ../../release/javacli.jar ./javacli