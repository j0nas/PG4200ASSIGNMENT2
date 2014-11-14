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
    public static void addToList(String[] values, List<ParliamentMember> list) {
        list.add(new ParliamentMember(values[0], values[1], values[2], values[3]));
    }

    public static void stashAllFoundMembers(String html, String url, HashMap<String, ArrayList<String>> parliamentMembersMentions) {
        final String finalHtml = html.toLowerCase();
        parliamentMembersMentions.entrySet().parallelStream()
                .filter(memberMap -> finalHtml.contains(memberMap.getKey()))
                .forEach(memberMap -> memberMap.getValue().add(url));
    }

    public static void main(String[] args) throws IOException {
        final String splitCharacter = " ";
        final String pathname = "resources/stortinget2014.txt";
        final String hostUrl = "klassekampen.no"; // HTTP(S) PREFIX IS APPENDED. NO SLASH AT END OF URL!
        final String hostUrlScheme = "http://"; // EDIT THIS TO MATCH URL SCHEME. (HTTP/HTTPS)

        final int pageAmountToTraverse = 100;
        final boolean debug = false;

        // Gets the link that the href parameter has as a value.
        final Pattern pageMatcherPattern =
                Pattern.compile("<a[^>]+href=[\"']?([^\"'>]+)[\"']?[^>]*>.+?</a>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

        System.out.printf("Indexing mentions of parliament representatives at %s%nPlease standby..%n", hostUrl);
        ArrayList<ParliamentMember> parsedMembers = getParliamentMembers(splitCharacter, pathname);
        HashMap<String, ArrayList<String>> membersMentions = new HashMap<>();
        parsedMembers.forEach(parliamentMember -> membersMentions.put(parliamentMember.fullNameLower, new ArrayList<>()));

        queueWebSearch(hostUrlScheme, hostUrl, pageAmountToTraverse, membersMentions, pageMatcherPattern);


        final HashMap<String, Integer> partyVotes = new HashMap<>();
        final Set<String> parties = assignment3_getDistinctParties(parsedMembers);

        membersMentions.entrySet().parallelStream()
                .filter(k -> k.getValue().size() > 0) // Filter to only mentioned parliament members
                .forEach(mentionedMembers -> parsedMembers.stream()
                        .filter(member -> member.fullNameLower.equals(mentionedMembers.getKey())) // Filter to only correct member ..
                        .forEach(memberObject -> partyVotes.put(memberObject.party, partyVotes.get(memberObject.party) == null ? 1 : partyVotes.get(memberObject.party) + 1))); // and get the party s/he belongs to

        partyVotes.forEach((party, voteCount) -> System.out.printf("%s: %d%n", party, voteCount));

        if (debug) {
            printAllFoundMentions(membersMentions);
        } else {
            final Scanner scanner = new Scanner(System.in);
            do {
                if (!membersMentions.isEmpty()) {
                    System.out.print("Name of parliament member: ");
                    final String requestedMember = scanner.nextLine().toLowerCase().trim();
                    if (membersMentions.containsKey(requestedMember)) {
                        printMentionsOfParliamentMember(requestedMember, membersMentions.get(requestedMember));
                    } else {
                        System.out.println("Requested parliament member was not found. Please check input.");
                    }
                } else {
                    System.out.println("No mentions of the parsed parliament members were found!");
                }
            } while (!queryUserWishesToExit(scanner));
        }
    }

    private static Set<String> assignment3_getDistinctParties(List<ParliamentMember> members) {
        // ASSIGNMENT 3
        final Set<String> parties = new TreeSet<>();
        members.forEach(member -> parties.add(member.party));
        return parties;
    }

    private static void printMentionsOfParliamentMember(final String memberName, ArrayList<String> memberMentions) {
        System.out.printf("Entries about %s:%n%n", memberName);
        memberMentions.forEach(System.out::println);
    }

    private static void printAllFoundMentions(final HashMap<String, ArrayList<String>> parliamentMembersMentions) {
        parliamentMembersMentions.forEach((memberName, memberMentions) -> {
            if (memberMentions.size() > 0) {
                printMentionsOfParliamentMember(memberName, memberMentions);
            }
        });
    }

    private static void queueWebSearch(final String hostUrlScheme, final String hostUrl, int traversingLimit,
                                       final HashMap<String, ArrayList<String>> parliamentMembersMentions, final Pattern pageMatcherPattern) throws IOException {
        final Queue<String> foundURLs = new Queue<>();
        int pagesTraversed = 0;
        final ArrayList<String> visitedUrls = new ArrayList<>();

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
