package uk.gov.hmcts.darts.ws;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.ws.transport.http.MessageDispatcherServlet;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class GetWsdlServlet extends MessageDispatcherServlet {
    @Override
    @SuppressWarnings("PMD.ConfusingTernary")
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if ("GET".equals(req.getMethod())) {
            if (req.getRequestURI().contains("ContextRegistryService")) {
                respondWithContextRegistryWsdl(res);
            } else if (req.getRequestURI().contains("DARTSService")) {
                respondWithDartsServiceWsdl(res);
            }
            res.setHeader("Content-Type", "text/xml");
        }
        else {
            super.service(req, res);
        }
    }

    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    private void respondWithContextRegistryWsdl(HttpServletResponse response) throws IOException {
        try (InputStream is =  GetWsdlServlet.class.getClassLoader()
                 .getResourceAsStream("ws/ContextRegistryService.wsdl")) {
            String text = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            response.getWriter().write(text);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    private void respondWithDartsServiceWsdl(HttpServletResponse response) throws IOException {
        try (InputStream is = GetWsdlServlet.class.getClassLoader()
                .getResourceAsStream("ws/dartsService.wsdl")) {
            String text = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            response.getWriter().write(text);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
