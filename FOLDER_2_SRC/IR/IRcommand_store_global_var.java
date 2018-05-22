/***********/
/* PACKAGE */
/***********/
package IR;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/
import TEMP.*;
import MIPS.*;

public class IRcommand_store_global_var extends IRcommand
{
	String name;
	TEMP src;
	
	public IRcommand_store_global_var(TEMP src,String name)
	{
		this.name = name;
		this.src = src;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().store_global(this.name,this.src);
	}
}
