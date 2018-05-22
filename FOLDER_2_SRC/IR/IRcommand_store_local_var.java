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

public class IRcommand_store_local_var extends IRcommand
{
	int fpOffset;
	TEMP src;
	
	public IRcommand_store_local_var(int fpOffset ,TEMP src)
	{
		if (src == null){
			System.out.println("somthing wrong here");
			System.exit(0);
		}
		this.fpOffset = fpOffset;
		this.src = src;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().store_local_var( this.fpOffset, this.src );
	}
}
