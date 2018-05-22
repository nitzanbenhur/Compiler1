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

public class IRcommand_Load_Address extends IRcommand
{
	TEMP dst;
	String label;
	
	public IRcommand_Load_Address(TEMP dst,String label)
	{
		this.dst = dst;
		this.label = label;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().loadLabelAddress(dst, label);
	}
}
