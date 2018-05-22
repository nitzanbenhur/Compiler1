package AST;
import TYPES.*;
import java.io.PrintWriter;
import SYMBOL_TABLE.*;
import java_cup.runtime.ComplexSymbolFactory.Location;
import TEMP.*;
import IR.*;

public class AST_DEC_ARRAY extends AST_DEC
{
	public String type;
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_DEC_ARRAY(String name, String type, int l)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		
		System.out.printf("====================== arrayDec -> ARRAY ID(%s) EQ ID(%s)\n",name,type);	

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.name = name;
		this.type = type;
		this.line = l;
	}
	
	/*************************************************/
	/* The printing message for a binop exp AST node */
	/*************************************************/
	public void PrintMe()
	{
		
		/*************************************/
		/* AST NODE TYPE = AST SUBSCRIPT VAR */
		/*************************************/
		System.out.print("AST NODE ARRAY DEC\n");


		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		AST_GRAPHVIZ.getInstance().logNode(
		SerialNumber,
		"ARRAY\nDEC\n");
		}
		
	public TYPE SemantMe(PrintWriter file_writer, int scope)
	{
		TYPE t;
		if (scope != 0){
			System.out.format(">> ERROR(%d) arrays can only be defiend in global scope\n",line);
			file_writer.printf("ERROR(%d)\n",line);
			file_writer.close();
			System.exit(0);
		}
		/****************************/
		/* [1] Check If Type exists */
		/****************************/
		t = SYMBOL_TABLE.getInstance().find(type);
		if (t == null || t instanceof TYPE_VAR || t instanceof TYPE_FUNCTION   )
		{
			System.out.format(">> ERROR(%d) non existing type %s\n",line,type);
			file_writer.printf("ERROR(%d)\n",line);
			file_writer.close();
			System.exit(0);
		}
		
		/**************************************/
		/* [2] Check That Name does NOT exist */
		/**************************************/
		if (SYMBOL_TABLE.getInstance().find(name) != null  || name.equals( "PrintInt" ) || name.equals( "PrintString") )
		{
			System.out.format(">> ERROR(%d) variable %s already exists in scope\n",line,name);	
			file_writer.printf("ERROR(%d)\n",line);
			file_writer.close();
			System.exit(0);
		}
		/***************************************************/
		/* [3] Enter the Function Type to the Symbol Table */
		/***************************************************/
		SYMBOL_TABLE.getInstance().enter(name,new TYPE_ARRAY(name,t,scope),scope);
		/*********************************************************/
		/* [4] Return value is irrelevant for class declarations */
		/*********************************************************/
		return null;		
	}
	
	public TEMP IRme()
	{
		return null; // Do Nothing! ha ha ha ...
	}
	
	public void IRWriteStrings() {
		//do nothing
	}
	
	
}
