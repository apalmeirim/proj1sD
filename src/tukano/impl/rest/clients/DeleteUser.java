package tukano.impl.rest.clients;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public class DeleteUser {
    private static Logger Log = Logger.getLogger(CreateUser.class.getName());

    public static void main(String[] args) throws IOException {

        if( args.length != 3) {
            System.err.println( "Use: java tukano.impl.rest.clients.DeleteUser url userId password ");
            return;
        }

        String serverUrl = args[0];
        String userId = args[1];
        String password = args[2];

        var client = new RestUsersClient( URI.create( serverUrl ) );

        var result = client.deleteUser(userId, password);
        if( result.isOK()  )
            Log.info("Deleted user:" + result.value() );
        else
            Log.info("Deleted user failed with error: " + result.error());
    }
}
