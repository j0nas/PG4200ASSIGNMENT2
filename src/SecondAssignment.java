import edu.princeton.cs.algs4.Queue;

import java.io.*;
import java.net.URL;
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

    public static void printAllFoundParliamentMembers(String html, HashMap<String, ArrayList<String>> parliamentMembersMentions) {
        final String finalHtml = html.toLowerCase();
        parliamentMembersMentions.keySet().parallelStream().filter(finalHtml::contains).forEach(System.out::println);
    }

    public static void main(String[] args) throws IOException {
        final String splitCharacter = " ";
        final String pathname = "resources/stortinget2014.txt";
        final String hostUrl = "http://www.klassekampen.no/";
        System.out.printf("Indexing mentions of parliament representatives%nat %s%nPlease standby..%n", hostUrl);

        ArrayList<ParliamentMember> parsedParliamentMembers = getParliamentMembers(splitCharacter, pathname);
        HashMap<String, ArrayList<String>> parliamentMembersMentions = new HashMap<>();
        parsedParliamentMembers.forEach(parliamentMember -> parliamentMembersMentions.put(parliamentMember.fullNameLower, new ArrayList<>()));

        final Queue<String> foundURLs = new Queue<>();
        foundURLs.enqueue(hostUrl);
        StringBuilder stringBuilder;
        while (!foundURLs.isEmpty())
            try (Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(new URL(foundURLs.dequeue()).openConnection().getInputStream(), "UTF-8")))) {
                stringBuilder = new StringBuilder();
                while (scanner.hasNextLine()) {
                    stringBuilder.append(scanner.nextLine());
                }

                printAllFoundParliamentMembers(stringBuilder.toString(), parliamentMembersMentions);
                System.exit(0);

                Matcher pageMatcher = Pattern.compile("<a[^>]+href=[\"']?([^\"'>]+)[\"']?[^>]*>(.+?)</a>",
                        Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(stringBuilder);

                while (pageMatcher.find()) {
                    foundURLs.enqueue(pageMatcher.group(1));

                    parliamentMembersMentions.keySet().parallelStream().filter(k -> pageMatcher.group(2).toLowerCase().contains(k.toLowerCase())).forEach(System.out::print);
                }
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
