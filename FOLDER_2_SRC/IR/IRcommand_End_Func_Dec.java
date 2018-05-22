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

public class IRcommand_End_Func_Dec extends IRcommand
{
	public int SizeFrame;
	public String name;
	public String endLabel;
	
	public IRcommand_End_Func_Dec( int SizeFrame,String endLabel, String name )
	{
		this.SizeFrame = SizeFrame;
		this.name = name;
		this.endLabel = endLabel;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().endFunc( SizeFrame, this.endLabel, this.name );
	}
}
