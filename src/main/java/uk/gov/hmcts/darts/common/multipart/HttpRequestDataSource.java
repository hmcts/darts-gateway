package uk.gov.hmcts.darts.common.multipart;


import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;

public class HttpRequestDataSource implements DataSource {

    private final HttpServletRequest request;

    public HttpRequestDataSource(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return request.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public String getContentType() {
        return "application/octet-stream";
    }

    @Override
    public String getName() {
        return "";
    }
}
