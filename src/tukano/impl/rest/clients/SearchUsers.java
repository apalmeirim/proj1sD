package tukano.impl.rest.clients;import tukano.api.User;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public class SearchUsers {
    private static Logger Log = Logger.getLogger(CreateUser.class.getName());

    public static void main(String[] args) throws IOException {

        if( args.length != 2) {
            System.err.println( "Use: java tukano.impl.rest.clients.SearchUser serverUrl pattern");
            return;
        }

        String serverUrl = args[0];
        String pattern = args[1];

        var client = new RestUsersClient( URI.create( serverUrl ) );

        var result = client.searchUsers(pattern);
        if( result.isOK()  )
            Log.info("Search user:" + result.value() );
        else
            Log.info("Search user failed with error: " + result.error());
    }
}
