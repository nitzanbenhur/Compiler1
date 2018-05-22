package RA;

import TEMP.TEMP_FACTORY;

import java.util.ArrayList;
import java.util.Arrays;

public class ControlFlowGraph {
	
	//public CFG_Node root = null;
	public ArrayList<CFG_Edge> edges;
	public ArrayList<CFG_Node> nodes;
	
	public ControlFlowGraph() {
		
		edges = new ArrayList<CFG_Edge>();
		nodes = new ArrayList<CFG_Node>();	
	}
	
	public void addNode(int line_num, String full_command, String instruction, String label
			,Integer[] temps_defined, Integer[] temps_used, boolean isLabel) {
		
		CFG_Node new_node = new CFG_Node(line_num, full_command, instruction, label, temps_defined, temps_used, isLabel);
		
		nodes.add(new_node);
		
		//if (root == null) root = new_node;
	}
	
	private CFG_Node getLine(int index) {
		
		for(CFG_Node node : nodes) {
			Command com = node.command;
			
			if(com.line_num == index)
				return node;
		}
		
		return null;
	}
	
	private CFG_Node getLabel(String label) {

		for(CFG_Node node : nodes) {
			Command com = node.command;

			if(com.isLabel) {

				if(label.equals(com.label)) return node;
			}
		}

		return null;
	}
	
	public void createEdges() {
		
		for (CFG_Node node : nodes) {
			Command com = node.command;
			
			// next line edge
			if(!com.isJump && !com.isExit) {

				CFG_Node next_node = getLine(com.line_num + 1);
				if(next_node != null) {
					CFG_Edge new_edge = new CFG_Edge(node,next_node);
				
					node.out_edges.add(new_edge);
					next_node.in_edges.add(new_edge);

					edges.add(new_edge);
				}
			}
			
			// branch line
			if(com.isBranch || com.instruction == "j") {
				CFG_Node label_node = getLabel(com.label);
				CFG_Edge new_edge = new CFG_Edge(node,label_node);
				
				node.out_edges.add(new_edge);
				label_node.in_edges.add(new_edge);
			}
		}
	}

	public void printNodes() {


		for (CFG_Node node : nodes) {
			Command com = node.command;
			
			System.out.println();
			System.out.println("Command:\t" + com.full_command);
			System.out.println();
			System.out.println("Instruction:\t" + com.instruction);
			System.out.println("Defined:\t" + com.temps_defined);
			System.out.println("Used:\t" + com.temps_used);
			System.out.println("Label:\t" + com.label);
			System.out.println("IsLabel:\t" + com.isLabel);
			System.out.println("IsBranch:\t" + com.isBranch);
			System.out.println("IsJump:\t" + com.isJump);
			System.out.println("Line num:\t" + com.line_num);

			System.out.println("out edges:");
			for(CFG_Edge edge : node.out_edges) {
				System.out.println("\t" + edge.dst_node.command.full_command);
			}


			System.out.println("in edges:");
			for(CFG_Edge edge : node.in_edges) {
				System.out.println("\t" + edge.src_node.command.full_command);
			}

			System.out.println("Live In: " + com.live_out);
			System.out.println("Live Out: " + com.live_out);

			System.out.println();
		}

	}

	public void printLivenessRanges(){
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		System.out.println("$$$$$$$$$    LIVENESS RANGES    $$$$$$$$$$$$");
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		int maxTempIndex = TEMP_FACTORY.getInstance().currentCounter();
		for (int temp=0; temp<= maxTempIndex; temp++){
			ArrayList<Command> allCommands = allCommands();
			int firstLineLive = Integer.MAX_VALUE;
			int lastLineLive = Integer.MIN_VALUE;
			for(Command cmd: allCommands){
				if(cmd.live_in.contains(temp)){
					firstLineLive = Math.min(firstLineLive, cmd.line_num);
					lastLineLive = Math.max(lastLineLive, cmd.line_num);
				}
			}
			System.out.format("temp_%d is alive from line %d to line %d\n", temp, firstLineLive, lastLineLive);
		}
	}

	public ArrayList<Command> allCommands(){
		ArrayList<Command> allCommands = new ArrayList<>();
		for (CFG_Node node : this.nodes) {
			allCommands.add(node.command);
		}
		return allCommands;
	}

	/*
    used only for assignments!
    assumption: an assignment has exactly one entering edge and exactly one exiting edge.
    // TODO aviv is this^^^ assumption correct?

    give the nodes names: A -> B -> C
    end result: A -> C
 */
	public void deleteAssignmentNode(CFG_Node B){
		this.nodes.remove(B);

		CFG_Edge A_B = B.in_edges.get(0);
		CFG_Edge B_C = B.out_edges.get(0);

		this.edges.remove(A_B);
		this.edges.remove(B_C);

		CFG_Node A = A_B.src_node;
		CFG_Node C = B_C.dst_node;

		CFG_Edge A_C = new CFG_Edge(A, C);
		this.edges.add(A_C);

		A.out_edges.remove(A_B);
		A.out_edges.add(A_C);

		C.in_edges.remove(B_C);
		C.in_edges.add(A_C);
	}
}

class CFG_Node {
 
	public Command command;
	
	public ArrayList<CFG_Edge> in_edges; // edges entering the node
	public ArrayList<CFG_Edge> out_edges; // edges exiting the node
	
	public CFG_Node(Command command) {
		
		this.command = command;
		
		in_edges = new ArrayList<CFG_Edge>();
		out_edges = new ArrayList<CFG_Edge>();
		
	}
	
	public CFG_Node(int line_num, String full_command, String instruction, String label
			,Integer[] temps_defined, Integer[] temps_used, boolean isLabel){
		
		/*ArrayList<String> paramsList = null;
		if(params != null) {
			paramsList = new ArrayList<String>(Arrays.asList(params));
		}*/
		
		ArrayList<Integer> usedList = null;
		if(temps_used != null) {
			usedList = new ArrayList<Integer>(Arrays.asList(temps_used));
		}
		
		ArrayList<Integer> definedList = null;
		if(temps_defined != null) {
			definedList = new ArrayList<Integer>(Arrays.asList(temps_defined));
		}
		
		command = new Command(line_num, full_command, instruction, label, definedList, usedList, isLabel);
		
		in_edges = new ArrayList<CFG_Edge>();
		out_edges = new ArrayList<CFG_Edge>();
	}

	public ArrayList<ArrayList<Integer>> successorsLiveInTags(){
		ArrayList<ArrayList<Integer>> liveInTagList = new ArrayList<>();
		for (CFG_Edge outEdge : this.out_edges){
			CFG_Node successor = outEdge.dst_node;
			liveInTagList.add(successor.command.live_in_tag);
		}
		return liveInTagList;
	}
}

class CFG_Edge {
	
	public CFG_Node src_node;
	public CFG_Node dst_node;
	
	public CFG_Edge(CFG_Node src_node, CFG_Node dst_node) {
		
		this.src_node = src_node;
		this.dst_node = dst_node;
	}
}
