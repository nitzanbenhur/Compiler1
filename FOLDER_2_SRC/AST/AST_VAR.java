package AST;
import TYPES.*;
import java.io.PrintWriter;
import TEMP.*;
import IR.*;

public abstract class AST_VAR extends AST_Node
{
	public String name;

	
	public TYPE SemantMe(PrintWriter file_writer, int scope)
	{
		return null;
	}
	
	public TEMP IRme()
	{
		return null;
	}
}
