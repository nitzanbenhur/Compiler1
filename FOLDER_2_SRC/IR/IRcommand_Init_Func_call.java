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

public class IRcommand_Init_Func_call extends IRcommand
{
	public int paramCnt;
	public String name;
	
	
	public IRcommand_Init_Func_call( int paramCnt ,String name)
	{
		this.paramCnt = paramCnt;
		this.name = name;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
	
		if ( !(name.equals("PrintInt") || name.equals("PrintString") ) ){
			sir_MIPS_a_lot.getInstance().store_registers();	// save registers before function call.
		}
		sir_MIPS_a_lot.getInstance().allocate_stack( this.paramCnt );	// make place on the stack for function parameters.
	}
}
