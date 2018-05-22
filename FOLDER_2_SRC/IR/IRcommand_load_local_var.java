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

public class IRcommand_load_local_var extends IRcommand
{
	int fpOffset;
	TEMP dest;
	
	public IRcommand_load_local_var(int fpOffset ,TEMP dest)
	{
		this.fpOffset = fpOffset;
		this.dest = dest;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().load_local_var( dest,this.fpOffset );
	}
}
