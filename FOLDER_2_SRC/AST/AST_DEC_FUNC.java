package AST;
import TYPES.*;
import java.io.PrintWriter;
import SYMBOL_TABLE.*;
import java_cup.runtime.ComplexSymbolFactory.Location;
import TEMP.*;
import IR.*;

public class AST_DEC_FUNC extends AST_DEC
{

	

	public String returnTypeName;
	
	

	public TYPE_CLASS cls;
	public AST_TYPE_NAME_LIST params;
	public AST_STMT_LIST body;
	public int line;
	
	// this is a tool I use to save the frame size.
	public static int frameSize;
	public static int paramCnt;
	public static String fName;
	public static String endLabel;
	// this is where I actually save it in the end.
	public int myParamCnt;
	public int myFrameSize;
	public String mylabel;
	public String myendLabel;
	
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_DEC_FUNC(String returnTypeName, String name, AST_TYPE_NAME_LIST params, AST_STMT_LIST body, int l)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		if (params == null){
			System.out.format("====================== funcDec -> ID(%s) ID(%s) LPAREN RPAREN LBRACE stmtList RBRACE\n",returnTypeName,name);	
		}
		else {
			System.out.format("====================== funcDec -> ID(%s) ID(%s) LPAREN idList RPAREN  LBRACE stmtList RBRACE\n",returnTypeName,name);
		}

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.returnTypeName = returnTypeName;
		this.name = name;
		this.params = params;
		this.body = body;
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
		System.out.print("AST NODE FUNC DEC\n");

		/**************************************/
		/* RECURSIVELY PRINT left + right ... */
		/**************************************/
		if (params != null) params.PrintMe();
		if (body != null) body.PrintMe();
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
		SerialNumber,
		"FUNC\nDEC\n");
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (params  != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,params.SerialNumber);
		if (body  != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,body.SerialNumber);
	}
	
	
	public TYPE SemantMe(PrintWriter file_writer, int scope)
	{
		TYPE t;
		TYPE returnType = null;
		TYPE_LIST type_list = null;
		AST_DEC_FUNC.frameSize = 0;
		AST_DEC_FUNC.paramCnt = 0;
		if (this.cls != null){
			AST_DEC_FUNC.paramCnt = 1;
		}
		AST_DEC_FUNC.fName = this.name;
		if ( this.name.equals( "main" ) ) {
			this.mylabel = this.name + "_function";
		}
		else {
			this.mylabel = IRcommand_Label.getInstance().getLabel(this.name);
		}
		this.myendLabel = IRcommand_Label.getInstance().getLabel("end" + this.name);
		AST_DEC_FUNC.endLabel = this.myendLabel;
		/*******************/
		/* [0] return type */
		/*******************/
		TYPE ts =  SYMBOL_TABLE.getInstance().find(name);
		if ( ts != null && ts.scope == scope  || name.equals("int") || name.equals("string") || name.equals("array") || name.equals("class") 
			|| name.equals("nil") || name.equals("if") || name.equals("while") || name.equals("return") ||
			name.equals("extends") || name.equals( "PrintInt" ) || name.equals( "PrintString") ) 
			{
			System.out.format(">> ERROR(%d) id %s is used in this scope\n",line,name);
			file_writer.printf("ERROR(%d)\n",line);
			file_writer.close();
			System.exit(0);
		}
		
		returnType = SYMBOL_TABLE.getInstance().find(returnTypeName);
		if (returnType == null || returnType instanceof TYPE_FUNCTION || returnType instanceof TYPE_VAR)
		{
			System.out.format(">> ERROR(%d) non existing return type %s\n",line,returnType);
			file_writer.printf("ERROR(%d)\n",line);
			file_writer.close();
			System.exit(0);
		}

	
		/****************************/
		/* [1] Begin Function Scope */
		/****************************/
		SYMBOL_TABLE.getInstance().beginScope();

		/***************************/
		/* [2] Semant Input Params */
		/***************************/
		if (params != null){
			type_list = params.SemantMe(file_writer,scope+1);
		}
		
		TYPE_FUNCTION recursive = new TYPE_FUNCTION(returnType,name,type_list,line,scope,AST_DEC_FUNC.paramCnt,this.mylabel,this.myendLabel);
		recursive.cls = this.cls;
		SYMBOL_TABLE.getInstance().enter(name,recursive,scope);
		/*******************/
		/* [3] Semant Body */
		/*******************/
		
		body.SemantMe(file_writer,scope+1,returnType);
		

		/*****************/
		/* [4] End Scope */
		/*****************/
		SYMBOL_TABLE.getInstance().endScope();

		/***************************************************/
		/* [5] Enter the Function Type to the Symbol Table */
		/***************************************************/
		TYPE_FUNCTION ret = new TYPE_FUNCTION(returnType,name,type_list,line,scope,AST_DEC_FUNC.paramCnt,this.mylabel,this.myendLabel);
		
		SYMBOL_TABLE.getInstance().enter(name,ret,scope);
		/*********************************************************/
		/* [6] Return value is irrelevant for class declarations */
		/*********************************************************/
		this.myParamCnt = AST_DEC_FUNC.paramCnt;
		this.myFrameSize = AST_DEC_FUNC.frameSize;
		return ret;		
	}
	
	
	public TEMP IRme()
	{
		
		IR.getInstance().Add_IRcommand( new IRcommand_Start_Func_Dec( this.mylabel,this.myFrameSize  ) );
		
		body.IRme();
		
		IR.getInstance().Add_IRcommand( new IRcommand_End_Func_Dec( this.myFrameSize,this.myendLabel,this.name ) );
		
		return null;
	}
	
	public void IRWriteStrings() {
		if (body != null) body.IRWriteStrings();
		if (params != null) params.IRWriteStrings();
	}
}
