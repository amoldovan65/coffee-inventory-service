package moldo.coffee.exception;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.extern.jbosslog.JBossLog;
import org.hibernate.exception.ConstraintViolationException;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

@JBossLog
public class ExceptionMapper {

    @ServerExceptionMapper
    public RestResponse<String> mapException(final ValidationException ex) {
        log.error(ex.getMessage());
        return RestResponse.status(Response.Status.BAD_REQUEST, ex.getMessage());
    }

    @ServerExceptionMapper
    public RestResponse<String> mapException(final NotFoundException ex) {
        log.warn(ex.getMessage());
        return RestResponse.status(Response.Status.NOT_FOUND, ex.getMessage());
    }

    @ServerExceptionMapper
    public RestResponse<String> mapException(final ConstraintViolationException ex) {
        log.warn(ex.getMessage(), ex);
        return RestResponse.status(Response.Status.BAD_REQUEST, ex.getMessage());
    }

}
