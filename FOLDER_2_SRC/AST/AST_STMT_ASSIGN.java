package AST;
import java.io.PrintWriter;
import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;

public class AST_STMT_ASSIGN extends AST_STMT
{
	/***************/
	/*  var := exp */
	/***************/
	public AST_VAR var;
	public AST_EXP exp;
	public int line;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_STMT_ASSIGN(AST_VAR var,AST_EXP exp, int l)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== stmt -> var ASSIGN exp SEMICOLON\n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.var = var;
		this.exp = exp;
		this.line = l;
	}

	/*********************************************************/
	/* The printing message for an assign statement AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/********************************************/
		/* AST NODE TYPE = AST ASSIGNMENT STATEMENT */
		/********************************************/
		System.out.print("AST NODE ASSIGN STMT\n");

		/***********************************/
		/* RECURSIVELY PRINT VAR + EXP ... */
		/***********************************/
		if (var != null) var.PrintMe();
		if (exp != null) exp.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"ASSIGN\nleft := right\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,var.SerialNumber);
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,exp.SerialNumber);
	}
	
	public TYPE SemantMe(PrintWriter file_writer, int scope, TYPE ret)
	{
		TYPE t1 = null;
		TYPE t2 = null;
		if (var != null) t1 = var.SemantMe(file_writer,scope);
		if (exp != null) t2 = exp.SemantMe(file_writer,scope,null,0);
		
		if (t1 == t2){
			return null;
		}
		if ( ( t1.isArray() || t1.isClass() ) && (t2 instanceof TYPE_NIL) ){
			return null;
		}
		if ( t1.isClass() && t2.isClass() ){
			TYPE_CLASS tc = (TYPE_CLASS) t2;
			while (tc != null){
				
				if ( tc.name == t1.name ){
					return null;
				}
				tc = tc.father;
			}
		}
		
		if (t1.isArray() && t2 instanceof TYPE_ARRAY_NEW  ){
			TYPE_ARRAY ta = (TYPE_ARRAY) t1;
			TYPE_ARRAY_NEW ta2 = (TYPE_ARRAY_NEW) t2;
			if (ta.dataType == ta2.dataType){
				return null;
			}
		}
		System.out.format(">> ERROR(%d) type mismatch for var: %s := exp: %s\n",line,t1.name,t2.name);
		file_writer.printf("ERROR(%d)\n",line);
		file_writer.close();
		System.exit(0);
		return null;
	}
	
	public TEMP IRme()
	{
		TEMP t2 = null;
		//IR.getInstance().Add_IRcommand( new IRcommand_Assign(t1,t2) );
		if (var instanceof AST_VAR_SIMPLE){
			AST_VAR_SIMPLE tv = (AST_VAR_SIMPLE) var;
			t2 = exp.IRme();
			if ( tv.status.equals( "global" ) ){
				IR.getInstance().Add_IRcommand( new IRcommand_store_global_var( t2,tv.name) );
			}
			else if ( tv.status.equals( "local" ) ){
				IR.getInstance().Add_IRcommand( new IRcommand_store_local_var(tv.fpOffset,t2) );
			}
			else if ( tv.status.equals( "param" ) ){
				IR.getInstance().Add_IRcommand( new IRcommand_change_param_var(tv.fpOffset,t2) );
			}
			else if ( tv.status.equals( "classField" ) ) {
				IR.getInstance().Add_IRcommand( new IRcommand_change_field_var(tv.classOffset,t2) );
			}
			else {
				System.out.println("something wrong we should never get here.\n");
			}
		}
		else if ( var instanceof AST_VAR_SUBSCRIPT ){
			AST_VAR_SUBSCRIPT sv = (AST_VAR_SUBSCRIPT) var;
			TEMP v = null;
			TEMP subscript = null;
			if (sv.subscript  != null) 	subscript = sv.subscript.IRme();
			if (sv.var  != null) 			v = sv.var.IRme();
			t2 = exp.IRme();
			IR.getInstance().Add_IRcommand( new IRcommand_change_Var_Subscript(t2,v,subscript) );
		}
		else if ( var instanceof AST_VAR_FIELD ) {
			AST_VAR_FIELD vf = (AST_VAR_FIELD) var;
			TYPE_CLASS tc = vf.type_class;
			
			TEMP var_temp = vf.var.IRme();
			t2 = exp.IRme();
			
			IR.getInstance().Add_IRcommand(new IRcommand_Test_Invalid_Pointer(var_temp));
			
			
			int offset = tc.fieldNames.indexOf(vf.fieldName) + 1;
			
			IR.getInstance().Add_IRcommand(new IRcommand_Add_Offset(var_temp,var_temp,4*offset));
			IR.getInstance().Add_IRcommand(new IRcommand_Store(var_temp, t2));
		}
		return null;
	}
	
	public void IRWriteStrings() {
		if(var != null) var.IRWriteStrings();
		if(exp != null) exp.IRWriteStrings();
	}
	
}
