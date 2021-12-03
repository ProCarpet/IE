package customMiniRetrieve;

public class TrecItem {
    private final int recordId;
    private final String text;

    public TrecItem(int recordId, String text) {
        this.recordId = recordId;
        this.text = text;
    }

    public Integer getRecordId() {
        return recordId;
    }

    public String getText() {
        return text;
    }
}
