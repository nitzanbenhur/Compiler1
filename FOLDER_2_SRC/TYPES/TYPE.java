package TYPES;

public abstract class TYPE
{
	/******************************/
	/*  Every type has a name ... */
	/******************************/
	public int line;
	public String name;
	public int scope;	

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
