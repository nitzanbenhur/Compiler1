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
import MIPS.*;
import TEMP.TEMP;
import TEMP.TEMP_FACTORY;

public class IRcommand_Add_To_Function_Table extends IRcommand
{
	public String tableLabel;
	public String functionLabel;
	public int funcOffset;
	
	public IRcommand_Add_To_Function_Table(String tableLabel, String funcLabel, int offset)
	{
		this.tableLabel = tableLabel;
		this.functionLabel = funcLabel;
		this.funcOffset = offset;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		TEMP tableAddress = TEMP_FACTORY.getInstance().getFreshTEMP();
		TEMP functionAddress = TEMP_FACTORY.getInstance().getFreshTEMP();
		
		sir_MIPS_a_lot.getInstance().addToFuncTable(tableLabel, functionLabel, tableAddress, functionAddress, funcOffset);
	}
}