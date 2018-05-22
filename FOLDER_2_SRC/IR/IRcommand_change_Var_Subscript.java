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

public class IRcommand_change_Var_Subscript extends IRcommand
{
	TEMP val;
	TEMP array_address;
	TEMP subscript;
	
	public IRcommand_change_Var_Subscript(TEMP val,TEMP array_address, TEMP subscript)
	{
		this.val = val;
		this.array_address = array_address;
		this.subscript = subscript;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		String label_invalid_subscript = getFreshLabel("invalid_subscript");
		String label_end = getFreshLabel("end");
		
		TEMP size = TEMP_FACTORY.getInstance().getFreshTEMP();
		
		// checking if 0 <= subscript < size of array
		sir_MIPS_a_lot.getInstance().load(size, array_address);
		sir_MIPS_a_lot.getInstance().bge(subscript, size, label_invalid_subscript);
		sir_MIPS_a_lot.getInstance().bltz(subscript, label_invalid_subscript);
		
		sir_MIPS_a_lot.getInstance().addi(subscript, subscript, 1);
		sir_MIPS_a_lot.getInstance().sll(subscript, subscript, 2);
		sir_MIPS_a_lot.getInstance().add(array_address, array_address, subscript);
		sir_MIPS_a_lot.getInstance().store( array_address,val );
		sir_MIPS_a_lot.getInstance().jump(label_end);
		
		sir_MIPS_a_lot.getInstance().label(label_invalid_subscript);
		sir_MIPS_a_lot.getInstance().print_data_string("string_access_violation");
		sir_MIPS_a_lot.getInstance().exit();
		
		sir_MIPS_a_lot.getInstance().label(label_end);
	}
}
