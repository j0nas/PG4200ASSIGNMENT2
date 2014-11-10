import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
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
//        ArrayList<ParliamentMember> parliamentMembers = getParliamentMembers(splitCharacter, pathname);

        final String hostUrl = "http://www.klassekampen.no/";
        try (Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(new URL(hostUrl).openConnection().getInputStream(), "UTF-8")))) {
            StringBuilder stringBuilder = new StringBuilder();
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine());
            }

            ArrayList<String> pageLinks = new ArrayList<>();
            ArrayList<String> pageDescriptions = new ArrayList<>();

            Matcher pageMatcher = Pattern.compile("<a[^>]+href=[\"']?([^\"'>]+)[\"']?[^>]*>(.+?)</a>",
                    Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(stringBuilder);
            while (pageMatcher.find()) {
                pageLinks.add(pageMatcher.group(1));
                pageDescriptions.add(pageMatcher.group(2));
            }

            final int[] i = {0};
            pageLinks.forEach(val -> System.out.println(val + " - " + pageDescriptions.get(i[0]++)));
        }
    }


            /*
                line = scanner.nextLine();
                indexOf = line.indexOf("\"http://"); // THIS SCANS FOR EXTERNAL URLS, OPPOSITE OF WHAT WE WANT
                if (indexOf != -1) {
                    pageURLs.add(line.substring(++indexOf, line.indexOf("\"", indexOf)));
                    // TODO document that this does not account for the JS-generated links, which we anyway don't want. :)
            }
              */

    private static ArrayList<ParliamentMember> getParliamentMembers(final String splitCharacter, final String pathname) throws IOException {
        ArrayList<ParliamentMember> parliamentMembers = new ArrayList<>();
        try (Stream<String> lines = Files.lines(new File(pathname).toPath())) {
            lines.forEach(line -> addToList(line.split(splitCharacter, 4), parliamentMembers));
        }

        return parliamentMembers;
    }
}
