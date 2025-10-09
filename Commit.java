import java.io.*;

public class Commit {
    public String makeCommit(String author, String message) {
        MyRepo repository = new MyRepo("StageRepo", true);
        String currentTreeHash = new GitTreeWorking(repository).buildTreeFromDirectory("myProgram");
        File headFile = new File("git/HEAD");
        String parentHash = "";
        if (headFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(headFile))) {
                parentHash = br.readLine();
            } catch (IOException e) {
                parentHash = "";
            }
        }
        String date = java.time.LocalDateTime.now().toString();
        StringBuilder commitContent = new StringBuilder();
        commitContent.append("tree: ").append(currentTreeHash).append("\n");
        if (parentHash != null && !parentHash.isEmpty()) {
            commitContent.append("parent: ").append(parentHash).append("\n");
        }
        commitContent.append("author: ").append(author).append("\n");
        commitContent.append("date: ").append(date).append("\n");
        commitContent.append("message: ").append(message).append("\n");
        String commitText = commitContent.toString();
        String commitHash = "";
        try {
            commitHash = Sha1Generator.generateSha1(commitText);
        } catch (Exception e) {
            System.out.println(e);
        }
        File commitFile = new File("git/objects/" + commitHash);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(commitFile))) {
            bw.write(commitText);
        } catch (IOException e) {
            parentHash = "";
        }
        try (BufferedWriter bw2 = new BufferedWriter(new FileWriter(headFile))) {
            bw2.write(commitHash);
        } catch (IOException e) {
            System.out.println(e);
        }
        return commitHash;
    }
}
