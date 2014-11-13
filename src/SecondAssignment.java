import edu.princeton.cs.algs4.Queue;

import java.io.*;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class SecondAssignment {
    public static void addToList(String[] values, List<ParliamentMember> list) {
        list.add(new ParliamentMember(values[0], values[1], values[2], values[3]));
    }

    public static void stashAllFoundParliamentMembers(String html, String url, HashMap<String, ArrayList<String>> parliamentMembersMentions) {
        final String finalHtml = html.toLowerCase();
        parliamentMembersMentions.entrySet().parallelStream()
                .filter(memberMap -> finalHtml.contains(memberMap.getKey()))
                .forEach(memberMap -> memberMap.getValue().add(url));
    }

    public static void main(String[] args) throws IOException {
        final String splitCharacter = " ";
        final String pathname = "resources/stortinget2014.txt";
        final String hostUrl = "http://www.klassekampen.no"; // NO SLASHES AT END OF URL!
        final int pageAmountToTraverse = 100;
        final boolean debug = false;

        System.out.printf("Indexing mentions of parliament representatives%nat %s%nPlease standby..%n", hostUrl);

        ArrayList<ParliamentMember> parsedParliamentMembers = getParliamentMembers(splitCharacter, pathname);
        HashMap<String, ArrayList<String>> parliamentMembersMentions = new HashMap<>();
        parsedParliamentMembers.forEach(parliamentMember -> parliamentMembersMentions.put(parliamentMember.fullNameLower, new ArrayList<>()));

        queueWebSearch(hostUrl, parliamentMembersMentions, pageAmountToTraverse);

        if (debug) {
            printAllFoundMentions(parliamentMembersMentions);
        }

        final Scanner scanner = new Scanner(System.in);
        do {
            if (!parliamentMembersMentions.isEmpty()) {
                System.out.print("Name of parliament member: ");
                final String requestedParliamentMember = scanner.nextLine();
                System.out.printf("%n%nEntries about %s:%n%n", requestedParliamentMember);
            }
        } while (!queryUserWishesToExit(scanner));
    }

    private static void printAllFoundMentions(final HashMap<String, ArrayList<String>> parliamentMembersMentions) {
        parliamentMembersMentions.forEach((parliamentMember, memberMentions) -> {
            System.out.println(parliamentMember + ": ");
            memberMentions.forEach(System.out::println);
        });
    }

    private static void queueWebSearch(final String hostUrl, final HashMap<String,
            ArrayList<String>> parliamentMembersMentions, int traversingLimit) throws IOException {
        final Queue<String> foundURLs = new Queue<>();
        foundURLs.enqueue(hostUrl);
        StringBuilder stringBuilder;
        int pagesTraversed = 0;
        final ArrayList<String> visitedUrls = new ArrayList<>();

        while (!foundURLs.isEmpty() && (pagesTraversed++ < traversingLimit)) {
            final String url = foundURLs.dequeue();

            try (Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(new URL(url).openConnection().getInputStream(), "UTF-8")))) {
                stringBuilder = new StringBuilder();
                while (scanner.hasNextLine()) {
                    stringBuilder.append(scanner.nextLine());
                }

                stashAllFoundParliamentMembers(stringBuilder.toString(), url, parliamentMembersMentions);

                // Group(1) = link, Group(2) = text that the anchor tag wraps around
                Matcher pageMatcher = Pattern.compile("<a[^>]+href=[\"']?([^\"'>]+)[\"']?[^>]*>(.+?)</a>",
                        Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(stringBuilder.toString());

                while (pageMatcher.find()) {
                    if (pageMatcher.group(1).toLowerCase().startsWith("mailto")) {
                        continue;
                    }

                    final boolean isHostSiteUrl = !pageMatcher.group(1).startsWith("http://");
                    if (isHostSiteUrl || pageMatcher.group(1).startsWith(hostUrl)) {
                        final String parsedUrl = (isHostSiteUrl ? hostUrl : "") + pageMatcher.group(1);
                        if (!visitedUrls.contains(parsedUrl)) {
                            visitedUrls.add(parsedUrl);
                            foundURLs.enqueue(parsedUrl);
                        }
                    }
                }

            } catch (UnknownHostException e) {
                System.out.println("Unknown host exception - " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Illegal argument exception - " + e.getMessage());
            } catch (FileNotFoundException e) {
                System.out.println("File not found exception - " + e.getMessage());
            }
        }
    }

    private static boolean queryUserWishesToExit(final Scanner scanner) {
        System.out.print("Do you wish to terminate the program? (y/n) ");
        String userResponse;
        while (!(userResponse = scanner.nextLine()).equalsIgnoreCase("y") && !(userResponse.equalsIgnoreCase("n"))) {
            System.out.println("Please enter either 'y' or 'n'.");
        }

        return userResponse.equalsIgnoreCase("y");
    }

    private static ArrayList<ParliamentMember> getParliamentMembers(final String splitCharacter, final String pathname) throws IOException {
        if (Files.notExists(new File(pathname).toPath())) {
            throw new FileNotFoundException("The provided file is not available!");
        }

        ArrayList<ParliamentMember> parliamentMembers = new ArrayList<>();
        try (Stream<String> lines = Files.lines(new File(pathname).toPath())) {
            lines.forEach(line -> addToList(line.split(splitCharacter, 4), parliamentMembers));
        }

        return parliamentMembers;
    }
}
