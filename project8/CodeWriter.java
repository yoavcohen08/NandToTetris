import java.io.*;

public class CodeWriter {
    private File targetfile;
    private FileWriter fileWriter;
    private int index;
    private int indexFunc;
    private String currentname;

    public CodeWriter(File targetfile, FileWriter fileWriter,int index,int indexFunc,String currentname) 
    {
        this.targetfile = targetfile;
        this.fileWriter = fileWriter;
        this.index = index;
        this.indexFunc = indexFunc;
        this.currentname = currentname;
    }

    public void writeArithmetic(String command) throws IOException {
        this.WriteToFile("@SP");
        this.WriteToFile("M=M-1");
        this.WriteToFile("A=M");
        this.WriteToFile("D=M");
        switch (command) {
            case "add": {
                this.SameFunction();
                this.WriteToFile("M=D+M");
                break;
            }
            case "sub": {
                this.SameFunction();
                this.WriteToFile("M=M-D");
                break;
            }
            case "neg": {
                this.WriteToFile("M=-M");
                break;
            }
            case "eq": {
                this.SameFunction();
                this.ifCondition("D;JEQ");
                break;
            }
            case "gt": {
                this.SameFunction();
                this.ifCondition("D;JGT");
                break;
            }
            case "lt": {
                this.SameFunction();
                this.ifCondition("D;JLT");
                break;
            }
            case "and": {
                this.SameFunction();
                this.WriteToFile("M=D&M");
                break;
            }
            case "or": {
                this.SameFunction();
                this.WriteToFile("M=D|M");
                break;
            }
            case "not": {
                this.WriteToFile("M=!M");
                break;
            }
        }
        this.WriteToFile("@SP");
        this.WriteToFile("M=M+1");
    }

    public void writePushPop(CommandType commandType, String segment, int ind) throws IOException {
        switch (segment) {
            case "constant": {
                this.WriteToFile("@" + ind);
                this.WriteToFile("D=A");
                this.WriteToFile("@SP");
                this.WriteToFile("A=M");
                this.WriteToFile("M=D");
                this.WriteToFile("@SP");
                this.WriteToFile("M=M+1");
                break;
            }
            case "local": {
                this.segment(commandType, "LCL", ind);
                break;
            }
            case "argument": {
                this.segment(commandType, "ARG", ind);
                break;
            }
            case "this": {
                this.segment(commandType, "THIS", ind);
                break;
            }
            case "that": {
                this.segment(commandType, "THAT", ind);
                break;
            }
            case "static": {
                String name =this.currentname;
                int index_static = name.lastIndexOf('.');
                name = name.substring(0, index_static) + "." + ind;
                this.CaseOf(commandType, name);
                break;
            }
            case "temp": {
                int name = 5 + ind;
                this.CaseOf(commandType, ("" + name));
                break;
            }
            case "pointer": {
                if (ind == 1) {
                    this.CaseOf(commandType, "THAT");
                }
                if (ind == 0) {
                    this.CaseOf(commandType, "THIS");
                }
                break;
            }
        }

    }

    private void WriteToFile(String str) throws IOException {
        this.fileWriter.append(str).append("\n");
    }

    private void SameFunction() throws IOException {
        this.WriteToFile("@SP");
        this.WriteToFile("M=M-1");
        this.WriteToFile("A=M");
    }

    private void ifCondition(String cond) throws IOException {
        this.WriteToFile("D=M-D");
        this.WriteToFile("M=-1");          //State True
        this.WriteToFile("@IF_" + index);
        this.WriteToFile(cond);                //If true jumps to If+index
        this.WriteToFile("@SP");           //Only if false
        this.WriteToFile("A=M");
        this.WriteToFile("M=0");           //State false
        this.WriteToFile("(IF_" + index + ")");
        index++;
    }

    private void segment(CommandType commandType, String str, int ind) throws IOException {
        if (commandType == CommandType.C_PUSH) {
            this.WriteToFile("@" + str);
            this.WriteToFile("D=M");
            this.WriteToFile("@" + ind);
            this.WriteToFile("D=D+A");
            this.WriteToFile("A=D");
            this.WriteToFile("D=M");
            this.WriteToFile("@SP");
            this.WriteToFile("A=M");
            this.WriteToFile("M=D");
            this.WriteToFile("@SP");
            this.WriteToFile("M=M+1");
        }
        if (commandType == CommandType.C_POP) {
            this.WriteToFile("@" + str);
            this.WriteToFile("D=M");
            this.WriteToFile("@" + ind);
            this.WriteToFile("D=D+A");
            this.WriteToFile("@R13");
            this.WriteToFile("M=D");
            this.WriteToFile("@SP");
            this.WriteToFile("M=M-1");
            this.WriteToFile("A=M");
            this.WriteToFile("D=M");
            this.WriteToFile("@R13");
            this.WriteToFile("A=M");
            this.WriteToFile("M=D");
        }
    }

