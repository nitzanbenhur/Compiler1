package AST;
import TYPES.*;
import java.io.PrintWriter;
import TEMP.*;
import IR.*;

public class AST_STMT_RETURN extends AST_STMT
{
	/***************/
	/*  var := exp */
	/***************/
	public AST_EXP exp;
	public int line;
	public String myLabel;
	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_STMT_RETURN(AST_EXP exp, int l)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		if (exp == null)System.out.print("====================== stmt -> RETRUN SEMICOLON\n");
		if (exp != null)System.out.print("====================== stmt -> RETRUN exp SEMICOLON\n");
		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/

		this.line = l;
		this.exp = exp;
	}

	/*********************************************************/
	/* The printing message for an assign statement AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/********************************************/
		/* AST NODE TYPE = AST ASSIGNMENT STATEMENT */
		/********************************************/
		System.out.print("AST NODE RETURN STMT\n");

		if (exp != null) exp.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		if (exp != null){
			AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"RETURN exp;");
		}
		else {
			AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"RETURN;");
		}
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/

		if (exp != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,exp.SerialNumber);
	}
	public TYPE SemantMe(PrintWriter file_writer, int scope , TYPE ret)
	{
		
		this.myLabel = AST_DEC_FUNC.endLabel;
		TYPE t = null;
		if (exp != null){
			t = exp.SemantMe(file_writer,scope,null,0);
		}
		if (t == null && ret instanceof TYPE_VOID ){
			return null;
		}
		if (t instanceof TYPE_VAR){
			TYPE_VAR tv = (TYPE_VAR) t;
			t = tv.type;
		}
		
		if ( (ret instanceof TYPE_VOID) && (t != null) ){
			System.out.format(">> ERROR(%d) void function cannot return value\n",line);
			file_writer.printf("ERROR(%d)\n",line);
			file_writer.close();
			System.exit(0);
		}
		else if (t == ret ){
			return t;
		}
		else if ( ( ret.isClass() || ret.isArray() ) && ( t instanceof TYPE_NIL) ){
			return ret;
		}
		else if ( ret.isClass() && t.isClass() ){
			TYPE_CLASS tc = (TYPE_CLASS) t;
			while (tc != null) {
				if (tc == ret){
					return tc;
				}
				tc = tc.father;
			}
		}
		else {
			System.out.format(">> ERROR(%d) return type doesn't match return value.\n",line);
			file_writer.printf("ERROR(%d)\n",line);
			file_writer.close();
			System.exit(0);
		}
		return null;
	}
	
	public TEMP IRme()
	{
		TEMP t = null;
		if (exp != null){
			t = exp.IRme();
		}
		IR.getInstance().Add_IRcommand( new IRcommand_Return( t,this.myLabel ) );
		return null;
	}
	
	public void IRWriteStrings() {
		if(exp != null) exp.IRWriteStrings();
	}
}
