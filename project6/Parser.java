

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;



public class Parser 
{
    private File filesource;
    private Scanner readsourcefile;
    private String line;
    private int coutner;

    // Constructor
    public Parser(File filesource) throws FileNotFoundException
    {
        this.filesource = filesource;
        this.readsourcefile = new Scanner((this.filesource));
        String name = this.filesource.getName();
        int index = name.lastIndexOf('.');
        name = name.substring(0, index) + ".hack";
        this.line = "";
        this.coutner=0;
        
    }

    // Check if there are more lines to process
    public boolean hasMoreLines()
    {
        return this.readsourcefile.hasNextLine();
    }

    // Advance to the next line
    public void advance()
    {
    
        while (hasMoreLines()) 
        {
            line = this.readsourcefile.nextLine();
            line = line.replaceAll("\\s+", "");
            if (line.contains("//")) 
            {
                line = line.substring(0, line.indexOf("//"));
            }
            if (!line.isEmpty()) 
            {
                if (this.instructionType() != instructioEnum.L_instruction ) 
                {
                    this.coutner++;
                }     
            return;
            }
        }

    }

    // Return the type of the current command: A_COMMAND, C_COMMAND, or L_COMMAND
    public instructioEnum instructionType() 
    {
        if (this.line.contains("@")) 
        {
          return instructioEnum.A_instruction;
        }
        if (this.line.contains("(")) 
        {
          return instructioEnum.L_instruction;
        }
        return instructioEnum.C_instruction;

    }

    // Return the symbol of an A_COMMAND or L_COMMAND (e.g., "@value" or "(LABEL)")
    public String symbol(){
        if (this.instructionType()==instructioEnum.A_instruction) 
        {
            return line.substring(line.indexOf("@")+1);
        }
        else if (this.instructionType()==instructioEnum.L_instruction) 
        {
            return line.substring(line.indexOf("(")+1,line.indexOf(")"));
        }
        return "";
    }

    // Return the dest field of a C_COMMAND
    public String dest(){
        if (this.instructionType()==instructioEnum.C_instruction && line.contains("="))  
        {
            return line.substring(0,line.indexOf('='));
        }
        return "";
    }

    // Return the comp field of a C_COMMAND
    public String comp(){
        if (this.instructionType()==instructioEnum.C_instruction ) 
        {
            if(line.contains(";"))
                return line.substring(line.indexOf('=')+1,line.indexOf(';'));
            return line.substring(line.indexOf('=')+1);
        }
        return "";
    }

    // Return the jump field of a C_COMMAND
    public String jump()
    {
        if (this.instructionType()==instructioEnum.C_instruction && line.contains(";")) 
        {
            return line.substring(line.indexOf(';')+1);
        }
        return "";
    }
    
     public String getline(){return this.line;}
     public int getcounter(){return this.coutner;}
     public void closeScanner(){this.readsourcefile.close();}
}
