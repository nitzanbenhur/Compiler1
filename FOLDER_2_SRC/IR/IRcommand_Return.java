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

public class IRcommand_Return extends IRcommand
{
	public TEMP ret;
	public String name;
	public IRcommand_Return(TEMP ret, String name)
	{
		this.name = name;
		this.ret = ret;
	}
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		if (ret != null){
			sir_MIPS_a_lot.getInstance().store_return_value( ret );	
		}
		sir_MIPS_a_lot.getInstance().jump( String.format(name) );
	}
}
