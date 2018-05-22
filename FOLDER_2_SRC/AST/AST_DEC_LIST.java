package AST;
import TYPES.*;
import java.io.PrintWriter;

import IR.IR;
import IR.IRcommand_Set_Zero;
import IR.IRcommand_store_global_var;
import TEMP.*;

public class AST_DEC_LIST extends AST_Node
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public AST_DEC_MAIN head;
	public AST_DEC_LIST tail;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_DEC_LIST(AST_DEC_MAIN head,AST_DEC_LIST tail)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		if (tail != null) System.out.print("====================== decs -> dec decs\n");
		if (tail == null) System.out.print("====================== decs -> dec      \n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.head = head;
		this.tail = tail;
	}

	/******************************************************/
	/* The printing message for a statement list AST node */
	/******************************************************/
	public void PrintMe()
	{
		/**************************************/
		/* AST NODE TYPE = AST DEC LIST */
		/**************************************/
		System.out.print("AST NODE DEC LIST\n");

		/*************************************/
		/* RECURSIVELY PRINT HEAD + TAIL ... */
		/*************************************/
		if (head != null) head.PrintMe();
		if (tail != null) tail.PrintMe();

		/**********************************/
		/* PRINT to AST GRAPHVIZ DOT file */
		/**********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"DEC\nLIST\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (head != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,head.SerialNumber);
		if (tail != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,tail.SerialNumber);
	}
	
	public TYPE SemantMe(PrintWriter file_writer, int scope)
	{		
		/*************************************/
		/* RECURSIVELY PRINT HEAD + TAIL ... */
		/*************************************/
		if (head != null) head.SemantMe(file_writer,scope);
		if (tail != null) tail.SemantMe(file_writer,scope);
		return null;	
	}
	
	
	public TEMP IRme()
	{
		if (head != null) head.IRme();
		if (tail != null) tail.IRme();
		return null;			
	}
	public void IRAllocateFuncTables(){
		if(head != null) {
			if ( head.dec instanceof AST_DEC_CLASS ) {
				AST_DEC_CLASS c = (AST_DEC_CLASS) head.dec;
				c.allocateFuncTable();
			}
		}
		if (tail != null) tail.IRAllocateFuncTables();
		return;
	}
	
	public void IRAllocateGlobalVars(){
		if(head != null) {
			if ( head.dec instanceof AST_DEC_VAR ) {
				AST_DEC_VAR gv = (AST_DEC_VAR) head.dec;
				gv.IRglobalVar();
			}
		}
		if (tail != null) tail.IRAllocateGlobalVars();
		return;
	}
	
	public void IRWriteData(){
		if(head != null) {
			if ( head.dec instanceof AST_DEC_CLASS ) {
				AST_DEC_CLASS c = (AST_DEC_CLASS) head.dec;
				c.writeFuncTable();
			}
			else if (head.dec instanceof AST_DEC_VAR) {
				AST_DEC_VAR var = (AST_DEC_VAR) head.dec; 
				TEMP t = null;
				
				if ( var.exp == null ) {
				 	t = TEMP_FACTORY.getInstance().getFreshTEMP();
					IR.getInstance().Add_IRcommand( new IRcommand_Set_Zero( t ) );
				}
				else {
					t = var.exp.IRme();	
				}
				
				IR.getInstance().Add_IRcommand( new IRcommand_store_global_var( t,var.name ) );
			}
		}
		if (tail != null) tail.IRWriteData();
		return;
	}
	
	public void IRWriteStrings() {
		if (head != null) head.IRWriteStrings();
		if (tail != null) tail.IRWriteStrings();
	}	
}
