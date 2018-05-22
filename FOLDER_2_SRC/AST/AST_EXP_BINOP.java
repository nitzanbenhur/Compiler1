package AST;
import TYPES.*;
import java.io.PrintWriter;
import TEMP.*;
import IR.*;

public class AST_EXP_BINOP extends AST_EXP
{
	String OP;
	public AST_EXP left;
	public AST_EXP right;
	public TYPE typeLeft;
	public TYPE typeRight;
	public int line;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_BINOP(AST_EXP left,AST_EXP right,String OP, int l)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== exp -> exp BINOP(%s) exp\n",OP);

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.left = left;
		this.right = right;
		this.OP = OP;
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
		System.out.print("AST NODE BINOP EXP\n");

		/**************************************/
		/* RECURSIVELY PRINT left + right ... */
		/**************************************/
		if (left != null) left.PrintMe();
		if (right != null) right.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("BINOP(%s)",OP));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (left  != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,left.SerialNumber);
		if (right != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,right.SerialNumber);
	}
	
	public TYPE SemantMe(PrintWriter file_writer, int scope, String param, int fpOffset)
	{
		TYPE t1 = null;
		TYPE t2 = null;
		
		
		this.fpOffset = fpOffset;
		this.param = param;
		
		if (left  != null) t1 = left.SemantMe(file_writer,scope,null,0);
		if (right != null) t2 = right.SemantMe(file_writer,scope,null,0);
		
		this.typeLeft = t1;
		this.typeRight = t2;
			
		if ( OP.equals("=") && t1.equals(t2) ){
			return TYPE_INT.getInstance();
		}
		
		else if (  OP.equals("=") && ( (t2 instanceof TYPE_NIL) && ( t1.isArray() || t1.isClass() || t1 instanceof TYPE_ARRAY_NEW ) ) || 
		( (t1 instanceof TYPE_NIL) && ( t2.isArray() || t2.isClass() || t1 instanceof TYPE_ARRAY_NEW ) ) ) {
			return TYPE_INT.getInstance();
		}
		
		else if (OP.equals("=") && t1.isClass() && t2.isClass() ){
			TYPE_CLASS tc2 = (TYPE_CLASS) t2;
			while (tc2 != null){
				if (tc2.name.equals( t1.name ) ){
					return TYPE_INT.getInstance();
				}
				tc2 = tc2.father;
			}
			TYPE_CLASS tc1 = (TYPE_CLASS) t1;
			while (tc1 != null){
				if (tc1.name.equals( t2.name ) ){
					return TYPE_INT.getInstance();
				}
				tc1 = tc1.father;
			}
			System.out.format(">> ERROR(%d) testing equality with non matching types\n",line);	
			file_writer.printf("ERROR(%d)\n",line);
			file_writer.close();
			System.exit(0);
		}
		else if ( OP.equals("=") && t1.isArray() && t2 instanceof TYPE_ARRAY_NEW )  {
			TYPE_ARRAY s1 = (TYPE_ARRAY) t1;
			TYPE_ARRAY_NEW s2 = (TYPE_ARRAY_NEW) t2;
			if (s1.dataType.name.equals( s2.dataType.name ) ){
				return TYPE_INT.getInstance();
			}
			System.out.format(">> ERROR(%d) testing equality with non matching types\n",line);	
			file_writer.printf("ERROR(%d)\n",line);
			file_writer.close();
			System.exit(0);
		}
		else if ( OP.equals("=") && t2.isArray() && t1 instanceof TYPE_ARRAY_NEW )  {
			TYPE_ARRAY_NEW s1 = (TYPE_ARRAY_NEW) t1;
			TYPE_ARRAY s2 = (TYPE_ARRAY) t2;
			if (s1.dataType.name.equals( s2.dataType.name ) ){
				return TYPE_INT.getInstance();
			}
			System.out.format(">> ERROR(%d) testing equality with non matching types\n",line);	
			file_writer.printf("ERROR(%d)\n",line);
			file_writer.close();
			System.exit(0);
		}
		else if ( OP.equals("=") && t1 instanceof TYPE_ARRAY_NEW && t2 instanceof TYPE_ARRAY_NEW) {
			TYPE_ARRAY_NEW s1 = (TYPE_ARRAY_NEW) t1;
			TYPE_ARRAY_NEW s2 = (TYPE_ARRAY_NEW) t2;
			if (s1.dataType.name.equals( s2.dataType.name ) ){
				return TYPE_INT.getInstance();
			}
			System.out.format(">> ERROR(%d) testing equality with non matching types\n",line);	
			file_writer.printf("ERROR(%d)\n",line);
			file_writer.close();
			System.exit(0);
		}
		if ( (t1.equals( TYPE_INT.getInstance()) ) && (t2.equals(TYPE_INT.getInstance()) ) &&
		(OP.equals("-") || OP.equals("*") || OP.equals("/") || OP.equals("<") || OP.equals(">") || OP.equals("+") ) )
		{
			return TYPE_INT.getInstance();
		}
		else if ((t1.equals(TYPE_STRING.getInstance()) ) && (t2.equals(TYPE_STRING.getInstance()) ) && (OP.equals("+") )){
			return TYPE_STRING.getInstance();
		}
		else {
			System.out.format(">> ERROR(%d) illeagel binop\n",line);	
			file_writer.printf("ERROR(%d)\n",line);
			file_writer.close();
			System.exit(0);
		}
		return null;
	}
	
	public TEMP IRme()
	{
		TEMP t1 = null;
		TEMP t2 = null;
		TEMP dst = TEMP_FACTORY.getInstance().getFreshTEMP();
		assert(false);
		
		if (left  != null) t1 = left.IRme();
		if (right != null) t2 = right.IRme();
			
		if ( OP.equals("=") ){
			
			if (this.typeLeft instanceof TYPE_STRING && this.typeRight instanceof TYPE_STRING)
				IR.getInstance().Add_IRcommand(new IRcommand_Binop_EQ_Strings(dst,t1,t2));
			
			else IR.getInstance().Add_IRcommand( new IRcommand_Binop_EQ(dst,t1,t2) );
		}
		
		if ( OP.equals("+") ){
			if (this.typeLeft instanceof TYPE_INT && this.typeRight instanceof TYPE_INT)
				IR.getInstance().Add_IRcommand(new IRcommand_Binop_Add_Integers(dst,t1,t2));
			
			if (this.typeLeft instanceof TYPE_STRING && this.typeRight instanceof TYPE_STRING)
				IR.getInstance().Add_IRcommand(new IRcommand_Binop_Add_Strings(dst,t1,t2));
		}
		
		if ( OP.equals("-") && this.typeLeft instanceof TYPE_INT && this.typeRight instanceof TYPE_INT){
			IR.getInstance().Add_IRcommand(new IRcommand_Binop_Subtract_Integers(dst,t1,t2));
		}
		
		if ( OP.equals("*") && this.typeLeft instanceof TYPE_INT && this.typeRight instanceof TYPE_INT){
			IR.getInstance().Add_IRcommand(new IRcommand_Binop_Multiply_Integers(dst,t1,t2));
		}
		
		if ( OP.equals("/") && this.typeLeft instanceof TYPE_INT && this.typeRight instanceof TYPE_INT){
			IR.getInstance().Add_IRcommand(new IRcommand_Binop_Divide_Integers(dst,t1,t2));
		}
		
		if ( OP.equals(">") && this.typeLeft instanceof TYPE_INT && this.typeRight instanceof TYPE_INT){
			IR.getInstance().Add_IRcommand(new IRcommand_Binop_BT_Integers(dst,t1,t2));
		}
		
		if ( OP.equals("<") && this.typeLeft instanceof TYPE_INT && this.typeRight instanceof TYPE_INT){
			IR.getInstance().Add_IRcommand(new IRcommand_Binop_LT_Integers(dst,t1,t2));
		}
		if (this.param != null){
			IR.getInstance().Add_IRcommand( new IRcommand_store_param_var( this.fpOffset,dst ) );
		}
		return dst;
	}
	
	public void IRWriteStrings() {
		if (left != null) left.IRWriteStrings();
		if (right != null) right.IRWriteStrings();
	}
	
}
