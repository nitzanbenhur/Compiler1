package AST;
import TYPES.*;
import java.io.PrintWriter;
import TEMP.*;
import IR.*;

public class AST_EXP_LIST extends AST_Node
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public AST_EXP head;
	public AST_EXP_LIST tail;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_LIST(AST_EXP head,AST_EXP_LIST tail)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		if (tail != null) System.out.print("====================== exps -> exp exps\n");
		if (tail == null) System.out.print("====================== exps -> exp      \n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.head = head;
		this.tail = tail;
	}

	/******************************************************/
	/* The printing message for a statement list AST node */
	/******************************************************/
	public void PrintMe()
	{
		/**************************************/
		/* AST NODE TYPE = AST DEC LIST */
		/**************************************/
		System.out.print("AST NODE EXP LIST\n");

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
			"EXP\nLIST\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (head != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,head.SerialNumber);
		if (tail != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,tail.SerialNumber);
	}
	public TYPE_LIST SemantMe(PrintWriter file_writer, int scope, String param, int fpOffset)
	{
		
		if (tail == null)
		{
			return new TYPE_LIST(
				head.SemantMe(file_writer,scope,param,fpOffset),
				null);
		}
		else
		{
			return new TYPE_LIST(
				head.SemantMe(file_writer,scope,param,fpOffset++),
				tail.SemantMe(file_writer,scope,param,fpOffset) );
		}
	}
	
	
	public TEMP IRme()
	{
		if (head != null) head.IRme();
		if (tail != null) tail.IRme();
		return null;			
	}
	
	public void IRWriteStrings() {
		if (head != null) head.IRWriteStrings();
		if (tail != null) tail.IRWriteStrings();
	}
	
}
