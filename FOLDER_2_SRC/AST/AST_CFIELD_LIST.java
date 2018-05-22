package AST;
import TYPES.*;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import SYMBOL_TABLE.*;
import TEMP.*;

public class AST_CFIELD_LIST extends AST_Node
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public AST_DEC_MAIN head;
	public AST_CFIELD_LIST tail;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_CFIELD_LIST(AST_DEC_MAIN head,AST_CFIELD_LIST tail)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		if (tail != null) System.out.print("====================== cfields -> cfield cfields\n");
		if (tail == null) System.out.print("====================== cfields -> cfield      \n");

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
		System.out.print("AST NODE CFIELD LIST\n");

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
			"CFIELD\nLIST\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (head != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,head.SerialNumber);
		if (tail != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,tail.SerialNumber);
	}
	
	public TYPE_CLASS_MEMBERS SemantMe(PrintWriter file_writer, int scope, String className, ArrayList<String> fieldNames, ArrayList<AST_EXP> fieldExps, 
			ArrayList<String> functions, ArrayList<String> functionLabels)
	{
		TYPE_CLASS_FIELDS fields = SemantField(tail,head,file_writer,scope,className, fieldNames, fieldExps);
		TYPE_CLASS_METHODS methods = SemantMehod(tail,head,file_writer,scope,className, functions, functionLabels);
		return new TYPE_CLASS_MEMBERS(methods,fields);
	}
	
	public TYPE_CLASS_FIELDS SemantField(AST_CFIELD_LIST tail, AST_DEC_MAIN head,PrintWriter file_writer, int scope, String className,
			ArrayList<String> fieldNames, ArrayList<AST_EXP> fieldExps)
	{
		if (head != null && (head.dec instanceof AST_DEC_VAR) ){
			AST_DEC_VAR v = (AST_DEC_VAR) head.dec;
			AST_EXP e = v.exp;
			if ( !( (e == null) || (e instanceof AST_EXP_NIL) || 
			(e instanceof AST_EXP_INT) || (e instanceof AST_EXP_STRING) ) ) {
				System.out.format(">> ERROR(%d) class field can only be initialised to string int or nil \n",v.line);	
				file_writer.printf("ERROR(%d)\n",v.line);
				file_writer.close();
				System.exit(0);
			}
			TYPE_VAR vr = (TYPE_VAR) head.SemantMe(file_writer,scope);
			vr.cls = className;
			vr.status = "classField";
			SYMBOL_TABLE.getInstance().addVar(className,vr);
			
			String varName = v.name;
			if(fieldNames.indexOf(varName) < 0) {
				fieldNames.add(varName);
				fieldExps.add(e);
			}
			
			return new TYPE_CLASS_FIELDS(vr,SemantField(tail,null,file_writer,scope,className, fieldNames, fieldExps), e);
		}
		AST_CFIELD_LIST d = tail;
		while (d != null && d.head.dec instanceof AST_DEC_FUNC){
			d = d.tail;
		}
		if (d == null){
			return null;
		}
		AST_DEC_VAR v = (AST_DEC_VAR) d.head.dec;
		AST_EXP e = v.exp;
		
		if ( !( (e == null) || (e instanceof AST_EXP_NIL) || 
			(e instanceof AST_EXP_INT) || (e instanceof AST_EXP_STRING) ) ) {
			System.out.format(">> ERROR(%d) class field can only be initialised to string int or nil \n",v.line);	
			file_writer.printf("ERROR(%d)\n",v.line);
			file_writer.close();
			System.exit(0);
		
		}
		TYPE_VAR vr = (TYPE_VAR) d.head.SemantMe(file_writer,scope);
		vr.cls = className;
		vr.status = "classField";
		SYMBOL_TABLE.getInstance().addVar(className,vr);
		
		String varName = v.name;
		if(fieldNames.indexOf(varName) < 0) {
			fieldNames.add(varName);
			fieldExps.add(e);
		}
		
		return new TYPE_CLASS_FIELDS( vr ,SemantField(d.tail,null,file_writer,scope,className, fieldNames, fieldExps),e);
	}
	
	public TYPE_CLASS_METHODS SemantMehod(AST_CFIELD_LIST tail, AST_DEC_MAIN head,PrintWriter file_writer, int scope,
			String className, ArrayList<String> functions, ArrayList<String> functionLabels)
	{
		if (head != null && (head.dec instanceof AST_DEC_FUNC) ){
			AST_DEC_FUNC dc = (AST_DEC_FUNC) head.dec;
			dc.cls = (TYPE_CLASS) SYMBOL_TABLE.getInstance().find( className ); 
			TYPE_FUNCTION f = (TYPE_FUNCTION) dc.SemantMe(file_writer,scope);
			f.cls = (TYPE_CLASS) SYMBOL_TABLE.getInstance().find( className ); 
			
			SYMBOL_TABLE.getInstance().addFunc(className,f);
			
			String funcName = head.dec.name;
			String funcLabel = ((AST_DEC_FUNC) head.dec).mylabel;
			int funcIndex = functions.indexOf(funcName);
			if(funcIndex > -1) {
				functionLabels.set(funcIndex, funcLabel);
			}
			else {
				functions.add(funcName);
				functionLabels.add(funcLabel);
			}
			
			return new TYPE_CLASS_METHODS(f,SemantMehod(tail,null,file_writer,scope,className, functions, functionLabels));
		}
		AST_CFIELD_LIST d = tail;
		while (d != null && d.head.dec instanceof AST_DEC_VAR){
			d = d.tail;
		}
		if (d == null){
			return null;
		}
		AST_DEC_FUNC dc = (AST_DEC_FUNC) d.head.dec;
		dc.cls = (TYPE_CLASS) SYMBOL_TABLE.getInstance().find( className ); 
		TYPE_FUNCTION f = (TYPE_FUNCTION) dc.SemantMe(file_writer,scope);
		f.cls = (TYPE_CLASS) SYMBOL_TABLE.getInstance().find( className ); 
		
		SYMBOL_TABLE.getInstance().addFunc(className,f);
		
		String funcName = d.head.dec.name;
		String funcLabel = ((AST_DEC_FUNC) d.head.dec).mylabel;
		int funcIndex = functions.indexOf(funcName);
		if(funcIndex > -1) {
			functionLabels.set(funcIndex, funcLabel);
		}
		else {
			functions.add(funcName);
			functionLabels.add(funcLabel);
		}
		return new TYPE_CLASS_METHODS( f ,SemantMehod(d.tail,null,file_writer,scope,className, functions, functionLabels));
	}
	
	public TEMP IRme()
	{
		if(head != null) {
			if(head.dec instanceof AST_DEC_FUNC) {
				head.IRme();
			}
		}
		
		if(tail != null) tail.IRme();
		
		return null; 
	}
	
	public void IRWriteStrings() {
		if (head != null) head.IRWriteStrings();
		if (tail != null) tail.IRWriteStrings();
	}
	
}
