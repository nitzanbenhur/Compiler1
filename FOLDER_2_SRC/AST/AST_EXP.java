package AST;
import TYPES.*;
import java.io.PrintWriter;
import TEMP.*;
import IR.*;


public abstract class AST_EXP extends AST_Node
{
	public int moish;
	public String param = null;
	public int fpOffset = 0;

	public TYPE SemantMe(PrintWriter file_writer, int scope, String param, int fpOffset)
	{
		return null;
	}
	
	public TEMP IRme() {
		return null;
	}
}