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

public class IRcommand_load_label_address extends IRcommand
{
	String label;
	TEMP dst;
	
	public IRcommand_load_label_address(TEMP dst,String label)
	{
		this.label = label;
		this.dst = dst;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().loadLabelAddress(dst, label);
	}
}
