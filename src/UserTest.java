import Supporting.Database;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;

/**
 * Created by u0861925 on 01/07/2019.
 */
public class UserTest {

    public static void main(String[] args) throws IOException{
        Database database;
        ObjectMapper mapper = new ObjectMapper();
        try(InputStream fileStream = new FileInputStream("src\\database.json")) {
            database = mapper.readValue(fileStream, Database.class);
        }
        try (
                Connection con = DriverManager.getConnection("jdbc:mysql://" + database.url,
                        database.username, database.password);

                Statement stmt = con.createStatement()
        ) {
            stmt.executeUpdate("INSERT INTO TEST_users(name, nick, email) " + "VALUES ('Josh Coulter', 'Josh', " +
                    "'joshcoulteruk@gmail.com')");
            String strSelect = "select id, name, nick, email from TEST_users";
            System.out.println("The SQL statement is: " + strSelect + "\n");

            ResultSet rset = stmt.executeQuery(strSelect);

            System.out.println("The records selected are:");
            int rowCount = 0;
            while (rset.next()) {
                int id = rset.getInt("id");
                String name = rset.getString("name");
                String nick = rset.getString("nick");
                String email = rset.getString("email");
                System.out.println(id + ", " + name + ", " + nick + ", " + email);
                ++rowCount;
            }
            System.out.println("Total number of record = " + rowCount);

        } catch (SQLException ex){
            ex.printStackTrace();
        }
    }


}
