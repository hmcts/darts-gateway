package uk.gov.hmcts.darts.ws;

import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ws.transport.http.MessageDispatcherServlet;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@MultipartConfig
public class AddAudioServlet extends MessageDispatcherServlet {
    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (req.getMethod().equals("GET"))
        {
                if (req.getRequestURI().contains("ContextRegistryService")) {

                    getContextRegistry(res);
                } else if (req.getRequestURI().contains("DARTSService")) {
                    getDartsService(res);
                }
            res.setHeader("Content-Type", "text/xml");
        }
        else
        {
            DartsRequestMultiPart request = new DartsRequestMultiPart(req);

            if (request.isMultipart())
            {
                super.service(request, res);
            }
            else {
                super.service(req, res);
            }
        }
    }

    public void getContextRegistry(HttpServletResponse response) throws IOException
    {
        try {
            String content = new String(Files.readAllBytes(Paths.get(
                AddAudioServlet.class.getClassLoader().getResource("ws/ContextRegistryService.wsdl").toURI())));
            response.getWriter().write(content);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new IOException();
        }
    }

    public void getDartsService(HttpServletResponse response) throws IOException
    {
        try {
            String content = new String(Files.readAllBytes(Paths.get(
                AddAudioServlet.class.getClassLoader().getResource("ws/dartsService.wsdl").toURI())));
            response.getWriter().write(content);
        }
        catch(Exception e)
        {
            throw new IOException();
        }
    }
}
