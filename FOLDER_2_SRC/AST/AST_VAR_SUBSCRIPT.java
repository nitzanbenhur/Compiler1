package AST;
import TYPES.*;
import java.io.PrintWriter;
import TEMP.*;
import IR.*;

public class AST_VAR_SUBSCRIPT extends AST_VAR
{
	public AST_VAR var;
	public AST_EXP subscript;
	public int line;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_VAR_SUBSCRIPT(AST_VAR var,AST_EXP subscript, int l)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== var -> var [ exp ]\n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.var = var;
		this.subscript = subscript;
		this.line = l;
	}

	/*****************************************************/
	/* The printing message for a subscript var AST node */
	/*****************************************************/
	public void PrintMe()
	{
		/*************************************/
		/* AST NODE TYPE = AST SUBSCRIPT VAR */
		/*************************************/
		System.out.print("AST NODE SUBSCRIPT VAR\n");

		/****************************************/
		/* RECURSIVELY PRINT VAR + SUBSRIPT ... */
		/****************************************/
		if (var != null) var.PrintMe();
		if (subscript != null) subscript.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"SUBSCRIPT\nVAR\n...[...]");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (var       != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,var.SerialNumber);
		if (subscript != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,subscript.SerialNumber);
	}
	public TYPE SemantMe(PrintWriter file_writer, int scope)
	{
		TYPE tv = var.SemantMe(file_writer,scope);
		TYPE te = subscript.SemantMe(file_writer,scope,null,0);
		if (!tv.isArray() ){
			System.out.format(">> ERROR(%d) index acces to non array type %s.\n",line,tv.name);							
			file_writer.printf("ERROR(%d)\n",line);
			file_writer.close();
			System.exit(0);
		}
		
		if (te != TYPE_INT.getInstance() ){
			System.out.format(">> ERROR(%d) non integer type used for array index accees.\n",line);							
			file_writer.printf("ERROR(%d)\n",line);
			file_writer.close();
			System.exit(0);
		}
		TYPE_ARRAY ta = (TYPE_ARRAY) tv;
		return ta.dataType;
	}

		public TEMP IRme() {
		
		TEMP var = null;
		TEMP subscript = null;
		TEMP dst = TEMP_FACTORY.getInstance().getFreshTEMP();
		
		if (this.var  != null) 			var = this.var.IRme();
		if (this.subscript  != null) 	subscript = this.subscript.IRme();
		
		IR.getInstance().Add_IRcommand(new IRcommand_Var_Subscript_Address(dst,var,subscript));
		
		return dst;
	}
	
	public void IRWriteStrings() {
		if(var != null) var.IRWriteStrings();
		if(subscript != null) subscript.IRWriteStrings();
	}
}
