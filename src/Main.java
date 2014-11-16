import java.util.*;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws Exception {
        // FIRST ASSIGNMENT
        FirstAssignment.getUserParameters();
        System.out.println();

        // SECOND ASSIGNMENT
        Map<String, List<String>> membersMentions = new HashMap<>();
        final Pattern pageMatcherPattern = Pattern.compile(Config.anchorPattern, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

        List<ParliamentMember> parsedMembers = SecondAssignment.parseParliamentMembersFile();
        parsedMembers.forEach(member -> membersMentions.put(member.fullNameLower, new ArrayList<>()));

        SecondAssignment.queueWebSearch(Config.hostUrlScheme, Config.hostUrl, Config.pageAmountToTraverse, membersMentions, pageMatcherPattern);

        if (membersMentions.isEmpty()) {
            System.err.println("No mentions of the parsed parliament members were found!");
            System.exit(0);
        }

        SecondAssignment.printUserQueryResults(membersMentions);

        // THIRD ASSIGNMENT
        ThirdAssignment.printMemberPartiesSorted(membersMentions, parsedMembers);
        ThirdAssignment.printMemberCommitteesSorted(membersMentions, parsedMembers);
        ThirdAssignment.printMostMentionedMembers(membersMentions);

        // FOURTH ASSIGNMENT
        System.out.println();
        LinkedListCustom<String> list = new LinkedListCustom<>();
        for (int i = 0; i < 10; i++) {
            list.add(String.valueOf(new Random().nextInt(100)));
        }

        list.forEach(item -> System.out.print(item + " "));
        System.out.println();
        list.mergeSort();
        list.forEach(item -> System.out.print(item + " "));
    }
}
