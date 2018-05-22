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

public class IRcommand_End_Method_call extends IRcommand
{
	public TEMP ret;
	public TEMP label;
	public int paramCnt;
	
	public IRcommand_End_Method_call( TEMP t, TEMP label, int paramCnt )
	{
		this.ret = t;
		this.label = label;
		this.paramCnt = paramCnt;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().jalr( this.label );	// jump and link.
		sir_MIPS_a_lot.getInstance().allocate_stack( -1*this.paramCnt );	// deallocate memory on stack. this is the memory we used for function parameters.
		sir_MIPS_a_lot.getInstance().re_store_registers();	// restore registers after function call.
		sir_MIPS_a_lot.getInstance().load_return_value( ret ); // load return value from register $v0 to TEMP t.
	}
}
