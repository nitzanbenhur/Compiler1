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

public class IRcommand_If_Pre_Body extends IRcommand
{
	public TEMP cond;
	public ArrayList<String> labels;
	
	public IRcommand_If_Pre_Body(TEMP cond, ArrayList<String> labels)
	{
		this.cond = cond;
		this.labels = labels;
		
		String label_post_body = getFreshLabel("PostIfBody");
		labels.add(label_post_body);
		
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{	
		sir_MIPS_a_lot.getInstance().beq_zero( cond,labels.get(0) );
	}
}
