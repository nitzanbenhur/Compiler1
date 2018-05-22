/***********/
/* PACKAGE */
/***********/
package IR;

/*******************/
/* GENERAL IMPORTS */
/*******************/

import MIPS.*;

public class IRcommand_Jump_Main extends IRcommand
{

	public IRcommand_Jump_Main()
	{
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().jump("main_function");
	}
}
