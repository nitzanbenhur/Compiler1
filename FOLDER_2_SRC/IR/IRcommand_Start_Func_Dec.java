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

public class IRcommand_Start_Func_Dec extends IRcommand
{
	public String label_func;
	public int SizeFrame;
	
	public IRcommand_Start_Func_Dec(String funcName, int SizeFrame)
	{
	this.label_func = funcName;
	this.SizeFrame = SizeFrame;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().label( this.label_func );
		sir_MIPS_a_lot.getInstance().startFunc( this.SizeFrame );
	}
}
