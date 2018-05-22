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

public class IRcommand_Binop_Subtract_Integers extends IRcommand
{
	public TEMP t1;
	public TEMP t2;
	public TEMP dst;
	
	public IRcommand_Binop_Subtract_Integers(TEMP dst,TEMP t1,TEMP t2)
	{
		this.dst = dst;
		this.t1 = t1;
		this.t2 = t2;
	}
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{	
		String label_end         = getFreshLabel("end");
		String label_overflow    = getFreshLabel("overflow");
		String label_underflow    = getFreshLabel("underflow");


		// dst := t1 - t2
		sir_MIPS_a_lot.getInstance().sub(dst,t1,t2);
		
		sir_MIPS_a_lot.getInstance().check_int_overflow_underflow(dst,
				label_overflow, label_underflow, label_end);
	}
}
