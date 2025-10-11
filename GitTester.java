import java.io.*;

public class GitTester {

    public static void cleanup(String filePath) {
        File file = new File(filePath);
        rmrf(file);
    }

    public static void rmrf(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            for (File f : file.listFiles()) { 
                rmrf(f);    
            }
        }
        file.delete(); 
    }

    public static void main(String args[]) {

        try {
            File repo = new File("StageRepo");
            if (!repo.exists()) {
                repo.mkdir();
            }
    
            File myProgramDir = new File(repo, "myProgram");
            if (!myProgramDir.exists()) myProgramDir.mkdir();

            File innerDir = new File(myProgramDir, "inner");
            if (!innerDir.exists()) innerDir.mkdir();

            File helloTxt = new File(myProgramDir, "hello.txt");
            if (!helloTxt.exists()) {
                helloTxt.createNewFile();
                try (FileWriter fw = new FileWriter(helloTxt)) {
                    fw.write("Hello, this is a test file!\n");
                }
            }

            File worldTxt = new File(innerDir, "world.txt");
            if (!worldTxt.exists()) {
                worldTxt.createNewFile();
                try (FileWriter fw = new FileWriter(worldTxt)) {
                    fw.write("World file for inner directory.\n");
                }
            }
            System.out.println("-- Test files created --");
        } catch (IOException e) {
            System.out.println("Failed to create files/folders: " + e);
            return;
        }


		GitWrapper gw = new GitWrapper();
        gw.add("myProgram/hello.txt");
        gw.add("myProgram/inner/world.txt");
        String commitHash = gw.commit("John Doe", "Initial commit");
        gw.checkout(commitHash);
        cleanup("StageRepo");
        GitTester.cleanup("StageRepo");

    }
    
}
