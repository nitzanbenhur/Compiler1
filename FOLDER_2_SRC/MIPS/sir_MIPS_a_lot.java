/***********/
/* PACKAGE */
/***********/
package MIPS;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import RA.ControlFlowGraph;
/*******************/
/* PROJECT IMPORTS */
/*******************/
import RA.LivenessAnalyzer;
import RA.RegisterWriter;
import RA.iGraph;
import TEMP.*;

public class sir_MIPS_a_lot
{
	private int WORD_SIZE=4;
	/***********************/
	/* The file writer ... */
	/***********************/
	public static PrintWriter fileWriter;
	public static String outputFilePath;
	public static ControlFlowGraph cfg;
	private static int lineCounter = 0;

	/***********************/
	/* The file writer ... */
	/***********************/
	
	public void jal( String label ){
		fileWriter.format("\tjal %s\n",label);
		
		//cfg.addNode(++lineCounter, String.format("jal %s",label), "jal", label, null, null, false);
	}
	
	public void jalr( TEMP address ){
		
		int idx = address.getSerialNumber();
		
		fileWriter.format("\tjalr Temp_%d_tmp\n",idx);
		cfg.addNode(++lineCounter, String.format("jalr Temp_%d_tmp",idx), "jalr", null, null, new Integer[]{idx}, false);
	}
	
		public void bgt(TEMP oprnd1,TEMP oprnd2,String label)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		
		fileWriter.format("\tbgt Temp_%d_tmp,Temp_%d_tmp,%s\n",i1,i2,label);
		
