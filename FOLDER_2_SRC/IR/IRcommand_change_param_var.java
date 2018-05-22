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

public class IRcommand_change_param_var extends IRcommand
{
	int fpOffset;
	TEMP dest;
	
	public IRcommand_change_param_var(int fpOffset ,TEMP dest)
	{
		this.fpOffset = fpOffset;
		this.dest = dest;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().change_param_var( dest,this.fpOffset );
	}
}