    private void CaseOf(CommandType commandType, String str) throws IOException {
        if (commandType == CommandType.C_PUSH) {
            this.WriteToFile("@" + str);
            this.WriteToFile("D=M");
            this.WriteToFile("@SP");
            this.WriteToFile("A=M");
            this.WriteToFile("M=D");
            this.WriteToFile("@SP");
            this.WriteToFile("M=M+1");
        }
        if (commandType == CommandType.C_POP) {
            this.WriteToFile("@SP");
            this.WriteToFile("M=M-1");
            this.WriteToFile("A=M");
            this.WriteToFile("D=M");
            this.WriteToFile("@" + str);
            this.WriteToFile("M=D");
        }
    }
    public int getindex(){return this.index;}

    /**
     * Writes assembly code for a label command.
     * @param label The label name.
     */
    public void writeLabel(String label) throws IOException {
        WriteToFile("(" +label + ")");
    }

    /**
     * Writes assembly code for a goto command.
     * @param label The label to jump to.
     */
    public void writeGoto(String label) throws IOException {
        WriteToFile("@"+label);
        WriteToFile("0;JMP");
    }

    /**
     * Writes assembly code for an if-goto command.
     * @param label The label to jump to if the top of the stack is not zero.
     */
    public void writeIf(String label) throws IOException {
        this.WriteToFile("@SP");
        this.WriteToFile("M=M-1");
        this.WriteToFile("A=M");
        this.WriteToFile("D=M");
        this.WriteToFile("@" + label);
        this.WriteToFile("D;JNE");
    }

    /**
     * Writes assembly code for a function declaration.
     * @param functionName The name of the function.
     * @param numLocals The number of local variables.
     */
    public void writeFunction(String functionName, int numLocals) throws IOException {
        this.WriteToFile('('+functionName+')');
        for (int i = 0; i < numLocals; i++) {
            writePushPop(CommandType.C_PUSH, "constant", 0);
        }
    }

    /**
     * Writes assembly code for a function call.
     * @param functionName The name of the function to call.
     * @param numArgs The number of arguments being passed.
     */
    public void writeCall(String functionName, int numArgs) throws IOException {
        String returnLabel = "RETURN_" + functionName + "_" + indexFunc++;
        this.WriteToFile("@" + returnLabel);
        this.WriteToFile("D=A");
        this.WriteToFile("@SP");
        this.WriteToFile("A=M");
        this.WriteToFile("M=D");
        this.WriteToFile("@SP");
        this.WriteToFile("M=M+1");
        CaseOf(CommandType.C_PUSH, "LCL");
        CaseOf(CommandType.C_PUSH, "ARG");
        CaseOf(CommandType.C_PUSH, "THIS");
        CaseOf(CommandType.C_PUSH, "THAT");
        this.WriteToFile("@SP");
        this.WriteToFile("D=M");
        int flag = numArgs + 5;
        this.WriteToFile("@" + flag);
        this.WriteToFile("D=D-A");
        this.WriteToFile("@ARG");
        this.WriteToFile("M=D");
        this.WriteToFile("@SP");
        this.WriteToFile("D=M");
        this.WriteToFile("@LCL");
        this.WriteToFile("M=D");
        this.writeGoto(functionName);
        this.writeLabel(returnLabel);
    }

    /**
     * Writes assembly code for a return command.
     */
    public void writeReturn()throws IOException{
        // FRAME = LCL (store the frame in R14)
        this.WriteToFile("@LCL");
        this.WriteToFile("D=M");
        this.WriteToFile("@R14");
        this.WriteToFile("M=D");
        // RET = *(FRAME - 5) (store the return address in R15)
        this.WriteToFile("@5");
        this.WriteToFile("A=D-A");
        this.WriteToFile("D=M");
        this.WriteToFile("@R15");
        this.WriteToFile("M=D");
        // *ARG = pop() (reposition the return value for the caller)
        this.WriteToFile("@SP");
        this.WriteToFile("M=M-1");
        this.WriteToFile("A=M");
        this.WriteToFile("D=M");
        this.WriteToFile("@ARG");
        this.WriteToFile("A=M");
        this.WriteToFile("M=D");
        // SP = ARG + 1 (restore SP)
        this.WriteToFile("@ARG");
        this.WriteToFile("D=M+1");
        this.WriteToFile("@SP");
        this.WriteToFile("M=D");
        // Restore THAT, THIS, ARG, LCL from the frame
        this.reposbyendframe("THAT",1);
        this.reposbyendframe("THIS",2);
        this.reposbyendframe("ARG",3);
        this.reposbyendframe("LCL",4);
        // Jump to RET
        this.WriteToFile("@R15");
        this.WriteToFile("A=M");
        this.WriteToFile("0;JMP");
    }
    private void reposbyendframe(String arg, int num)throws IOException{
        this.WriteToFile("@R14");
        this.WriteToFile("D=M");
        this.WriteToFile("@" + num);
        this.WriteToFile("A=D-A");
        this.WriteToFile("D=M");
        this.WriteToFile("@" + arg);
        this.WriteToFile("M=D");
    }
    public int getIndexFunc(){
        return this.indexFunc;
    }
    public void bootstrap() throws IOException{
        this.WriteToFile("@256");
        this.WriteToFile("D=A");
        this.WriteToFile("@SP");
        this.WriteToFile("M=D");
        this.writeCall("Sys.init",0);
    }
    public void writecommet(String str) throws IOException
    {
        this.WriteToFile("//"+str);
    }
}