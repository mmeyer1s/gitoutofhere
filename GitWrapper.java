import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Blob;

public class GitWrapper {

    private MyRepo repository;

    public GitWrapper() {
        this.repository = new MyRepo("StageRepo", true);
    }

    /**
     * Initializes a new Git repository.
     * This method creates the necessary directory structure
     * and initial files (index, HEAD) required for a Git repository.
     */
    public void init() {
        repository = new MyRepo("StageRepo", true);
    }

    /**
     * Stages a file for the next commit.
     * This method adds a file to the index file.
     * If the file does not exist, it throws an IOException.
     * If the file is a directory, it throws an IOException.
     * If the file is already in the index, it does nothing.
     * If the file is successfully staged, it creates a blob for the file.
     * @param filePath The path to the file to be staged.
     */
    public void add(String filePath) {
        
        try {
     
            File file = new File("StageRepo/" + filePath);
            String fileName = file.getName();
            String parentPath = file.getParent();

            String actualFilePath = repository.findFile(fileName, parentPath);
            File actualFile = new File(actualFilePath);
            if (actualFile.isDirectory() || !actualFile.exists()) {
                throw new IOException();
            }

            boolean alreadyIndexed = false;
            try (BufferedReader br = new BufferedReader(new FileReader("StageRepo/git/index"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.endsWith(actualFilePath)) {
                        alreadyIndexed = true;
                        break;
                    }
                }
            } catch (IOException e) {

            }
            if (alreadyIndexed) {
                return;
            }
            String blobSha1 = repository.createBlobFromFile(actualFile);
            repository.addFileToIndex(blobSha1, fileName);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error in add: " + e.getMessage());
        }

        
    }



    /**
     * Creates a commit with the given author and message.
     * It should capture the current state of the repository by building trees based on the index file,
     * writing the tree to the objects directory,
     * writing the commit to the objects directory,
     * updating the HEAD file,
     * and returning the commit hash.
     * 
     * The commit should be formatted as follows:
     * tree: <tree_sha>
     * parent: <parent_sha>
     * author: <author>
     * date: <date>
     * summary: <summary>
     *
     * @param author  The name of the author making the commit.
     * @param message The commit message describing the changes.
     * @return The SHA1 hash of the new commit.
     */
    public String commit(String author, String message) {
        return Commit.makeCommit(author, message);
    }

     /**
     * EXTRA CREDIT:
     * Checks out a specific commit given its hash.
     * This method should read the HEAD file to determine the "checked out" commit.
     * Then it should update the working directory to match the
     * state of the repository at that commit by tracing through the root tree and
     * all its children.
     *
     * @param commitHash The SHA1 hash of the commit to check out.
     */
    public void checkout(String commitHash) {
        File commitFile = new File(repository.gitFolder.getPath() + "/objects/" + commitHash);
        if (!commitFile.exists()) {
            throw new RuntimeException("Commit not found: " + commitHash);
        } 
        try (BufferedReader br = new BufferedReader(new FileReader(commitFile))) {
            String treeSha = null, line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("tree: ")) {
                    treeSha = line.substring(6).trim();
                    break;
                }
            }
            if (treeSha == null) {
                throw new RuntimeException("No tree found for commit!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error during checkout", e);
        }

    }
}
