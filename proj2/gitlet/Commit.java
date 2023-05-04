package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;

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
        this.ref = parentCommit.ref;
        HashMap<String, String> addition = StageArea.getAddition();
        ArrayList<String> removal = StageArea.getRemoval();
        HashMap<String, byte[]> contents = StageArea.getContents();
        for (String fileName : addition.keySet()) {
            String hash = addition.get(fileName);
            // update the ref
            this.ref.put(fileName, hash);
            // save the new snapshot
            writeContents(join(Repository.GITLET_DIR, "blobs", hash), (Object) contents.get(fileName));
        }
        for (String fileName : removal) {
            this.ref.remove(fileName);
        }

        this.timestamp = new Date();
    }

    public String getMessage() {
        return this.message;
    }

    public String getParent() {
        return parent;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public String getTimeString() {
        SimpleDateFormat ft = new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
        return ft.format(this.timestamp);
    }

    /**
     * Save the Commit
     *
     * @return the SHA1 of this commit
     */
    public String save() {
        String fileName = sha1((Object) serialize(this));
        writeObject(join(Repository.GITLET_DIR, "blobs", fileName), this);
        return fileName;
    }

    public Commit load(String sha1) {
        File f = join(Repository.GITLET_DIR, "blobs", sha1);
        if (!f.exists()) {
            return null;
        }
        return readObject(f, Commit.class);
    }
}
