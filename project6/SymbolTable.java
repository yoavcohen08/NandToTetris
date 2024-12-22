import java.util.HashMap;


public class SymbolTable 
{
    private HashMap<String,Integer> hash;
    private int index;

    public SymbolTable()
    {
        hash = new HashMap<String,Integer>();
        for(int i=0;i<16;i++)
        {
            String name = "R"+i;
            hash.put(name, i);
        }
        hash.put("SCREEN", 16384);
        hash.put("KBD", 24575);
        hash.put("SP", 0);
        hash.put("LCL", 1);
        hash.put("ARG", 2);
        hash.put("THIS", 3);
        hash.put("THAT", 4);
        

        index = 16;

    }
    //add an key with spesific number
    public void addVaribale(String symbol)
    {
        if (this.contains(symbol)) 
        {
            return;
        }     
            hash.put(symbol, this.index);
            this.index++;
    }

    // Check if a symbol exists in the table
    public boolean contains(String symbol)
    {
        return this.hash.containsKey(symbol);
    }

    // Get the address associated with a symbol
    public int getAddress(String symbol)
    {
        if (!this.contains(symbol)) 
        {
            return -1;
        }
        return this.hash.get(symbol);
    }
    public void addLinstruction(String symbol,int value)
    {
            hash.put(symbol, value);
    }
}
