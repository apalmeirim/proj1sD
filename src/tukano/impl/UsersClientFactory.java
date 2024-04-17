package tukano.impl;

import tukano.api.java.Users;
import tukano.impl.grpc.clients.GrpcUsersClient;
import tukano.impl.java.Discovery;
import tukano.impl.rest.clients.RestUsersClient;

public class UsersClientFactory {


    public static Users getClients() {
        var serverURI = Discovery.getInstance().knownUrisOf("users", 1)[0];
        if (serverURI.toString().contains("rest")) return new RestUsersClient(serverURI);
        else return new GrpcUsersClient(serverURI);
    }


}



