package technology.xor.notes.database;

public class CipherPrefs {

    private int id;
    private String key;
    private String value;

    public CipherPrefs() { }

    public CipherPrefs(int id, String key, String value) {
        this.id = id;
        this.key = key;
        this.value = value;
    }

    public CipherPrefs(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public int GetId() {
        return this.id;
    }

    public void SetId(int id) {
        this.id = id;
    }

    public String GetKey() {
        return this.key;
    }

    public void SetKey(String key) {
        this.key = key;
    }

    public String GetValue() {
        return this.value;
    }

    public void SetValue(String value) {
        this.value = value;
    }
}
