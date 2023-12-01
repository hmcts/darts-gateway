package uk.gov.hmcts.darts.ws.multipart;

import jakarta.servlet.*;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;

@Component
public class CloseableRequestFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);

        if (request instanceof Closeable) {
            ((Closeable) request).close();
        }
    }
}
