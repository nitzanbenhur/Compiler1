package RA;

import TEMP.TEMP_FACTORY;
import java.util.*;

import static java.util.Collections.max;

public class iGraph {
    public static final int NUM_REGISTERS = 8;

    HashMap<Integer, iGraphNode> nodes;

    public iGraph(ControlFlowGraph cfg){
        addNodePerTemp(cfg);
        addInterferingNodes(cfg);
    }


    /*
        ################################################
                Related to Construction of the graph
        ################################################
     */
    private void addInterferingNodes(ControlFlowGraph cfg) {
        for (Command command: cfg.allCommands()){
            addInterferences(command.live_out);
            addInterferences(command.live_in);
        }
    }

    private void addInterferences(ArrayList<Integer> liveTogether) {
        for(Integer tempRegister: liveTogether){
            iGraphNode currentNode = nodes.get(tempRegister);
            for (Integer tempRegister2: liveTogether){
                if(tempRegister2!= tempRegister){
                    currentNode.interferingNodes.add(nodes.get(tempRegister2));
                }
            }
        }
    }

    private void addNodePerTemp(ControlFlowGraph cfg){
        TreeSet<Integer> tempsSeen = allTempsSeen(cfg);
        nodes = new HashMap<>();
        for (Integer i: tempsSeen){
            nodes.put(i, new iGraphNode(i));
        }
    }

    private TreeSet<Integer> allTempsSeen(ControlFlowGraph cfg) {
        TreeSet<Integer> tempsSeen = new TreeSet<>();
//        for (CFG_Node node: cfg.nodes){
//            for(Integer i: node.command.live_out){
//                tempsSeen.add(i);
//            }
//            for(Integer i: node.command.live_in){
//                tempsSeen.add(i);
//            }
//        }
        int maxTempIndex = TEMP_FACTORY.getInstance().currentCounter();
        for (int i=0; i<= maxTempIndex; i++){
            tempsSeen.add(i);
        }
        return tempsSeen;
    }

    private void print(){
        System.out.println("BEFORE ASSIGNMENT: ");
        System.out.println("strict graph {");
        for (iGraphNode n: nodes.values()){
            for(iGraphNode no: n.interferingNodes){
                System.out.println(n.tempRegister + " -- " + no.tempRegister);
            }
        }
        System.out.println("}");
    }

    private void printAfterAssignment(){
        System.out.println("AFTER ASSIGNMENT: ");
        System.out.println("strict graph {");
        for (iGraphNode n: nodes.values()){
            if(n==null)
                continue;
            for(iGraphNode no: n.interferingNodes){
                System.out.println("\"t" + n.tempRegister + ".r" + n.registerAllocated + "\" -- \"" + "t" + no.tempRegister + ".r" + no.registerAllocated + "\"");
            }
        }
        for (iGraphNode n: nodes.values()){
            if(n==null)
                continue;
            System.out.println("\"t" + n.tempRegister + ".r" + n.registerAllocated + "\" [color = " + numToColor(n.registerAllocated) +" ]" );
        }

        System.out.println("}");


    }

    private String numToColor(int i){
        switch (i){
            case 1: return "red";
            case 2: return "blue";
            case 3: return "green";
            case 4: return "yellow";
            case 5: return "pink";
            case 6: return "purple";
            case 7: return "gray";
            case 0: return "magenta";
            case -1: return "black";
        }
        return "black";
    }

    /*
    ################################################
                   MAIN ALGORITHM
    ################################################
    */
    public void allocateRegisters(){
//        print();
        HashSet<iGraphNode> nodeSet = new HashSet<>(nodes.values());
        LinkedList<iGraphNode> nodeStack = new LinkedList<>();
        LinkedList<iGraphNode> spilledStack = new LinkedList<>();

        while(nodeSet.size() > 0){
            iGraphNode popped = popUnderKNode(nodeSet);
            if(popped!= null)
                nodeStack.push(popped);
            else{
                spilledStack.push(popSpillCandidate(nodeSet));
            }
        }

        while(nodeStack.size() > 0){
            iGraphNode node = nodeStack.pop();
            HashSet<Integer> takenRegisters = registersAssignedToNeighbours(node, nodeSet);
            node.registerAllocated = assignRegister(takenRegisters);
            nodeSet.add(node);
        }

        while(spilledStack.size() > 0){
            iGraphNode node = spilledStack.pop();
            HashSet<Integer> takenRegisters = registersAssignedToNeighbours(node, nodeSet);
            node.registerAllocated = assignRegister(takenRegisters);
            nodeSet.add(node);
        }
//        printAfterAssignment();
    }



    /*
        TESTING THE ALGO:
     */
    public boolean validRegisterAllocation(){
        boolean valid = true;
        for(iGraphNode n: nodes.values()){
            if(n==null)
                continue;
            if (n.registerAllocated < 0)
            {
                System.out.println(n);
                valid = false;
            }

            for (iGraphNode nei: n.interferingNodes)
            {
                if(nei.registerAllocated == n.registerAllocated && n.registerAllocated != -1){
                    System.out.println("bad pair:");
                    System.out.println(n);
                    System.out.println(nei);
                    valid = false;
                }

            }
        }
        return valid;
    }
    private int assignRegister(HashSet<Integer> takenRegisters) {
        for(Integer i=0; i < NUM_REGISTERS; i++){
            if(!takenRegisters.contains(i))
                return i;
        }
        return -1; // never happens if every node popped has less than k neighbours.
    }

    private HashSet<Integer> registersAssignedToNeighbours(iGraphNode node, HashSet<iGraphNode> nodeSet) {
        HashSet<iGraphNode> neighbours = neighboursInSet(node, nodeSet);
        HashSet<Integer> takenRegisters = new HashSet<>();
        for(iGraphNode neighbour: neighbours){
            if(neighbour.registerAllocated >= 0){
                takenRegisters.add(neighbour.registerAllocated);
            }
        }
        return takenRegisters;
    }

    private iGraphNode popUnderKNode(HashSet<iGraphNode> nodeSet) {
        for (iGraphNode node: nodeSet){
            int numNeighbours = neighboursInSet(node, nodeSet).size();
            if(numNeighbours < NUM_REGISTERS){
                nodeSet.remove(node);
                return node;
            }
        }
        return null;
    }

    private iGraphNode popSpillCandidate(HashSet<iGraphNode> nodeSet) {
        iGraphNode minNeiNode = new iGraphNode(-1);
        int numNei = 100;
        for (iGraphNode node: nodeSet){
            int numNeighbours = neighboursInSet(node, nodeSet).size();
            if(numNeighbours < numNei)
                minNeiNode = node;
        }
        nodeSet.remove(minNeiNode);
        return minNeiNode;
    }

    private HashSet<iGraphNode> neighboursInSet(iGraphNode node, HashSet<iGraphNode> nodeSet) {
        HashSet<iGraphNode> neighbours = new HashSet<>();
        for (iGraphNode neighbour: node.interferingNodes){
            if (nodeSet.contains(neighbour))
                neighbours.add(neighbour);
        }
        return neighbours;
    }


}

class iGraphNode {

    int tempRegister;
    int registerAllocated = -1;
    HashSet<iGraphNode> interferingNodes = new HashSet<>();

    public iGraphNode(int tempRegister){
        this.tempRegister = tempRegister;
    }

    public void interferesWith(iGraphNode node){
        this.interferingNodes.add(node);
    }

    @Override
    public String toString() {
        return "<t" + tempRegister + " , r" + registerAllocated + ">";
    }
}