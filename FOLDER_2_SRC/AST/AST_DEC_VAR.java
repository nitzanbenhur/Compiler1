package AST;
import java.io.PrintWriter;
import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;

public class AST_DEC_VAR extends AST_DEC
{
	public String type;
	public AST_EXP exp;
	
	public String status;
	public int fpOffset;
	
	public int line;
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_DEC_VAR(String type, String name, AST_EXP exp,int l)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		
		
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		if (exp == null){
			System.out.format("====================== varDec -> ID(%s) ID(%s) SEMICOLON\n",type,name);	
		}
		else {
			System.out.format("====================== varDec -> ID(%s) ID(%s) ASSIGN exp SEMICOLON\n",type,name);
		}

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.type = type;
		this.name = name;
		this.exp = exp;
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
		System.out.print("AST NODE VAR DEC\n");

		/**************************************/
		/* RECURSIVELY PRINT left + right ... */
		/**************************************/
		if (exp != null) exp.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
		SerialNumber,
		"VAR\nDEC\n");
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (exp  != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,exp.SerialNumber);
	}
	
	public TYPE SemantMe(PrintWriter file_writer, int scope, int fpOffset, String status)
	{
		this.fpOffset = fpOffset;
		this.status = status;
		TYPE t;
		/****************************/
		/* [1] Check If Type exists */
		/****************************/
		t = SYMBOL_TABLE.getInstance().find(type);

		
		if (t == null || t instanceof TYPE_VAR ||  t instanceof TYPE_FUNCTION || t instanceof TYPE_VOID)
		{
		
			System.out.format(">> ERROR(%s) non existing type %s \n",line,type);
			file_writer.printf("ERROR(%d)\n",line);
			file_writer.close();
			System.exit(0);
		}
		
		/**************************************/
		/* [2] Check That Name does NOT exist */
		/**************************************/
		TYPE t2 = SYMBOL_TABLE.getInstance().find(name) ;
		if (t2 != null && t2.scope == scope || name.equals("int") || name.equals("string") || name.equals("array") || name.equals("class") 
		|| name.equals("nil") || name.equals("if") || name.equals("while") || name.equals("return") ||
		name.equals("extends") || name.equals( "PrintInt" ) || name.equals( "PrintString") )
		{
			System.out.format(">> ERROR(%s) variable %s already exists in scope\n",line,name);	
			file_writer.printf("ERROR(%d)\n",line);
			file_writer.close();
			System.exit(0);
		}
		if (exp != null){
			TYPE t3 = exp.SemantMe(file_writer,scope,null,0);
			checkTypes(t,t3,file_writer);
			
		}
		/***************************************************/
		/* [3] Enter the Function Type to the Symbol Table */
		/***************************************************/
		TYPE_VAR s = new TYPE_VAR( name, t, line,scope, this.fpOffset, this.status );
		SYMBOL_TABLE.getInstance().enter( name, s, scope );
		/*********************************************************/
		/* [4] Return value is irrelevant for class declarations */
		/*********************************************************/
		return s;		
	}
	
	public void checkTypes(TYPE t1, TYPE t2,PrintWriter file_writer){
		if (t1.equals( t2 ) ){
			return;
		}
		if ( ( t1.isArray() || t1.isClass() ) && (t2 instanceof TYPE_NIL) ) {
			return;
		}
		if ( t1.isClass() && t2.isClass() ){
			TYPE_CLASS tc = (TYPE_CLASS) t2;
			while (tc != null){
				
				if ( tc.name.equals( t1.name ) ){
					return;
				}
				tc = tc.father;
			}
		}
		if (t1.isArray() && t2 instanceof TYPE_ARRAY_NEW  ){
			TYPE_ARRAY ta = (TYPE_ARRAY) t1;
			TYPE_ARRAY_NEW ta2 = (TYPE_ARRAY_NEW) t2;
			if (ta.dataType == ta2.dataType){
				return;
			}
		}
		
		System.out.format(">> ERROR(%d) type mismatch for var := exp\n",line);
		file_writer.printf("ERROR(%d)\n",line);
		file_writer.close();
		System.exit(0);
		return;
	}
	
	public void IRglobalVar() {
		IR.getInstance().Add_IRcommand( new IRcommand_add_global_var( this.name ) );
	}
	
	
	public TEMP IRme()
	{
		TEMP t = null;
		
		if ( this.status.equals( "global" ) ){
			return null;
		}
		
		if ( exp == null ) {
		 	t = TEMP_FACTORY.getInstance().getFreshTEMP();
			IR.getInstance().Add_IRcommand( new IRcommand_Set_Zero( t ) );
		}
		else {
			t = exp.IRme();	
		}
		if (this.status.equals( "local" ) ){
			IR.getInstance().Add_IRcommand( new IRcommand_store_local_var( this.fpOffset,t ) );
		}
		else if ( this.status.equals( "param" ) ){
			IR.getInstance().Add_IRcommand( new IRcommand_change_field_var( this.fpOffset,t ) );
		}
		else if ( this.status.equals( "classField" ) ){
			return null;
		}
		else {
			System.out.print( " somthing wrong we should never get here. @@@\n" );
			return null;
		}
		return null;
	}
	
	public void IRWriteStrings() {
		if (exp != null) exp.IRWriteStrings();
	}
	
	
}