package co.devcon.mynotes.model;

/**
 * Created by root on 28/10/2017.
 */

public class NoteModel {

    private String title;
    private String description;

    public NoteModel(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
