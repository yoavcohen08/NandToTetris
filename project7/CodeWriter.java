import java.io.*;

public class CodeWriter {
    private File targetfile;
    private FileWriter fileWriter;
    private int index;

    public CodeWriter(File targetfile, FileWriter fileWriter,int index) {
        this.targetfile = targetfile;
        this.fileWriter = fileWriter;
        this.index = index;
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
                String name = targetfile.getName();
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
}