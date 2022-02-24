import java.time.LocalDate;

public class AudioBook {
    private long id;
    private String title;
    private LocalDate releaseDate;

    public AudioBook(long id, String title, LocalDate releaseDate) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setId(long id) {
        this.id = id;
    }
}
