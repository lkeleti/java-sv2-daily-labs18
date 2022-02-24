import org.mariadb.jdbc.ClientSidePreparedStatement;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.*;

public class AudioBooksRepository {
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    public AudioBooksRepository(DataSource dataSource) {
        this.dataSource = dataSource;
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public long saveAudiobookGetBackId(AudioBook audioBook) {
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO audiobooks (title, release_date) VALUES(?,?);",
                        Statement.RETURN_GENERATED_KEYS);
        ) {
            pstmt.setString(1, audioBook.getTitle());
            pstmt.setDate(2, Date.valueOf(audioBook.getReleaseDate()));
            pstmt.executeUpdate();
            return saveAudioBookByStatement(pstmt);
        } catch (SQLException sqle) {
            throw new IllegalStateException("Can't execute insert.", sqle);
        }
    }

    public long saveAudiobookGetBackIdJdbcT(AudioBook audioBook) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update((conn) -> {
                    PreparedStatement pstmt = conn.prepareStatement(
                            "INSERT INTO audiobooks (title, release_date) VALUES(?,?);",
                            Statement.RETURN_GENERATED_KEYS);
                    pstmt.setString(1, audioBook.getTitle());
                    pstmt.setDate(2, Date.valueOf(audioBook.getReleaseDate()));
                    return pstmt;
                }, kh);
        return kh.getKey().longValue();
    }

    private long saveAudioBookByStatement(PreparedStatement pstmt) throws SQLException {
        try (
                ResultSet rs = pstmt.getGeneratedKeys();
        ) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            throw new IllegalArgumentException("Can't get id");
        }
    }

    public void saveAudioBook(AudioBook audioBook) {
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO audiobooks (title, release_date) VALUES(?,?);")
        ) {
            pstmt.setString(1, audioBook.getTitle());
            pstmt.setDate(2, Date.valueOf(audioBook.getReleaseDate()));
            pstmt.executeUpdate();
        } catch (SQLException sqle) {
            throw new IllegalStateException("Can't run insert command.", sqle);
        }
    }

    public void saveAudioBookJdbcT(AudioBook audioBook) {
        jdbcTemplate.update("INSERT INTO audiobooks (title, release_date) VALUES(?,?);", audioBook.getTitle(), Date.valueOf(audioBook.getReleaseDate()));
    }

    public AudioBook findAudioBookById(long id) {
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM audiobooks WHERE id =?");
        ) {
            pstmt.setLong(1, id);
            return findAudioBookByStatement(pstmt);
        } catch (SQLException sqle) {
            throw new IllegalStateException("Can't find by id", sqle);
        }
    }

    public AudioBook findAudioBookByIdJdbcT(long id) {
        return jdbcTemplate.queryForObject("SELECT * FROM audiobooks WHERE id =?",
                (rs, rowNum) -> new AudioBook(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getDate("release_date").toLocalDate()
                ), id
        );
    }

    private AudioBook findAudioBookByStatement(PreparedStatement pstmt) throws SQLException {
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return new AudioBook(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getDate("release_date").toLocalDate()
                );
            }
            throw new IllegalArgumentException("Can't find by id");
        }
    }
}
