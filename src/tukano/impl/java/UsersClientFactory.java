package tukano.impl.java;

import tukano.api.java.Users;
import tukano.impl.grpc.clients.GrpcUsersClient;
import tukano.impl.rest.clients.RestUsersClient;

public class UsersClientFactory {


    public static Users getClients() {
        var serverURI = Discovery.getInstance().knownUrisOf("UsersService", 1)[0];
        if (serverURI.toString().endsWith("rest")) return new RestUsersClient(serverURI);
        else return new GrpcUsersClient(serverURI);
        }


}



