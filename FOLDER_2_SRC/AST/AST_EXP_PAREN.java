package AST;
import TYPES.*;
import java.io.PrintWriter;
import TEMP.*;
import IR.*;

public class AST_EXP_PAREN extends AST_EXP
{
	public AST_EXP exp;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_PAREN(AST_EXP exp)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== exp -> LPAREN exp RPAREN\n");


		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.exp = exp;
	}

	/************************************************/
	/* The printing message for an INT EXP AST node */
	/************************************************/
	public void PrintMe()
	{
		/*******************************/
		/* AST NODE TYPE = AST INT EXP */
		/*******************************/
		System.out.print("AST NODE (EXP)\n");

		/*********************************/
		/* Print to AST GRAPHIZ DOT file */
		/*********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"(EXP)");
			
		if (exp != null)AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,exp.SerialNumber);
	}
	public TYPE SemantMe(PrintWriter file_writer, int scope, String param, int fpOffset)
	{
	
		this.fpOffset = fpOffset;
		this.param = param;
	
		return exp.SemantMe(file_writer,scope,null,0);
	}
	
	public TEMP IRme(){
		TEMP t = exp.IRme();
		if (this.param != null){
			IR.getInstance().Add_IRcommand( new IRcommand_store_param_var( this.fpOffset,t ) );
		}
		return t;
	}
	
	public void IRWriteStrings() {
		if (exp != null) exp.IRWriteStrings();
	}
	
}
