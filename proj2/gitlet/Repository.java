package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import static gitlet.Utils.*;

/**
 * Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 * <p>
 * Saves the snapshots of files
 *
 * @author zxdcly
 */
public class Repository {
    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /**
     * current HEAD (a branch name)
     */
    public static String HEAD;
    /**
     * Map which tracks the HEAD of each branch
     */
    public static HashMap<String, String> branches;

    /**
     * Load the saved variables
     */
    public static void load() {
        // check if .gitlet exits
        if (!Repository.GITLET_DIR.exists()) {
            return;
        }
        // if exists, should assure that the subdir exits
        File refDIR = join(Repository.GITLET_DIR, "refs");
        HEAD = readObject(join(refDIR, "HEAD"), String.class);
        branches = (HashMap<String, String>) readObject(join(refDIR, "heads"), HashMap.class);
    }

    /**
     * Save the variables
     */
    public static void dump() {
        // check if .gitlet exits
        if (!Repository.GITLET_DIR.exists()) {
            return;
        }
        // if exists, should assure that the subdir exits
        File refDIR = join(Repository.GITLET_DIR, "refs");
        writeObject(join(refDIR, "HEAD"), HEAD);
        writeObject(join(refDIR, "heads"), branches);
    }

    /**
     * Initialize the repo
     */
    public static void init() {
        // check if there already exits a repo
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        // create .gitlet dir, structure:
        // .gitlet/
        //      refs/ -- for branching
        //          heads
        //          HEAD
        //      blobs/ -- saving commits and snapshots
        //      index -- staging area
        GITLET_DIR.mkdir();
        join(GITLET_DIR, "refs").mkdir();
        join(GITLET_DIR, "blobs").mkdir();

        // create and save init commit
        Commit initCommit = new Commit();
        String sha1 = initCommit.save();
        HEAD = "master";
        branches = new HashMap<>();
        branches.put(HEAD, sha1);
    }

    public static Commit getHeadCommit() {
        String fileName = branches.get(HEAD);
        return readObject(join(GITLET_DIR, "blobs", fileName), Commit.class);
    }

    public static void commit(String message) {
        if (message == null || message.length() == 0) {
            System.out.println("Please enter a commit message.");
        }
        HashMap<String, String> addition = StageArea.getAddition();
        ArrayList<String> removal = StageArea.getRemoval();
        if (addition.isEmpty() && removal.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }
        // create the new commit
        Commit c = new Commit(message, branches.get(HEAD));
        String sha1 = c.save();
        StageArea.clear();
        branches.put(HEAD, sha1);
    }
}
