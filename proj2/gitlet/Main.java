package gitlet;

import java.io.File;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author zxdcly
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     * <p>
     * TODO: check operands
     */
    public static void main(String[] args) {
        if (!operandCheck(args)) {
            return;
        }

        // Read the variables from file
        Repository.load();
        StageArea.load();

        String firstArg = args[0];
        switch (firstArg) {
            case "init" -> Repository.init();
            case "add" -> StageArea.add(new File(args[1]));
            case "status" -> status();
            case "rm" -> StageArea.rm(new File(args[1]));
            case "commit" -> Repository.commit(args[1]);

            default -> System.out.println("No command with that name exists.");
        }

        // Write the variables to disk
        Repository.dump();
        StageArea.dump();
    }

    private static boolean operandCheck(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return false;
        }

        switch (args[0]) {
            case "init", "status" -> {
                if (args.length != 1) {
                    System.out.println("Incorrect operands.");
                    return false;
                }
            }
            case "add", "rm", "commit" -> {
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    return false;
                }
            }
        }

        return true;
    }

    private static void status() {
        // branches
        System.out.println("=== Branches ===");
        for (String b : Repository.branches.keySet()) {
            if (b.equals(Repository.HEAD)) {
                System.out.println("*" + b);
            } else {
                System.out.println(b);
            }
        }
        System.out.println();
        // Staged files
        System.out.println("=== Staged Files ===");
        for (String f : StageArea.getStagedFiles()) {
            System.out.println(f);
        }
        System.out.println();
        // Removed files
        System.out.println("=== Removed Files ===");
        for (String f : StageArea.getRemoval()) {
            System.out.println(f);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();

    }
}
