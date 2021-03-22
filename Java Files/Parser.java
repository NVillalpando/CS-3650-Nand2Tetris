import java.util.Scanner;

public class Parser {
    Scanner reader;
    String currentCommand;

    // Passes in the file and stores it. Also sets up the scanner for future use.
    public Parser(Scanner reader) {
        this.reader = reader;
    }

    // Checks for any remaining commands in the file.
    public boolean hasMoreCommands() {
        return reader.hasNextLine();
    }

    // Read next command from input and then makes it the current command. Should be called if hasMoreCommands is true. In the beginning there is no current command.
    public void advance() {
        this.currentCommand = reader.nextLine();
    }

    public String getCurrentCommand() {
        return currentCommand;
    }

    // Returns the type of the current command.
    // A_COMMAND: Case of @XXX where XXX is a symbol or a decimal number
    // C_COMMAND: Case of dest=comp;jump
    // L_COMMAND: Case of a pseudo command for XXX where XXX is a symbol
    public String commandType() {
        // Checks to see if there is instructions on the line
        if (currentCommand.length() == 0) {
            return "empty";
        }
        // Removes all white space on the line
        currentCommand = currentCommand.replace(" ", "");

        // First attempts to determine if the command is a comment, if so returns that it is.
        if (currentCommand.contains("//")) {
            int commentIndex = currentCommand.indexOf("//");
            currentCommand = currentCommand.substring(0, commentIndex);
            if (currentCommand.length() == 0) {
                return "comment";
            }
        }
        // Begin check for A_COMMAND
        if (currentCommand.charAt(0) == '(') {
            return "L";
        }
        // Check for L_COMMAND based on if the instruction has a parenthesis.
        else if (currentCommand.contains("" + '@')) {
            return "A";
        }
        // If it doesn't match prior checks, its a C_COMMAND.
        return "C";
    }

    public String symbol(String XXX) {
        // Assumption that after the @ symbol, XXX is a decimal.
        int i = Integer.parseInt(XXX);
        String binaryNum = Integer.toBinaryString(i);

        // returns the binary num completely adjusted for.
        return "0".repeat(Math.max(0, 16 - binaryNum.length())) + binaryNum;
    }

    public String dest() {
        // Determines if a dest exists, if not then dest is returned as 000 (null)
        if (currentCommand.contains("" + "=")) {
            int equalsIndex = currentCommand.indexOf("=");
            return Code.dest(currentCommand.substring(0, equalsIndex));
        }
        return "000";
    }

    public String comp() {
        // comp must always exist, therefore it is important to see where the dest is based on characters ; or =
        // First check based on there being an equals sign, if this fails then its assumed line has ;.
        if (currentCommand.contains("" + "=")) {
            int equalsIndex = currentCommand.indexOf("=");
            return Code.comp(currentCommand.substring(equalsIndex + 1));
        }
        int semicolonIndex = currentCommand.indexOf(";");
        return Code.comp(currentCommand.substring(0, semicolonIndex));
    }

    public String jump() {
        // Determines if a jump exists, if not then jump is returned as 000 (null)
        if (currentCommand.contains("" + ";")) {
            int semicolonIndex = currentCommand.indexOf(";");
            return Code.jump(currentCommand.substring(semicolonIndex + 1));
        }
        return "000";
    }
}