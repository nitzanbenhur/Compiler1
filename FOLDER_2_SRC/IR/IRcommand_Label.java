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

public class IRcommand_Label extends IRcommand
{
	/*****************/
	/* Label Factory */

	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static IRcommand_Label instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected IRcommand_Label() {}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static IRcommand_Label getInstance()
	{
		if (instance == null)
		{
			/*******************************/
			/* [0] The instance itself ... */
			/*******************************/
			instance = new IRcommand_Label();
		}
		return instance;
	}
	
	public String getLabel(String name){
		return getFreshLabel(name);
	}
	public void MIPSme(){}
}
