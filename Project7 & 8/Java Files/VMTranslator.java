import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class VMTranslator {

    public static void main(String[] args) throws IOException {
        // cast user string as a file and setup of other variables to be used.
        File userInput = new File(args[0]);
        String userPath = userInput.getAbsolutePath();
        ArrayList<File> vmFiles = new ArrayList<>();

        // check to see if the user input is a file, if so performs functions under such assumption.
        // program also assumes that there exists a file in the location, if not an error will be thrown.
        if (userInput.isFile()) {
            vmFiles.add(userInput);
        }

        // if prior check failed, assumes the input is a directory and gets all vm extension files.
        else {
            File[] allFiles = userInput.listFiles();
            assert allFiles != null;
            for (File f: allFiles) {
                if (f.getName().endsWith(".vm")) {
                    vmFiles.add(f);
                }
            }
        }

        // sets up file output path for file creation.
        CodeWriterVM writer = new CodeWriterVM(new File(userPath));

        // sets file name based off of directory name and determines if init needs to be written.
        writer.setFileName(vmFiles.get(0).getParent().substring(vmFiles.get(0).getParent().lastIndexOf("\\") + 1) + ".asm");
        for (File vmFile : vmFiles) {
            if (vmFile.getName().equals("Sys.vm")) {
                writer.writeInit();
            }
        }

        // begin for loop for all available files to translate.
        // within each for lies a while loop which goes through all available lines of a file.
        for (File f: vmFiles){
            ParserVM parser = new ParserVM(f);
            while (parser.hasMoreCommands()) {
                parser.advance();
                String commandType = parser.commandType();

                // if the command is arithmetic, there won't be arg1 or arg2 in the line. comments and white space ignored.
                switch (commandType) {
                    case "arithmetic" -> {
                        writer.quickWrite("// " + parser.getCurrentLine() + "\n");
                        writer.writeArithmetic(parser.arg0());
                    }
                    case "push", "pop" -> {
                        writer.quickWrite("// " + parser.getCurrentLine() + "\n");
                        writer.writePushPop(commandType, parser.arg1(), parser.arg2(), f.getName().replace(".vm", ""));
                    }
                    case "label" -> {
                        writer.quickWrite("// " + parser.getCurrentLine() + "\n");
                        writer.writeLabel(parser.arg1());
                    }
                    case "goto" -> {
                        writer.quickWrite("// " + parser.getCurrentLine() + "\n");
                        writer.writeGoto(parser.arg1());
                    }
                    case "if-goto" -> {
                        writer.quickWrite("// " + parser.getCurrentLine() + "\n");
                        writer.writeIf(parser.arg1());
                    }
                    case "call" -> {
                        writer.quickWrite("// " + parser.getCurrentLine() + "\n");
                        writer.writeCall(parser.arg1(), parser.arg2());
                    }
                    case "return" -> {
                        writer.quickWrite("// " + parser.getCurrentLine() + "\n");
                        writer.writeReturn();
                    }
                    case "function" -> {
                        writer.quickWrite("// " + parser.getCurrentLine() + "\n");
                        writer.writeFunction(parser.arg1(), parser.arg2());
                    }
                }
            }
        }
        writer.close();
    }
}
