package uk.gov.hmcts.darts.testutils.request;

import uk.gov.hmcts.darts.common.function.ConsumerWithIoException;
import uk.gov.hmcts.darts.common.multipart.SizeableInputSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class DummyXmlWithFileMultiPartRequest extends XmlWithFileMultiPartRequestGuard {
    private final File fileRepresentingUpload;

    public DummyXmlWithFileMultiPartRequest(File fileRepresentingUpload) {
        super();
        this.fileRepresentingUpload = fileRepresentingUpload;
    }

    @Override
    public long getBinarySize() throws IOException {
        return fileRepresentingUpload.length();
    }

    @Override
    @SuppressWarnings("PMD.UseTryWithResources")
    public boolean consumeFileBinaryStream(ConsumerWithIoException<SizeableInputSource> fileInputStream) throws IOException {

        SizeableInputSource is = new SizeableInputSource() {
            @Override
            public long getSize() {
                return fileRepresentingUpload.length();
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return Files.newInputStream(Path.of(fileRepresentingUpload.getAbsolutePath()));
            }
        };
        try {
            fileInputStream.accept(is);
        } finally {
            is.getInputStream().close();
        }

        return true;
    }

    @Override
    public boolean consumeFileBinary(ConsumerWithIoException<File> call) throws IOException {
        call.accept(fileRepresentingUpload);
        return true;
    }
}
