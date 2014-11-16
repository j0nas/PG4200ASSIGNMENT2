import java.util.*;

public class ThirdAssignment {
    /**
     * Gets all distinct parties and the amount of times their members have been mentioned in total.
     *
     * @param parsedMembers   List of ParliamentMember objects, for member <-> party relations.
     * @param membersMentions Map containing names of all parliament members
     *                        and the URLs where they have been mentioned.
     * @return Map containing distinct party names and the amount of times their members have been mentioned in total.
     */
    private static Map<String, Integer> getPartiesTotalMentions(final List<ParliamentMember> parsedMembers,
                                                                final Map<String, List<String>> membersMentions) {
        final Map<String, Integer> partyVotes = new HashMap<>();
        SecondAssignment.getDistinctParties(parsedMembers).forEach(party -> partyVotes.put(party, 0));
        membersMentions.forEach((name, mentions) ->
                partyVotes.compute(SecondAssignment.getMemberParty(name, parsedMembers), (party, partyMentions) -> partyMentions++));
        return partyVotes;
    }

    public static void printMembersPartiesSorted(final Map<String, List<String>> membersMentions, final List<ParliamentMember> parsedMembers) {
        final Map<String, Integer> sortedPartyTotalMentions =
                new TreeMap<>(getPartiesTotalMentions(parsedMembers, membersMentions)); // TODO ORDER BY MENTIONS; NOT ALPHABETICALLY!
        sortedPartyTotalMentions.forEach((party, votes) -> System.out.println(party + ": " + votes));
    }

    public static void printCommitteeMemberMentions(final Map<String, List<String>> membersMentions, final List<ParliamentMember> parsedMembers) {
        final Map<String, Integer> sortedCommitteeMemberMentions =
                new TreeMap<>(getCommitteeMembersTotalMentions(membersMentions, parsedMembers)); // TODO SWITCH KEY AND VALUE TO ORDER BY MENTIONS
        sortedCommitteeMemberMentions.forEach((k, v) -> System.out.println(k + ": " + v));
    }

    public static Map<String, Integer> getCommitteeMembersTotalMentions(final Map<String, List<String>> membersMentions, final List<ParliamentMember> parsedMembers) {
        final Set<String> committees = new HashSet<>();
        parsedMembers.forEach(member -> committees.add(member.committee));

        final Map<String, Integer> committeeMemberMentions = new HashMap<>();
        committees.forEach(committee -> committeeMemberMentions.put(committee, 0));

        membersMentions.forEach((name, mentions) -> {
            committeeMemberMentions.compute(getMemberCommittee(name, parsedMembers), (k, v) -> v++);
        });
        return committeeMemberMentions;
    }

    private static String getMemberCommittee(final String memberName, final List<ParliamentMember> memberList) {
        return memberList.parallelStream().filter(member -> member.fullNameLower.equals(memberName)).findAny().get().committee;
    }
}
