package TYPES;

import java.util.ArrayList;
import java.util.HashMap;

import AST.AST_EXP;

public class TYPE_CLASS extends TYPE
{
	/*********************************************************************/
	/* If this class does not extend a father class this should be null  */
	/*********************************************************************/
	public TYPE_CLASS father;

	/**************************************************/
	/* Gather up all data members in one place        */
	/* Note that data members coming from the AST are */
	/* packed together with the class methods         */
	/**************************************************/
	public TYPE_CLASS_MEMBERS data_members;
	public int fieldCnt;
	
	public String functionTableLabel;
	//public HashMap<String,Integer> fieldOffsets;
	public ArrayList<String> fieldNames;
	public ArrayList<AST_EXP> fieldExps;
	public ArrayList<String> functionNames;
	public ArrayList<String> functionLabels;
	
	/****************/
	/* CTROR(S) ... */
	/****************/
	public TYPE_CLASS(TYPE_CLASS father, String name, TYPE_CLASS_MEMBERS data_members, int  scope)
	{
		this.name = name;
		this.father = father;
		this.data_members = data_members;
		this.scope = scope;
		this.fieldCnt = coutfield();
	}
	
	private int coutfield(){
		TYPE_CLASS_FIELDS fields = data_members.fields;
		int cnt = 0;
		
		while (fields != null){
			cnt++;
			fields = fields.tail;
		}
		return cnt;
	}
	
	
	
	
	
	public boolean isClass(){ return true;}

}

/*class Field {
	public String name;
	public int offset;
	public AST_EXP exp;
	
	public Field(String name, int offset, AST_EXP exp){
		this.name = name;
		this.offset = offset;
		this.exp = exp;
	}
}*/