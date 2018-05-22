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

public class IRcommand_load_global_var extends IRcommand
{
	String name;
	TEMP dst;
	
	public IRcommand_load_global_var(TEMP dst,String name)
	{
		this.name = name;
		this.dst = dst;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().load_global(this.name,this.dst);
	}
}
