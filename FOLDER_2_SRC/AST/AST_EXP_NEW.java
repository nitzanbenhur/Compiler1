package AST;
import TYPES.*;
import java.io.PrintWriter;
import java.util.ArrayList;

import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;

public class AST_EXP_NEW extends AST_EXP
{
	/***************/
	/*  var := exp */
	/***************/
	public String type;
	public AST_EXP exp;
	public int line;
	public String funcTable_label;
	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_EXP_NEW(String type, AST_EXP exp, int l)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		if (exp == null)System.out.format("====================== exp -> NEW ID( %s )\n",type);
		if (exp != null)System.out.format("====================== exp -> NEW ID( %s ) [ exp ]\n",type);
		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.type = type;
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
		System.out.print("AST NODE NEW EXP\n");

		if (exp != null) exp.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		if (exp != null){
			AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("NEW ID (%s ) exp",type));
		}
		else {
			AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("NEW ID (%s )",type));
		}
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/

		if (exp != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,exp.SerialNumber);
	}
	
	public TYPE SemantMe(PrintWriter file_writer, int scope, String param, int fpOffset)
	{
		this.fpOffset = fpOffset;
		this.param = param;
		
		TYPE t;	
		t = SYMBOL_TABLE.getInstance().find(type);
		if ( t == null || t instanceof TYPE_VAR || t instanceof TYPE_FUNCTION)
		{
			System.out.format(">> ERROR(%d) non existing type %s\n",line,type);
			file_writer.printf("ERROR(%d)\n",line);
			file_writer.close();
			System.exit(0);
		}
		if (exp == null){
			if ( !t.isClass() ){
				System.out.format(">> ERROR(%d) New operator can only be used to initialise class \n",line);
				file_writer.printf("ERROR(%d)\n",line);
				file_writer.close();
				System.exit(0);	
			}
			return t;
		}
		if (exp.SemantMe(file_writer, scope,null,0) != TYPE_INT.getInstance()){
			System.out.format(">> ERROR(%d) Array allocation with non integer expression type %s\n",line,type);
			file_writer.printf("ERROR(%d)\n",line);
			file_writer.close();
			System.exit(0);	
		}
		return new TYPE_ARRAY_NEW(t);
	}
	
	public TEMP IRme()
	{	
		TEMP dst = TEMP_FACTORY.getInstance().getFreshTEMP();	// address
		TEMP t1 = TEMP_FACTORY.getInstance().getFreshTEMP();	// address
		TYPE type;	
		type = SYMBOL_TABLE.getInstance().find(this.type);
		if (exp != null){	// Array allocation done.
			TEMP t2 = exp.IRme();	// array size
			IR.getInstance().Add_IRcommand( new IRcommand_Array_Allocate(t1, t2) );
			if (this.param != null){
				IR.getInstance().Add_IRcommand( new IRcommand_store_param_var( this.fpOffset,t1 ) );
			}
			return t1;
		}
		else if (type instanceof TYPE_CLASS) {
			TYPE_CLASS tc = (TYPE_CLASS) type;
			int size = tc.fieldNames.size();
			//TYPE_CLASS_FIELDS tf = tc.data_members.fields;
			IR.getInstance().Add_IRcommand( new IRcommand_Allocate_Class( t1, size + 1 ) ); // Allocation
			IR.getInstance().Add_IRcommand( new IRcommand_Add_Offset(dst,t1,0) );
			
			// saving function table address
			TEMP t3 = TEMP_FACTORY.getInstance().getFreshTEMP();
			funcTable_label = tc.functionTableLabel;
			IR.getInstance().Add_IRcommand( new IRcommand_load_label_address(t3,funcTable_label) );
			IR.getInstance().Add_IRcommand( new IRcommand_store_field(t1,t3) );
			
			// Fields initialization
			ArrayList<AST_EXP> fieldExps = tc.fieldExps;
			for (int i = 0; i < size; i++) {
				TEMP t2 = null;
				AST_EXP exp = fieldExps.get(i); 
				if(exp != null) {
					t2 = exp.IRme();
				}
				else {
					t2 = TEMP_FACTORY.getInstance().getFreshTEMP();
					IR.getInstance().Add_IRcommand( new IRcommand_Set_Zero(t2) );
				}
				IR.getInstance().Add_IRcommand( new IRcommand_Add_Offset(t1,t1,4) );
				IR.getInstance().Add_IRcommand( new IRcommand_store_field(t1,t2) );
			}			
		}
		if (this.param != null){
			IR.getInstance().Add_IRcommand( new IRcommand_store_param_var( this.fpOffset,dst ) );
		}
		return dst;
	}
	
	public void IRWriteStrings() {
		if (exp != null) exp.IRWriteStrings();
	}

}