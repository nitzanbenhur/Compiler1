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

public class IRcommand_Binop_Divide_Integers extends IRcommand
{
	public TEMP t1;
	public TEMP t2;
	public TEMP dst;
	
	public IRcommand_Binop_Divide_Integers(TEMP dst,TEMP t1,TEMP t2)
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
		String label_not_div_by_zero = getFreshLabel("not_div_by_zero");
		String label_overflow    = getFreshLabel("overflow");
		String label_underflow   = getFreshLabel("underflow");
		
		// bne t2, $0, not_div_by_zero
		sir_MIPS_a_lot.getInstance().bne_zero(t2,label_not_div_by_zero);
		
		// print error and exit()
		sir_MIPS_a_lot.getInstance().print_data_string("string_illegal_div_by_0");
		sir_MIPS_a_lot.getInstance().exit();
		
		// not_div_by_zero:
		sir_MIPS_a_lot.getInstance().label(label_not_div_by_zero);
		
		// dst = t1 div t2
		sir_MIPS_a_lot.getInstance().divide_int(dst, t1, t2);
		
		sir_MIPS_a_lot.getInstance().check_int_overflow_underflow(dst,
				label_overflow, label_underflow, label_end);
		
		
	}
}
