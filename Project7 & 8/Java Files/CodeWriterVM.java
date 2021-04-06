import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CodeWriterVM {
    String fileOutput;
    int jumpCounter;
    int labelCounter;
    FileWriter writer;

    // Setup file writing, sets output location.
    public CodeWriterVM(File fileOutput) {
        this.fileOutput = fileOutput + "\\";
    }

    // Sets the file name of the output file and creates a new file.
    public void setFileName(String fileName) throws IOException {
        this.jumpCounter = 0;
        this.labelCounter = 0;
        File asm = new File(this.fileOutput + fileName);
        asm.createNewFile();
        this.writer = new FileWriter(this.fileOutput + fileName);
    }

    // Writes the assembly code that is the translation for arithmetic commands.
    public void writeArithmetic(String command) throws IOException {
        String hackLine = "";
        switch (command) {
            // translates the addition of two values currently in the stack. broken up for readability & similar to rest
            case "add" -> {
                hackLine += "@SP\n";
                hackLine += "AM=M-1\n";
                hackLine += "D=M\n";
                hackLine += "A=A-1\n";
                hackLine += "M=D+M\n";
                writer.write(hackLine);
            }
            case "sub" -> {
                hackLine += "@SP\n";
                hackLine += "AM=M-1\n";
                hackLine += "D=M\n";
                hackLine += "A=A-1\n";
                hackLine += "M=M-D\n";
                writer.write(hackLine);
            }
            case "neg" -> {
                hackLine += "@SP\n";
                hackLine += "A=M-1\n";
                hackLine += "D=0\n";
                hackLine += "M=D-M\n";
                writer.write(hackLine);
            }
            case "eq" -> {
                // jump counter is used to infinitely perform conditional checks. also some in depth talk for this deeper command and similar
                hackLine += "@SP\n";
                hackLine += "AM=M-1\n";
                hackLine += "D=M\n";
                hackLine += "A=A-1\n";
                hackLine += "D=M-D\n";  // both values have been subtracted by each other and will be used in jump.
                hackLine += "@TRUE" + jumpCounter + "\n";
                hackLine += "D;JEQ\n";  // checks to see if difference is equal to 0. if not, assigns a 0 to stack.
                hackLine += "@SP\n";
                hackLine += "A=M-1\n";
                hackLine += "M=0\n";
                hackLine += "@CONTINUE" + jumpCounter + "\n";
                hackLine += "0;JMP\n";    // unconditional jump to continue to rest of code.
                hackLine += "(TRUE" + jumpCounter + ")\n";
                hackLine += "@SP\n";    // jumped here if both values equal each other. assigns a -1 to the stack
                hackLine += "A=M-1\n";
                hackLine += "M=-1\n";
                hackLine += "(CONTINUE" + jumpCounter + ")\n";
                jumpCounter++;
                writer.write(hackLine);
            }
            case "gt" -> {
                hackLine += "@SP\n";
                hackLine += "AM=M-1\n";
                hackLine += "D=M\n";
                hackLine += "A=A-1\n";
                hackLine += "D=M-D\n";
                hackLine += "@TRUE" + jumpCounter + "\n";
                hackLine += "D;JGT\n";
                hackLine += "@SP\n";
                hackLine += "A=M-1\n";
                hackLine += "M=0\n";
                hackLine += "@CONTINUE" + jumpCounter + "\n";
                hackLine += "0;JMP\n";
                hackLine += "(TRUE" + jumpCounter + ")\n";
                hackLine += "@SP\n";
                hackLine += "A=M-1\n";
                hackLine += "M=-1\n";
                hackLine += "(CONTINUE" + jumpCounter + ")\n";
                jumpCounter++;
                writer.write(hackLine);
            }
            case "lt" -> {
                hackLine += "@SP\n";
                hackLine += "AM=M-1\n";
                hackLine += "D=M\n";
                hackLine += "A=A-1\n";
                hackLine += "D=M-D\n";
                hackLine += "@TRUE" + jumpCounter + "\n";
                hackLine += "D;JLT\n";
                hackLine += "@SP\n";
                hackLine += "A=M-1\n";
                hackLine += "M=0\n";
                hackLine += "@CONTINUE" + jumpCounter + "\n";
                hackLine += "0;JMP\n";
                hackLine += "(TRUE" + jumpCounter + ")\n";
                hackLine += "@SP\n";
                hackLine += "A=M-1\n";
                hackLine += "M=-1\n";
                hackLine += "(CONTINUE" + jumpCounter + ")\n";
                jumpCounter++;
                writer.write(hackLine);
            }
            case "and" -> {
                hackLine += "@SP\n";
                hackLine += "AM=M-1\n";
                hackLine += "D=M\n";
                hackLine += "A=A-1\n";
                hackLine += "M=D&M\n";
                writer.write(hackLine);
            }
            case "or" -> {
                hackLine += "@SP\n";
                hackLine += "AM=M-1\n";
                hackLine += "D=M\n";
                hackLine += "A=A-1\n";
                hackLine += "M=D|M\n";
                writer.write(hackLine);
            }
            case "not" -> {
                hackLine += "@SP\n";
                hackLine += "A=M-1\n";
                hackLine += "M=!M\n";
                writer.write(hackLine);
            }
        }
    }

    // Writes the assembly code that is the translation for C_PUSH or C_POP commands.
    public void writePushPop(String command, String segment, int index, String fileName) throws IOException {
        String hackLine = "";

        // push segment of the code.
        if (command.equals("push")) {
            switch (segment) {
                // push directions interpreted from the book's suggested mapping. uses reg name instead of base + i when possible.
                case "argument" -> {
                    hackLine += "@ARG\n";
                    hackLine += "D=M\n";
                    hackLine += "@" + index + "\n";
                    hackLine += "A=D+A\n";
                    hackLine += "D=M\n";
                    hackLine += "@SP\n";
                    hackLine += "A=M\n";
                    hackLine += "M=D\n";
                    hackLine += "@SP\n";
                    hackLine += "M=M+1\n";
                    writer.write(hackLine);
                }
                case "local" -> {
                    hackLine += "@LCL\n";
                    hackLine += "D=M\n";
                    hackLine += "@" + index + "\n";
                    hackLine += "A=D+A\n";
                    hackLine += "D=M\n";
                    hackLine += "@SP\n";
                    hackLine += "A=M\n";
                    hackLine += "M=D\n";
                    hackLine += "@SP\n";
                    hackLine += "M=M+1\n";
                    writer.write(hackLine);
                }
                case "static" -> {
                    // uses fileName in order to properly jump to defined static labels.
                    hackLine += "@" + fileName + "." + index + "\n";
                    hackLine += "D=M\n";
                    hackLine += "@SP\n";
                    hackLine += "A=M\n";
                    hackLine += "M=D\n";
                    hackLine += "@SP\n";
                    hackLine += "M=M+1\n";
                    writer.write(hackLine);
                }
                case "constant" -> {
                    hackLine += "@" + index + "\n";
                    hackLine += "D=A\n";
                    hackLine += "@SP\n";
                    hackLine += "A=M\n";
                    hackLine += "M=D\n";
                    hackLine += "@SP\n";
                    hackLine += "M=M+1\n";
                    writer.write(hackLine);
                }
                case "this" -> {
                    hackLine += "@THIS\n";
                    hackLine += "D=M\n";
                    hackLine += "@" + index + "\n";
                    hackLine += "A=D+A\n";
                    hackLine += "D=M\n";
                    hackLine += "@SP\n";
                    hackLine += "A=M\n";
                    hackLine += "M=D\n";
                    hackLine += "@SP\n";
                    hackLine += "M=M+1\n";
                    writer.write(hackLine);
                }
                case "that" -> {
                    hackLine += "@THAT\n";
                    hackLine += "D=M\n";
                    hackLine += "@" + index + "\n";
                    hackLine += "A=D+A\n";
                    hackLine += "D=M\n";
                    hackLine += "@SP\n";
                    hackLine += "A=M\n";
                    hackLine += "M=D\n";
                    hackLine += "@SP\n";
                    hackLine += "M=M+1\n";
                    writer.write(hackLine);
                }
                case "pointer" -> {
                    hackLine += "@R" + (3 + index) + "\n";
                    hackLine += "D=M\n";
                    hackLine += "@SP\n";
                    hackLine += "A=M\n";
                    hackLine += "M=D\n";
                    hackLine += "@SP\n";
                    hackLine += "M=M+1\n";
                    writer.write(hackLine);
                }
                case "temp" -> {
                    hackLine += "@R" + (5 + index) + "\n";
                    hackLine += "D=M\n";
                    hackLine += "@SP\n";
                    hackLine += "A=M\n";
                    hackLine += "M=D\n";
                    hackLine += "@SP\n";
                    hackLine += "M=M+1\n";
                    writer.write(hackLine);
                }
            }
        }

        // pop segment of the code.
        else {
            switch (segment) {
                // for pop commands, reg 13 is used under VM general purposes. similar functionality to push
                case "argument" -> {
                    hackLine += "@ARG\n";
                    hackLine += "D=M\n";
                    hackLine += "@" + index + "\n";
                    hackLine += "A=D+A\n";
                    hackLine += "D=A\n";
                    hackLine += "@R13\n";
                    hackLine += "M=D\n";
                    hackLine += "@SP\n";
                    hackLine += "M=M-1\n";
                    hackLine += "A=M\n";
                    hackLine += "D=M\n";
                    hackLine += "@R13\n";
                    hackLine += "A=M\n";
                    hackLine += "M=D\n";
                    writer.write(hackLine);
                }
                case "local" -> {
                    hackLine += "@LCL\n";
                    hackLine += "D=M\n";
                    hackLine += "@" + index + "\n";
                    hackLine += "A=D+A\n";
                    hackLine += "D=A\n";
                    hackLine += "@R13\n";
                    hackLine += "M=D\n";
                    hackLine += "@SP\n";
                    hackLine += "M=M-1\n";
                    hackLine += "A=M\n";
                    hackLine += "D=M\n";
                    hackLine += "@R13\n";
                    hackLine += "A=M\n";
                    hackLine += "M=D\n";
                    writer.write(hackLine);
                }
                case "static" -> {
                    hackLine += "@" + fileName + "." + index + "\n";
                    hackLine += "D=A\n";
                    hackLine += "@R13\n";
                    hackLine += "M=D\n";
                    hackLine += "@SP\n";
                    hackLine += "M=M-1\n";
                    hackLine += "A=M\n";
                    hackLine += "D=M\n";
                    hackLine += "@R13\n";
                    hackLine += "A=M\n";
                    hackLine += "M=D\n";
                    writer.write(hackLine);
                }
                case "this" -> {
                    hackLine += "@THIS\n";
                    hackLine += "D=M\n";
                    hackLine += "@" + index + "\n";
                    hackLine += "A=D+A\n";
                    hackLine += "D=A\n";
                    hackLine += "@R13\n";
                    hackLine += "M=D\n";
                    hackLine += "@SP\n";
                    hackLine += "M=M-1\n";
                    hackLine += "A=M\n";
                    hackLine += "D=M\n";
                    hackLine += "@R13\n";
                    hackLine += "A=M\n";
                    hackLine += "M=D\n";
                    writer.write(hackLine);
                }
                case "that" -> {
                    hackLine += "@THAT\n";
                    hackLine += "D=M\n";
                    hackLine += "@" + index + "\n";
                    hackLine += "A=D+A\n";
                    hackLine += "D=A\n";
                    hackLine += "@R13\n";
                    hackLine += "M=D\n";
                    hackLine += "@SP\n";
                    hackLine += "M=M-1\n";
                    hackLine += "A=M\n";
                    hackLine += "D=M\n";
                    hackLine += "@R13\n";
                    hackLine += "A=M\n";
                    hackLine += "M=D\n";
                    writer.write(hackLine);
                }
                case "pointer" -> {
                    hackLine += "@R" + (3 + index) + "\n";
                    hackLine += "D=A\n";
                    hackLine += "@R13\n";
                    hackLine += "M=D\n";
                    hackLine += "@SP\n";
                    hackLine += "M=M-1\n";
                    hackLine += "A=M\n";
                    hackLine += "D=M\n";
                    hackLine += "@R13\n";
                    hackLine += "A=M\n";
                    hackLine += "M=D\n";
                    writer.write(hackLine);
                }
                case "temp" -> {
                    hackLine += "@R" + (5 + index) + "\n";
                    hackLine += "D=A\n";
                    hackLine += "@R13\n";
                    hackLine += "M=D\n";
                    hackLine += "@SP\n";
                    hackLine += "M=M-1\n";
                    hackLine += "A=M\n";
                    hackLine += "D=M\n";
                    hackLine += "@R13\n";
                    hackLine += "A=M\n";
                    hackLine += "M=D\n";
                    writer.write(hackLine);
                }
            }
        }
    }

    // Quick access to a write when convenient in VMTranslator
    public void quickWrite(String line) throws IOException {
        writer.write(line);
    }

    // Closes the writer upon request
    public void close() throws IOException {
        writer.close();
    }

    // Project 8 Code Below
    // Writes the assembly code which effects the VM initialization. Placed in beginning of output file.
    public void writeInit() throws IOException {
        String hackLine = "@256\n";
        hackLine += "D=A\n";
        hackLine += "@SP\n";
        hackLine += "M=D\n";
        writer.write(hackLine);
        writeCall("Sys.init", 0);
    }

    // Writes assembly code for label commands. Simply creates a label.
    public void writeLabel(String label) throws IOException {
        writer.write("(" + label + ")\n");
    }

    // Writes assembly code for goto commands. Simply just jumps to label.
    public void writeGoto(String label) throws IOException {
        String hackLine = "@" + label + "\n";
        hackLine += "0;JMP\n";
        writer.write(hackLine);
    }

    // Writes assembly code for if-goto commands. Uses similar logic to arithmetic commands.
    public void writeIf(String label) throws IOException {
        String hackLine = "@SP\n";
        hackLine += "M=M-1\n";
        hackLine += "A=M\n";
        hackLine += "D=M\n";
        hackLine += "@" + label + "\n";
        hackLine += "D;JNE\n";
        writer.write(hackLine);
    }

    // Writes assembly code for call commands.
    public void writeCall(String functionName, int numArgs) throws IOException {
        // begins by creating the return label for the return address and push it to stack.
        String hackLine = "@RETURN_LABEL" + labelCounter + "\n";
        hackLine += "D=A\n";
        hackLine += "@SP\n";
        hackLine += "A=M\n";
        hackLine += "M=D\n";
        hackLine += "@SP\n";
        hackLine += "M=M+1\n";
        writer.write(hackLine);
        hackLine = "";

        // Push relevant registers (1 to 4) to the stack.
        hackLine += "@LCL\n";
        hackLine += "D=M\n";
        hackLine += "@SP\n";
        hackLine += "A=M\n";
        hackLine += "M=D\n";
        hackLine += "@SP\n";
        hackLine += "M=M+1\n";
        writer.write(hackLine);
        hackLine = "";

        hackLine += "@ARG\n";
        hackLine += "D=M\n";
        hackLine += "@SP\n";
        hackLine += "A=M\n";
        hackLine += "M=D\n";
        hackLine += "@SP\n";
        hackLine += "M=M+1\n";
        writer.write(hackLine);
        hackLine = "";

        hackLine += "@THIS\n";
        hackLine += "D=M\n";
        hackLine += "@SP\n";
        hackLine += "A=M\n";
        hackLine += "M=D\n";
        hackLine += "@SP\n";
        hackLine += "M=M+1\n";
        writer.write(hackLine);
        hackLine = "";

        hackLine += "@THAT\n";
        hackLine += "D=M\n";
        hackLine += "@SP\n";
        hackLine += "A=M\n";
        hackLine += "M=D\n";
        hackLine += "@SP\n";
        hackLine += "M=M+1\n";
        writer.write(hackLine);
        hackLine = "";

        // Finish up call command by calling the function itself and preparing for return.
        hackLine += "@SP\n";
        hackLine += "D=M\n";
        hackLine += "@LCL\n";
        hackLine += "M=D\n";
        hackLine += "@" + (numArgs + 5) + "\n";
        hackLine += "D=D-A\n";
        hackLine += "@ARG\n";
        hackLine += "M=D\n";
        hackLine += "@" + functionName + "\n";
        hackLine += "0;JMP\n";
        hackLine += "(RETURN_LABEL" + labelCounter + ")\n";
        writer.write(hackLine);
        labelCounter++;
    }

    // Writes assembly code for return commands.
    public void writeReturn() throws IOException {
        // FRAME = LCL and RET = (FRAME - 5)
        String hackLine = "@LCL\n";
        hackLine += "D=M\n";
        hackLine += "@R13\n";
        hackLine += "M=D\n";
        hackLine += "@R13\n";
        hackLine += "D=M\n";
        hackLine += "@5\n";
        hackLine += "D=D-A\n";
        hackLine += "A=D\n";
        hackLine += "D=M\n";
        hackLine += "@R14\n";
        hackLine += "M=D\n";
        writer.write(hackLine);
        hackLine = "";

        // pop arg when returning
        hackLine += "@SP\n";
        hackLine += "M=M-1\n";
        hackLine += "A=M\n";
        hackLine += "D=M\n";
        hackLine += "@ARG\n";
        hackLine += "A=M\n";
        hackLine += "M=D\n";
        hackLine += "@ARG\n";
        hackLine += "D=M\n";
        hackLine += "@SP\n";
        hackLine += "M=D+1\n";

        // store val of other relevant registers mentioned in writeCall
        hackLine += "@R13\n";
        hackLine += "D=M\n";
        hackLine += "@1\n";
        hackLine += "D=D-A\n";
        hackLine += "A=D\n";
        hackLine += "D=M\n";
        hackLine += "@THAT\n";
        hackLine += "M=D\n";

        hackLine += "@R13\n";
        hackLine += "D=M\n";
        hackLine += "@2\n";
        hackLine += "D=D-A\n";
        hackLine += "A=D\n";
        hackLine += "D=M\n";
        hackLine += "@THIS\n";
        hackLine += "M=D\n";

        hackLine += "@R13\n";
        hackLine += "D=M\n";
        hackLine += "@3\n";
        hackLine += "D=D-A\n";
        hackLine += "A=D\n";
        hackLine += "D=M\n";
        hackLine += "@ARG\n";
        hackLine += "M=D\n";

        hackLine += "@R13\n";
        hackLine += "D=M\n";
        hackLine += "@4\n";
        hackLine += "D=D-A\n";
        hackLine += "A=D\n";
        hackLine += "D=M\n";
        hackLine += "@LCL\n";
        hackLine += "M=D\n";

        // ending up of return code by jumping.
        hackLine += "@R14\n";
        hackLine += "A=M\n";
        hackLine += "0;JMP\n";
        writer.write(hackLine);
    }

    // Writes assembly code for function commands.
    public void writeFunction(String functionName, int numLocals) throws IOException {
        writer.write("(" + functionName + ")\n");

        // uses writePushPop in for loop to do push constant 0 for as many numLocals as possible.
        for (int i = 0; i < numLocals; i++){
            writePushPop("push", "constant", 0, "");
        }
    }
}
