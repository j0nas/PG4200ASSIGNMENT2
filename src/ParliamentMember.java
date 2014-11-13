public class ParliamentMember {
    String party;
    String sector;
    String forename;
    String surname;
    String fullName;
    String fullNameLower;

    public ParliamentMember(final String party, final String sector, final String forename, final String surname) {
        this.surname = surname;
        this.forename = forename;
        this.sector = sector;
        this.party = party;

        this.fullName = forename + " " + surname;

        // Convert to lowercase in order to avoid further runtime conversions when needing to use String.contains
        this.fullNameLower = fullName.toLowerCase();
    }

    public String toString() {
        return "ParliamentMember{" +
                "party='" + party + '\'' +
                ", sector='" + sector + '\'' +
                ", forename='" + forename + '\'' +
                ", surname='" + surname + '\'' +
                '}';
    }
}
