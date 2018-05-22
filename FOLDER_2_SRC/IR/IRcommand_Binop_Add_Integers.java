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

public class IRcommand_Binop_Add_Integers extends IRcommand
{
	public TEMP t1;
	public TEMP t2;
	public TEMP dst;
	
	public IRcommand_Binop_Add_Integers(TEMP dst,TEMP t1,TEMP t2)
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
		/*****************************************************************/
		/* [5] Allocate a fresh label for possible overflow or underflow */
		/*****************************************************************/
		String label_end         = getFreshLabel("end");
		String label_overflow    = getFreshLabel("overflow");
		String label_underflow    = getFreshLabel("underflow");

		/*********************/
		/* [6] t4 := t1 + t2 */
		/*********************/
		sir_MIPS_a_lot.getInstance().add(dst,t1,t2);
		
		sir_MIPS_a_lot.getInstance().check_int_overflow_underflow(dst,
				label_overflow, label_underflow, label_end);
		
	
	}
}
