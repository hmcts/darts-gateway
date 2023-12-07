package uk.gov.hmcts.darts.common.client.multipart;

import org.apache.commons.io.IOUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class DefaultMultipart implements MultipartFile {

    private String name;

    @Nullable
    private String contentType;

    private File file;

    private InputStream fileStream;

    public DefaultMultipart(
        String name, @Nullable String contentType, File fileContents, InputStream stream) {

        this.name = name;
        this.contentType = contentType;
        this.file = fileContents;
        this.fileStream = stream;
    }

    public DefaultMultipart(
        String name, @Nullable String contentType, File fileContents) {

        this(name, contentType, fileContents, null);
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
        return file.length() > 0;
    }

    @Override
    public long getSize() {
        return file.length();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (fileStream == null) {
            try (
                InputStream fileInputStream = Files.newInputStream(Path.of(file.getAbsolutePath()))) {
                return fileInputStream;
            }
        }

        return fileStream;
    }

    @Override
    public void transferTo(File dest) throws IOException {
        try (
            InputStream fileInputStream = Files.newInputStream(Path.of(file.getAbsolutePath()));
            OutputStream fileOutputStream = Files.newOutputStream(Path.of(dest.getAbsolutePath()))) {
            IOUtils.copy(fileInputStream, fileOutputStream);
        }
    }

    @Override
    public byte[] getBytes() throws IOException {
        try (
            InputStream fileInputStream = Files.newInputStream(Path.of(file.getAbsolutePath()))) {
            return IOUtils.toByteArray(fileInputStream);
        }
    }
}
