import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Created by u0861925 on 02/07/2019.
 */
public class PasswordDigest{

    public static void main(String[]args) throws Exception{

        String password="josh1234";

        //Standard Java password hashing
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] array = md.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
        }
        System.out.println(sb.toString());

        //BCrypt password hashing
        // Hash a password for the first time
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt(12));
        String candidate = password;
        // gensalt's log_rounds parameter determines the complexity
        // the work factor is 2**log_rounds, and the default is 10

        // Check that an unencrypted password matches one that has
        // previously been hashed
        System.out.println(hashed);
        if (BCrypt.checkpw(candidate, hashed))
            System.out.println("It matches");
        else
            System.out.println("It does not match");

    }
}
