import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static boolean flag = true;


    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new Exception("Not valid data type");
        }
        File filesource = new File(args[0]);
        if (!filesource.exists()) {
            throw new FileNotFoundException("File Not Found");
        }
        if (filesource.isDirectory()) {
            String name = filesource.getName();
            name = name + ".asm";
            File filetarget = new File(filesource, name);
            File[] files = filesource.listFiles();
            int []lastindex = {0,0};
            ArrayList<File> arr = new ArrayList<File>(); 
            for (File file : files) 
            {
                if (file.getName().endsWith(".vm")) 
                {
                    arr.add(file);
                }
            }
            if(arr.size()==1)//if there is only one file in the dir
                {Process(arr.get(0));}
            else
            {
                if (!filetarget.exists()) 
                {
                    filetarget.createNewFile();
                }
                for(File file : arr)
                {
                    lastindex = ProceesDir(file, filetarget, lastindex);
                }
            }
        } 
        else 
        {
            Process(filesource);
        }
    }

    public static int[] ProceesDir(File filesource, File filetarget, int []index) throws IOException {
        Scanner readsourcefile = new Scanner((filesource));
        Parser parser = new Parser(filesource);
        parser = new Parser(filesource);
        int lastindex = 0,lastindexof =0;
        try (FileWriter writetagetbuffer = new FileWriter(filetarget, true);) {
            CodeWriter codewriter = new CodeWriter(filetarget, writetagetbuffer, index[0],index[1],parser.getname());
            if (flag)
            {
                codewriter.bootstrap();
            }
            while (parser.hasMoreLines()) 
            {
                parser.advance();
                codewriter.writecommet(parser.getline());
                switch (parser.commandType()) 
                {
                    case C_ARITHMETIC: {
                        codewriter.writeArithmetic(parser.arg1());
                        break;
                    }
                    case C_POP, C_PUSH: {
                        codewriter.writePushPop(parser.commandType(), parser.arg1(), parser.arg2());
                        break;
                    }
                    case C_LABEL: {
                        codewriter.writeLabel(parser.arg1());
                        break;
                    }
                    case C_GOTO: {
                        codewriter.writeGoto(parser.arg1());
                        break;
                    }
                    case C_IF: {
                        codewriter.writeIf(parser.arg1());
                        break;
                    }
                    case C_FUNCTION: {
                        codewriter.writeFunction(parser.arg1(), parser.arg2());
                        break;
                    }
                    case C_CALL: {
                        codewriter.writeCall(parser.arg1(), parser.arg2());
                        break;
                    }
                    case C_RETURN: {
                        codewriter.writeReturn();
                        break;
                    }
                }
            
        }
            lastindex = codewriter.getindex();
            lastindexof = codewriter.getIndexFunc();

        } finally {
            readsourcefile.close();
        }
       int[] array = {lastindex,lastindexof};
       return array;
    }

    public static void Process(File filesource) throws IOException {
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
                CodeWriter codewriter = new CodeWriter(filetarget, writetagetbuffer, 0,0,parser.getname());
                while (parser.hasMoreLines()) {
                    parser.advance();

                    switch (parser.commandType()) {
                        case C_ARITHMETIC: {
                            codewriter.writeArithmetic(parser.arg1());
                            break;
                        }
                        case C_POP, C_PUSH: {
                            codewriter.writePushPop(parser.commandType(), parser.arg1(), parser.arg2());
                            break;
                        }
                        case C_LABEL: {
                            codewriter.writeLabel(parser.arg1());
                            break;
                        }
                        case C_GOTO: {
                            codewriter.writeGoto(parser.arg1());
                            break;
                        }
                        case C_IF: {
                            codewriter.writeIf(parser.arg1());
                            break;
                        }
                        case C_FUNCTION: {
                            codewriter.writeFunction(parser.arg1(), parser.arg2());
                            break;
                        }
                        case C_CALL: {
                            codewriter.writeCall(parser.arg1(), parser.arg2());
                            break;
                        }
                        case C_RETURN: {
                            codewriter.writeReturn();
                            break;
                        }
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
