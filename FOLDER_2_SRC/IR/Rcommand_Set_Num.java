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


public class Rcommand_Set_Num extends IRcommand
{
	public TEMP t;
	public int val;
	
	public Rcommand_Set_Num(TEMP t,int val)
	{
		this.t = t;
		this.val = val;
	}
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().Set_Num( t,val );	
	}
}


