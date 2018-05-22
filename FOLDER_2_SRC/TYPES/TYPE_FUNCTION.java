package TYPES;

public class TYPE_FUNCTION extends TYPE
{
	/***********************************/
	/* The return type of the function */
	/***********************************/
	public TYPE returnType;

	/*************************/
	/* types of input params */
	/*************************/
	public TYPE_LIST params;
	public int paramCnt;
	public String label;
	public String endLabel;
	public TYPE_CLASS cls;
	
	/****************/
	/* CTROR(S) ... */
	/****************/
	public TYPE_FUNCTION(TYPE returnType,String name,TYPE_LIST params, int line, int scope, int paramCnt, String label, String endLabel)
	{
		this.name = name;
		this.returnType = returnType;
		this.params = params;
		this.line = line;
		this.scope = scope;
		this.paramCnt = paramCnt;
		this.label = label;
		this.endLabel = endLabel;
	}
	
	/*************/
	/* isFunc() */
	/*************/
	public boolean isFunc(){ return true;}
}