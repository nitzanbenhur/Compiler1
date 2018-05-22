package AST;
import TYPES.*;
import java.io.PrintWriter;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;

public class AST_VAR_SIMPLE extends AST_VAR
{
	/************************/
	/* simple variable name */
	/************************/
	public int line;
	
	public String status;
	public int fpOffset;
	public int classOffset;
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_VAR_SIMPLE(String name, int l)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();
	
		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== var -> ID( %s )\n",name);

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.name = name;
		this.line = l;
	}

	/**************************************************/
	/* The printing message for a simple var AST node */
	/**************************************************/
	public void PrintMe()
	{
		/**********************************/
		/* AST NODE TYPE = AST SIMPLE VAR */
		/**********************************/
		System.out.format("AST NODE SIMPLE VAR( %s )\n",name);

		/*********************************/
		/* Print to AST GRAPHIZ DOT file */
		/*********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("SIMPLE\nVAR\n(%s)",name));
	}
	public TYPE SemantMe(PrintWriter file_writer, int scope)
	{
		
		TYPE t = SYMBOL_TABLE.getInstance().find(name);
		if (t == null  || !(t instanceof TYPE_VAR) ){
			System.out.format(">> ERROR(%d) var %s does not exist in this context.\n",line,name);							
			file_writer.printf("ERROR(%d)\n",line);
			file_writer.close();
			System.exit(0);
		}
		TYPE_VAR tv = (TYPE_VAR) t;
		if (tv.cls != null){
			TYPE t2 = SYMBOL_TABLE.getInstance().find(tv.cls);
			TYPE_CLASS tc = (TYPE_CLASS) t2;
			this.classOffset = tc.fieldNames.indexOf( this.name ) + 1;
		}
		
		this.fpOffset = tv.fpOffset;
		this.status = tv.status;
		return tv.type;
	}
	
	
	
	public TEMP IRme()
	{
		TEMP t = TEMP_FACTORY.getInstance().getFreshTEMP();
		if ( this.status.equals( "global" ) ){
			IR.getInstance().Add_IRcommand( new IRcommand_load_global_var(t,this.name) );
			return t;
		}
		else if (this.status.equals( "local" ) ){
			IR.getInstance().Add_IRcommand( new IRcommand_load_local_var( this.fpOffset,t ) );
			return t;
		}
		else if ( this.status.equals( "param" ) ){
			IR.getInstance().Add_IRcommand( new IRcommand_load_param_var( this.fpOffset,t ) );
			return t;
		}
		else if ( this.status.equals( "classField" ) ){
			IR.getInstance().Add_IRcommand( new IRcommand_load_field_var( this.classOffset,t ) );
			return t;
		}
		else {
			System.out.print( " somthing wrong we should never get here. @ \n" );
			return null;
		}
	}
	
	public void IRWriteStrings() {
		//do nothing
	}
}