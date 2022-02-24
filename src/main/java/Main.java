import org.flywaydb.core.Flyway;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        MariaDbDataSource datasource = new MariaDbDataSource();
        try {
            datasource.setUrl("jdbc:mariadb://localhost:3306/bookstore?useUnicode=true");
            datasource.setUserName("bookstore");
            datasource.setPassword("bookstore");
        } catch (SQLException sqle) {
            throw new IllegalStateException("Can't connect to database!", sqle);
        }

        Flyway flyway = Flyway.configure().locations("db/migration/bookstore").dataSource(datasource).load();
        flyway.clean();
        flyway.migrate();

        BooksRepository booksRepository = new BooksRepository(datasource);
        AudioBooksRepository audioBooksRepository = new AudioBooksRepository(datasource);

        booksRepository.insertBook("Fekete István", "VUK", 3500, 1);
        booksRepository.insertBook("Fekete István", "Téli berek", 3800, 5);
        booksRepository.insertBook("Fekete Péter", "Kártyatrükkök", 2500, 5);
        long id = booksRepository.insertBookGetBackId("Móra Ferenc", "Kincskereső kisködmön", 3000, 5);
        System.out.println(id);

        booksRepository.updatePieces(1l,30);

        List<Book> books = booksRepository.findBooksByWriter("Fekete");
        System.out.println(books);

        System.out.println(booksRepository.findBookById(2l));

        audioBooksRepository.saveAudioBook(
                new AudioBook(0,"Harry Potter és a bölcsek köve.", LocalDate.of(1997,1,15))
        );

        audioBooksRepository.saveAudioBookJdbcT(
                new AudioBook(0,"Harry Potter és a titkok kamrája.", LocalDate.of(1998,1,15))
        );

        System.out.println(audioBooksRepository.findAudioBookById(1).getTitle());
        System.out.println(audioBooksRepository.findAudioBookByIdJdbcT(2).getTitle());

        long id1 = audioBooksRepository.saveAudiobookGetBackId(
                new AudioBook(0,"Harry Potter és az Azkabani fogoly.", LocalDate.of(1999,1,15))
        );

        System.out.println(audioBooksRepository.findAudioBookById(id1).getTitle());

        long id2 = audioBooksRepository.saveAudiobookGetBackId(
                new AudioBook(0,"Harry Potter és a tűz serlege.", LocalDate.of(2000,1,15))
        );

        System.out.println(audioBooksRepository.findAudioBookById(id2).getTitle());
    }
}
