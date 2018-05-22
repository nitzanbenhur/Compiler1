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

public class IRcommand_Var_Subscript_Address extends IRcommand
{
	TEMP dst;
	TEMP src;
	TEMP subscript;
	
	public IRcommand_Var_Subscript_Address(TEMP dst,TEMP src, TEMP subscript)
	{
		this.dst = dst;
		this.src = src;
		this.subscript = subscript;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		String label_invalid_subscript = getFreshLabel("invalid_subscript");
		String label_end = getFreshLabel("end");
		
		//TEMP offset = TEMP_FACTORY.getInstance().getFreshTEMP();
		TEMP size = TEMP_FACTORY.getInstance().getFreshTEMP();
		
		// checking if 0 <= subscript < size of array
		sir_MIPS_a_lot.getInstance().load(size, src);
		sir_MIPS_a_lot.getInstance().bge(subscript, size, label_invalid_subscript);
		sir_MIPS_a_lot.getInstance().bltz(subscript, label_invalid_subscript);
		
		//sir_MIPS_a_lot.getInstance().get_array_subscript_addr(src, subscript, offset);
		
		sir_MIPS_a_lot.getInstance().addi(subscript, subscript, 1);
		sir_MIPS_a_lot.getInstance().sll(subscript, subscript, 2);
		sir_MIPS_a_lot.getInstance().add(src, src, subscript);
		sir_MIPS_a_lot.getInstance().load(dst, src);
		sir_MIPS_a_lot.getInstance().jump(label_end);
		
		sir_MIPS_a_lot.getInstance().label(label_invalid_subscript);
		sir_MIPS_a_lot.getInstance().print_data_string("string_access_violation");
		sir_MIPS_a_lot.getInstance().exit();
		
		sir_MIPS_a_lot.getInstance().label(label_end);
	}
}
