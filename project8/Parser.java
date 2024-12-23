import java.io.*;
import java.util.Scanner;

public class Parser 
{  
    private File filesource;
    private Scanner readsourcefile;
    private String line;


    public Parser(File filePath) throws IOException 
    {
        this.filesource = filePath;
        this.readsourcefile = new Scanner((this.filesource));
        this.line = "";
    }

    public boolean hasMoreLines()
    {
        return this.readsourcefile.hasNextLine();
    }

    public void advance()
    {
        while (hasMoreLines())
        {
            line = this.readsourcefile.nextLine();
            if (line.contains("//")) 
            {
                line = line.substring(0, line.indexOf("//"));
            }
            if (!line.isEmpty() && !line.replaceAll("\\s", "").equals(""))
            {
                return;
            }
        }
    }

    public CommandType commandType()
    {
        String []array =  line.split(" ");
        array[0] = array[0].replaceAll("\\s", "");
        if (array[0].equals("return"))
        {
            return CommandType.C_RETURN;
        }
        if (array[0].equals("push"))
        {
            return CommandType.C_PUSH;
        }
        if (array[0].equals("pop"))
        {
            return CommandType.C_POP;
        }
        if (array[0].equals("function"))
        {
            return CommandType.C_FUNCTION;
        }
        if (array[0].equals("goto"))
        {
            return CommandType.C_GOTO;
        }
        if (array[0].equals("call"))
        {
            return CommandType.C_CALL;
        }
        if (array[0].equals("label"))
        {
            return CommandType.C_LABEL;
        }
        if (array[0].equals("if-goto"))
        {
            return CommandType.C_IF;
        }
        return  CommandType.C_ARITHMETIC;
    }

    public String arg1() {
        if (this.commandType()== CommandType.C_ARITHMETIC) 
        {         
            return line.replaceAll("\\s", "");
        }
           String []array =  line.split(" ");
           return array[1];
    }
    public int arg2() 
    {
        String []array =  line.split(" ");
        return Integer.parseInt(array[2].replaceAll("\\s", ""));
    }
    public String getline(){return this.line;}
    public String getname(){return this.filesource.getName();}
}
