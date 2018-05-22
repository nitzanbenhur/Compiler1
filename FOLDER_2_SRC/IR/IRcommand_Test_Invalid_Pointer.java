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

public class IRcommand_Test_Invalid_Pointer extends IRcommand
{
	public TEMP var;
	
	public IRcommand_Test_Invalid_Pointer(TEMP var)
	{
		this.var = var;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		String pass_label = getFreshLabel("not_nil");
		sir_MIPS_a_lot.getInstance().bne_zero(var,pass_label);
		sir_MIPS_a_lot.getInstance().print_data_string("string_invalid_ptr_dref");
		sir_MIPS_a_lot.getInstance().exit();
		sir_MIPS_a_lot.getInstance().label(pass_label);
	}
}
