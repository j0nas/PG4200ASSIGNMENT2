import java.util.*;

public class ThirdAssignment {
    /**
     * Convenience method which outputs all parties and the number of times their respective
     * members have been mentioned, sorted by amount of mentions.
     *
     * @param membersMentions Map of all mentioned members and the URLs containing their mentions.
     * @param parsedMembers   List of List of ParliamentMember objects.
     */
    public static void printMemberPartiesSorted(final Map<String, List<String>> membersMentions, final List<ParliamentMember> parsedMembers) {
        printMapSortedByValue(getPartiesMembersTotalMentions(membersMentions, parsedMembers));
    }

    /**
     * Convenience method which outputs all committees and the number of times their respective
     * members have been mentioned, sorted by amount of mentions.
     *
     * @param membersMentions Map of all mentioned members and the URLs containing their mentions.
     * @param parsedMembers   List of List of ParliamentMember objects.
     */
    public static void printMemberCommitteesSorted(final Map<String, List<String>> membersMentions,
                                                   final List<ParliamentMember> parsedMembers) {
        printMapSortedByValue(getCommitteeMembersTotalMentions(membersMentions, parsedMembers));
    }

    /**
     * Gets all distinct parties and the amount of times their members have been mentioned in total.
     *
     * @param parsedMembers   List of ParliamentMember objects, for member <-> party relations.
     * @param membersMentions Map containing names of all parliament members
     *                        and the URLs where they have been mentioned.
     * @return Map containing distinct party names and the amount of times their members have been mentioned in total.
     */
    private static Map<String, Integer> getPartiesMembersTotalMentions(final Map<String, List<String>> membersMentions,
                                                                       final List<ParliamentMember> parsedMembers) {
        final Map<String, Integer> partyVotes = new HashMap<>();
        getDistinctParties(parsedMembers).forEach(party -> partyVotes.put(party, 0));
        membersMentions.forEach((name, mentions) ->
                partyVotes.compute(SecondAssignment.getMemberParty(name, parsedMembers), (party, partyMentions) -> partyMentions++));
        return partyVotes;
    }

    public static void printMostMentionedMembers(final Map<String, List<String>> membersMentions) {
        final Map<String, Integer> memberMentionCount = new HashMap<>();
        membersMentions.entrySet().parallelStream().filter(member -> !member.getValue().isEmpty())
                .forEach(member -> memberMentionCount.put(member.getKey(), member.getValue().size()));

        List list = new LinkedList(memberMentionCount.entrySet());
        Collections.sort(list, (o1, o2) -> ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue()));

        HashMap<String, Integer> sortedHashMap = new LinkedHashMap<>();
        for (final Object item : list) {
            Map.Entry entry = (Map.Entry) item;
            sortedHashMap.put((String) entry.getKey(), (Integer) entry.getValue());
        }

        System.out.println();
        sortedHashMap.entrySet().stream().limit(10).forEach((member) ->
                System.out.println(member.getKey() + ": " + member.getValue()));
    }

    /**
     * Returns a Set of parties, containing distinct values.
     *
     * @param members A list of ParliamentMembers.
     * @return A set of distinct parties as strings, which the provided ParliamentMembers belong to.
     */
    public static Set<String> getDistinctParties(List<ParliamentMember> members) {
        final Set<String> parties = new HashSet<>();
        members.forEach(member -> parties.add(member.party));
        return parties;
    }

    /**
     * Returns a list of each committee and the amount of times its respective members have been mentioned.
     *
     * @param membersMentions List of member names and the URLs where they have been mentioned.
     * @param parsedMembers   List of List of ParliamentMember objects.
     * @return Map of each distinct party and the amount of times its members have been mentioned.
     */
    private static Map<String, Integer> getCommitteeMembersTotalMentions(final Map<String, List<String>> membersMentions,
                                                                         final List<ParliamentMember> parsedMembers) {
        final Map<String, Integer> committeeMemberMentions = new HashMap<>();
        getDistinctCommittees(parsedMembers).forEach(c -> committeeMemberMentions.put(c, 0));
        membersMentions.forEach((name, mentions) ->
                committeeMemberMentions.compute(getMemberCommittee(name, parsedMembers), (k, v) -> v++));
        return committeeMemberMentions;
    }

    /**
     * Returns a Set of committees, containing distinct values.
     *
     * @param members A list of ParliamentMembers.
     * @return A set of distinct committees as strings, which the provided ParliamentMembers belong to.
     */
    private static Set<String> getDistinctCommittees(List<ParliamentMember> members) {
        final Set<String> committees = new HashSet<>();
        members.forEach(member -> committees.add(member.committee));
        return committees;
    }

    /**
     * Sorts a map by its values and outputs it.
     *
     * @param entrySet The set to sort and output.
     */
    private static void printMapSortedByValue(final Map<String, Integer> entrySet) {
        SortedSet<Map.Entry<String, Integer>> sortedSet =
                new TreeSet<>((set1, set2) -> set2.getValue().compareTo(set1.getValue()));
        sortedSet.addAll(entrySet.entrySet());
        System.out.println();
        sortedSet.forEach(k -> System.out.println(k.getKey() + ": " + k.getValue()));
    }

    /**
     * Gets a ParliamentMember's corresponding party name as a String.
     *
     * @param memberName The name of the member for which the party lookup is requested.
     * @param memberList List of ParliamentMembers and their respective parties.
     * @return The name of the party which the specified member is a member of, as a String.
     */
    private static String getMemberCommittee(final String memberName, final List<ParliamentMember> memberList) {
        return memberList.parallelStream().filter(member ->
                member.fullNameLower.equals(memberName)).findAny().get().committee;
    }
}