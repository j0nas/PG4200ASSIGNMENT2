public class Config {
    public static final String splitCharacter = " ";
    public static final String pathname = "resources/stortinget2014.txt";
    public static final String hostUrl = "klassekampen.no"; // HTTP(S) PREFIX IS APPENDED. NO SLASH AT END OF URL!
    public static final String hostUrlScheme = "http://"; // EDIT THIS TO MATCH URL SCHEME. (HTTP/HTTPS)

    public static final int pageAmountToTraverse = 100;

    // Prints all found mentions of all party members
    public static final boolean debug = false;

    // Returns value inside href param of all proper anchor tags
    public static final String anchorPattern = "<a[^>]+href=[\"']?([^\"'>]+)[\"']?[^>]*>.+?</a>";
}
