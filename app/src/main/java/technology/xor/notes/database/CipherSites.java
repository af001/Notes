package technology.xor.notes.database;

public class CipherSites {

    private int id;
    private String siteId;
    private String dateCreated;
    private String siteLocation;

    public CipherSites() { }

    public CipherSites(int id, String siteId, String dateCreated, String siteLocation) {
        this.id = id;
        this.siteId = siteId;
        this.dateCreated = dateCreated;
        this.siteLocation = siteLocation;
    }

    public CipherSites(String siteId, String dateCreated, String siteLocation) {
        this.siteId = siteId;
        this.dateCreated = dateCreated;
        this.siteLocation = siteLocation;
    }

    public int GetId() {
        return this.id;
    }

    public void SetId(int id) {
        this.id = id;
    }

    public String GetSiteId() {
        return this.siteId;
    }

    public void SetSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String GetDateCreated() {
        return this.dateCreated;
    }

    public void SetDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String GetSiteLocation() {
        return this.siteLocation;
    }

    public void SetSiteLocation(String siteLocation) {
        this.siteLocation = siteLocation;
    }
}
