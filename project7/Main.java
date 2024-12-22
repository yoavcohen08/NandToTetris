import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new Exception("Not valid data type");
        }
        File filesource = new File(args[0]);
        if (!filesource.exists()) {
            throw new FileNotFoundException("File Not Found");
        }
        if(filesource.isDirectory())
        {
            String name = filesource.getName();
            name = name + ".asm";
            File filetarget = new File(filesource, name);
            if(!filetarget.exists()){
                filetarget.createNewFile();
            }
            File[] files = filesource.listFiles();
            int lastindex = 0;
            for (File file : files) 
            {
                if(file.getName().endsWith(".vm"))
                {
                    lastindex = ProceesDir(file, filetarget,lastindex);
                }
            }
        }
        else {
            Process(filesource);
        }
    }
    public static  int ProceesDir (File filesource, File filetarget,int index) throws IOException {
        Scanner readsourcefile = new Scanner((filesource));
        Parser parser = new Parser(filesource);
        parser = new Parser(filesource);
        int lastindex = 0;
        try (FileWriter writetagetbuffer = new FileWriter(filetarget,true);) {
            CodeWriter codewriter = new CodeWriter(filetarget, writetagetbuffer,index);
            while (parser.hasMoreLines()) 
            {
                parser.advance();
                if (parser.commandType() == CommandType.C_ARITHMETIC) {
                    codewriter.writeArithmetic(parser.arg1());
                } else {
                    codewriter.writePushPop(parser.commandType(), parser.arg1(), parser.arg2());
                }
            }
            lastindex = codewriter.getindex();

        }  finally {
            readsourcefile.close();
        }
        return lastindex;
    }
    public static  void Process (File filesource) throws IOException {
        Scanner readsourcefile = new Scanner((filesource));
        String name = filesource.getName();
        int index = name.lastIndexOf('.');
        name = name.substring(0, index) + ".asm";
        File filetarget = new File(filesource.getParent(), name);
        Parser parser = new Parser(filesource);
        parser = new Parser(filesource);
        try {
            filetarget.createNewFile();
            try (FileWriter writetagetbuffer = new FileWriter(filetarget);) {
                CodeWriter codewriter = new CodeWriter(filetarget, writetagetbuffer,0);
                while (parser.hasMoreLines()) {
                    parser.advance();
                    if (parser.commandType() == CommandType.C_ARITHMETIC) {
                        codewriter.writeArithmetic(parser.arg1());
                    } else {
                        codewriter.writePushPop(parser.commandType(), parser.arg1(), parser.arg2());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("File cant be executed");
        } finally {
            readsourcefile.close();
        }
    }
}
