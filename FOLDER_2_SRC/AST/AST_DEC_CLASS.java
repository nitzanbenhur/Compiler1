package AST;
import TYPES.*;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;

public class AST_DEC_CLASS extends AST_DEC
{
	public String father;
	public AST_CFIELD_LIST members;
	public TYPE_CLASS clss_type;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_DEC_CLASS(String name, String father, AST_CFIELD_LIST members, int l)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		if (father == null){
			System.out.printf("====================== classDec -> CLASS ID(%s) LBRACE cfieldList RBRACE\n",name);	
		}
		else {
			System.out.printf("====================== classDec -> CLASS ID(%s) EXTENDS ID(%s) LBRACE cfieldList RBRACE\n",name,father);
		}

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.name = name;
		this.father = father;
		this.members = members;
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
		System.out.print("AST NODE CLASS DEC\n");

		/**************************************/
		/* RECURSIVELY PRINT left + right ... */
		/**************************************/
		if (members != null) members.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
		SerialNumber,
		"CLASS\nDEC\n");
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (members  != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,members.SerialNumber);
	}
	
	public TYPE SemantMe(PrintWriter file_writer, int scope)
	{
		if (scope != 0){
			System.out.format(">> ERROR(%d) class cannot be defind in this scope\n",line);
			file_writer.printf("ERROR(%d)\n",line);
			file_writer.close();
			System.exit(0);	
		}
		if (SYMBOL_TABLE.getInstance().find(name) != null  || name.equals( "PrintInt" ) || name.equals( "PrintString")  ){
			System.out.format(">> ERROR(%d) id %s is used in this scope\n",line,name);
			file_writer.printf("ERROR(%d)\n",line);
			file_writer.close();
			System.exit(0);
		}
		
		TYPE father_type = null;
		TYPE_CLASS father_class = null;
		if (father != null){
			father_type = SYMBOL_TABLE.getInstance().find(father);
			if (father_type == null || !father_type.isClass() ){
				System.out.format(">> ERROR(%d) class %s does not exist in this context\n",line,father);
				file_writer.printf("ERROR(%d)\n",line);
				file_writer.close();
				System.exit(0);
			}
			father_class = (TYPE_CLASS) father_type;
		}
		SYMBOL_TABLE.getInstance().beginScope();
		
		addFatherMembers(father_class,scope);
		
		clss_type = new TYPE_CLASS(father_class,name, new TYPE_CLASS_MEMBERS(null,null),scope+1);
		createFieldLists();
		ArrayList<String> fieldNames = clss_type.fieldNames;
		ArrayList<AST_EXP> fieldExps = clss_type.fieldExps;
		createfunctionLabels();
		ArrayList<String> functions = clss_type.functionNames;
		ArrayList<String> functionLabels = clss_type.functionLabels;
		
		SYMBOL_TABLE.getInstance().enter(name, clss_type,scope+1);
		
		/***************************/
		/* [2] Semant Data Members */
		/***************************/
		clss_type = new TYPE_CLASS(father_class,name,members.SemantMe(file_writer, scope + 1,name, fieldNames,fieldExps, functions, functionLabels),scope + 1);
		clss_type.fieldNames = fieldNames;
		clss_type.fieldExps = fieldExps;
		clss_type.functionNames = functions;
		clss_type.functionLabels = functionLabels;
		/*****************/
		/* [3] End Scope */
		/*****************/
		SYMBOL_TABLE.getInstance().endScope();
		
		checkMembers(father_class,file_writer);
		
		/************************************************/
		/* [4] Enter the Class Type to the Symbol Table */
		/************************************************/
		SYMBOL_TABLE.getInstance().enter(name,clss_type,scope);
		/*********************************************************/
		/* [5] Return value is irrelevant for class declarations */
		/*********************************************************/
		return null;		
	}
	
	void createFieldLists(){
		
		if(clss_type.father != null) {
			clss_type.fieldNames = new ArrayList<String>(clss_type.father.fieldNames);
			clss_type.fieldExps = new ArrayList<AST_EXP>(clss_type.father.fieldExps);
		}
		else {
			clss_type.fieldNames = new ArrayList<String>();
			clss_type.fieldExps = new ArrayList<AST_EXP>();
		}
	}
	
	void createfunctionLabels(){
		
		if(clss_type.father != null) {
			clss_type.functionNames = new ArrayList<String>(clss_type.father.functionNames);
			clss_type.functionLabels = new ArrayList<String>(clss_type.father.functionLabels);
		}
		else {
			clss_type.functionNames = new ArrayList<String>();
			clss_type.functionLabels = new ArrayList<String>();
		}
	}
	
	void addFatherMembers(TYPE_CLASS father_class, int scope){
		while (father_class != null){
			for (TYPE_CLASS_FIELDS tcf = father_class.data_members.fields; tcf != null; tcf = tcf.tail){
				SYMBOL_TABLE.getInstance().enter(tcf.head.name,tcf.head,scope+1);
			}
			for (TYPE_CLASS_METHODS tcm = father_class.data_members.methods; tcm != null; tcm = tcm.tail){
				boolean flag = true;
				for (AST_CFIELD_LIST sonMethod = members;  sonMethod != null; sonMethod = sonMethod.tail){
					
					if (sonMethod.head.dec instanceof AST_DEC_FUNC && sonMethod.head.dec.name.equals(tcm.head.name) ){
						flag = false;
					}
				}
				if ( flag ){
					SYMBOL_TABLE.getInstance().enter(tcm.head.name,tcm.head,scope+1);
				}
			}
			father_class = father_class.father;
		}
	}
	
	
	void checkMembers(TYPE_CLASS father_class,PrintWriter file_writer){
		checkFields(father_class,clss_type,file_writer);
		checkMethods(father_class,clss_type,file_writer);
	}
	
	void checkFields(TYPE_CLASS father_class, TYPE_CLASS t,PrintWriter file_writer){
		TYPE_CLASS_FIELDS d = t.data_members.fields;
		while (d != null){
			checkFatherField(d.head.name,father_class,d.head.line,file_writer);
			d = d.tail;
		}
	}
	
	void checkFatherField(String fieldname ,TYPE_CLASS father_class, int fieldline, PrintWriter file_writer){
		if (father_class == null){
			return;
		}
		TYPE_CLASS_FIELDS d1 = father_class.data_members.fields;
		while (d1 != null){
			if (d1.head.name.equals( fieldname ) ){
				System.out.format(">> ERROR(%d) extending type cannot have fields of with the same identifier name: %s \n",fieldline,fieldname);
				file_writer.printf("ERROR(%d)\n",fieldline);
				file_writer.close();
				System.exit(0);
			}
			d1 = d1.tail;
		}
		TYPE_CLASS_METHODS d2 = father_class.data_members.methods;
		while (d2 != null){
			if (d2.head.name.equals( fieldname ) ){
				System.out.format(">> ERROR(%d) extending type cannot have fields of with the same identifier name: %s\n",fieldline,fieldname);
				file_writer.printf("ERROR(%d)\n",fieldline);
				file_writer.close();
				System.exit(0);
			}
			d2 = d2.tail;
		}
		if (father_class.father != null){
			checkFatherField(fieldname ,father_class.father,fieldline,file_writer);
		}
	}
	
	
	void checkMethods(TYPE_CLASS father_class, TYPE_CLASS t,PrintWriter file_writer){
		TYPE_CLASS_METHODS d = t.data_members.methods;
		while (d != null){
			checkFatherMethods( (TYPE_FUNCTION) d.head,father_class,d.head.line,file_writer);
			d = d.tail;
		}
	}
	
	void checkFatherMethods(TYPE_FUNCTION func ,TYPE_CLASS father_class, int funcline, PrintWriter file_writer){
		if (father_class == null){
			return;
		}
		TYPE_CLASS_FIELDS d1 = father_class.data_members.fields;
		while (d1 != null){
			if (d1.head.name.equals( func.name ) ){
				System.out.format(">> ERROR(%d) extending type cannot have fields of with the same identifier\n",funcline);
				file_writer.printf("ERROR(%d)\n",funcline);
				file_writer.close();
				System.exit(0);
			}
			d1 = d1.tail;
		}
		TYPE_CLASS_METHODS d2 = father_class.data_members.methods;
		while (d2 != null){
			if (d2.head.name.equals( func.name ) ){
				checkArgs(d2.head,func,funcline,file_writer);
			}
			d2 = d2.tail;
		}
		if (father_class.father != null){
			checkFatherMethods(func ,father_class.father,funcline,file_writer);
		}
	}
	
	void checkArgs(TYPE_FUNCTION func1, TYPE_FUNCTION func2, int funcline, PrintWriter file_writer){
		TYPE_LIST l1 = func1.params;
		TYPE_LIST l2 = func2.params;
		if ( !func1.returnType.name.equals( func2.returnType.name ) ){
			System.out.format(">> ERROR(%d) overriding with differnt signature is illeagle\n",funcline);
			file_writer.printf("ERROR(%d)\n",funcline);
			file_writer.close();
			System.exit(0);
		}
		if (l1 == null && l2 == null){
			return;
		}
		while (l1 != null){
			if ( !l1.head.name.equals( l2.head.name ) ){
				System.out.format(">> ERROR(%d) overriding with differnt signature is illeagle\n",funcline);
				file_writer.printf("ERROR(%d)\n",funcline);
				file_writer.close();
				System.exit(0);
			}
			l1 = l1.tail;
			l2 = l2.tail;  
		}
		if (l2 != null){
			System.out.format(">> ERROR(%d) overriding with differnt signature is illeagle \n",funcline);
			file_writer.printf("ERROR(%d)\n",funcline);
			file_writer.close();
			System.exit(0);
		}
	}
	
	public TEMP IRme()
	{
		members.IRme();
		
		return null; 
	}
	
	public void allocateFuncTable() {
		clss_type.functionTableLabel = IRcommand_Label.getInstance().getLabel(clss_type.name + "_funcTable");
		IR.getInstance().Add_IRcommand(new IRcommand_Create_Function_Table(clss_type.functionTableLabel, clss_type.functionNames.size()));
	}

	public void writeFuncTable() {
		ArrayList<String> functionLabels = clss_type.functionLabels;
		for (int i = 0; i < functionLabels.size(); i++) {
			String func = functionLabels.get(i);
			IR.getInstance().Add_IRcommand(new IRcommand_Add_To_Function_Table(clss_type.functionTableLabel, func, i));
		}
	}
	
	public void IRWriteStrings() {
		if (members != null) members.IRWriteStrings();
	}
}