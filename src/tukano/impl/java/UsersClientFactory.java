package tukano.impl.java;

import tukano.api.java.Users;
import tukano.impl.grpc.clients.GrpcUsersClient;
import tukano.impl.rest.clients.RestUsersClient;

public class UsersClientFactory {


    public static Users getClients() throws InterruptedException{
        var serverURI = Discovery.getInstance().knownUrisOf("UsersService", 1);
        if (serverURI.toString().endsWith("rest")) return new RestUsersClient(serverURI[0]);
        else return new GrpcUsersClient(serverURI[0]);
        }


}



