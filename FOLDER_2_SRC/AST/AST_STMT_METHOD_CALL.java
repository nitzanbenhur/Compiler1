package AST;
import TYPES.*;
import java.io.PrintWriter;
import TEMP.*;
import IR.*;

public class AST_STMT_METHOD_CALL extends AST_STMT
{
	/***************/
	/*  var := exp */
	/***************/
	public AST_VAR cls;
	public String name;
	public AST_EXP_LIST args;
	public int myParamCnt;
	public int label_offset;
	public int line;
	
	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_STMT_METHOD_CALL(AST_VAR cls, String name, AST_EXP_LIST args, int l)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		if (args == null)System.out.format("====================== stmt -> var DOT ID(%s) LPAREN RPAREN SEMICOLON\n",name);
		if (args != null)System.out.format("====================== stmt -> var DOT ID(%s) LPAREN expList RPAREN SEMICOLON\n",name);
		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/

		this.cls = cls;
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
		System.out.print("AST NODE METHOD CALL STMT\n");

		if (cls != null) cls.PrintMe();
		if (args != null) args.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		if (args != null){
			AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"METHOD\nCALL\n");
		}
		else {
			AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"METHOD(expList)\nCALL\n");
		}
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/

		if (args != null)AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,args.SerialNumber);
		if (cls != null)AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,cls.SerialNumber);
	}
	
	public TYPE SemantMe(PrintWriter file_writer, int scope, TYPE ret)
	{
		
		TYPE cls_type = cls.SemantMe(file_writer,scope);
		if (!cls_type.isClass() ){
			System.out.format(">> ERROR(%d) field access to non class type variable %s\n",line,name);	
			file_writer.printf("ERROR(%d)\n",line);
			file_writer.close();
			System.exit(0);
		}
		
		TYPE_CLASS tc = (TYPE_CLASS) cls_type;
		label_offset = tc.functionNames.indexOf(name);
		
		TYPE_LIST typeArgs = null;
		
		if (args != null){
			typeArgs = args.SemantMe(file_writer,scope,"param",1);
		}
		
		while (tc != null){
			for ( TYPE_CLASS_METHODS l1 = tc.data_members.methods; l1 != null; l1 = l1.tail){
				if (l1.head.name.equals( name ) ){
					myParamCnt = l1.head.paramCnt;
					if (checkArgs(l1.head.params,typeArgs,file_writer) ){
						return null;
					}
				}
			}
			tc = tc.father;
		}

		System.out.format(">> ERROR(%d) there is no function %s \n",line,name);	
		file_writer.printf("ERROR(%d)\n",line);
		file_writer.close();
		System.exit(0);
		return null;
	}
	
	boolean checkArgs(TYPE_LIST l1, TYPE_LIST l2,PrintWriter file_writer){
		TYPE_LIST m2 = l2;
		for (TYPE_LIST m1 = l1; m1 != null; m1 = m1.tail){
			if( m2 == null ){
				System.out.format(">> ERROR(%d) incompatible type with type %s in function %s\n",line,name);	
				file_writer.printf("ERROR(%d)\n",line);
				file_writer.close();
				System.exit(0);
			}
			checkArg(m1.head,m2.head,file_writer);
			m2 = m2.tail;
		}
		if (m2 != null){
			System.out.format(">> ERROR(%d) too many arguments in function %s\n",line,name);	
			file_writer.printf("ERROR(%d)\n",line);
			file_writer.close();
			System.exit(0);
		}
		return true;
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
				if ( tc.name.equals( arg1.name ) ){
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
		TEMP dst =TEMP_FACTORY.getInstance().getFreshTEMP();
		TEMP func_address = TEMP_FACTORY.getInstance().getFreshTEMP();
		
		IR.getInstance().Add_IRcommand( new IRcommand_Set_Zero( dst ) );
		
		IR.getInstance().Add_IRcommand( new IRcommand_Init_Func_call( this.myParamCnt + 1 ,this.name ) );
		
		if (args != null) args.IRme();
		
		TEMP v = cls.IRme();
		
		IR.getInstance().Add_IRcommand(new IRcommand_Test_Invalid_Pointer(v));
		
		IR.getInstance().Add_IRcommand( new IRcommand_store_param_var(0, v) );
		
		IR.getInstance().Add_IRcommand( new IRcommand_Get_Label_from_FuncTable( func_address, v, label_offset ) );
		
		IR.getInstance().Add_IRcommand( new IRcommand_End_Method_call( dst, func_address, this.myParamCnt + 1) );
		
		return dst;		
	}
	
	public void IRWriteStrings() {
		if(cls != null) cls.IRWriteStrings();
		if(args != null) args.IRWriteStrings();
	}
}
