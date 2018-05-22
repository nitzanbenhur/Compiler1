package AST;
import TYPES.*;
import java.io.PrintWriter;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;

public class AST_EXP_FUNC_CALL extends AST_EXP
{
	/***************/
	/*  var := exp */
	/***************/
	public String name;
	public AST_EXP_LIST args;
	public int line;
	public int paramCnt;
	public String myLabel;
	public TYPE_CLASS cls = null;
	public int label_offset;
	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_EXP_FUNC_CALL(String name, AST_EXP_LIST args, int l)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		if (args == null)System.out.format("====================== exp -> ID(%s) LPAREN RPAREN\n",name);
		if (args != null)System.out.format("====================== exp -> ID(%s) LPAREN expList RPAREN\n",name);
		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/

		this.name = name;
		this.args = args;
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
		System.out.print("AST NODE FUNC CALL EXP\n");

		if (args != null) args.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		if (args != null){
			AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"FUNC\nCALL\n");
		}
		else {
			AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"FUNC(expList)\nCALL\n");
		}
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/

		if (args != null)AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,args.SerialNumber);
	}
	
	
	
	public TYPE SemantMe(PrintWriter file_writer, int scope, String param, int fpOffset)
	{
		TYPE t;
		this.fpOffset = fpOffset;
		this.param = param;
		t = SYMBOL_TABLE.getInstance().find(name);	
		if (t == null || !t.isFunc() ){
			System.out.format(">> ERROR(%d) non existing function %s\n",line,name);	
			file_writer.printf("ERROR(%d)\n",line);
			file_writer.close();
			System.exit(0);
		}
		TYPE_FUNCTION tf = (TYPE_FUNCTION) t;
		this.paramCnt = tf.paramCnt;
		this.myLabel = tf.label;
		TYPE_LIST tl = tf.params;
		this.cls = tf.cls;
		int offset = 0;
		for (AST_EXP_LIST args1 = args; args1 != null; args1 = args1.tail){
			TYPE arg = args1.head.SemantMe(file_writer,scope,"param",((this.cls != null)? ++offset: offset++));
			if( tl == null ){
				System.out.format(">> ERROR(%d) incompatible type with type %s in function %s\n",line,name);	
				file_writer.printf("ERROR(%d)\n",line);
				file_writer.close();
				System.exit(0);
			}
			checkArg(tl.head,arg,file_writer);
			tl = tl.tail;
		}
		if (tl != null){
			System.out.format(">> ERROR(%d) too many arguments in function %s\n",line,name);	
			file_writer.printf("ERROR(%d)\n",line);
			file_writer.close();
			System.exit(0);
		}
		return tf.returnType;
	}
	
	void checkArg(TYPE arg1, TYPE arg2, PrintWriter file_writer){
		if (arg1.name.equals( arg2.name ) ){
			return;
		}
		else if ( ( arg1.isClass() || arg1.isArray() ) && arg2 instanceof TYPE_NIL ){
			return;
		}
		else if ( arg1.isArray() && arg2 instanceof  TYPE_ARRAY_NEW){
			TYPE_ARRAY ta = (TYPE_ARRAY) arg1;
			TYPE_ARRAY_NEW tan = (TYPE_ARRAY_NEW) arg2; 
			if ( ta.dataType.name.equals(tan.dataType.name) ){
				return;
			}
		}
		else if ( arg1.isClass() && arg2.isClass() ){
			TYPE_CLASS tc = (TYPE_CLASS) arg2;
			while ( tc != null ){
				if ( tc.name.equals(arg1.name) ){
					return;
				}
				tc = tc.father;
			}
		}
		System.out.format(">> ERROR(%d) too many arguments in function %s\n",line,name);	
		file_writer.printf("ERROR(%d)\n",line);
		file_writer.close();
		System.exit(0);	
	}
	
	public TEMP IRme()
	{
		if (this.cls != null){
			label_offset = this.cls.functionNames.indexOf(name);
		}
		
		TEMP t=TEMP_FACTORY.getInstance().getFreshTEMP();
			
		TEMP v = TEMP_FACTORY.getInstance().getFreshTEMP();
		IR.getInstance().Add_IRcommand(new IRcommand_load_param_var(0, v));
		
		IR.getInstance().Add_IRcommand( new IRcommand_Init_Func_call( (this.cls != null)? this.paramCnt + 1 : this.paramCnt,this.name) );
		
		if (args != null) { args.IRme(); }
		
		if(this.cls != null) {
			TEMP func_address = TEMP_FACTORY.getInstance().getFreshTEMP();
			
			IR.getInstance().Add_IRcommand(new IRcommand_store_param_var(0, v));
			IR.getInstance().Add_IRcommand( new IRcommand_Get_Label_from_FuncTable( func_address, v, label_offset ) );
			
			IR.getInstance().Add_IRcommand( new IRcommand_End_Method_call( t,func_address,this.paramCnt + 1) );
		}
		else if ( name.equals("PrintInt") ){
			IR.getInstance().Add_IRcommand(new IRcommandPrintInt());
		}
		else if ( name.equals("PrintString") ){
			IR.getInstance().Add_IRcommand(new IRcommandPrintString() );
		}
		else {
			IR.getInstance().Add_IRcommand( new IRcommand_End_Func_call( t,this.myLabel,this.paramCnt ) );
		}
		
		if (this.param != null){
			IR.getInstance().Add_IRcommand( new IRcommand_store_param_var( this.fpOffset,t ) );
		}
		
		return t;
	}
	
	public void IRWriteStrings() {
		if (args != null) args.IRWriteStrings();
	}
}
