package uk.gov.hmcts.darts.datastore;

import com.azure.core.http.rest.Response;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.DeleteSnapshotsOptionType;
import com.azure.storage.blob.models.ParallelTransferOptions;
import com.azure.storage.blob.options.BlobParallelUploadOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.datastore.azure.DataManagementAzureClientFactory;

import java.io.InputStream;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.valueOf;

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

    @Override
    public void deleteBlobData(String containerName, UUID blobId) {
        if (dataManagementConfiguration.isDisableUpload()) {
            log.info("Azure upload is disabled in properties. Skipping file deletion");
        }

        try {
            BlobServiceClient serviceClient = blobServiceFactory.getBlobServiceClient(dataManagementConfiguration.getBlobStorageAccountConnectionString());
            BlobContainerClient containerClient = blobServiceFactory.getBlobContainerClient(containerName, serviceClient);

            BlobClient client = blobServiceFactory.getBlobClient(containerClient, blobId);
            Response<Boolean> response = client.deleteIfExistsWithResponse(DeleteSnapshotsOptionType.INCLUDE, null,
                                                                           dataManagementConfiguration.getBlobClientDeleteTimeout(), null
            );

            HttpStatus httpStatus = valueOf(response.getStatusCode());
            if (httpStatus.is2xxSuccessful() || NOT_FOUND.equals(httpStatus)) {
                return;
            }
            log.error("Failed to delete from storage container={}, blobId={}, httpStatus={}",
                      containerName, blobId, httpStatus);
        } catch (RuntimeException e) {
            log.error("Failed to delete from storage container={}, blobId={}", containerName, blobId, e);
        }
    }

    private ParallelTransferOptions createCommonTransferOptions() {
        return new ParallelTransferOptions()
            .setBlockSizeLong(dataManagementConfiguration.getBlobClientBlockSizeBytes())
            .setMaxSingleUploadSizeLong(dataManagementConfiguration.getBlobClientMaxSingleUploadSizeBytes())
            .setMaxConcurrency(dataManagementConfiguration.getBlobClientMaxConcurrency());
    }
}