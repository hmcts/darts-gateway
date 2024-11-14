package uk.gov.hmcts.darts.datastore;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.ParallelTransferOptions;
import com.azure.storage.blob.options.BlobParallelUploadOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.datastore.azure.DataManagementAzureClientFactory;

import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class DataManagementServiceImpl implements DataManagementService {

    private final DataManagementAzureClientFactory blobServiceFactory;
    private final DataManagementConfiguration dataManagementConfiguration;

    @Override
    public UUID saveBlobData(String containerName, InputStream inputStream, Map<String, String> metadata) {
        UUID uniqueBlobId = UUID.randomUUID();
        if (dataManagementConfiguration.isDisableUpload()) {
            log.info("Azure upload is disabled in properties. Skipping file upload");
            return uniqueBlobId;
        }
        BlobServiceClient serviceClient = blobServiceFactory.getBlobServiceClient(dataManagementConfiguration.getBlobStorageAccountConnectionString());
        BlobContainerClient containerClient = blobServiceFactory.getBlobContainerClient(containerName, serviceClient);

        BlobClient client = blobServiceFactory.getBlobClient(containerClient, uniqueBlobId);

        var uploadOptions = new BlobParallelUploadOptions(inputStream);
        uploadOptions.setMetadata(metadata);
        uploadOptions.setParallelTransferOptions(createCommonTransferOptions());
        client.uploadWithResponse(uploadOptions, dataManagementConfiguration.getBlobClientTimeout(), null);

        return uniqueBlobId;
    }

    private ParallelTransferOptions createCommonTransferOptions() {
        return new ParallelTransferOptions()
            .setBlockSizeLong(dataManagementConfiguration.getBlobClientBlockSizeBytes())
            .setMaxSingleUploadSizeLong(dataManagementConfiguration.getBlobClientMaxSingleUploadSizeBytes())
            .setMaxConcurrency(dataManagementConfiguration.getBlobClientMaxConcurrency());
    }
}