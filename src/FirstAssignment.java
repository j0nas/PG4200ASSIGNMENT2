import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Stream;

public class FirstAssignment {

    public static void main(String[] args) throws Exception {
        getUserParameters();
    }

    public static File[] recursiveSearch(File folder, String needle) throws Exception {
        if (folder == null || !folder.exists() || !folder.isDirectory() || !folder.canRead()) {
            throw new IOException("Provided file is not a directory/cannot be accessed!");
        }

        ArrayList<File> results = new ArrayList<>();
        ArrayList<File> foldersToSearch = new ArrayList<>();

        for (File file : folder.listFiles()) {
            if (file.isDirectory() && file.listFiles() != null) {
                // Add to queue of folders to check out if the folder isn't empty
                foldersToSearch.add(file);
            } else if (file.canRead() && containsString(file, needle)) {
                results.add(file);
            }
        }

        for (File file : foldersToSearch) {
            Collections.addAll(results, recursiveSearch(file, needle));
        }

        return results.toArray(new File[results.size()]);
    }

    private static boolean containsString(final File file, final String needle) throws IOException {
        final String lowerCase = needle.toLowerCase();
        try (Stream<String> lines = Files.lines(file.toPath())) {
            return lines.anyMatch(line -> line.toLowerCase().contains(lowerCase));
        } catch (Exception e) {
            // TODO add option for /q or /debug performance
            //System.out.printf("Could not read file: '%s' - error: %s %n", file.getAbsolutePath(), e.getLocalizedMessage());
            return false;
        }
    }

    private static void getUserParameters() throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.print("String to search for (casing will be ignored): ");
        String needle = scanner.nextLine();

        System.out.print("File path to search: ");
        File searchTargetDirectory = getUserTargetedFolder(scanner);

        System.out.print("\nSorting criteria:\n\n" +
                "1: File name\n" +
                "2: Last modified\n" +
                "3: Size\n" +
                "4: Executability\n\n" +
                "Enter desired sorting criteria (1-4): ");
        int sortingMethod = getUserRequestedSortingMethod(scanner);

        System.out.println("Scanning directory, please standby..\n");
        File[] filesToSort = recursiveSearch(searchTargetDirectory, needle);
        sortByMethod(sortingMethod, filesToSort);

        System.out.println("Files:");
        Arrays.asList(filesToSort).forEach(file -> System.out.println("\t" + file.getName()));
    }

    private static void sortByMethod(final int sortingMethod, final File[] filesToSort) {
        switch (sortingMethod) {
            case 1:
                sortByName(filesToSort);
                break;
            case 2:
                sortByLastModified(filesToSort);
                break;
            case 3:
                sortBySize(filesToSort);
                break;
            case 4:
                sortByExecutability(filesToSort);
                break;
            default:
                throw new NoSuchElementException(sortingMethod + " is not a valid sorting option!");
        }
    }

    private static void sortByName(final File[] containingFiles) {
        Arrays.sort(containingFiles, (o1, o2) -> o1.getName().compareTo(o2.getName()));
    }

    private static void sortByLastModified(final File[] containingFiles) {
        Arrays.sort(containingFiles, (o1, o2) -> Long.compare(o1.lastModified(), o2.lastModified()));
    }

    private static void sortBySize(final File[] containingFiles) {
        Arrays.sort(containingFiles, (o1, o2) -> Long.compare(o1.length(), o2.length()));
    }

    private static void sortByExecutability(final File[] containingFiles) {
        Arrays.sort(containingFiles, (o1, o2) -> Boolean.compare(o1.canExecute(), o2.canExecute()));
    }

    private static int getUserRequestedSortingMethod(final Scanner scanner) {
        int userChoice = 1;
        do {
            if (userChoice > 4 || userChoice < 1) {
                System.out.print("Please select a valid option (1-4): ");
            }

            try {
                userChoice = Integer.valueOf(scanner.nextLine());
            } catch (NumberFormatException ignored) {
                userChoice = 0;
            }
        } while (userChoice > 4 || userChoice < 1);
        return userChoice;
    }

    private static File getUserTargetedFolder(final Scanner scanner) {
        File searchDirectory = null;
        do {
            if (searchDirectory != null) {
                System.out.print("Please enter a valid path: ");
            }

            searchDirectory = new File(scanner.nextLine());
        } while (!searchDirectory.isDirectory());
        return searchDirectory;
    }
}