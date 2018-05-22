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

public class IRcommandPrintInt extends IRcommand
{	
	public IRcommandPrintInt()
	{
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		TEMP t = TEMP_FACTORY.getInstance().getFreshTEMP();
		
		sir_MIPS_a_lot.getInstance().load_sp( t );
		sir_MIPS_a_lot.getInstance().print_int( t );
		sir_MIPS_a_lot.getInstance().print_data_string("string_space_character");
		sir_MIPS_a_lot.getInstance().allocate_stack( -1 );	// deallocate memory on stack. this is the memory we used for function parameters.
	}
}
