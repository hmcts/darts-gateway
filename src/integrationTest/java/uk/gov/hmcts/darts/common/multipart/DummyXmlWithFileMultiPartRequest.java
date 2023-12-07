package uk.gov.hmcts.darts.common.multipart;

import uk.gov.hmcts.darts.common.function.ConsumerWithIoException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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
    public boolean consumeFileBinaryStream(ConsumerWithIoException<InputStream> fileInputStream) throws IOException {
        return true;
    }

    @Override
    public boolean consumeFileBinary(ConsumerWithIoException<File> call) throws IOException {
        call.accept(fileRepresentingUpload);
        return true;
    }
}
