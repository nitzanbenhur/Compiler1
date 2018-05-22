package TYPES;

public class TYPE_VAR extends TYPE
{
	/******************************/
	/*  Every type has a name ... */
	/******************************/
	public TYPE type;
	public int fpOffset;
	public String status;
	public String cls = null;

	public TYPE_VAR(String name,TYPE type, int line, int scope, int fpOffset, String status)
	{
		this.name = name;
		this.type = type;
		this.line = line;
		this.scope = scope;
		this.fpOffset = fpOffset;
		this.status = status;
	}
	
	/*************/
	/* isClass() */
	/*************/
	public boolean isClass(){ return false;}

	/*************/
	/* isArray() */
	/*************/
	public boolean isArray(){ return false;}
	
	/*************/
	/* isFunc() */
	/*************/
	public boolean isFunc(){ return false;}

	public boolean isVar(){ return false;}
}
