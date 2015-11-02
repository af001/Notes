package technology.xor.notes.database;

public class CipherNotes {
    private int id;
    private String date;
    private String location;
    private String siteId;
    private String note;

    public CipherNotes() { }

    public CipherNotes(int id, String date, String location, String siteId, String note) {
        this.id = id;
        this.date = date;
        this.location = location;
        this.siteId = siteId;
        this.note = note;
    }

    public CipherNotes(String date, String location, String siteId, String note) {
        this.date = date;
        this.location = location;
        this.siteId = siteId;
        this.note = note;
    }

    public int GetId() {
        return this.id;
    }

    public void SetId(int id) {
        this.id = id;
    }

    public String GetDate() {
        return this.date;
    }

    public void SetDate(String date) {
        this.date = date;
    }

    public String GetLocation() {
        return this.location;
    }

    public void SetLocation(String location) {
        this.location = location;
    }

    public String GetSiteId() {
        return this.siteId;
    }

    public void SetSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String GetNote() {
        return this.note;
    }

    public void SetNote(String note) {
        this.note = note;
    }
}
