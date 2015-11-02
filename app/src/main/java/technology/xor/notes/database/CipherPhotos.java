package technology.xor.notes.database;

public class CipherPhotos {
    private int id;
    private String siteId;
    private byte[] photo;

    public CipherPhotos() { }

    public CipherPhotos(int id, String siteId, byte[] photo) {
        this.id = id;
        this.siteId = siteId;
        this.photo = photo;
    }

    public CipherPhotos(String siteId, byte[] photo) {
        this.siteId = siteId;
        this.photo = photo;
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

    public byte[] GetPhoto() {
        return this.photo;
    }

    public void SetPhoto(byte[] photo) {
        this.photo = photo;
    }
}
