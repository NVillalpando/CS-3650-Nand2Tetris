import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class HackAssembler {

    public static void main(String[] args) throws IOException {

        // Gets file from the user. This program assumes the file is in the same directory as this file.
        String userFile = args[0];
        String workingDir = new File(".").getCanonicalPath();

        // Creates a new file that will be contain the hack code. File will be in same directory as this file.
        File hack = new File(userFile.replace(".asm", ".hack"));
        hack.createNewFile();
        FileWriter hackWriter = new FileWriter(userFile.replace(".asm", ".hack"));

        // Begin while loop for the symbols table (first pass)
        SymbolTable table = new SymbolTable();
        Scanner symbolReader = new Scanner(new File(workingDir + "\\" + userFile));
        Parser symbolParser = new Parser(symbolReader);
        int counter = 0;
        while (symbolParser.hasMoreCommands()) {
            symbolParser.advance();
            String command = symbolParser.commandType();

            if (command.equals("C") || command.equals("A")) {
                counter++;
            } else if (command.equals("L")) {
                int closingParenthesis = symbolParser.getCurrentCommand().indexOf(")");
                String XXX = symbolParser.getCurrentCommand().substring(1, closingParenthesis);
                table.addEntry(XXX, counter);
            }
        }

        // Sets up parser once more for the second pass along with other changes.
        String instructionBits;
        Scanner reader = new Scanner(new File(workingDir + "\\" + userFile));
        Parser parser = new Parser(reader);
        int n = 16;

        while (parser.hasMoreCommands()) {
            parser.advance();
            String command = parser.commandType();

            // If the command is a c-instruction, translates instruction as is.
            if (command.equals("C")) {
                instructionBits = "111" + parser.comp() + parser.dest() + parser.jump();
                hackWriter.write(instructionBits + "\n");
            }

            // If the command is an a-instruction, begins task of translating instruction.
            else if (command.equals("A")) {
                // Begin check to see if the symbol is a value in the symbols table.
                String symbolCheck = parser.getCurrentCommand().substring(1);

                // Tries to parse @XXX as an int, if it succeeds then it converts the A-instruction as usual.
                try {
                    Integer.parseInt(symbolCheck);
                    instructionBits = parser.symbol(symbolCheck);
                    hackWriter.write(instructionBits + "\n");
                }
                // This chunk of code gets run if @XXX is a symbol and not a number.
                catch (Exception NumberFormatException) {
                    // Test to see if the table contains XXX, if so then it retrieves the address and uses it for instruction translation.
                    boolean test = table.contains(symbolCheck);
                    if (test) {
                        int symbol = table.getAddress(symbolCheck);
                        instructionBits = parser.symbol(Integer.toString(symbol));
                        hackWriter.write(instructionBits + "\n");
                    }
                    // If the table doesn't contain XXX, retrieves n and uses it for instruction translation and increments n.
                    else {
                        table.addEntry(symbolCheck, n);
                        instructionBits = parser.symbol(Integer.toString(n));
                        hackWriter.write(instructionBits + "\n");
                        n++;
                    }
                }
            }
        }
        hackWriter.close();
    }

}