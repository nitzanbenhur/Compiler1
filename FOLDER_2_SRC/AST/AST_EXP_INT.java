package AST;
import java.io.PrintWriter;
import TYPES.*;
import TEMP.*;
import IR.*;

public class AST_EXP_INT extends AST_EXP
{
	public int value;
	public int sign;
	public int line;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_INT(int value,int sign, int l,PrintWriter file_writer)
	{
		if ((sign == -1 && value > Math.pow(2,15)) || 
			(sign == 1 && value > Math.pow(2,15)-1)){
			file_writer.printf("ERROR(%d)\n",line);
			file_writer.close();
			System.exit(0);	
		}
			
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		if (sign == 1){
			System.out.format("====================== exp -> INT( %d )\n", value);
		}
		if (sign == -1){
			System.out.format("====================== exp -> MINUS INT( %d )\n", value);
		}

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.value = value;
		this.sign = sign;
		this.line = l;
	}

	/************************************************/
	/* The printing message for an INT EXP AST node */
	/************************************************/
	public void PrintMe()
	{
		/*******************************/
		/* AST NODE TYPE = AST INT EXP */
		/*******************************/
		System.out.format("AST NODE INT( %d )\n",sign*value);

		/*********************************/
		/* Print to AST GRAPHIZ DOT file */
		/*********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("INT(%d)",(this.sign)*(this.value)));
	}
	
	public TYPE SemantMe(PrintWriter file_writer, int scope, String param, int fpOffset)
	{
	
		this.fpOffset = fpOffset;
		this.param = param;
		return TYPE_INT.getInstance();
	}
	
	public TEMP IRme()
	{
		TEMP t = TEMP_FACTORY.getInstance().getFreshTEMP();
		IR.getInstance().Add_IRcommand( new IRcommandConstInt(t,value*sign) );
		
		if (this.param != null){
			IR.getInstance().Add_IRcommand( new IRcommand_store_param_var( this.fpOffset,t ) );
		}
		
		return t;
	}
	
	public void IRWriteStrings() {
		//do nothing
	}
	
}
