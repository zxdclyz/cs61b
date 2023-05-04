package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

import static gitlet.Utils.*;

/**
 * Represents a gitlet staging area.
 * <p>
 * Saves the staged files
 *
 * @author zxdcly
 */
public class StageArea {
    private static class StagedData implements Serializable {
        /**
         * A map from file name to sha1 hash of added files
         */
        HashMap<String, String> addition = new HashMap<>();
        /**
         * The contents of staged files
         */
        HashMap<String, byte[]> contents = new HashMap<>();
        /**
         * List of removed files
         */
        ArrayList<String> removal = new ArrayList<>();

    }

    private static StagedData data;

    public static HashMap<String, String> getAddition() {
        return data.addition;
    }

    public static HashMap<String, byte[]> getContents() {
        return data.contents;
    }

    public static ArrayList<String> getRemoval() {
        return data.removal;
    }

    public static void load() {
        // check if .gitlet exits
        if (!Repository.GITLET_DIR.exists()) {
            return;
        }
        // if exists, should assure that the staging file exits
        data = readObject(join(Repository.GITLET_DIR, "index"), StagedData.class);
    }

    public static void dump() {
        // check if .gitlet exits
        if (!Repository.GITLET_DIR.exists()) {
            return;
        }
        writeObject(join(Repository.GITLET_DIR, "index"), Objects.requireNonNullElseGet(data, StagedData::new));
    }


    public static void clear() {
        data = new StagedData();
    }

    public static void add(File f) {
        if (!f.exists()) {
            System.out.println("File does not exist.");
            return;
        }

        String fileName = f.getName();
        byte[] content = readContents(f);
        String hash = sha1((Object) content);
        Commit head = Repository.getHeadCommit();

        if (head.ref.containsKey(fileName) && head.ref.get(fileName).equals(hash)) {
            // not modified, remove it
            data.addition.remove(fileName);
            data.contents.remove(fileName);
        } else {
            // file updated or add a new file
            data.addition.put(fileName, hash);
            data.contents.put(fileName, content);
        }
    }

    public static Set<String> getStagedFiles() {
        return data.addition.keySet();
    }

    public static void rm(File f) {
        String fileName = f.getName();
        boolean rm = false;
        // remove from staged area
        if (data.addition.containsKey(fileName)) {
            rm = true;
            data.addition.remove(fileName);
            data.contents.remove(fileName);
        }
        // remove the file if tracked
        Commit head = Repository.getHeadCommit();
        if (head.ref.containsKey(fileName)) {
            rm = true;
            data.removal.add(fileName);
            if (f.exists()) {
//                data.contents.put(fileName, readContents(f));
                f.delete();
            }
        }
        if (!rm) {
            System.out.println("No reason to remove the file.");
        }
    }
}
