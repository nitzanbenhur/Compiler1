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

public class IRcommand_While_Pre_Body extends IRcommand
{
	public TEMP cond;
	public ArrayList<String> labels;
	
	public IRcommand_While_Pre_Body(TEMP cond, ArrayList<String> labels)
	{
		this.cond = cond;
		this.labels = labels;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{	
		String label_cond  = getFreshLabel("WhileCondition");
		String label_body  = getFreshLabel("WhileBody");
		
		labels.add(label_cond);
		labels.add(label_body);
		
		sir_MIPS_a_lot.getInstance().jump(label_cond);
		
		sir_MIPS_a_lot.getInstance().label(label_body);
	}
}
