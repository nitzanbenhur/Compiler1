package TYPES;

public class TYPE_ARRAY extends TYPE
{
	/***********************************/
	/* The return type of the function */
	/***********************************/
	public TYPE dataType;
	
	/****************/
	/* CTROR(S) ... */
	/****************/
	public TYPE_ARRAY(String name, TYPE dataType, int scope)
	{
		this.dataType = dataType;
		this.name = name;
		this.scope = scope;
	}
	
	/*************/
	/* isFunc() */
	/*************/
	public boolean isArray(){ return true;}
}
