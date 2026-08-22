package model;

/** Model cua bang CourtTypes. Tag: U7, A8. */
public class CourtType {
    private int courtTypeID;
    private String typeName;
    private String description;
    private boolean active;

    public CourtType() { }
    public CourtType(int courtTypeID, String typeName, String description, boolean active) {
        this.courtTypeID = courtTypeID; this.typeName = typeName;
        this.description = description; this.active = active;
    }
    public int getCourtTypeID() { return courtTypeID; }
    public void setCourtTypeID(int courtTypeID) { this.courtTypeID = courtTypeID; }
    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
