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
import java.util.ArrayList;

public class IRcommand_While_Post_Cond extends IRcommand
{
	public TEMP cond;
	public ArrayList<String> labels;
	
	public IRcommand_While_Post_Cond(TEMP cond, ArrayList<String> labels)
	{
		this.cond = cond;
		this.labels = labels;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{	
		sir_MIPS_a_lot.getInstance().bne_zero(cond,labels.get(1));
	}
}
