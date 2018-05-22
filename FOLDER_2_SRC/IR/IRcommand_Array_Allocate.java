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

public class IRcommand_Array_Allocate extends IRcommand
{
	public TEMP array_address;
	public TEMP size;
	
	public IRcommand_Array_Allocate(TEMP array_address,TEMP size)
	{
		this.array_address = array_address;
		this.size = size;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		String clear_label = getFreshLabel("clear_allocation");
		
		sir_MIPS_a_lot.getInstance().addi(this.size,this.size,1);
		sir_MIPS_a_lot.getInstance().sll(this.size,this.size,2);
		sir_MIPS_a_lot.getInstance().Malloc(this.array_address, this.size);
		sir_MIPS_a_lot.getInstance().Clear_Alloaction(array_address, size, clear_label);
		sir_MIPS_a_lot.getInstance().srl(this.size,this.size,2);
		sir_MIPS_a_lot.getInstance().addi(this.size,this.size,-1);
		sir_MIPS_a_lot.getInstance().store(this.array_address,this.size);	
	}
}
