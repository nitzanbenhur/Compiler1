package RA;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

/*
    This class receives a control flow graph (CFG), and sets the live_in, live_out lists (semantically sets) for each of
    the commands in the CFG.

    Follows the pseudo code from the recitation (in comments above the respective methods)
 */
public class LivenessAnalyzer {

    /*
    #############################
              PUBLIC
    #############################
    */



    public static void analyze(ControlFlowGraph cfg){
        initialize(cfg);

        boolean changeMade;
        do{
            step(cfg); // [1]
            propogate(cfg); // [2]
            changeMade = wasChangeMade(cfg); // [3]
            reassignLiveSets(cfg); // [4]
        } while(changeMade);
    }

    /*
        #############################
                 PRIVATE
        #############################
     */
    /*
        for (int command=0;command<numCommands;command++)
        {
            live__in[command] := {}
            live_out[command] := {}
        }
    */
    private static void initialize(ControlFlowGraph cfg) {
        ArrayList<Command> commands = cfg.allCommands();

        for (Command command: commands){
            command.live_in = new ArrayList<>();
            command.live_out = new ArrayList<>();
        }
    }

    /*
        [1]
        for (int command=0;command<numCommands;command++)
        {
            live__in_tag[command] := use[command] U (live_out[command] - def[command]);
        }
     */
    private static void step(ControlFlowGraph cfg) {
        ArrayList<Command> commands = cfg.allCommands();

        for (Command command: commands){
            command.live_in_tag = union(command.temps_used, subtract(command.live_out, command.temps_defined));
        }
    }

    /*
        [2]
        for (int command=0;command<numCommands;command++)
        {
            live_out_tag[command] := U_{s in successor[n]} live__in_tag[s]
        }
     */
    private static void propogate(ControlFlowGraph cfg) {
        for (CFG_Node node: cfg.nodes){
            Command command = node.command;

            command.live_out_tag = union(node.successorsLiveInTags());
        }
    }

    /*
        [3]
        for (int command=0;command<numCommands;command++)
        {
            if (live__in[command] != live__in_tag[command]) did_not_reach_fix_point = 1;
            if (live_out[command] != live_out_tag[command]) did_not_reach_fix_point = 1;
        }
     */
    private static boolean wasChangeMade(ControlFlowGraph cfg) {
        ArrayList<Command> commands = cfg.allCommands();
        for (Command command: commands){

            if (!setEquality(command.live_in, command.live_in_tag))
                return true;

            if (!setEquality(command.live_out, command.live_out_tag))
                return true;
        }
        return false;
    }

    /*
        [4]
        for (int command=0;command<numCommands;command++)
        {
            live__in[command] := live__in_tag[command];
            live_out[command] := live_out_tag[command];
        }
     */
    private static void reassignLiveSets(ControlFlowGraph cfg) {
        ArrayList<Command> commands = cfg.allCommands();
        for (Command command: commands){
            command.live_in = command.live_in_tag;
            command.live_out = command.live_out_tag;
        }
    }




    /*
        #############################
                 UTILITIES
        #############################
     */

    /*
    l1 U l2
     */
    private static ArrayList<Integer> union(ArrayList<Integer> l1, ArrayList<Integer> l2){
        if (l2 == null)
            return l1;
        if (l1 == null)
            return l2;
        TreeSet<Integer> h = new TreeSet<>(l1);
        h.addAll(l2);
        return new ArrayList<>(h);
    }

    /*
        U_{l in listOfLists}
     */
    private static ArrayList<Integer> union(ArrayList<ArrayList<Integer>> listOfLists){
        TreeSet<Integer> h = new TreeSet<>();
        for (ArrayList<Integer> l: listOfLists){
            h.addAll(l);
        }
        return new ArrayList<>(h);
    }

    /*
    l1 - l2
     */
    private static ArrayList<Integer> subtract(ArrayList<Integer> l1, ArrayList<Integer> l2){
        if (l2 == null)
            return l1;
        TreeSet<Integer> h = new TreeSet<>(l1);
        h.removeAll(l2);
        return new ArrayList<>(h);
    }

    /*
        l1 <= l2 and l2 <= l1
     */
    private static boolean setEquality(ArrayList<Integer> l1, ArrayList<Integer> l2){
        return l1.containsAll(l2) && l2.containsAll(l1);
    }







    /*
        #############################
                 REDUNDANT
        #############################
     */
    public static void removeDeadAssignments(ControlFlowGraph cfg) {
        ArrayList<CFG_Node> deadAssignments = new ArrayList<>();
        for (CFG_Node node: cfg.nodes){
            if(node.command.isDeadAssignment()){
                deadAssignments.add(node);
                System.out.println(node.command.full_command);
            }
        }

        for(CFG_Node node: deadAssignments){
            cfg.deleteAssignmentNode(node);
        }
    }

    /*
        analyzes and deletes dead assignments until no changes possible
     */
    public static void process(ControlFlowGraph cfg){
        int preNumNodes;
        int postNumNodes;

        do{
            preNumNodes = cfg.nodes.size();
            System.out.format(" --> PRE Number of nodes: %d\n", preNumNodes);
            analyze(cfg);
            removeDeadAssignments(cfg);
            postNumNodes = cfg.nodes.size();
            System.out.format(" ----> POST Number of nodes: %d\n\n", postNumNodes);
        } while(preNumNodes != postNumNodes);
    }

}