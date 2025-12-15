package uk.gov.hmcts.darts.utilities;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.boot.test.system.CapturedOutput;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.fail;

@UtilityClass
@Slf4j
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class TestUtils {

    public String getContentsFromFile(String filelocation) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource(filelocation).getFile());
        return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    }

    public void compareJson(String expected, String actual) throws JSONException {
        try {
            JSONAssert.assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
        } catch (JSONException e) {
            log.error("Expected json {} does not match actual {}", expected, actual);
            throw e;
        }
    }

    @SneakyThrows
    @SuppressWarnings("PMD.DoNotUseThreads")//Required to prevent busy waiting
    //Used to allow logs to catch up with the test
    public static void waitUntilMessage(CapturedOutput capturedOutput, String message,
                                        int timeoutInSeconds) {
        long startTime = System.currentTimeMillis();
        while (!capturedOutput.getAll().contains(message)) {
            if (System.currentTimeMillis() - startTime > timeoutInSeconds * 1000L) {
                fail("Timeout waiting for message: " + message);
            }
            Thread.sleep(100);
        }
    }
}
