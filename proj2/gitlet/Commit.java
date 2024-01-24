package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;

/**
 * Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author zxdclyz
 */
public class Commit implements Serializable {
    /**
     * The message of this Commit.
     */
    private String message;
    /**
     * Timestamp of the Commit;
     */
    private Date timestamp;

    /**
     * Parent id
     */
    private String parent;
    /**
     * Second Parent id, used to track merge
     */
    private String secondParent = null;

    /**
     * Reference of files tracked in this commit
     */
    public HashMap<String, String> ref;

    /**
     * Create an init Commit
     */
    public Commit() {
        this.timestamp = new Date(0);
        this.message = "initial commit";
        this.parent = null;
        this.ref = new HashMap<>();
    }

    /**
     * Create a Commit with message and parent
     */
    public Commit(String message, String parent) {
        this.message = message;
        this.parent = parent;

        Commit parentCommit = load(parent);
        assert parentCommit != null;
        this.ref = parentCommit.ref;
        HashMap<String, String> addition = StageArea.getAddition();
        ArrayList<String> removal = StageArea.getRemoval();
        HashMap<String, byte[]> contents = StageArea.getContents();
        for (String fileName : addition.keySet()) {
            String hash = addition.get(fileName);
            // update the ref
            this.ref.put(fileName, hash);
            // save the new snapshot
            writeContents(join(Repository.GITLET_DIR, "blobs", "snapshots", hash), (Object) contents.get(fileName));
        }
        for (String fileName : removal) {
            this.ref.remove(fileName);
        }

        this.timestamp = new Date();
    }

    public Commit(String message, String parent, String secondParent) {
        this(message, parent);
        this.secondParent = secondParent;
    }

    public String getMessage() {
        return this.message;
    }

    public String getParent() {
        return parent;
    }

    public String getSecondParent() {
        return this.secondParent;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public String getTimeString() {
        Formatter f = new Formatter(Locale.US);
        return f.format("%1$ta %1$tb %1$td %1$tT %1$tY %1$tz", this.timestamp).toString();
    }

    /**
     * Save the Commit
     *
     * @return the SHA1 of this commit
     */
    public String save() {
        String fileName = sha1((Object) serialize(this));
        writeObject(join(Repository.GITLET_DIR, "blobs", "commits", fileName), this);
        return fileName;
    }

    public static Commit load(String sha1) {
        File f = join(Repository.GITLET_DIR, "blobs", "commits", sha1);
        if (!f.exists()) {
            return null;
        }
        return readObject(f, Commit.class);
    }
}
