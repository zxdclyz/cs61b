package gitlet;

import java.io.File;
import java.util.*;

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
    public static TreeMap<String, String> branches;

    /**
     * Load the saved variables
     */
    public static boolean load() {
        // check if .gitlet exits
        if (!Repository.GITLET_DIR.exists()) {
            return false;
        }
        // if exists, should assure that the subdir exits
        File refDIR = join(Repository.GITLET_DIR, "refs");
        HEAD = readObject(join(refDIR, "HEAD"), String.class);
        branches = (TreeMap<String, String>) readObject(join(refDIR, "heads"), TreeMap.class);
        return true;
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
        //          commits/
        //          snapshots/
        //      index -- staging area
        GITLET_DIR.mkdir();
        join(GITLET_DIR, "refs").mkdir();
        join(GITLET_DIR, "blobs", "commits").mkdirs();
        join(GITLET_DIR, "blobs", "snapshots").mkdirs();


        // create and save init commit
        Commit initCommit = new Commit();
        String sha1 = initCommit.save();
        HEAD = "master";
        branches = new TreeMap<>();
        branches.put(HEAD, sha1);
    }

    public static Commit getHeadCommit() {
        String fileName = branches.get(HEAD);
        return readObject(join(GITLET_DIR, "blobs", "commits", fileName), Commit.class);
    }

    public static void commit(String message) {
        if (message == null || message.isEmpty()) {
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

    public static void commit(String message, String secondParent) {
        if (message == null || message.isEmpty()) {
            System.out.println("Please enter a commit message.");
        }
        HashMap<String, String> addition = StageArea.getAddition();
        ArrayList<String> removal = StageArea.getRemoval();
        if (addition.isEmpty() && removal.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }
        // create the new commit
        Commit c = new Commit(message, branches.get(HEAD), secondParent);
        String sha1 = c.save();
        StageArea.clear();
        branches.put(HEAD, sha1);
    }

    public static void log() {
        String currentNode = branches.get(HEAD);
        while (currentNode != null) {
            Commit c = Commit.load(currentNode);
            assert c != null;

            System.out.println("===");
            System.out.println("commit " + currentNode);
            String sp = c.getSecondParent();
            String parent = c.getParent();
            if (sp != null) {
                System.out.println("Merge: " + parent.substring(0, 7) + " " + sp.substring(0, 7));
            }
            System.out.println("Date: " + c.getTimeString());
            System.out.println(c.getMessage());
            System.out.println();

            currentNode = parent;
        }
    }

    public static void globalLog() {
        List<String> commits = getAllCommitIds();
        for (String id : commits) {
            Commit c = Commit.load(id);
            assert c != null;

            System.out.println("===");
            System.out.println("commit " + id);
            String sp = c.getSecondParent();
            if (sp != null) {
                System.out.println("Merge: " + c.getParent().substring(0, 7) + " " + sp.substring(0, 7));
            }
            System.out.println("Date: " + c.getTimeString());
            System.out.println(c.getMessage());
            System.out.println();
        }
    }

    private static List<String> getAllCommitIds() {
        List<String> commits = plainFilenamesIn(join(GITLET_DIR, "blobs", "commits"));

        // this should not happen, cause there always be an initial commit
        assert commits != null;
        return commits;
    }

    /**
     * Fix the commit id to 40 chars
     *
     * @param id short id
     * @return fixed id
     */
    public static String fixCommitId(String id) {
        for (String fName : Objects.requireNonNull(join(GITLET_DIR, "blobs", "commits").list())) {
            if (fName.startsWith(id)) {
                return fName;
            }
        }
        return null;
    }

    public static void checkout(String fileName, String commitId) {
        if (commitId == null) {
            commitId = branches.get(HEAD);
        }
        if (commitId.length() < 40) {
            commitId = fixCommitId(commitId);
        }

        Commit c = Commit.load(commitId);
        if (c == null) {
            System.out.println("No commit with that id exists.");
            return;
        }
        if (!c.ref.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        byte[] fileContent = readContents(join(GITLET_DIR, "blobs", "snapshots", c.ref.get(fileName)));
        writeContents(join(CWD, fileName), (Object) fileContent);
    }

    /**
     * Checkout to a commit, doesn't modify the current HEAD
     *
     * @param dstCommitId Destination commit id
     */
    private static void checkout(String dstCommitId) {
        // get all files in the CWD
        List<String> filesInCWD = plainFilenamesIn(CWD);
        if (filesInCWD == null)
            filesInCWD = new ArrayList<>();
        // get the current commit and destination commit
        Commit cur = getHeadCommit();
        Commit dst = Commit.load(dstCommitId);
        assert dst != null;

        // check for untracked files
        for (String file : filesInCWD) {
            if (!cur.ref.containsKey(file) && dst.ref.containsKey(file)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                return;
            }
        }
        // do checkout
        dst.ref.forEach((file, hash) -> {
            // write new contents
            if (cur.ref.getOrDefault(file, "").equals(hash)) {
                // skip if file is the same as cur
                return;
            }
            byte[] fileContent = readContents(join(GITLET_DIR, "blobs", "snapshots", hash));
            writeContents(join(CWD, file), (Object) fileContent);
        });
        for (String file : filesInCWD) {
            // delete files not in new branch
            if (!dst.ref.containsKey(file)) {
                File fileToRm = join(CWD, file);
                if (fileToRm.exists()) fileToRm.delete();
            }
        }

        StageArea.clear();
    }

    public static void checkoutBranch(String branchName) {
        if (!branches.containsKey(branchName)) {
            System.out.println("No such branch exists.");
            return;
        }
        if (branchName.equals(HEAD)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }

        checkout(branches.get(branchName));

        // make new branch current head
        HEAD = branchName;
    }

    public static void reset(String commitId) {
        String fullCommitId = fixCommitId(commitId);
        if (fullCommitId == null) {
            System.out.println("No commit with that id exists.");
            return;
        }
        checkout(fullCommitId);

        // change HEAD
        branches.put(HEAD, fullCommitId);
    }

    public static void branch(String branchName) {
        if (branches.containsKey(branchName)) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        branches.put(branchName, branches.get(HEAD));
    }

    public static void removeBranch(String branchName) {
        if (!branches.containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if (HEAD.equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        branches.remove(branchName);
    }

    public static void find(String message) {
        ArrayList<String> results = new ArrayList<>();
        List<String> commits = getAllCommitIds();

        for (String id : commits) {
            Commit c = Commit.load(id);
            assert c != null;

            if (c.getMessage().equals(message)) {
                results.add(id);
            }
        }

        if (results.isEmpty()) {
            System.out.println("Found no commit with that message.");
        }
        for (String id : results) {
            System.out.println(id);
        }
    }

    //---------------------- Below is the code fot MERGE ----------------------

    /**
     * A function that finds the split point of current commit and given branch
     * <p>
     * This function will check for:
     * <p>
     * 1. If the split point is the same commit as the given branch
     * <p>
     * 2. If the split point is the current branch, then the effect is to check out the given branch
     * And handle the corresponding log, then return NULL
     * <p>
     * Otherwise, return the split point
     *
     * @param current    current commit id
     * @param sourceName given branch's NAME
     * @return split point or null
     */
    private static Commit findSplitPoint(String current, String sourceName) {
        String source = branches.get(sourceName);
        HashMap<String, Integer> depthMap1 = traverse(current);
        HashMap<String, Integer> depthMap2 = traverse(source);

        String splitPoint = "";
        int depth = Integer.MAX_VALUE;

        for (String id : depthMap1.keySet()) {
            if (depthMap2.containsKey(id)) {
                if (depthMap1.get(id) < depth) {
                    depth = depthMap1.get(id);
                    splitPoint = id;
                }
            }
        }

        if (splitPoint.equals(current)) {
            checkoutBranch(sourceName);
            System.out.println("Current branch fast-forwarded.");
            return null;
        }
        if (splitPoint.equals(source)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return null;
        }

        return Commit.load(splitPoint);
    }

    private static HashMap<String, Integer> traverse(String commitId) {
        HashMap<String, Integer> record = new HashMap<>();
        record.put(commitId, 0);

        Queue<String> q = new ArrayDeque<>();
        q.add(commitId);
        while (!q.isEmpty()) {
            String id = q.poll();
            Commit commit = Commit.load(id);
            assert commit != null;

            // record parent
            String parent = commit.getParent();
            if (parent != null) {
                record.put(parent, record.get(id) + 1);
                q.add(parent);
            }
            //record second parent
            String secondParent = commit.getSecondParent();
            if (secondParent != null) {
                record.put(secondParent, record.get(id) + 1);
                q.add(secondParent);
            }
        }

        return record;
    }

    /**
     * Handle merge conflict of given file
     *
     * @param fileName    name of the conflict file
     * @param sourceHash  hash of the file in source branch, null if not exists
     * @param currentHash hash of the file in current branch, null if not exists
     */
    private static void handleConflict(String fileName, String sourceHash, String currentHash) {
        byte[] sourceFile = new byte[0], currentFile = new byte[0];
        if (sourceHash != null)
            sourceFile = readContents(join(GITLET_DIR, "blobs", "snapshots", sourceHash));

        if (currentHash != null)
            currentFile = readContents(join(GITLET_DIR, "blobs", "snapshots", currentHash));

        writeContents(join(CWD, fileName),
                "<<<<<<< HEAD\n",
                currentFile,
                "=======\n",
                sourceFile,
                ">>>>>>>\n");

        StageArea.add(join(CWD, fileName));
    }

    /**
     * Merge
     *
     * @param branchName branch name to merge from
     */
    public static void merge(String branchName) {
        if (!StageArea.getAddition().isEmpty() || !StageArea.getRemoval().isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return;
        }
        if (!branches.containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if (HEAD.equals(branchName)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }

        String currentId = branches.get(HEAD);
        String sourceId = branches.get(branchName);
        Commit current = getHeadCommit();
        Commit source = Commit.load(sourceId);

        assert source != null;

        // check for untracked files
        List<String> filesInCWD = plainFilenamesIn(CWD);
        if (filesInCWD == null)
            filesInCWD = new ArrayList<>();
        for (String file : filesInCWD) {
            if (!current.ref.containsKey(file) && source.ref.containsKey(file)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                return;
            }
        }

        Commit splitPoint = findSplitPoint(currentId, branchName);
        if (splitPoint == null)
            return;

        // ---------------------- Main part of merge ----------------------
        boolean hasConflict = false;
        // collect all files
        Set<String> allFiles = new HashSet<>();
        allFiles.addAll(source.ref.keySet());
        allFiles.addAll(splitPoint.ref.keySet());
        allFiles.addAll(current.ref.keySet());

        for (String file : allFiles) {
            if (source.ref.containsKey(file) &&
                    current.ref.containsKey(file) &&
                    splitPoint.ref.containsKey(file)) {
                String sourceHash = source.ref.get(file);
                String currentHash = current.ref.get(file);
                String spHash = splitPoint.ref.get(file);
                if (!sourceHash.equals(spHash) && currentHash.equals(spHash)) {
                    // 1.
                    checkout(file, sourceId);
                    StageArea.add(join(CWD, file));
                    continue;
                } else if (sourceHash.equals(spHash) && !currentHash.equals(spHash)) {
                    // 2.
                    continue;
                } else if (sourceHash.equals(currentHash) && sourceHash.equals(spHash)) {
                    // 3. both modified
                    continue;
                } else if (!sourceHash.equals(spHash) &&
                        !currentHash.equals(spHash) &&
                        !sourceHash.equals(currentHash)) {
                    // 8. both modified
                    hasConflict = true;
                    handleConflict(file, sourceHash, currentHash);
                    continue;
                }
            } else if (!source.ref.containsKey(file) &&
                    current.ref.containsKey(file) &&
                    !splitPoint.ref.containsKey(file)) {
                // 4.
                continue;
            } else if (source.ref.containsKey(file) &&
                    !current.ref.containsKey(file) &&
                    !splitPoint.ref.containsKey(file)) {
                // 5.
                checkout(file, sourceId);
                StageArea.add(join(CWD, file));
                continue;
            } else if (!source.ref.containsKey(file) &&
                    !current.ref.containsKey(file) &&
                    splitPoint.ref.containsKey(file)) {
                // 3. both deleted
                continue;
            } else if (!source.ref.containsKey(file) &&
                    current.ref.containsKey(file) &&
                    splitPoint.ref.containsKey(file)) {
                String currentHash = current.ref.get(file);
                String spHash = splitPoint.ref.get(file);
                if (currentHash.equals(spHash)) {
                    // 6.
                    StageArea.rm(join(CWD, file));
                } else {
                    // 8. current modified, source deleted
                    hasConflict = true;
                    handleConflict(file, null, currentHash);
                }
                continue;
            } else if (source.ref.containsKey(file) &&
                    !current.ref.containsKey(file) &&
                    splitPoint.ref.containsKey(file)) {
                String sourceHash = source.ref.get(file);
                String spHash = splitPoint.ref.get(file);
                if (sourceHash.equals(spHash)) {
                    // 7.
                    continue;
                } else {
                    // 8. source modified, current deleted
                    hasConflict = true;
                    handleConflict(file, sourceHash, null);
                }

            } else if (source.ref.containsKey(file) &&
                    current.ref.containsKey(file) &&
                    !splitPoint.ref.containsKey(file)) {
                String sourceHash = source.ref.get(file);
                String currentHash = current.ref.get(file);
                if (!sourceHash.equals(currentHash)) {
                    // 8. not in sp, both modified
                    hasConflict = true;
                    handleConflict(file, sourceHash, currentHash);
                }
            }
        }

        // commit the merge
        commit("Merged " + branchName + " into " + HEAD + ".", sourceId);
        if (hasConflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }
}
