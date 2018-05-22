package AST;
import TYPES.*;
import java.io.PrintWriter;
import java.util.ArrayList;

import IR.*;
import SYMBOL_TABLE.*;
import TEMP.TEMP;

public class AST_STMT_WHILE extends AST_STMT
{
	/***************/
	/*  var := exp */
	/***************/
	public AST_EXP cond;
	public AST_STMT_LIST body;
	public int line;
	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_STMT_WHILE(AST_EXP cond,AST_STMT_LIST body, int l)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== stmt -> WHILE LPAREN exp RPAREN LBRACE stmtList RBRACEN\n");
		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.body = body;
		this.cond = cond;
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
		System.out.print("AST NODE WHILE STMT\n");

		if (cond != null) cond.PrintMe();
		if (body != null) body.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
		SerialNumber,
		"STMT\nWHILE\n");
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/

		if (body != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,body.SerialNumber);
		if (cond != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,cond.SerialNumber);
	}
	
	public TYPE SemantMe(PrintWriter file_writer, int scope, TYPE ret)
	{
		/****************************/
		/* [0] Semant the Condition */
		/****************************/
		if (cond.SemantMe( file_writer,scope,null,0) != TYPE_INT.getInstance() )
		{
			System.out.format(">> ERROR(%d) condition inside while is not integral\n",line);
			file_writer.printf("ERROR(%d)\n",line);
			file_writer.close();
			System.exit(0);
		}
		
		/*************************/
		/* [1] Begin Class Scope */
		/*************************/
		SYMBOL_TABLE.getInstance().beginScope();

		/***************************/
		/* [2] Semant Data Members */
		/***************************/
		body.SemantMe(file_writer,scope + 1 ,ret);

		/*****************/
		/* [3] End Scope */
		/*****************/
		SYMBOL_TABLE.getInstance().endScope();

		/*********************************************************/
		/* [4] Return value is irrelevant for class declarations */
		/*********************************************************/
		return null;		
	}
	
	public TEMP IRme()
	{	
		TEMP condTemp = null;
		
		ArrayList<String> labels = new ArrayList<String>();
		
		IR.getInstance().Add_IRcommand(new IRcommand_While_Pre_Body(condTemp, labels));
		
		if(body != null) body.IRme();
		
		IR.getInstance().Add_IRcommand(new IRcommand_While_Post_Body(condTemp, labels));
		
		if(cond != null) condTemp = cond.IRme();
		
		IR.getInstance().Add_IRcommand(new IRcommand_While_Post_Cond(condTemp, labels));
		
		return null;
	}
	
	public void IRWriteStrings() {
		if(cond != null) cond.IRWriteStrings();
		if(body != null) body.IRWriteStrings();
	}
}