		cfg.addNode(++lineCounter, String.format("bgt Temp_%d_tmp,Temp_%d_tmp,%s",i1,i2,label), "bgt", label,
				null, new Integer[]{i1,i2}, false);
	}
	public void ble(TEMP oprnd1,TEMP oprnd2,String label)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		
		fileWriter.format("\tble Temp_%d_tmp,Temp_%d_tmp,%s\n",i1,i2,label);	
		
		cfg.addNode(++lineCounter, String.format("ble Temp_%d_tmp,Temp_%d_tmp,%s",i1,i2,label), "ble", label,
				null, new Integer[]{i1,i2}, false);
	}

	public void bltz(TEMP oprnd,String label)
	{
		int i =oprnd.getSerialNumber();
		
		fileWriter.format("\tbltz Temp_%d_tmp, %s\n",i,label);
		
		cfg.addNode(++lineCounter, String.format("bltz Temp_%d_tmp, %s",i,label), "bltz", label,
				null, new Integer[]{i}, false);
	}
	
	public void bne_zero(TEMP oprnd,String label)
	{
		int i =oprnd.getSerialNumber();
		
		fileWriter.format("\tbne Temp_%d_tmp,$0,%s\n",i,label);	
		
		cfg.addNode(++lineCounter, String.format("bne Temp_%d_tmp,$0,%s",i,label), "bne", label,
				null, new Integer[]{i}, false);
	}
	
	
	public void assign_zero_or_one(TEMP dst, String label_AssignOne, String label_AssignZero, String label_end) {
		
		int idxdst=dst.getSerialNumber();
		
		fileWriter.format("%s:\n",label_AssignOne);
		cfg.addNode(++lineCounter, String.format("%s:",label_AssignOne), null, label_AssignOne, null, null, true);
		
		fileWriter.format("\tli Temp_%d_tmp,%d\n",idxdst,1);
		cfg.addNode(++lineCounter, String.format("li Temp_%d_tmp,%d",idxdst,1), "li", null,
				new Integer[]{idxdst}, null,false);
		
		fileWriter.format("\tj %s\n",label_end);
		cfg.addNode(++lineCounter, String.format("j %s",label_end), "j", label_end, null, null, false);
		
		fileWriter.format("%s:\n",label_AssignZero);
		cfg.addNode(++lineCounter, String.format("%s:",label_AssignZero), null, label_AssignZero, null, null, true);
		
		fileWriter.format("\tli Temp_%d_tmp,%d\n",idxdst,0);
		cfg.addNode(++lineCounter, String.format("li Temp_%d_tmp,%d",idxdst,1), "li", null,
				new Integer[]{idxdst}, null, false);
		
		fileWriter.format("%s:\n",label_end);
		cfg.addNode(++lineCounter, String.format("%s:",label_end), null,label_end, null, null, true);
	}
	
	public void print_string(TEMP t)
	{
		int idx=t.getSerialNumber();
		
		fileWriter.format("\taddi $a0,Temp_%d_tmp,0\n",idx);
		cfg.addNode(++lineCounter, String.format("\taddi $a0,Temp_%d_tmp,0\n",idx), "addi", null,
				null, new Integer[]{idx}, false);
		
		fileWriter.format("\tli $v0,4\n");
		
		fileWriter.format("\tsyscall\n");
	}
	
	public void beq(TEMP oprnd1,TEMP oprnd2,String label)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		
		fileWriter.format("\tbeq Temp_%d_tmp,Temp_%d_tmp,%s\n",i1,i2,label);
		
		cfg.addNode(++lineCounter, String.format("beq Temp_%d_tmp,Temp_%d_tmp,%s",i1,i2,label), "beq", label,
				null, new Integer[]{i1,i2}, false);
	}
	
	
	
	public void beq_zero(TEMP oprnd,String label)
	{
		int i =oprnd.getSerialNumber();
		
		fileWriter.format("\tbeq Temp_%d_tmp,$0,%s\n",i,label);
		cfg.addNode(++lineCounter, String.format("beq Temp_%d_tmp,$0,%s",i,label), "beq", label,
				null, new Integer[]{i}, false);
	}
	
	
	public void finalizeFile()
	{
		fileWriter.print("\tli $v0,10\n");
		
		fileWriter.print("\tsyscall\n");
		
		cfg.addNode(++lineCounter, "li $v0,10\nsyscall", "exit", null, null, null, false);
		
		fileWriter.close();
		
		cfg.createEdges();

		LivenessAnalyzer.analyze(cfg);

		//cfg.printNodes();
		//cfg.printLivenessRanges();

		iGraph g = new iGraph(cfg);

		//printOutFile();

		g.allocateRegisters();
		System.out.println("Valid Register Allocation: "  + g.validRegisterAllocation());

		RegisterWriter.filePath = outputFilePath;
		RegisterWriter.replaceAllTempsWithFinalRegisters(g);
	}

	public static void printOutFile(){
		System.out.println("############ FILE BEFORE REGISTER ALLOCATION #############");
		try (BufferedReader br = new BufferedReader(new FileReader(outputFilePath))) {
			String line = null;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		} catch (Exception e){
			System.out.println("file not found");
		}
	}

	public void exit()
	{
		fileWriter.format("\tli $v0,10\n");
		
		fileWriter.format("\tsyscall\n");
		
		cfg.addNode(++lineCounter, "li $v0,10\nsyscall", "exit", null, null, null, false);
	}
	
	public void print_int(TEMP t)
	{
		int idx=t.getSerialNumber();
		// fileWriter.format("\taddi $a0,Temp_%d_tmp,0\n",idx);
		fileWriter.format("\tmove $a0,Temp_%d_tmp\n",idx);
		cfg.addNode(++lineCounter, String.format("move $a0,Temp_%d_tmp",idx), "move", null,
				null, new Integer[]{idx}, false);
		
		fileWriter.format("\tli $v0,1\n");
		
		fileWriter.format("\tsyscall\n");
	}
	public TEMP addressLocalVar(int serialLocalVarNum)
	{
		TEMP t  = TEMP_FACTORY.getInstance().getFreshTEMP();
		int idx = t.getSerialNumber();
		int offset = -serialLocalVarNum*WORD_SIZE;
		
		fileWriter.format("\taddi Temp_%d_tmp,$fp,%d\n",idx, offset);
		cfg.addNode(++lineCounter, String.format("addi Temp_%d_tmp,$fp,%d",idx,offset), "addi", null,
				new Integer[]{idx}, null, false);
		
		return t;
	}
	
	public void addi(TEMP t1, TEMP t2, int num)
	{
		int i1 = t1.getSerialNumber();
		int i2 = t2.getSerialNumber();
		
		fileWriter.format( "\taddi Temp_%d_tmp,Temp_%d_tmp,%d\n", i1, i2, num);
		cfg.addNode(++lineCounter, String.format( "\taddi Temp_%d_tmp,Temp_%d_tmp,%d\n", i1, i2, num), "addi", null,
				new Integer[]{i1}, new Integer[]{i2}, false);
	}
	
	public void sll(TEMP dst, TEMP src, int num)
	{
		int idxdst = dst.getSerialNumber();
		int idxsrc = src.getSerialNumber();
		
		fileWriter.format( "\tsll Temp_%d_tmp,Temp_%d_tmp,%d\n", idxdst, idxsrc, num);
		cfg.addNode(++lineCounter, String.format( "sll Temp_%d_tmp,Temp_%d_tmp,%d", idxdst, idxsrc, num), "sll", null,
				new Integer[]{idxdst}, new Integer[]{idxsrc}, false);
	}
	
	public void srl(TEMP dst, TEMP src, int num)
	{
		int idxdst = dst.getSerialNumber();
		int idxsrc = src.getSerialNumber();
		
		fileWriter.format( "\tsrl Temp_%d_tmp,Temp_%d_tmp,%d\n", idxdst, idxsrc, num);
		cfg.addNode(++lineCounter, String.format( "srl Temp_%d_tmp,Temp_%d_tmp,%d", idxdst, idxsrc, num), "srl", null,
				new Integer[]{idxdst}, new Integer[]{idxsrc}, false);
	}
	
	public void Clear_Alloaction(TEMP address, TEMP size, String label) {
		TEMP offset = TEMP_FACTORY.getInstance().getFreshTEMP();
		
		int idxadr = address.getSerialNumber();
		int idxsiz = size.getSerialNumber();
		int idxoff = offset.getSerialNumber();
		
		fileWriter.format( "\tadd Temp_%d_tmp, Temp_%d_tmp, Temp_%d_tmp\n", idxoff, idxadr,idxsiz);
		cfg.addNode(++lineCounter, String.format( "add Temp_%d_tmp, Temp_%d_tmp, Temp_%d_tmp", idxoff, idxadr,idxsiz), "add", null,
				new Integer[]{idxoff} ,new Integer[]{idxadr,idxsiz}, false);
		
		fileWriter.format("%s: \n",label);
		cfg.addNode(++lineCounter, String.format("%s:",label), null, label, null, null, true);
		
		fileWriter.format( "\taddi Temp_%d_tmp, Temp_%d_tmp, -4\n", idxoff, idxoff);
		cfg.addNode(++lineCounter, String.format( "addi Temp_%d_tmp, Temp_%d_tmp, -4", idxoff, idxoff), "addi", null,
				new Integer[]{idxoff} ,new Integer[]{idxoff}, false);
		
		 fileWriter.format("\tsw $zero, 0(Temp_%d_tmp)\n", idxoff );
		 cfg.addNode(++lineCounter, String.format("sw $zero, 0(%d)", idxoff), "sw", null,
					null, new Integer[]{idxoff}, false);
		 
		fileWriter.format("\tbne Temp_%d_tmp,Temp_%d_tmp,%s\n",idxoff,idxadr, label);
		cfg.addNode(++lineCounter, String.format("beq Temp_%d_tmp,Temp_%d_tmp,%s",idxoff,idxadr, label), "beq", label,
				null, new Integer[]{idxoff,idxadr}, false);
		
		
	}
	
	public void Malloc( TEMP dest, TEMP size ){
		
		int idxdst = dest.getSerialNumber();
		int idxsiz = size.getSerialNumber();
		
		fileWriter.format( "\taddi $a0, Temp_%d_tmp, 0\n",idxsiz );
		cfg.addNode(++lineCounter, String.format( "addi $a0, Temp_%d_tmp, 0",idxsiz ), "addi", null,
				null ,new Integer[]{idxsiz}, false);
		
		fileWriter.format( "\tli $v0,9\n" );
		
		fileWriter.format( "\tsyscall\n" );
		
		fileWriter.format( "\taddi Temp_%d_tmp ,$v0 , 0\n",idxdst );
		cfg.addNode(++lineCounter, String.format( "\taddi Temp_%d_tmp ,$v0 , 0\n",idxdst ), "addi", null,
				new Integer[]{idxdst}, null, false);
	}
	
	public void Malloc( TEMP dest, int size){
		
		int idxdst = dest.getSerialNumber();
		
		fileWriter.format( "\taddi $a0, $zero, %d\n", 4*size);
		
		fileWriter.format( "\tli $v0,9\n" );
		
		fileWriter.format( "\tsyscall\n" );
		
		fileWriter.format( "\taddi Temp_%d_tmp ,$v0 , 0\n",idxdst );
		cfg.addNode(++lineCounter, String.format( "\taddi Temp_%d_tmp ,$v0 , 0\n",idxdst ), "addi", null,
				new Integer[]{idxdst}, null, false);
	}
	
	public void MallocBytes( TEMP dest, TEMP size){
		
		int idxdst = dest.getSerialNumber();
		int idxsiz = size.getSerialNumber();

		fileWriter.format("\taddi $a0, Temp_%d_tmp, 0\n",idxsiz);
		cfg.addNode(++lineCounter, String.format("\taddi $a0, Temp_%d_tmp, 0\n",idxsiz), "addi", null,
				null, new Integer[]{idxsiz}, false);
		
		fileWriter.format( "\tli $v0,9\n" );

		fileWriter.format( "\tsyscall\n" );
		
		fileWriter.format( "\taddi Temp_%d_tmp ,$v0 , 0\n",idxdst );
		cfg.addNode(++lineCounter, String.format( "\taddi Temp_%d_tmp ,$v0 , 0\n",idxdst ), "addi", null,
				new Integer[]{idxdst}, null, false);
	}
	
	public void store_registers(){
		fileWriter.format("\taddi $sp, $sp, -32\n");
		for (int i = 0; i < 8; i++){
			fileWriter.format("\tsw	$t%d, %d($sp)\n",i,4*i);
		}
	}
	
	public void re_store_registers(){
		for (int i = 0; i < 8; i++){
			fileWriter.format("\tlw	$t%d, %d($sp)\n",i,4*i);
		}
		fileWriter.format("\taddi $sp, $sp, 32\n");
	}
	
	
	public void allocate_stack(int size){
		fileWriter.format( "\taddi $sp, $sp, %d\n",-4*size );
	}
	
	
	public void load_return_value( TEMP t ){
		fileWriter.format( "\taddi Temp_%d_tmp, $v0, 0\n", t.getSerialNumber() );
		cfg.addNode(++lineCounter, String.format( "\taddi Temp_%d_tmp, $v0, 0\n", t.getSerialNumber() ), "addi", null,
				new Integer[]{t.getSerialNumber()}, null, false);
	}
	
	
	public void store_return_value( TEMP t ){
		fileWriter.format( "\taddi $v0, Temp_%d_tmp, 0\n", t.getSerialNumber() );	
		cfg.addNode(++lineCounter, String.format( "addi Temp_%d_tmp, $v0, 0", t.getSerialNumber() ), "addi", null,
				null, new Integer[]{t.getSerialNumber()}, false);
	}
	
	
	public void storeByte(TEMP dst,TEMP src)
	{
		int idxdst=dst.getSerialNumber();
		int idxsrc=src.getSerialNumber();
		
		fileWriter.format("\tsb Temp_%d_tmp,0(Temp_%d_tmp)\n",idxsrc,idxdst);
		cfg.addNode(++lineCounter, String.format("sb Temp_%d_tmp,0(Temp_%d_tmp)",idxsrc,idxdst), "sb", null,
				null, new Integer[]{idxsrc,idxdst}, false);
	}

	public void lb(TEMP dst,TEMP src)
	{
		int idxdst=dst.getSerialNumber();
		int idxsrc=src.getSerialNumber();
		fileWriter.format("\tlb Temp_%d_tmp, 0(Temp_%d_tmp)\n",idxdst , idxsrc);
		cfg.addNode(++lineCounter, String.format("lb Temp_%d_tmp,0(Temp_%d_tmp)",idxsrc,idxdst), "lb", null,
				new Integer[]{idxdst}, new Integer[]{idxsrc}, false);
	}
	
	
	public void startFunc( int SizeFrame )
	{
		fileWriter.format("\taddi $sp, $sp, -8\n");	// make place to save the farme pointer and ra
		fileWriter.format("\tsw	$fp, 0($sp)\n");	// save fp
		fileWriter.format("\tsw	$ra, 4($sp)\n");	// save ra
		fileWriter.format("\taddi $fp, $sp, 0\n");	// set fp = sp
		fileWriter.format("\taddi $sp, $sp, %d\n",-4*SizeFrame);	// allocate the stack frame.
	}
	
	public void endFunc( int SizeFrame,String endLabel,String name )
	{
		fileWriter.format("%s: \n",endLabel);
		cfg.addNode(++lineCounter, String.format("%s:",endLabel), null, endLabel, null, null, true);
		
		fileWriter.format("\taddi $sp, $sp, %d\n",4*SizeFrame );	// deallocate the stack frame.
		fileWriter.format("\tlw	$ra, 4($fp)\n");					// restore ra 
		fileWriter.format("\tlw	$fp, ($fp)\n");						// restore fp
		fileWriter.format("\taddi $sp, $sp, 8\n");					// deallocate place for ra,fp 
		if (name.equals( "main" ) ){
			return;	
		}
		fileWriter.format("\tjr $ra\n");							// jump register.
		cfg.addNode(++lineCounter, "jr $ra", "jr", null, null, null, false);
	}
	
	public void divide_int(TEMP dst, TEMP oprnd1,TEMP oprnd2)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		int dstidx=dst.getSerialNumber();

		fileWriter.format("\tdiv Temp_%d_tmp,Temp_%d_tmp\n",i1,i2);
		cfg.addNode(++lineCounter, String.format("div Temp_%d_tmp,Temp_%d_tmp",i1,i2), "div", null,
				null, new Integer[]{i1,i2}, false);
		
		fileWriter.format("\tmflo Temp_%d_tmp\n",dstidx);
		cfg.addNode(++lineCounter, String.format("\tmflo Temp_%d_tmp\n",dstidx), "mflo", null,
				new Integer[]{dstidx}, null, false);
	}
	
	public void multiply_int(TEMP dst, TEMP oprnd1,TEMP oprnd2)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		int dstidx=dst.getSerialNumber();

		fileWriter.format("\tmult Temp_%d_tmp,Temp_%d_tmp\n",i1,i2);
		cfg.addNode(++lineCounter, String.format("mult Temp_%d_tmp,Temp_%d_tmp",i1,i2), "mult", null,
				null, new Integer[]{i1,i2}, false);
		
		fileWriter.format("\tmflo Temp_%d_tmp\n",dstidx);
		cfg.addNode(++lineCounter, String.format("\tmflo Temp_%d_tmp\n",dstidx), "mflo", null,
				new Integer[]{dstidx}, null, false);
	}
		
	public void compare_strings(TEMP offset_s1, TEMP offset_s2, TEMP char_s1, TEMP char_s2,
			String label_Comp_Loop, String label_AssignOne, String label_AssignZero) {
		
		int idxch1=char_s1.getSerialNumber();
		int idxch2=char_s2.getSerialNumber();
		int idxof1=offset_s1.getSerialNumber();
		int idxof2=offset_s2.getSerialNumber();
		
		fileWriter.format("%s:\n",label_Comp_Loop);
		cfg.addNode(++lineCounter, String.format("%s:",label_Comp_Loop), null, label_Comp_Loop, null, null, true);
		
		fileWriter.format("\tlb Temp_%d_tmp, 0(Temp_%d_tmp)\n",idxch1, idxof1);
		cfg.addNode(++lineCounter, String.format("lb Temp_%d_tmp, 0(Temp_%d_tmp)",idxch1 , idxof1), "lb", null,
				new Integer[]{idxch1}, new Integer[]{idxof1}, false);
		
		fileWriter.format("\tlb Temp_%d_tmp, 0(Temp_%d_tmp)\n", idxch2, idxof2);
		cfg.addNode(++lineCounter, String.format("lb Temp_%d_tmp, 0(Temp_%d_tmp)",idxch2 , idxof2), "lb", null,
				new Integer[]{idxch2}, new Integer[]{idxof2}, false);
		
		fileWriter.format("\tbne Temp_%d_tmp,Temp_%d_tmp,%s\n",idxch1,idxch2,label_AssignOne);
		cfg.addNode(++lineCounter, String.format("\tbne Temp_%d_tmp,Temp_%d_tmp,%s\n",idxch1,idxch2,label_AssignOne), "bne", label_AssignOne,
				null, new Integer[]{idxch1,idxch2}, false);
		
		fileWriter.format("\tbeq Temp_%d_tmp,$0,%s\n",idxch1,label_AssignZero);
		cfg.addNode(++lineCounter, String.format("beq Temp_%d_tmp,$0,%s",idxch1,label_AssignZero), "beq", label_AssignZero,
				null, new Integer[]{idxch1}, false);
		
		fileWriter.format("\taddi Temp_%d_tmp,Temp_%d_tmp,1\n",idxof1,idxof1);
		cfg.addNode(++lineCounter, String.format("addi Temp_%d_tmp,Temp_%d_tmp,1",idxof1,idxof1), "addi", null,
				new Integer[]{idxof1}, new Integer[]{idxof1}, false);
		
		fileWriter.format("\taddi Temp_%d_tmp,Temp_%d_tmp,1\n",idxof2,idxof2);
		cfg.addNode(++lineCounter, String.format("addi Temp_%d_tmp,Temp_%d_tmp,1",idxof2,idxof2), "addi", null,
				new Integer[]{idxof2}, new Integer[]{idxof2}, false);
		
		fileWriter.format("\tj %s\n",label_Comp_Loop);
		cfg.addNode(++lineCounter, String.format("j %s",label_Comp_Loop), "j", label_Comp_Loop, null, null, false);
	}
		
	public void print_data_string(String label)
	{
		fileWriter.format("\tla $a0,%s\n",label);
		fileWriter.format("\tli $v0,4\n");
		fileWriter.format("\tsyscall\n");
	}
	
	public void check_int_overflow_underflow(TEMP dst, String overflow_label, String underflow_label, String end_label) {
				
		int idxdst=dst.getSerialNumber();
		
		// li max, 32767
		fileWriter.format("\tli $a2,%d\n",32767);
		cfg.addNode(++lineCounter, String.format("li $a2,%d",32767), "li", null, null, null, false);
		
		// blt max, dst, overflow_label
		fileWriter.format("\tblt $a2,Temp_%d_tmp,%s\n",idxdst,overflow_label);
		cfg.addNode(++lineCounter, String.format("blt $a2,Temp_%d_tmp,%s",idxdst,overflow_label), "blt", overflow_label,
				null, new Integer[]{idxdst}, false);
		
		// li min, -32768
		fileWriter.format("\tli $a2,%d\n",-32768);
		cfg.addNode(++lineCounter, String.format("li $a2,%d",-32768), "li", null, null, null, false);
		
		// blt dst, min, underflow_label
		fileWriter.format("\tblt Temp_%d_tmp,$a2,%s\n",idxdst,underflow_label);
		cfg.addNode(++lineCounter, String.format("blt Temp_%d_tmp,$a2,%s",idxdst,underflow_label), "blt", underflow_label,
				null ,new Integer[]{idxdst}, false);
		
		// j end_label
		fileWriter.format("\tj %s\n",end_label);
		cfg.addNode(++lineCounter, String.format("j %s",end_label), "j", end_label, null, null, false);
		
		// overflow_label:
		fileWriter.format("%s:\n",overflow_label);
		cfg.addNode(++lineCounter, String.format("%s:",overflow_label), null, overflow_label, null, null, true);
		
		// li dst, 32767
		fileWriter.format("\tli Temp_%d_tmp,%d\n",idxdst,32767);
		cfg.addNode(++lineCounter, String.format("li Temp_%d_tmp,%d",idxdst,32767), "li", null,
				new Integer[]{idxdst}, null, false);
		
		// j end_label
		fileWriter.format("\tj %s\n",end_label);
		cfg.addNode(++lineCounter, String.format("j %s",end_label), "j", end_label, null, null, false);
		
		//underflow_label:
		fileWriter.format("%s:\n",underflow_label);
		cfg.addNode(++lineCounter, String.format("%s:",underflow_label), null, underflow_label, null, null, true);
		
		// li dst, -32768
		fileWriter.format("\tli Temp_%d_tmp,%d\n",idxdst,-32768);
		cfg.addNode(++lineCounter, String.format("li Temp_%d_tmp,%d",idxdst,-32768), "li", null,
				new Integer[]{idxdst}, null, false);
		
		// end_label:
		fileWriter.format("%s:\n",end_label);
		cfg.addNode(++lineCounter, String.format("%s:",end_label), null, end_label, null, null, true);
	}
	
	public void length_of_string(TEMP length,TEMP charTemp,TEMP src,String inlabel)
	{
		int idxchar=charTemp.getSerialNumber();
		int idxsrc=src.getSerialNumber();
		int idxlen=length.getSerialNumber();
		
		//inlabel:
		fileWriter.format("%s:\n",inlabel);
		cfg.addNode(++lineCounter, String.format("%s:",inlabel), null, inlabel, null, null, true);
		
		//lb char, 0(src)
		fileWriter.format("\tlb Temp_%d_tmp, 0(Temp_%d_tmp)\n",idxchar , idxsrc);
		cfg.addNode(++lineCounter, String.format("lb Temp_%d_tmp, 0(Temp_%d_tmp)",idxchar , idxsrc), "lb", null,
				new Integer[]{idxchar}, new Integer[]{idxsrc}, false);
		
		//addi len,len,1
		fileWriter.format("\taddi Temp_%d_tmp,Temp_%d_tmp,1\n",idxlen,idxlen);
		cfg.addNode(++lineCounter, String.format("addi Temp_%d_tmp,Temp_%d_tmp,1",idxlen,idxlen), "addi", null,
				new Integer[]{idxlen}, new Integer[]{idxlen}, false);
		
		//addi src,src,1
		fileWriter.format("\taddi Temp_%d_tmp,Temp_%d_tmp,1\n",idxsrc,idxsrc);
		cfg.addNode(++lineCounter, String.format("addi Temp_%d_tmp,Temp_%d_tmp,1",idxsrc,idxsrc), "addi", null,
				new Integer[]{idxsrc}, new Integer[]{idxsrc}, false);
		
		//bne char,$0,inlabel
		fileWriter.format("\tbne Temp_%d_tmp,$0,%s\n",idxchar,inlabel);
		cfg.addNode(++lineCounter, String.format("bne Temp_%d_tmp,$0,%s",idxchar,inlabel), "bne", inlabel,
				null, new Integer[]{idxchar}, false);
		
		//addi len,len,-1
		fileWriter.format("\taddi Temp_%d_tmp,Temp_%d_tmp,-1\n",idxlen,idxlen);
		cfg.addNode(++lineCounter, String.format("addi Temp_%d_tmp,Temp_%d_tmp,-1",idxlen,idxlen), "addi", null,
				new Integer[]{idxlen}, new Integer[]{idxlen}, false);
		
		
	}
	
	public void move(TEMP dst,TEMP src)
	{
		int idxdst=dst.getSerialNumber();
		int idxsrc=src.getSerialNumber();
		fileWriter.format("\tmove Temp_%d_tmp,Temp_%d_tmp\n",idxdst,idxsrc);
		cfg.addNode(++lineCounter, String.format("move Temp_%d_tmp,Temp_%d_tmp",idxdst,idxsrc), "move", null,
				new Integer[]{idxdst}, new Integer[]{idxsrc}, false);
	}
	
	public void move_a_string(TEMP dst,TEMP charTemp,TEMP src,String inlabel)
	{
		int idxchar=charTemp.getSerialNumber();
		int idxsrc=src.getSerialNumber();
		int idxdst=dst.getSerialNumber();
		
		//inlabel:
		fileWriter.format("%s:\n",inlabel);
		cfg.addNode(++lineCounter, String.format("%s:",inlabel), null, inlabel, null, null, true);
		
		//lb char, 0(src)
		fileWriter.format("\tlb Temp_%d_tmp, 0(Temp_%d_tmp)\n",idxchar , idxsrc);
		cfg.addNode(++lineCounter, String.format("lb Temp_%d_tmp, 0(Temp_%d_tmp)",idxchar , idxsrc), "lb", null,
				new Integer[]{idxchar}, new Integer[]{idxsrc}, false);
		
		//sb, src,0(dst)
		fileWriter.format("\tsb Temp_%d_tmp,0(Temp_%d_tmp)\n",idxchar,idxdst);
		cfg.addNode(++lineCounter, String.format("sb Temp_%d_tmp,0(Temp_%d_tmp)",idxchar,idxdst), "sb", null,
				null, new Integer[]{idxsrc,idxdst}, false);
		
		//addi dst,dst,1
		fileWriter.format("\taddi Temp_%d_tmp,Temp_%d_tmp,1\n",idxdst,idxdst);
		cfg.addNode(++lineCounter, String.format("addi Temp_%d_tmp,Temp_%d_tmp,1",idxdst,idxdst), "addi", null,
				new Integer[]{idxdst}, new Integer[]{idxdst}, false);
		
		//addi src,src,1
		fileWriter.format("\taddi Temp_%d_tmp,Temp_%d_tmp,1\n",idxsrc,idxsrc);
		cfg.addNode(++lineCounter, String.format("addi Temp_%d_tmp,Temp_%d_tmp,1",idxsrc,idxsrc), "addi", null,
				new Integer[]{idxsrc}, new Integer[]{idxsrc}, false);
		
		//bne char,$0,inlabel
		fileWriter.format("\tbne Temp_%d_tmp,$0,%s\n",idxchar,inlabel);
		cfg.addNode(++lineCounter, String.format("bne Temp_%d_tmp,$0,%s",idxchar,inlabel), "bne", inlabel,
				null, new Integer[]{idxchar}, false);
		
		//addi dst,dst,-1
		fileWriter.format("\taddi Temp_%d_tmp,Temp_%d_tmp,-1\n",idxdst,idxdst);
		cfg.addNode(++lineCounter, String.format("addi Temp_%d_tmp,Temp_%d_tmp,-1",idxdst,idxdst), "addi", null,
				new Integer[]{idxdst}, new Integer[]{idxdst}, false);
		
	}
	
	public void load(TEMP dst,TEMP src)
	{
		int idxdst=dst.getSerialNumber();
		int idxsrc=src.getSerialNumber();
		fileWriter.format("\tlw Temp_%d_tmp,0(Temp_%d_tmp)\n",idxdst,idxsrc);
		cfg.addNode(++lineCounter, String.format("lw Temp_%d_tmp,0(Temp_%d_tmp)",idxdst,idxsrc), "lw", null,
				new Integer[]{idxdst}, new Integer[]{idxsrc}, false);
	}
	
	public void load_param_var( TEMP dst, int fpOffset ){
		int idx=dst.getSerialNumber();
		int offset = 4*fpOffset+8;
		
		fileWriter.format("\tlw Temp_%d_tmp, %d($fp)\n",idx , offset );
		cfg.addNode(++lineCounter, String.format("lw Temp_%d_tmp, %d($fp)",idx, offset ), "lw", null,
				new Integer[]{idx}, null, false);
	}
	
	public void change_param_var( TEMP dst, int fpOffset ){
		 int idx=dst.getSerialNumber();
		 int offset = 4*fpOffset+8;
		 
		 fileWriter.format("\tsw Temp_%d_tmp, %d($fp)\n",idx , offset );
		 cfg.addNode(++lineCounter, String.format("\tsw Temp_%d_tmp, %d($fp)\n",idx , offset ), "sw", null,
					null, new Integer[]{idx}, false);
	}
	
	public void load_local_var( TEMP dst, int fpOffset ){
		int idx=dst.getSerialNumber();
		int offset = -4*fpOffset;
		
		fileWriter.format("\tlw Temp_%d_tmp, %d($fp)\n",idx , offset );
		cfg.addNode(++lineCounter, String.format("\tlw Temp_%d_tmp, %d($fp)\n",idx , offset), "lw", null,
				new Integer[]{idx}, null, false);
	}
	
	public void load_sp( TEMP dst ){
		int idx=dst.getSerialNumber();
		fileWriter.format("\tlw Temp_%d_tmp, 0($sp)\n",idx );
		cfg.addNode(++lineCounter, String.format("\tlw Temp_%d_tmp, 0($sp)\n",idx ), "lw", null,
				new Integer[]{idx}, null, false);
	}
		
	public void store(TEMP dst,TEMP src)
	{
		int idxdst=dst.getSerialNumber();
		int idxsrc=src.getSerialNumber();
		fileWriter.format("\tsw Temp_%d_tmp,0(Temp_%d_tmp)\n",idxsrc,idxdst);
		cfg.addNode(++lineCounter, String.format("sw Temp_%d_tmp,0(Temp_%d_tmp)",idxsrc,idxdst), "sw", null,
				null, new Integer[]{idxsrc, idxdst}, false);
	}
		
	public void storeRetValue(TEMP src)
	{
		int idx=src.getSerialNumber();
		fileWriter.format("\tsw Temp_%d_tmp, 8($sp)\n",idx);
		cfg.addNode(++lineCounter, String.format("sw Temp_%d_tmp, 8($sp)",idx), "sw", null,
				null, new Integer[]{idx}, false);
	}
	
	public void store_local_var(int fpOffset, TEMP src){
		int idx=src.getSerialNumber();
		fileWriter.format("\tsw Temp_%d_tmp, %d($fp)\n", idx, -4*fpOffset );
		cfg.addNode(++lineCounter, String.format("sw Temp_%d_tmp, %d($fp)", idx, -4*fpOffset ), "sw", null,
				null, new Integer[]{idx}, false);
	}
	
	public void store_param_var(int fpOffset, TEMP src){
		int idx=src.getSerialNumber();
		fileWriter.format("\tsw Temp_%d_tmp, %d($sp)\n", idx, 4*fpOffset );
		cfg.addNode(++lineCounter, String.format("sw Temp_%d_tmp, %d($sp)", idx, -4*fpOffset ), "sw", null,
				null, new Integer[]{idx}, false);
	}
	
	
	public void Set_Zero( TEMP t )
	{
		int idx=t.getSerialNumber();
		fileWriter.format("\taddi Temp_%d_tmp, $zero, 0\n",idx);
		cfg.addNode(++lineCounter, String.format("addi Temp_%d_tmp, $zero, 0",idx), "addi", null,
				new Integer[]{idx}, null, false);
		
	}
	
	public void Set_Num( TEMP t, int val )
	{
		int idx=t.getSerialNumber();
		fileWriter.format("\taddi Temp_%d_tmp, $zero, %d\n",idx,val);
		cfg.addNode(++lineCounter, String.format("addi Temp_%d_tmp, $zero, %d",idx,val), "addi", null,
				new Integer[]{idx}, null, false);
		
	}
	
	public void bge(TEMP oprnd1,TEMP oprnd2,String label)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		
		fileWriter.format("\tbge Temp_%d_tmp,Temp_%d_tmp,%s\n",i1,i2,label);
		cfg.addNode(++lineCounter, String.format("bge Temp_%d_tmp,Temp_%d_tmp,%s",i1,i2,label), "bge", label,
				null, new Integer[]{i1,i2}, false);
	}
	
	public void bne(TEMP oprnd1,TEMP oprnd2,String label)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		
		fileWriter.format("\tbne Temp_%d_tmp,Temp_%d_tmp,%s\n",i1,i2,label);
		cfg.addNode(++lineCounter, String.format("bne Temp_%d_tmp,Temp_%d_tmp,%s",i1,i2,label), "bne", label,
				null, new Integer[]{i1,i2}, false);
	}
	
	public void li(TEMP t,int value)
	{
		int idx=t.getSerialNumber();
		fileWriter.format("\tli Temp_%d_tmp,%d\n",idx,value);
		cfg.addNode(++lineCounter, String.format("li Temp_%d_tmp,%d",idx,value), "li", null,
				new Integer[]{idx}, null, false);
	}
	public void add(TEMP dst,TEMP oprnd1,TEMP oprnd2)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		int dstidx=dst.getSerialNumber();

		fileWriter.format("\tadd Temp_%d_tmp,Temp_%d_tmp,Temp_%d_tmp\n",dstidx,i1,i2);
		cfg.addNode(++lineCounter, String.format("add Temp_%d_tmp,Temp_%d_tmp,Temp_%d_tmp",dstidx,i1,i2), "add", null,
				new Integer[]{dstidx}, new Integer[]{i1,i2}, false);
	}
	public void label(String inlabel)
	{
		fileWriter.format("%s:\n",inlabel);
		cfg.addNode(++lineCounter, String.format("%s:\n",inlabel), null, inlabel, null, null, true);
	}	
	public void jump(String inlabel)
	{
		fileWriter.format("\tj %s\n",inlabel);
		cfg.addNode(++lineCounter, String.format("\tj %s\n",inlabel), "j", inlabel, null, null, false);
	}	
	public void blt(TEMP oprnd1,TEMP oprnd2,String label)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		
		fileWriter.format("\tblt Temp_%d_tmp,Temp_%d_tmp,%s\n",i1,i2,label);
		cfg.addNode(++lineCounter, String.format("blt Temp_%d_tmp,Temp_%d_tmp,%s",i1,i2,label), "blt", label,
				null, new Integer[]{i1,i2}, false);
	}

	public void sub(TEMP dst,TEMP oprnd1,TEMP oprnd2)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		int dstidx=dst.getSerialNumber();

		fileWriter.format("\tsub Temp_%d_tmp,Temp_%d_tmp,Temp_%d_tmp\n",dstidx,i1,i2);
		cfg.addNode(++lineCounter, String.format("sub Temp_%d_tmp,Temp_%d_tmp,Temp_%d_tmp",dstidx,i1,i2), "sub", null,
				new Integer[]{dstidx}, new Integer[]{i1,i2}, false);
	}
	
	public void addGlobalVar(String name){
		fileWriter.printf("\t%s: .word 0\n",name);
	}
	
	public void store_global(String name, TEMP data){
		fileWriter.printf("\tla $a0, %s\n",name);
		
		fileWriter.printf("\tsw Temp_%d_tmp 0($a0)\n",data.getSerialNumber() );
		cfg.addNode(++lineCounter, String.format("sw Temp_%d_tmp 0($a0)",data.getSerialNumber() ), "sw", null,
				null, new Integer[]{data.getSerialNumber()}, false);
	}
	
	public void load_global(String name, TEMP dst){
		fileWriter.printf("\tla $a0, %s\n",name);
		
		fileWriter.printf("\tlw Temp_%d_tmp, 0($a0)\n",dst.getSerialNumber());
		cfg.addNode(++lineCounter, String.format("lw Temp_%d_tmp, 0($a0)",dst.getSerialNumber()), "lw", null,
				new Integer[]{dst.getSerialNumber()}, null, false);
	}
	
	public void end_data(){
		
		fileWriter.print("\tstring_access_violation: .asciiz \"Access Violation\"\n");
		fileWriter.print("\tstring_illegal_div_by_0: .asciiz \"Division By Zero\"\n");
		fileWriter.print("\tstring_invalid_ptr_dref: .asciiz \"Invalid Pointer Dereference\"\n");
		fileWriter.print("\tstring_space_character:	.asciiz \" \"\n");
		fileWriter.print(".text\n" );
	}
	
	public void allocateDataSpace(String name, int size) {
		fileWriter.printf("\t%s: .space %d\n",name, 4*size);
	}
	
	public void loadLabelAddress (TEMP dst, String label) {
		int idxdst = dst.getSerialNumber();
		
		fileWriter.format("\tla Temp_%d_tmp,%s\n", idxdst, label);
		cfg.addNode(++lineCounter, String.format("la Temp_%d_tmp,%s", idxdst, label), "la", null,
				new Integer[]{idxdst}, null, false);
	}
	
	public void store_string_exp(String label, String value) {
		fileWriter.format("\t%s: .asciiz \"%s\"\n", label, value);
	}
	
	public void addToFuncTable(String tableLabel, String funcionLabel, TEMP tableAddress, TEMP functionAddress, int offset) {
		
		int idxtable = tableAddress.getSerialNumber();
		int idxfunc = functionAddress.getSerialNumber();
		
		fileWriter.format("\tla Temp_%d_tmp,%s\n", idxtable, tableLabel);
		cfg.addNode(++lineCounter, String.format("la Temp_%d_tmp,%s", idxtable, tableLabel), "la", null,
				new Integer[]{idxtable}, null, false);
		
		fileWriter.format("\tla Temp_%d_tmp,%s\n", idxfunc, funcionLabel);
		cfg.addNode(++lineCounter, String.format("la Temp_%d_tmp,%s", idxfunc, funcionLabel), "la", null,
				new Integer[]{idxfunc}, null, false);
		
		fileWriter.format("\taddi Temp_%d_tmp,Temp_%d_tmp,%d\n", idxtable, idxtable, 4 * offset);
		cfg.addNode(++lineCounter, String.format("addi Temp_%d_tmp,Temp_%d_tmp,0", idxtable, idxtable), "addi", null,
				new Integer[]{idxtable}, new Integer[]{idxtable}, false);
		
		fileWriter.format("\tsw Temp_%d_tmp,(Temp_%d_tmp)\n", idxfunc, idxtable);
		cfg.addNode(++lineCounter, String.format("sw Temp_%d_tmp,(Temp_%d_tmp)", idxfunc, idxtable), "sw", null,
				null, new Integer[]{idxtable,idxfunc}, false);
	}
	
	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static sir_MIPS_a_lot instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected sir_MIPS_a_lot() {
		cfg = new ControlFlowGraph();
	}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	
	public void setWriter(PrintWriter file_writer){
		this.fileWriter = fileWriter;
	}

	public static sir_MIPS_a_lot getInstance()
	{
		if (instance == null)
		{
			/*******************************/
			/* [0] The instance itself ... */
			/*******************************/
			instance = new sir_MIPS_a_lot();
			System.out.print("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");

			/*************************/
			/* [3] Print data section*/
			/*************************/
			instance.fileWriter.print(".data\n");
			System.out.print("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
				
			/************************************************/
			/* [4] Print text section with entry point main */
			/************************************************/
		}
		return instance;
	}
}
