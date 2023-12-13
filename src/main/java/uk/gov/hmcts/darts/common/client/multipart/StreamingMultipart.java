package uk.gov.hmcts.darts.common.client.multipart;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.darts.common.multipart.SizesableInputSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class DefaultMultipart implements MultipartFile {

    private final String name;

    @Nullable
    private final String contentType;

    private final SizesableInputSource is;

    private final InputStream stream;

    public DefaultMultipart(
        String name, @Nullable String contentType, SizesableInputSource source) throws IOException {
        this.name = name;
        this.contentType = contentType;
        this.is = source;
        stream = is.getInputStream();
    }


    @Override
    public String getName() {
        return this.name;
    }

    @Override
    @NonNull
    public String getOriginalFilename() {
        return getName();
    }

    @Override
    @Nullable
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public boolean isEmpty() {
        return is.getSize() > 0;
    }

    @Override
    public long getSize() {
        return is.getSize();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return stream;
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
                "This method should NEVER be used as" +
                " it loads all of the file into memory getInputStream() should be used instead. " +
                "See https://github.com/OpenFeign/feign-form/issues/88");
    }
}
