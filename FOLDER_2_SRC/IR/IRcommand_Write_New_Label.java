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

public class IRcommand_Write_New_Label extends IRcommand
{
	public TEMP cond;
	public ArrayList<String> labels;
	
	public IRcommand_Write_New_Label(TEMP cond, ArrayList<String> labels)
	{
		this.cond = cond;
		this.labels = labels;
		
		String label_post_body = getFreshLabel("Skip_Func_Decs");
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
