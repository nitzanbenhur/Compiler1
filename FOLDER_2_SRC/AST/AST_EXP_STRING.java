package AST;
import TYPES.*;
import java.io.PrintWriter;

import IR.*;
import TEMP.TEMP;
import TEMP.TEMP_FACTORY;

public class AST_EXP_STRING extends AST_EXP
{
	public String value;
	public String label;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_STRING(String value)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		this.value = value.substring(1, value.length() - 1);
		System.out.format("====================== exp -> STRING( %s )\n", value);
	}

	/************************************************/
	/* The printing message for an INT EXP AST node */
	/************************************************/
	public void PrintMe()
	{
		/*******************************/
		/* AST NODE TYPE = AST INT EXP */
		/*******************************/
		System.out.format("AST NODE STRING( %s )\n",value);

		/*********************************/
		/* Print to AST GRAPHIZ DOT file */
		/*********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("STRING(%s)",value));
	}
	public TYPE SemantMe(PrintWriter file_writer, int scope, String param, int fpOffset)
	{
	
		this.fpOffset = fpOffset;
		this.param = param;
		return TYPE_STRING.getInstance();
	}
	
	public TEMP IRme() {
		
		TEMP dst = TEMP_FACTORY.getInstance().getFreshTEMP();
		
		IR.getInstance().Add_IRcommand(new IRcommand_Load_Address(dst, this.label));
		
		if (this.param != null){
			IR.getInstance().Add_IRcommand( new IRcommand_store_param_var( this.fpOffset, dst) );
		}
		
		return dst;
	}
	
	public void IRWriteStrings() {
		this.label = IRcommand_Label.getInstance().getLabel("String_exp");
		
		IR.getInstance().Add_IRcommand(new IRcommand_store_string_exp(this.label, this.value));
	}
}
