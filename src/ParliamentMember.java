public class ParliamentMember {
    String party;
    String committee;
    String forename;
    String surname;
    String fullName;
    String fullNameLower;

    public ParliamentMember(final String party, final String committee, final String forename, final String surname) {
        this.surname = surname;
        this.forename = forename;
        this.committee = committee;
        this.party = party;

        this.fullName = forename + " " + surname;

        // Convert to lowercase in order to avoid further runtime conversions when needing to use String.contains
        this.fullNameLower = fullName.toLowerCase();
    }

    public String toString() {
        return "ParliamentMember{" +
                "party='" + party + '\'' +
                ", committee='" + committee + '\'' +
                ", forename='" + forename + '\'' +
                ", surname='" + surname + '\'' +
                '}';
    }
}
