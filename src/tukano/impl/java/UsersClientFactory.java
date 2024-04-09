package tukano.impl.java;

import tukano.api.java.Users;
import tukano.impl.grpc.clients.GrpcUsersClient;
import tukano.impl.rest.clients.RestUsersClient;

public class UsersClientFactory {


    public static Users getClients() {
        var serverURI = Discovery.getInstance().knownUrisOf("Users", 1);
        if (serverURI[0].getFragment().contains("rest")) return new RestUsersClient(serverURI[0]);
        else return new GrpcUsersClient(serverURI[0]);
        }


}



