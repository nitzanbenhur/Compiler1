package AST;
import java.io.PrintWriter;
import TYPES.*;
import TEMP.*;
import IR.*;

public class AST_VAR_FIELD extends AST_VAR
{
	public AST_VAR var;
	public String fieldName;
	public int line;
	public TYPE_CLASS type_class;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_VAR_FIELD(AST_VAR var,String fieldName,int l)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== var -> var DOT ID( %s )\n",fieldName);

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.var = var;
		this.fieldName = fieldName;
		this.line = l;
		this.type_class = null;
	}

	/*************************************************/
	/* The printing message for a field var AST node */
	/*************************************************/
	public void PrintMe()
	{
		/*********************************/
		/* AST NODE TYPE = AST FIELD VAR */
		/*********************************/
		System.out.print("AST NODE FIELD VAR\n");

		/**********************************************/
		/* RECURSIVELY PRINT VAR, then FIELD NAME ... */
		/**********************************************/
		if (var != null) var.PrintMe();
		System.out.format("FIELD NAME( %s )\n",fieldName);

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("FIELD\nVAR\n...->%s",fieldName));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (var != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,var.SerialNumber);
	}
	
	
	public TYPE SemantMe(PrintWriter file_writer, int scope)
	{
		TYPE t = null;
		TYPE_CLASS tc = null;
		
		/******************************/
		/* [1] Recursively semant var */
		/******************************/
		if (var != null) t = var.SemantMe(file_writer,scope);
		/*********************************/
		/* [2] Make sure type is a class */
		/*********************************/
		if (t.isClass() == false)
		{
			System.out.format(">> ERROR(%d) access %s field of a non-class variable\n",line,fieldName);
			file_writer.printf("ERROR(%d)\n",line);
			file_writer.close();
			System.exit(0);
		}
		else
		{
			tc = (TYPE_CLASS) t;
			this.type_class = tc;
		}
		
		/************************************/
		/* [3] Look for fiedlName inside tc */
		/************************************/
		while (tc != null){
			for ( TYPE_CLASS_FIELDS it = tc.data_members.fields ;it != null;it=it.tail)
			{
				if ( it.head.name.equals(fieldName) )
				{
					return it.head.type;
				}
			}
			tc = tc.father;
		}
		
		/*********************************************/
		/* [4] fieldName does not exist in class var */
		/*********************************************/
		System.out.format(">> ERROR(%d) field %s does not exist in class\n",line,fieldName);							
		file_writer.printf("ERROR(%d)\n",line);
		file_writer.close();
		System.exit(0);
		return null;
	}
	
	public TEMP IRme()
	{
		TEMP var_temp = null;
		TEMP field_temp = TEMP_FACTORY.getInstance().getFreshTEMP();
		
		int offset = type_class.fieldNames.indexOf(fieldName) + 1;
		
		if (this.var  != null)	var_temp = this.var.IRme();
		
		IR.getInstance().Add_IRcommand(new IRcommand_Test_Invalid_Pointer(var_temp));
		
		IR.getInstance().Add_IRcommand(new IRcommand_Add_Offset(var_temp,var_temp,4*offset));
		IR.getInstance().Add_IRcommand(new IRcommand_Load(field_temp, var_temp));
		
		return field_temp; 
	}
	
	public void IRWriteStrings() {
		if(var != null) var.IRWriteStrings();
	}
	
}
