package AST;
import TYPES.*;
import java.io.PrintWriter;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;

public class AST_STMT_FUNC_CALL extends AST_STMT
{
	/***************/
	/*  var := exp */
	/***************/
	public String name;
	public AST_EXP_LIST args;
	public int line;
	public int myParamCnt;
	public String myLabel;
	public TYPE_CLASS cls = null;
	public int label_offset;
	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_STMT_FUNC_CALL(String name, AST_EXP_LIST args, int l)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		if (args == null)System.out.format("====================== stmt -> ID(%s) LPAREN RPAREN SEMICOLON\n",name);
		if (args != null)System.out.format("====================== stmt -> ID(%s) LPAREN expList RPAREN SEMICOLON\n",name);
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
		System.out.print("AST NODE FUNC CALL STMT\n");

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
	
	public TYPE SemantMe(PrintWriter file_writer, int scope, TYPE ret)
	{
		TYPE t;
		t = SYMBOL_TABLE.getInstance().find(name);	
		if (t == null || !t.isFunc() ){
			System.out.format(">> ERROR(%d) non existing function %s\n",line,name);	
			file_writer.printf("ERROR(%d)\n",line);
			file_writer.close();
			System.exit(0);
		}

		TYPE_FUNCTION tf = (TYPE_FUNCTION) t;
		this.myParamCnt = tf.paramCnt;
		this.myLabel = tf.label;
		TYPE_LIST tl = tf.params;
		this.cls = tf.cls;
		int offset = 0;
		for (AST_EXP_LIST args1 = args; args1 != null; args1 = args1.tail){
			TYPE arg = args1.head.SemantMe(file_writer,scope,"param",((this.cls != null)? ++offset: offset++));
			if (tl == null ){
				System.out.format(">> ERROR(%d) incompatible type %s in function: %s\n",line,tl.head.name,name);	
				file_writer.printf("ERROR(%d)\n",line);
				file_writer.close();
				System.exit(0);
			}
			checkArg(tl.head,arg,file_writer);
			tl = tl.tail;
		}
		if (tl != null){
			System.out.format(">> ERROR(%d) too many arguments in function %s tl: %s\n",line,name,tl);	
			file_writer.printf("ERROR(%d)\n",line);
			file_writer.close();
			System.exit(0);
		}
		return null;	// return value is not used in call stmt no need to return it.
	}
	
		
	void checkArg(TYPE arg1, TYPE arg2, PrintWriter file_writer){
		if (arg1.equals( arg2 ) ){
			return;
		}
		else if ( ( arg1.isClass() || arg1.isArray() ) && arg2 instanceof TYPE_NIL ){
			return;
		}
		else if ( arg1.isArray() && arg2 instanceof  TYPE_ARRAY_NEW){
			TYPE_ARRAY ta = (TYPE_ARRAY) arg1;
			 TYPE_ARRAY_NEW tan = (TYPE_ARRAY_NEW) arg2; 
			if ( ta.dataType.name.equals( tan.dataType.name ) ){
				return;
			}
		}
		else if ( arg1.isClass() && arg2.isClass() ){
			TYPE_CLASS tc = (TYPE_CLASS) arg2;
			while ( tc != null ){
				if ( tc.name.equals( arg1.name ) ){
					return;
				}
				tc = tc.father;
			}
		}
		System.out.format(">> ERROR(%d) incompatible type in function: %s\n",line,name);	
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
		
		IR.getInstance().Add_IRcommand( new IRcommand_Init_Func_call( (this.cls != null)? this.myParamCnt + 1 : this.myParamCnt, this.name) );
		
		if (args != null) { args.IRme(); }
		
		if(this.cls != null) {
			System.out.println("--------------------- " + this.name);
			
			TEMP func_address = TEMP_FACTORY.getInstance().getFreshTEMP();
			
			IR.getInstance().Add_IRcommand(new IRcommand_store_param_var(0, v));
			
			IR.getInstance().Add_IRcommand( new IRcommand_Get_Label_from_FuncTable( func_address, v, label_offset ) );
			
			IR.getInstance().Add_IRcommand( new IRcommand_End_Method_call( t,func_address,this.myParamCnt + 1) );
		}
		else if ( name.equals("PrintInt") ){
			IR.getInstance().Add_IRcommand(new IRcommandPrintInt());
		}
		else if ( name.equals("PrintString") ){
			IR.getInstance().Add_IRcommand(new IRcommandPrintString() );
		}
		else {
			System.out.println("--------------------- this was taken " + this.name + " " + this.cls);
			IR.getInstance().Add_IRcommand( new IRcommand_End_Func_call( t,this.myLabel,this.myParamCnt ) );
		}
		return t;
	}
	
	public void IRWriteStrings() {
		if(args != null) args.IRWriteStrings();
	}
}
