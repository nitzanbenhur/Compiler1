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

public class IRcommand_Get_Label_from_FuncTable extends IRcommand
{
	TEMP dst;
	TEMP src;
	int offset;
	
	public IRcommand_Get_Label_from_FuncTable(TEMP dst, TEMP src, int offset)
	{
		this.dst = dst;
		this.src = src;
		this.offset = offset;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		System.out.println("------------------------------- " + offset);
		TEMP label_address = TEMP_FACTORY.getInstance().getFreshTEMP();
		sir_MIPS_a_lot.getInstance().load(label_address, src);
		sir_MIPS_a_lot.getInstance().addi(label_address,label_address,4*offset);
		sir_MIPS_a_lot.getInstance().load(dst,label_address);
	}
}
