package uk.gov.hmcts.darts.ws;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import uk.gov.hmcts.darts.metadata.EndpointMetaData;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;


/**
 * A class that extends the core SOAP WS servlet @see MessageDispatcherServlet. This class
 * adds the ability to obtain the endpoint WSDL based on web context matching
 * in the context of a HTTP GET request
 */
@Component
@RequiredArgsConstructor
@MultipartConfig
public class DartsMessageDispatcherServlet extends MessageDispatcherServlet {

    private final List<EndpointMetaData> endpointMetaDataList;

    @Override
    @SuppressWarnings("PMD.ConfusingTernary")
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpMethod getMethod = HttpMethod.GET;

        if (getMethod.equals(req.getMethod())) {
            EndpointMetaData fndMetaData = null;
            for (EndpointMetaData metaData : endpointMetaDataList) {
                String query = req.getQueryString();
                if ("wsdl".equalsIgnoreCase(query) && metaData.getEndpointMatcher().asMatchPredicate().test(req.getRequestURI())) {
                    try (InputStream wsdlInputStream = metaData.getConsolidatedWsdlStream().getInputStream()) {
                        writeWsdlStreamToResponse(wsdlInputStream, res);

                        // we know that this will be a wsdl and therefore xml
                        res.setHeader("Content-Type", "text/xml");
                        fndMetaData = metaData;
                        break;
                    } catch (Exception e) {
                        throw new IOException(e);
                    }
                }
            }

            if (fndMetaData == null) {
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            // if the request is a POST then let the parent implementation deal with this
            super.service(req, res);
        }
    }

    private void writeWsdlStreamToResponse(InputStream wsdlInputStream, HttpServletResponse response) throws IOException {
        String wsdlText = new String(wsdlInputStream.readAllBytes(), StandardCharsets.UTF_8);
        response.getWriter().write(wsdlText);
    }
}
