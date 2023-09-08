package uk.gov.hmcts.darts.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.io.FileUtils;
import org.springframework.ws.test.server.ResponseActions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@UtilityClass
public class TestUtils {

    public String getContentsFromFile(String filelocation) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource(filelocation).getFile());
        return FileUtils.readFileToString(file, "UTF-8");

    }

    public String getResponse(ResponseActions responseActions) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        responseActions.andExpect((request, response) -> response.writeTo(baos));
        return baos.toString();
    }
}
