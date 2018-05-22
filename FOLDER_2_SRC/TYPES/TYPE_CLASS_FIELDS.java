package TYPES;
import AST.*;

public class TYPE_CLASS_FIELDS 
{
	public TYPE_VAR head;
	public TYPE_CLASS_FIELDS  tail;
	public AST_EXP exp;
	
	public TYPE_CLASS_FIELDS (TYPE_VAR head,TYPE_CLASS_FIELDS  tail, AST_EXP exp)
	{
		this.exp = exp;
		this.head = head;
		this.tail = tail;
	}	
}