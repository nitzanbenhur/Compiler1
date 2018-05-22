package RA;

import java.io.*;

public class RegisterWriter {
	
	public static String filePath;

    public static void replaceAllTempsWithFinalRegisters(iGraph g){
        for (iGraphNode n: g.nodes.values()){
            replaceTempWith(n.tempRegister, n.registerAllocated);
        }
    }

    public static void replaceTempWith(int tempRegister, int realRegister){
        copyToTempFileWhileReplacing(tempRegister, realRegister);
        copyBack();
    }


    private static void copyToTempFileWhileReplacing(int tempRegister, int realRegister){
        File source= new File(filePath);
        File destination= new File(filePath+"_temp.txt");
        String search = "Temp_" + tempRegister + "_tmp";
        String replace = "\\$t" + realRegister;
        try{
            FileReader fr = new FileReader(source);
            String s;
            try (BufferedReader br = new BufferedReader(fr)) {
                FileWriter fw = new FileWriter(destination);

                while ((s = br.readLine()) != null) {
                    s = s.replaceAll(search, replace);
                    fw.write(s);
                    fw.write("\n");
                }
                fw.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void copyBack(){
        File destination= new File(filePath);
        File source= new File(filePath+"_temp.txt");
        try{
            FileReader fr = new FileReader(source);
            String s;
            try (BufferedReader br = new BufferedReader(fr)) {
                FileWriter fw = new FileWriter(destination);

                while ((s = br.readLine()) != null) {
                    fw.write(s);
                    fw.write("\n");
                }
                fw.close();
		source.delete();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
