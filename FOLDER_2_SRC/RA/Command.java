package RA;

import java.util.ArrayList;

public class Command {

	public static int current_line_index = 1;
		
	public int line_num;
	public String full_command;
	public boolean isLabel = false;
	public boolean isBranch = false;
	public boolean isJump = false;
	public boolean isExit = false;
	public String instruction;
	//public ArrayList<String> params;
	public String label; // contains the label if isLabel is true, and in case branch or jump its contains the label to branch/jump jump to
	public ArrayList<Integer> temps_defined;
	public ArrayList<Integer> temps_used;
		
	public ArrayList<Integer> live_in;
	public ArrayList<Integer> live_out;
	public ArrayList<Integer> live_in_tag;
	public ArrayList<Integer> live_out_tag;
		
	public Command(int line_num, String full_command, String instruction, String label
			,ArrayList<Integer> temps_defined, ArrayList<Integer> temps_used, boolean isLabel) {
			
		this.line_num = line_num;
		this.full_command = full_command;
		this.instruction = instruction;
		this.label = label;
		this.temps_defined = temps_defined;
		this.temps_used = temps_used;
		//this.params = params;
		this.isLabel = isLabel;
		
		if(instruction == "j" || instruction == "jr") {
			isJump = true;
		}
		else if(instruction == "beq" || instruction == "bgez" || instruction == "bgezal" || instruction == "bgtz" || instruction == "blez"
				|| instruction == "bltz" || instruction == "bltzal" || instruction == "bne" || instruction == "ble" || instruction == "bgt"
				|| instruction == "blt" || instruction == "bge") {
			isBranch = true;
		}
		else if(instruction == "exit" ) {
			isExit = true;
		}
	}

	public boolean isDeadAssignment() {
		if (temps_defined == null || temps_defined.size() == 0)
			return false;

		for(Integer i: temps_defined){
			if(live_out.contains(i))
				return false;
		}

		// temps are defined and all of them are not in live_out
		return true;
	}
	
	/*public String getLastParam() {
		int last_index = params.size() - 1;
		return params.get(last_index);
	}*/

}

