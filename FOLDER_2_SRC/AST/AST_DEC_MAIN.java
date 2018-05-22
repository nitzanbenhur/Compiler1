package AST;
import TYPES.*;
import java.io.PrintWriter;
import TEMP.*;

public class AST_DEC_MAIN extends AST_Node
{

	public AST_DEC dec;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_DEC_MAIN(AST_DEC dec)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		if (dec instanceof AST_DEC_FUNC){
			System.out.print("====================== dec -> funcDec\n");	
		}else if (dec instanceof AST_DEC_VAR){
			System.out.print("====================== dec -> varDec\n");	
		}else if (dec instanceof AST_DEC_CLASS){
			System.out.print("====================== dec -> classDec\n");	
		}else if (dec instanceof AST_DEC_ARRAY){
			System.out.print("====================== dec -> arrayDec\n");	
		}

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.dec = dec;
	}
	
	/*************************************************/
	/* The printing message for a binop exp AST node */
	/*************************************************/
	public void PrintMe()
	{
		
		/*************************************/
		/* AST NODE TYPE = AST SUBSCRIPT VAR */
		/*************************************/
		System.out.print("AST NODE DEC\n");

		/**************************************/
		/* RECURSIVELY PRINT left + right ... */
		/**************************************/
		if (dec != null) dec.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
		SerialNumber,
		"DEC\n");
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (dec  != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,dec.SerialNumber);
	}
	public TYPE SemantMe(PrintWriter file_writer, int scope)
	{	
		if (dec instanceof AST_DEC_VAR && scope == 0 ){
			 AST_DEC_VAR dv = (AST_DEC_VAR) dec;
			return dv.SemantMe( file_writer,scope,0,"global");
		}
		if (dec instanceof AST_DEC_VAR && scope != 0 ){
			AST_DEC_VAR dv = (AST_DEC_VAR) dec;
			return dv.SemantMe( file_writer,scope,0,"classField");
		}
		return dec.SemantMe( file_writer,scope);		
	}
	
	public TEMP IRme() {
		dec.IRme();
		return null;
	}
	
	public void IRWriteStrings() {
		if (dec != null) dec.IRWriteStrings();
	}
	
}