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

public class IRcommand_If_Post_Body extends IRcommand
{
	public TEMP cond;
	public ArrayList<String> labels;
	
	public IRcommand_If_Post_Body(TEMP cond, ArrayList<String> labels)
	{
		this.cond = cond;
		this.labels = labels;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		String label_post_body  = labels.get(0);
		sir_MIPS_a_lot.getInstance().label(label_post_body);
	}
}
