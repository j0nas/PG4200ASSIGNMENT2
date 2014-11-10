public class ParliamentMember {
    String party;
    String sector;
    String forename;
    String surname;

    public ParliamentMember(final String party, final String sector, final String forename, final String surname) {
        this.surname = surname;
        this.forename = forename;
        this.sector = sector;
        this.party = party;
    }

    @Override
    public String toString() {
        return "ParliamentMember{" +
                "party='" + party + '\'' +
                ", sector='" + sector + '\'' +
                ", forename='" + forename + '\'' +
                ", surname='" + surname + '\'' +
                '}';
    }
}
