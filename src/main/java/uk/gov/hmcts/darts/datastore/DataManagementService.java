package uk.gov.hmcts.darts.datastore;

import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

public interface DataManagementService {

    UUID saveBlobData(String containerName, InputStream inputStream, Map<String, String> metadata);

    void deleteBlobData(String inboundContainerName, UUID uuid);
}