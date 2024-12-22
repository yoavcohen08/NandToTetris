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
            if (!line.isEmpty()) 
            {
                return;
            }
        }
    }

    public CommandType commandType() 
    {
        String []array =  line.split(" ");
        if (array.length<2) 
        {
            return CommandType.C_ARITHMETIC;   
        }
        if (array[0].equals("push"))
        {
            return CommandType.C_PUSH;
        }
        return CommandType.C_POP;
        
    }

    public String arg1() {
        if (this.commandType()== CommandType.C_ARITHMETIC) 
        {         
            return line;
        }
           String []array =  line.split(" ");
           return array[1];
    }
    public int arg2() 
    {
        String []array =  line.split(" ");
        return Integer.parseInt(array[2]);
    }
}
