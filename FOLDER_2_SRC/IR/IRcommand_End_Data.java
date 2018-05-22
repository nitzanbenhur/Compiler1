/***********/
/* PACKAGE */
/***********/
package IR;

/*******************/
/* GENERAL IMPORTS */
/*******************/

import MIPS.*;

public class IRcommand_End_Data extends IRcommand
{

	public IRcommand_End_Data()
	{
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().end_data();
		sir_MIPS_a_lot.getInstance().label("main");
	}
}
