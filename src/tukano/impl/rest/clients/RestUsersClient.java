package tukano.impl.rest.clients;

import java.net.URI;
import java.util.List;

import jakarta.ws.rs.ProcessingException;
import org.glassfish.jersey.client.ClientConfig;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import tukano.api.User;
import tukano.api.java.Result;
import tukano.api.java.Result.ErrorCode;
import tukano.api.java.Users;
import tukano.api.rest.RestUsers;
import utils.Sleep;

public class RestUsersClient implements Users {

	protected static final int MAX_RETRIES = 10;
	protected static final int RETRY_SLEEP = 1000;
	final URI serverURI;
	final Client client;
	final ClientConfig config;

	final WebTarget target;
	
	public RestUsersClient( URI serverURI ) {
		this.serverURI = serverURI;
		this.config = new ClientConfig();
		this.client = ClientBuilder.newClient(config);

		target = client.target( serverURI ).path( RestUsers.PATH );
	}
		
	@Override
	public Result<String> createUser(User user) {
		WebTarget target = client.target( serverURI ).path( RestUsers.PATH );
		for (int i = 0; i < MAX_RETRIES; i++)
			try {
				Response r = target.request()
						.accept(MediaType.APPLICATION_JSON)
						.post(Entity.entity(user, MediaType.APPLICATION_JSON));

				if( r.getStatus() == Status.OK.getStatusCode() && r.hasEntity() )
					// SUCCESS
					return Result.ok(r.readEntity(String.class));
				else {
					return Result.error(getErrorCodeFrom(r.getStatus()));
				}
			} catch (ProcessingException x) {
				Sleep.ms( RETRY_SLEEP );
			}
		return null; // Report failure
	}

	@Override
	public Result<User> getUser(String name, String pwd) {
		WebTarget target = client.target(serverURI).path(RestUsers.PATH);
		for (int i = 0; i < MAX_RETRIES; i++)
			try {
				Response r = target.path(name)
						.queryParam(RestUsers.PWD, pwd).request()
						.accept(MediaType.APPLICATION_JSON)
						.get();

				if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity())
					// SUCCESS
					return Result.ok(r.readEntity(User.class));
				else {
					return Result.error(getErrorCodeFrom(r.getStatus()));
				}
			} catch (ProcessingException x) {
				Sleep.ms(RETRY_SLEEP);
			}
		return null; // Report failure

	}
	public Result<Void> checkPassword(String name, String pwd) {
		WebTarget target = client.target(serverURI).path(RestUsers.PATH);
		for (int i = 0; i < MAX_RETRIES; i++)
			try {
				Response r = target.path( name ).path("/check")
						.queryParam(RestUsers.PWD, pwd).request()
						.get();

				if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity())
					// SUCCESS
					return Result.ok();
				else {
					return Result.error(getErrorCodeFrom(r.getStatus()));
				}
			} catch (ProcessingException x) {
				Sleep.ms(RETRY_SLEEP);
			}
		return null; // Report failure
	}

	@Override
	public Result<User> updateUser(String userId, String password, User user) {
		WebTarget target = client.target(serverURI).path(RestUsers.PATH);
		for (int i = 0; i < MAX_RETRIES; i++)
			try {
				Response r = target.path( userId )
						.queryParam(RestUsers.PWD, password).request()
						.put(Entity.entity(user, MediaType.APPLICATION_JSON));

				if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity())
					// SUCCESS
					return Result.ok( r.readEntity( User.class ));
				else {
					return Result.error(getErrorCodeFrom(r.getStatus()));
				}
			} catch (ProcessingException x) {
				Sleep.ms(RETRY_SLEEP);
			}
		return null; // Report failure
	}

	@Override
	public Result<User> deleteUser(String userId, String password) {
		WebTarget target = client.target(serverURI).path(RestUsers.PATH);
		for (int i = 0; i < MAX_RETRIES; i++)
			try {
				Response r = target.path( userId )
						.queryParam(RestUsers.PWD, password).request()
						.accept(MediaType.APPLICATION_JSON)
						.delete();

				if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity())
					// SUCCESS
					return Result.ok( r.readEntity( User.class ));
				else {
					return Result.error(getErrorCodeFrom(r.getStatus()));
				}
			} catch (ProcessingException x) {
				Sleep.ms(RETRY_SLEEP);
			}
		return null; // Report failure
	}

	@Override
	public Result<List<User>> searchUsers(String pattern) {
		throw new RuntimeException("Not Implemented...");
	}

	public static ErrorCode getErrorCodeFrom(int status) {
		return switch (status) {
		case 200, 209 -> ErrorCode.OK;
		case 409 -> ErrorCode.CONFLICT;
		case 403 -> ErrorCode.FORBIDDEN;
		case 404 -> ErrorCode.NOT_FOUND;
		case 400 -> ErrorCode.BAD_REQUEST;
		case 500 -> ErrorCode.INTERNAL_ERROR;
		case 501 -> ErrorCode.NOT_IMPLEMENTED;
		default -> ErrorCode.INTERNAL_ERROR;
		};
	}
}
