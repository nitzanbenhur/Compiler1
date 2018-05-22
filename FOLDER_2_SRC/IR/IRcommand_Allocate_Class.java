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

public class IRcommand_Allocate_Class extends IRcommand
{
	TEMP dst;
	int size;
	
	public IRcommand_Allocate_Class(TEMP dst,int size)
	{
		this.dst = dst;
		this.size = size;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		TEMP t =  TEMP_FACTORY.getInstance().getFreshTEMP();
		sir_MIPS_a_lot.getInstance().Set_Num( t , 4*size );
		sir_MIPS_a_lot.getInstance().Malloc(dst, t);
	}
}