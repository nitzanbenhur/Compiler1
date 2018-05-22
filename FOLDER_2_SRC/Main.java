   
import java.io.*;
import java.io.PrintWriter;
import java_cup.runtime.Symbol;
import AST.*;
import IR.*;
import MIPS.*;

public class Main
{
	static public void main(String argv[])
	{
		Lexer l;
		Parser p;
		Symbol s;
		AST_DEC_LIST AST;
		FileReader file_reader;
		PrintWriter file_writer;
		String inputFilename = argv[0];
		String outputFilename = argv[1];
		
		try
		{
			/********************************/
			/* [1] Initialize a file reader */
			/********************************/
			file_reader = new FileReader(inputFilename);

			/********************************/
			/* [2] Initialize file writers */
			/********************************/
			file_writer = new PrintWriter(outputFilename);
			
			/******************************/
			/* [3] Initialize a new lexer */
			/******************************/
			l = new Lexer(file_reader);
			
			/*******************************/
			/* [4] Initialize a new parser */
			/*******************************/
			p = new Parser(l,file_writer);

			/***********************************/
			/* [5] 3 ... 2 ... 1 ... Parse !!! */
			/***********************************/
			AST = (AST_DEC_LIST) p.parse().value;
			
			/*************************/
			/* [6] Print the AST ... */
			/*************************/
			AST.PrintMe();

			/**************************/
			/* [7] Semant the AST ... */
			/**************************/
			AST.SemantMe(file_writer,0);

			/**********************/
			/* [8] IR the AST ... */
			/**********************/
			AST.IRAllocateFuncTables();
			AST.IRAllocateGlobalVars();
			AST.IRWriteStrings();
			IR.getInstance().Add_IRcommand( new IRcommand_End_Data() );
			AST.IRWriteData();
			IR.getInstance().Add_IRcommand( new IRcommand_Jump_Main() );
			AST.IRme();
			/***********************/
			/* [9] Set file writer 
			/***********************/
			sir_MIPS_a_lot.outputFilePath = argv[1];
			 sir_MIPS_a_lot.fileWriter = file_writer;
			
			/***********************/
			/* [10]MIPS the IR ... */
			/***********************/
			
			IR.getInstance().MIPSme();

			/**************************************/
			/* [11 Finalize AST GRAPHIZ DOT file */
			/**************************************/
			AST_GRAPHVIZ.getInstance().finalizeFile();			

			/***************************/
			/* [12] Finalize MIPS file */
			/***************************/
			sir_MIPS_a_lot.getInstance().finalizeFile();			

			/**************************/
			/* [12] Close output file */
			/**************************/
			file_writer.close();
    	}
			     
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}


