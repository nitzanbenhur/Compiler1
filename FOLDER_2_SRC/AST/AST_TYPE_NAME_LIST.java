package AST;
import java.io.PrintWriter;
import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;

public class AST_TYPE_NAME_LIST extends AST_Node
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public AST_TYPE_NAME head;
	public AST_TYPE_NAME_LIST tail;
	public int fpOffset;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_TYPE_NAME_LIST(AST_TYPE_NAME head,AST_TYPE_NAME_LIST tail)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();
		if (tail != null) System.out.print("====================== ids -> typeName COMMA typeNameListComma\n");
		if (tail == null) System.out.print("====================== ids -> typeName      \n");
 
		this.head = head;
		this.tail = tail;
	}

	/******************************************************/
	/* The printing message for a type name list AST node */
	/******************************************************/
	public void PrintMe()
	{
		/**************************************/
		/* AST NODE TYPE = AST TYPE NAME LIST */
		/**************************************/
		System.out.print("AST TYPE NAME LIST\n");

		/*************************************/
		/* RECURSIVELY PRINT HEAD + TAIL ... */
		/*************************************/
		if (head != null) head.PrintMe();
		if (tail != null) tail.PrintMe();

		/**********************************/
		/* PRINT to AST GRAPHVIZ DOT file */
		/**********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"TYPE-NAME\nLIST\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (head != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,head.SerialNumber);
		if (tail != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,tail.SerialNumber);
	}

	public TYPE_LIST SemantMe(PrintWriter file_writer, int scope)
	{	
		if (tail == null)
		{
			return new TYPE_LIST(
				head.SemantMe(file_writer,scope),
				null);
		}
		else
		{
			return new TYPE_LIST(
				head.SemantMe(file_writer,scope),
				tail.SemantMe(file_writer,scope) );
		}
	}
	
	public TEMP IRme()
	{
		if (head != null) head.IRme();
		if (tail != null) tail.IRme();
		return null;			
	}
	
	public void IRWriteStrings() {
		if(head != null) head.IRWriteStrings();
		if(tail != null) tail.IRWriteStrings();
	}
	
}
