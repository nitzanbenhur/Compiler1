/***********/
/* PACKAGE */
/***********/
package AST;
import java.io.PrintWriter;
import TEMP.*;
import IR.*;

/*******************/
/* PROJECT IMPORTS */
/*******************/
import TYPES.*;
import SYMBOL_TABLE.*;

public class AST_TYPE_NAME extends AST_Node
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public String type;
	public String name;
	public int line;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_TYPE_NAME(String type,String name, int l)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();
	
		this.type = type;
		this.name = name;
		this.line = l;
	}

	/*************************************************/
	/* The printing message for a type name AST node */
	/*************************************************/
	public void PrintMe()
	{
		/**************************************/
		/* AST NODE TYPE = AST TYPE NAME NODE */
		/**************************************/
		System.out.format("NAME(%s):TYPE(%s)\n",name,type);

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("NAME:TYPE\n%s:%s",name,type));
	}

	/*****************/
	/* SEMANT ME ... */
	/*****************/
	public TYPE SemantMe(PrintWriter file_writer, int scope)
	{
		
		TYPE t = SYMBOL_TABLE.getInstance().find(type);
		TYPE t2 = SYMBOL_TABLE.getInstance().find(name);
		
		if (t2 != null && t2.scope == scope || name.equals("int") || name.equals("string") || name.equals("array") || name.equals("class") 
		|| name.equals("nil") || name.equals("if") || name.equals("while") || name.equals("return") ||
		name.equals("extends") || t2 instanceof TYPE_CLASS )
		{
			System.out.format(">> ERROR(%s) variable %s already exists in scope\n",line,name);	
			file_writer.printf("ERROR(%d)\n",line);
			file_writer.close();
			System.exit(0);
		}
		
		if (t == null)
		{
			/**************************/
			/* ERROR: undeclared type */
			/**************************/
			System.out.format(">> ERROR(%d) undeclared type %s\n",line,name);	
			file_writer.printf("ERROR(%d)\n",line);
			file_writer.close();
			System.exit(0);
			return null;
		}
		else
		{
			/*******************************************************/
			/* Enter var with name=name and type=t to symbol table */
			/*******************************************************/
			SYMBOL_TABLE.getInstance().enter(name,new TYPE_VAR(name,t,line,scope,AST_DEC_FUNC.paramCnt++,"param") ,scope);
		}
		/****************************/
		/* return (existing) type t */
		/****************************/
		return t;
	}
	public TEMP IRme()
	{
		return null; // Do Nothing! ha ha ha ...
	}
	
	public void IRWriteStrings() {
		//do nothing
	}
	
}
