import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Scanner;

public class ParserVM {
    Scanner reader;
    String currentLine;
    String[] currentCommands;
    Hashtable<String, String> commandTypes;

    public ParserVM(File file) throws FileNotFoundException {
        this.reader = new Scanner(file);
        this.currentLine = "";
        this.commandTypes = new Hashtable<>();

        commandTypes.put("add", "arithmetic");
        commandTypes.put("sub", "arithmetic");
        commandTypes.put("neg", "arithmetic");
        commandTypes.put("eq", "arithmetic");
        commandTypes.put("gt", "arithmetic");
        commandTypes.put("lt", "arithmetic");
        commandTypes.put("and", "arithmetic");
        commandTypes.put("or", "arithmetic");
        commandTypes.put("not", "arithmetic");
        commandTypes.put("push", "push");
        commandTypes.put("pop", "pop");
        commandTypes.put("label", "label");
        commandTypes.put("goto", "goto");
        commandTypes.put("if-goto", "if-goto");
        commandTypes.put("call", "call");
        commandTypes.put("return", "return");
        commandTypes.put("function", "function");
    }

    public boolean hasMoreCommands() {
        return reader.hasNextLine();
    }

    // Advances current line and adds command to the command array if the line is not a comment.
    public void advance() {
        this.currentLine = reader.nextLine();
        if (currentLine.contains("//")) {
            int commentIndex = currentLine.indexOf("//");
            currentLine = currentLine.substring(0, commentIndex);
        }
        this.currentCommands = currentLine.split(" ");
        for (int i = 0; i < currentCommands.length; i++) {
            currentCommands[i] = currentCommands[i].replaceAll("\\s+", "");
        }
    }

    // Return type of the current VM command.
    public String commandType() {
        if (currentLine.length() == 0) {
            return "empty";
        }
        return commandTypes.get(currentCommands[0]);
    }

    public String getCurrentLine(){
        return currentLine;
    }

    public String arg0() {return currentCommands[0];}

    // Return the first argument of the current command. Shouldn't be called if current command is C_RETURN.
    public String arg1() {
        return currentCommands[1];
    }

    // Returns the second argument of the current command.
    // Not called if the current command is C_PUSH, C_POP, C_FUNCTION, or C_CALL.
    public int arg2() {
        return Integer.parseInt(currentCommands[2]);
    }
}
