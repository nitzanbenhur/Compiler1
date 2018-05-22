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

public class IRcommand_Malloc_Bytes extends IRcommand
{
	TEMP dst;
	TEMP size;
	
	public IRcommand_Malloc_Bytes(TEMP dst,TEMP size)
	{
		this.dst = dst;
		this.size = size;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().MallocBytes(dst, size);
	}
}
