package uk.gov.hmcts.darts.common.client.multipart;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.darts.common.multipart.SizeableInputSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Getter
public class StreamingMultipart implements MultipartFile {
    private final String name;

    @Nullable
    private final String contentType;

    private final SizeableInputSource is;

    private final InputStream inputStream;

    public StreamingMultipart(
        String name, @Nullable String contentType, SizeableInputSource source) throws IOException {
        this.name = name;
        this.contentType = contentType;
        this.is = source;
        inputStream = is.getInputStream();
    }

    @Override
    @NonNull
    public String getOriginalFilename() {
        return getName();
    }

    @Override
    public boolean isEmpty() {
        return is.getSize() == 0;
    }

    @Override
    public long getSize() {
        return is.getSize();
    }


    @Override
    public void transferTo(File dest) throws IOException {
        try (
            OutputStream fileOutputStream = Files.newOutputStream(Path.of(dest.getAbsolutePath()))) {
            IOUtils.copy(is.getInputStream(), fileOutputStream);
        }
    }

    @Override
    public byte[] getBytes() throws IOException {
        throw new UnsupportedOperationException("We throw an exception here to fail fast on file upload. " +
                "This method should NEVER be used with feign as " +
                " it loads all of the file into memory. getInputStream() should be used instead. " +
                "See feign bug https://github.com/OpenFeign/feign-form/issues/88");
    }
}
