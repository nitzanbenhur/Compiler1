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

public class IRcommand_store_param_var extends IRcommand
{
	int fpOffset;
	TEMP src;
	
	public IRcommand_store_param_var(int fpOffset ,TEMP src)
	{
		this.fpOffset = fpOffset;
		this.src = src;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().store_param_var( this.fpOffset, this.src );
	}
}
