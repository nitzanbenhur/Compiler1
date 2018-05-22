package AST;
import TYPES.*;
import java.io.PrintWriter;
import TEMP.*;
import IR.*;

public class AST_STMT_DEC_VAR extends AST_STMT
{


	public AST_DEC_VAR v;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_STMT_DEC_VAR(AST_DEC_VAR v)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== stmt -> varDec\n");
		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/

		this.v = v;
	}

	/*********************************************************/
	/* The printing message for an assign statement AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/********************************************/
		/* AST NODE TYPE = AST ASSIGNMENT STATEMENT */
		/********************************************/
		System.out.print("AST NODE VAR DEC STMT\n");
		
		if (v != null) v.PrintMe();

		AST_GRAPHVIZ.getInstance().logNode(
		SerialNumber,
		"STMT\nVAR\nDEC");
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/

		if (v != null)AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,v.SerialNumber);
	}
	
	public TYPE SemantMe(PrintWriter file_writer, int scope,TYPE ret)
	{
		return v.SemantMe(file_writer,scope, AST_DEC_FUNC.frameSize,"local");
	}
	
	public TEMP IRme()
	{
		return v.IRme();
	}
	
	public void IRWriteStrings() {
		if(v != null) v.IRWriteStrings();
	}
}
