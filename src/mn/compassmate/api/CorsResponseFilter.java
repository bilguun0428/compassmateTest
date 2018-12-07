package mn.compassmate.api;

import org.jboss.netty.handler.codec.http.HttpHeaders;
import mn.compassmate.Context;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import java.io.IOException;

public class CorsResponseFilter implements ContainerResponseFilter {

    private static final String ORIGIN_ALL = "*";
    private static final String HEADERS_ALL = "origin, content-type, accept, authorization";
    private static final String METHODS_ALL = "GET, POST, PUT, DELETE, OPTIONS";

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
        if (!response.getHeaders().containsKey(HttpHeaders.Names.ACCESS_CONTROL_ALLOW_HEADERS)) {
            response.getHeaders().add(HttpHeaders.Names.ACCESS_CONTROL_ALLOW_HEADERS, HEADERS_ALL);
        }

        if (!response.getHeaders().containsKey(HttpHeaders.Names.ACCESS_CONTROL_ALLOW_CREDENTIALS)) {
            response.getHeaders().add(HttpHeaders.Names.ACCESS_CONTROL_ALLOW_CREDENTIALS, true);
        }

        if (!response.getHeaders().containsKey(HttpHeaders.Names.ACCESS_CONTROL_ALLOW_METHODS)) {
            response.getHeaders().add(HttpHeaders.Names.ACCESS_CONTROL_ALLOW_METHODS, METHODS_ALL);
        }

        if (!response.getHeaders().containsKey(HttpHeaders.Names.ACCESS_CONTROL_ALLOW_ORIGIN)) {
            String origin = request.getHeaderString(HttpHeaders.Names.ORIGIN);
            String allowed = Context.getConfig().getString("web.origin");

            if (origin == null) {
                response.getHeaders().add(HttpHeaders.Names.ACCESS_CONTROL_ALLOW_ORIGIN, ORIGIN_ALL);
            } else if (allowed == null || allowed.equals(ORIGIN_ALL) || allowed.contains(origin)) {
                response.getHeaders().add(HttpHeaders.Names.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
            }
        }
    }

}
