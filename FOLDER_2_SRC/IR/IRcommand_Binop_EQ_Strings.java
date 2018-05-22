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

public class IRcommand_Binop_EQ_Strings extends IRcommand
{
	public TEMP t1;
	public TEMP t2;
	public TEMP dst;

	public IRcommand_Binop_EQ_Strings(TEMP dst,TEMP t1,TEMP t2)
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
		TEMP char1 = TEMP_FACTORY.getInstance().getFreshTEMP();	
		TEMP char2 = TEMP_FACTORY.getInstance().getFreshTEMP();
		TEMP offset1 = TEMP_FACTORY.getInstance().getFreshTEMP();
		TEMP offset2 = TEMP_FACTORY.getInstance().getFreshTEMP();
		
		/*******************************/
		/* [1] Allocate 2 fresh labels */
		/*******************************/
		String label_end        = getFreshLabel("end");
		String label_AssignOne  = getFreshLabel("AssignOne");
		String label_AssignZero = getFreshLabel("AssignZero");
		String label_Comp_Loop  = getFreshLabel("Comp_Loop");
		
		/**********************/
		/* [1] offset1 := t1; */
		/*     offset2 := t2; */
		/**********************/
		sir_MIPS_a_lot.getInstance().move(offset1, t1);
		sir_MIPS_a_lot.getInstance().move(offset2, t2);

		sir_MIPS_a_lot.getInstance().compare_strings(offset1, offset2, char1, char2,
				label_Comp_Loop, label_AssignOne, label_AssignZero);
		
		sir_MIPS_a_lot.getInstance().assign_zero_or_one(dst, label_AssignOne, label_AssignZero, label_end);
	}
}
