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

public class IRcommand_change_field_var extends IRcommand
{
	TEMP val;
	int classOffset;
	
	public IRcommand_change_field_var( int classOffset ,TEMP val )
	{
		this.val = val;
		this.classOffset = classOffset;
	}	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		TEMP cls = TEMP_FACTORY.getInstance().getFreshTEMP();
		sir_MIPS_a_lot.getInstance().load_param_var( cls,0 );
		sir_MIPS_a_lot.getInstance().addi(cls,cls,4*this.classOffset);
		sir_MIPS_a_lot.getInstance().store(cls,val);
	}
}