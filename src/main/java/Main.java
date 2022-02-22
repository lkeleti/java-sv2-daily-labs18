import org.flywaydb.core.Flyway;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.SQLException;
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
        booksRepository.insertBook("Fekete István", "VUK", 3500, 1);
        booksRepository.insertBook("Fekete István", "Téli berek", 3800, 5);
        booksRepository.insertBook("Fekete Péter", "Kártyatrükkök", 2500, 5);
        booksRepository.insertBook("Móra Ferenc", "Kincskereső kisködmön", 3000, 5);

        booksRepository.updatePieces(1l,30);

        List<Book> books = booksRepository.findBooksByWriter("Fekete");
        System.out.println(books);

        System.out.println(booksRepository.findBookById(2l));
    }
}
