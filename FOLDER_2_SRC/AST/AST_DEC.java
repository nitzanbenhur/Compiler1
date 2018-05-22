package AST;
import TYPES.*;
import java.io.PrintWriter;
import TEMP.*;
import IR.*;


public abstract class AST_DEC extends AST_Node
{
	public String name;
	public int line;
	public TYPE SemantMe(PrintWriter file_writer, int scope)
	{	
		return null;
	}
	
		
	public TEMP IRme()
	{
		return null;
	}

}