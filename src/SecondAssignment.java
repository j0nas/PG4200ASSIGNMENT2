import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.introcs.In;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class SecondAssignment {
    public static void main(String[] args) throws IOException {
        Map<String, List<String>> membersMentions = new HashMap<>();
        final Pattern pageMatcherPattern = Pattern.compile(Config.anchorPattern, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

        /*
        List<ParliamentMember> parsedMembers = parseParliamentMembersFile();
        parsedMembers.forEach(member -> membersMentions.put(member.fullNameLower, new ArrayList<>()));

        queueWebSearch(Config.hostUrlScheme, Config.hostUrl, Config.pageAmountToTraverse, membersMentions, pageMatcherPattern);

        if (membersMentions.isEmpty()) {
            System.err.println("No mentions of the parsed parliament members were found!");
            System.exit(0);
        }

        ThirdAssignment.printMemberPartiesSorted(membersMentions, parsedMembers);
        ThirdAssignment.printMemberCommitteesSorted(membersMentions, parsedMembers);
        ThirdAssignment.printMostMentionedMembers(membersMentions);

        printUserQueryResults(membersMentions);
*/
        Random random = new Random();
        FourthAssignment.LinkedList linkedList = new FourthAssignment().new LinkedList();
        for (int i = 0; i < 31; i++) {
            linkedList.add(random.nextInt(100));
        }

        linkedList.forEach(item -> System.out.print(item + " "));
        System.out.println();
        linkedList.mergeSort();
        linkedList.forEach(item -> System.out.print(item + " "));
    }

    /**
     * Instantiates a new ParliamentMember object and adds it to the provided list.
     *
     * @param values list of values, as retrieved from the provided text file.
     *               [0] = party
     *               [1] = sector
     *               [2] = forename
     *               [3] = surname
     * @param list   the list to which the new ParliamentMember instance is added.
     */
    public static void addToList(String[] values, List<ParliamentMember> list) {
        list.add(new ParliamentMember(values[0], values[1], values[2], values[3]));
    }

    /**
     * Lowercases and checks whether the provided html string contains any of the keys in the provided Map.
     * Adds the url to the corresponding map's ArrayList if html contained mention of key.
     *
     * @param html           the string to search in.
     * @param url            the URL to add to the ArrayList of the provided Map, if html contains key.
     * @param memberMentions A map containing all parsed parliament members and their respective
     *                       ArrayLists holding URLs to pages in which they have been mentioned.
     */
    public static void stashAllFoundMembers(String html, String url, Map<String, List<String>> memberMentions) {
        final String lowerCaseHtml = html.toLowerCase();
        memberMentions.entrySet().parallelStream()
                .filter(memberMap -> lowerCaseHtml.contains(memberMap.getKey()))
                .forEach(memberMap -> memberMap.getValue().add(url));
    }

    /**
     * Prints mentions of requested parliament member, or mentions of all members if program is
     * configured to debug extensively.
     *
     * @param membersMentions Map containing names of all parliament members and the URLs
     *                        where they have been mentioned.
     */
    private static void printUserQueryResults(final Map<String, List<String>> membersMentions) {
        if (Config.debug) {
            printAllFoundMentions(membersMentions);
        } else {
            final Scanner scanner = new Scanner(System.in);
            do {
                System.out.print("\nName of parliament member: ");
                final String requestedMember = scanner.nextLine().toLowerCase().trim();
                if (membersMentions.containsKey(requestedMember)) {
                    printMentionsOfMember(requestedMember, membersMentions.get(requestedMember));
                } else {
                    System.out.println("Requested parliament member was not found. Please check input.");
                }
            } while (!Util.UserInteraction.userWishesToExit(scanner));
        }
    }

    /**
     * Returns a list of ParliamentMember objects parsed from the provided
     * parliament members file, specified in the config.
     *
     * @return the list of ParliamentMember objects.
     * @throws IOException
     */
    private static List<ParliamentMember> parseParliamentMembersFile() throws IOException {
        System.out.printf("Indexing mentions of parliament representatives at %s%nPlease standby..%n", Config.hostUrl);
        return getParliamentMembers(Config.splitCharacter, Config.pathname);
    }

    /**
     * Fetches the party which a ParliamentMember belongs to by looking the relation up in a list of ParliamentMembers.
     *
     * @param memberName The member for which the corresponding party is needed.
     * @param members    A list of ParliamentMembers.
     * @return The name of the party which the provided member belongs to, as a String.
     */
    public static String getMemberParty(final String memberName, List<ParliamentMember> members) {
        return members.parallelStream().filter(member -> member.fullNameLower.equals(memberName)).findAny().get().party;
    }

    /**
     * Outputs the member's name and the corresponding URLs where s/he has been mentioned.
     *
     * @param memberName     The name of the member to output.
     * @param memberMentions The list of mentions corresponding to the member.
     */
    private static void printMentionsOfMember(final String memberName, List<String> memberMentions) {
        System.out.printf("Entries about %s:%n%n", memberName);
        memberMentions.forEach(System.out::println);
        System.out.println();
    }


    /**
     * Prints all mentions of all members.
     *
     * @param memberMentions A map containing all parsed parliament members and their respective
     *                       ArrayLists holding URLs to pages in which they have been mentioned.
     */
    private static void printAllFoundMentions(final Map<String, List<String>> memberMentions) {
        memberMentions.entrySet().stream()
                .filter(map -> map.getValue().size() > 0)
                .forEach(map -> printMentionsOfMember(map.getKey(), map.getValue()));
    }



    private static void queueWebSearch(final String hostUrlScheme, final String hostUrl, int traversingLimit,
                                       final Map<String, List<String>> parliamentMembersMentions, final Pattern pageMatcherPattern) throws IOException {
        final Queue<String> foundURLs = new Queue<>();
        int pagesTraversed = 0;
        final List<String> visitedUrls = new ArrayList<>();

        foundURLs.enqueue(hostUrlScheme + hostUrl);
        Matcher pageMatcher;
        In urlParser = null;
        String html;
        String queuedUrl;

        while (!foundURLs.isEmpty() && (pagesTraversed++ < traversingLimit)) {
            try {
                queuedUrl = foundURLs.dequeue();
                urlParser = new In(queuedUrl);
                html = urlParser.readAll();

                stashAllFoundMembers(html, queuedUrl, parliamentMembersMentions);

                pageMatcher = pageMatcherPattern.matcher(html);
                while (pageMatcher.find()) {
                    String parsedUrl = fixRelativeUrl(pageMatcher.group(1).toLowerCase(), hostUrlScheme + hostUrl);
                    if (urlIsOnHostSite(parsedUrl, hostUrl) && !visitedUrls.contains(parsedUrl)) {
                        visitedUrls.add(parsedUrl);
                        foundURLs.enqueue(parsedUrl);
                    }
                }
            } catch (NullPointerException ignored) {
            } finally {
                try {
                    if (urlParser != null && urlParser.exists()) {
                        urlParser.close();
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    private static boolean urlIsOnHostSite(final String parsedUrl, final String hostUrl) {
        return parsedUrl.startsWith("http") && parsedUrl.contains(hostUrl);
    }

    private static String fixRelativeUrl(String url, String completeHostUrl) {
        String parsedUrl = !url.startsWith("http") ? completeHostUrl + (url.charAt(0) != '/' ? "/" : "") : "";
        return parsedUrl + url;
    }

    private static List<ParliamentMember> getParliamentMembers(final String splitCharacter, final String pathname) throws IOException {
        if (Files.notExists(new File(pathname).toPath())) {
            throw new FileNotFoundException("getParliamentMembers: The provided file is not available!");
        }

        List<ParliamentMember> parliamentMembers = new ArrayList<>();
        try (Stream<String> lines = Files.lines(new File(pathname).toPath())) {
            lines.forEach(line -> addToList(line.split(splitCharacter, 4), parliamentMembers));
        }

        return parliamentMembers;
    }
}
