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

public class IRcommand_Binop_Add_Strings extends IRcommand
{
	public TEMP t1;
	public TEMP t2;
	public TEMP dst;

	public IRcommand_Binop_Add_Strings(TEMP dst,TEMP t1,TEMP t2)
	{
		this.dst = dst;
		this.t1 = t1;
		this.t2 = t2;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{

		// Allocate fresh Temps
		TEMP charTemp = TEMP_FACTORY.getInstance().getFreshTEMP();
		TEMP src_offset1 = TEMP_FACTORY.getInstance().getFreshTEMP();
		TEMP src_offset2 = TEMP_FACTORY.getInstance().getFreshTEMP();
		TEMP src_offset3 = TEMP_FACTORY.getInstance().getFreshTEMP();
		TEMP src_offset4 = TEMP_FACTORY.getInstance().getFreshTEMP();
		TEMP dst_offset = TEMP_FACTORY.getInstance().getFreshTEMP();
		TEMP length = TEMP_FACTORY.getInstance().getFreshTEMP();
		
		// Allocate 2 fresh labels
		String label_length_s1  = getFreshLabel("Length_String1");
		String label_length_s2  = getFreshLabel("Length_String2");
		String label_concat_s1  = getFreshLabel("Concat_String1");
		String label_concat_s2  = getFreshLabel("Concat_String2");

		sir_MIPS_a_lot.getInstance().li(length, 1); // initialize length at one (null terminated)
		// Length of String 1
		sir_MIPS_a_lot.getInstance().move(src_offset1, t1);
		sir_MIPS_a_lot.getInstance().length_of_string(length,charTemp,src_offset1,label_length_s1);
		
		// Length of String 2
		sir_MIPS_a_lot.getInstance().move(src_offset2, t2);
		sir_MIPS_a_lot.getInstance().length_of_string(length,charTemp,src_offset2,label_length_s2);
		
		//Allocation
		sir_MIPS_a_lot.getInstance().MallocBytes(dst, length);
		
		sir_MIPS_a_lot.getInstance().move(dst_offset, dst);
		
		// Write string 1
		sir_MIPS_a_lot.getInstance().move(src_offset3, t1);
		sir_MIPS_a_lot.getInstance().move_a_string(dst_offset,charTemp,src_offset3,label_concat_s1);

		
		// Write string 2
		sir_MIPS_a_lot.getInstance().move(src_offset4, t2);
		sir_MIPS_a_lot.getInstance().move_a_string(dst_offset,charTemp,src_offset4,label_concat_s2);

	}
}
