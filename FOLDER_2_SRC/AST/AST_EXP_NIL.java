package AST;
import TYPES.*;
import java.io.PrintWriter;
import TEMP.*;
import IR.*;

public class AST_EXP_NIL extends AST_EXP
{

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_NIL()
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== exp -> NIL\n");
	}

	/************************************************/
	/* The printing message for an INT EXP AST node */
	/************************************************/
	public void PrintMe()
	{
		/*******************************/
		/* AST NODE TYPE = AST INT EXP */
		/*******************************/
		System.out.print("AST NODE NIL)\n");

		/*********************************/
		/* Print to AST GRAPHIZ DOT file */
		/*********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"NIL");
	}
	
	public TYPE SemantMe(PrintWriter file_writer, int scope, String param, int fpOffset)
	{
	
		this.fpOffset = fpOffset;
		this.param = param;
		return TYPE_NIL.getInstance();
	}
	
	public TEMP IRme()
	{
		TEMP t = TEMP_FACTORY.getInstance().getFreshTEMP();
		IR.getInstance().Add_IRcommand( new IRcommand_Set_Zero( t ) );
		
		if (this.param != null){
			IR.getInstance().Add_IRcommand( new IRcommand_store_param_var( this.fpOffset,t ) );
		}
		
		return t;
	}
	
	public void IRWriteStrings() {
		// do nothing
	}
	
}
