import java.sql.*;

/**
 * Created by u0861925 on 01/07/2019.
 */
public class UserTest {

    public static void main(String[] args) {
        try (
                Connection con = DriverManager.getConnection("jdbc:mysql://selene.hud.ac.uk:3306/u0861925", "u0861925", "02jan90");

                Statement stmt = con.createStatement()
        ) {
            stmt.executeUpdate("INSERT INTO TEST_users(name, nick, email) " + "VALUES ('Josh Coulter', 'Josh', 'joshcoulteruk@gmail.com')");
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
