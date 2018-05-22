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

public class IRcommand_store_string_exp extends IRcommand
{
	String label;
	String value;
	
	public IRcommand_store_string_exp(String label, String value)
	{
		this.label = label;
		this.value = value;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().store_string_exp(label, value);
	}
}
