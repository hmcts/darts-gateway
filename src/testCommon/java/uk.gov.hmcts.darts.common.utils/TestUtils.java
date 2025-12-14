package uk.gov.hmcts.darts.common.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@UtilityClass
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class TestUtils {

    public String getContentsFromFile(String filelocation) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource(filelocation).getFile());
        return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    }

    public String getContentsFromFile(String filelocation, Map<String, String> substitution) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource(filelocation).getFile());

        String contens = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        Set<String> keys = substitution.keySet();
        Iterator<String> keysIt = keys.iterator();

        while (keysIt.hasNext()) {
            String key = keysIt.next();
            contens = contens.replace(key, substitution.get(key));
        }

        return contens;
    }
}
