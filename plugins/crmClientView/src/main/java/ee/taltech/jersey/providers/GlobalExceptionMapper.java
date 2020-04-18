package ee.taltech.jersey.providers;

import ee.taltech.jersey.response.Error;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        Response.StatusType type = getStatusType(exception);
        Error error = new Error(type.getStatusCode(), type.getReasonPhrase(), exception.getLocalizedMessage());
        return Response.status(error.getStatusCode()).entity(error).type(MediaType.APPLICATION_JSON).build();

    }

    private Response.StatusType getStatusType(Throwable ex) {
        if (ex instanceof WebApplicationException) {
            return ((WebApplicationException) ex).getResponse().getStatusInfo();
        } else {
            return Response.Status.INTERNAL_SERVER_ERROR;
        }
    }

}
