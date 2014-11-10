import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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

    public static void main(String[] args) throws IOException {
        final String splitCharacter = " ";
        final String pathname = "F:\\Google Drive\\Documents\\Westerdals\\3. Semester\\PG4200\\Assignment2\\resources\\stortinget2014.txt";
        ArrayList<ParliamentMember> parliamentMembers = getParliamentMembers(splitCharacter, pathname);
        parliamentMembers.forEach(System.out::println);
        System.out.println();

        final String hostUrl = "http://www.klassekampen.no/";
        try (Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(new URL(hostUrl).openConnection().getInputStream(), "UTF-8")))) {
            StringBuilder stringBuilder = new StringBuilder();
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine());
            }

            final HashMap<String, String> linkDescriptionMap = new HashMap<>();
            Matcher pageMatcher = Pattern.compile("<a[^>]+href=[\"']?([^\"'>]+)[\"']?[^>]*>(.+?)</a>",
                    Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(stringBuilder);
            while (pageMatcher.find()) {
                linkDescriptionMap.put(pageMatcher.group(1), pageMatcher.group(2));
            }

            linkDescriptionMap.forEach((k, v) -> System.out.println(k + " - " + v));
        }
    }

    private static ArrayList<ParliamentMember> getParliamentMembers(final String splitCharacter, final String pathname) throws IOException {
        ArrayList<ParliamentMember> parliamentMembers = new ArrayList<>();
        try (Stream<String> lines = Files.lines(new File(pathname).toPath())) {
            lines.forEach(line -> addToList(line.split(splitCharacter, 4), parliamentMembers));
        }

        return parliamentMembers;
    }
}
