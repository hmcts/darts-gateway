package uk.gov.hmcts.darts.datastore;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.darts.datastore.azure.DataManagementAzureClientFactory;

import java.io.ByteArrayInputStream;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataManagementServiceImplTest {
    public static final String BLOB_CONTAINER_NAME = "dummy_container";
    private static final String TEST_BINARY_STRING = "Test String to be converted to binary!";
    @Mock
    private DataManagementAzureClientFactory dataManagementFactory;
    @Mock
    private BlobClient blobClient;
    @Mock
    private BlobServiceClient serviceClient;
    @Mock
    private BlobContainerClient blobContainerClient;
    @Mock
    private DataManagementConfiguration dataManagementConfiguration;
    @InjectMocks
    private DataManagementServiceImpl dataManagementService;

    @BeforeEach
    void beforeEach() {
        when(dataManagementFactory.getBlobContainerClient(Mockito.anyString(), Mockito.notNull())).thenReturn(blobContainerClient);
        when(dataManagementFactory.getBlobServiceClient(Mockito.notNull())).thenReturn(serviceClient);
        when(dataManagementConfiguration.getBlobStorageAccountConnectionString()).thenReturn("connection");
    }

    @Test
    void testSaveBlobDataViaInputStream() {
        // Given
        when(dataManagementConfiguration.getBlobClientBlockSizeBytes()).thenReturn(1L);
        when(dataManagementConfiguration.getBlobClientMaxSingleUploadSizeBytes()).thenReturn(1L);
        when(dataManagementConfiguration.getBlobClientMaxConcurrency()).thenReturn(1);
        when(dataManagementConfiguration.getBlobClientTimeout()).thenReturn(Duration.ofMinutes(1));

        when(dataManagementFactory.getBlobClient(any(), any())).thenReturn(blobClient);

        // When
        UUID uuid = dataManagementService.saveBlobData(BLOB_CONTAINER_NAME, new ByteArrayInputStream(TEST_BINARY_STRING.getBytes()), Map.of("key", "value"));

        // Then
        assertNotNull(uuid);
        verify(dataManagementFactory, times(1)).getBlobServiceClient("connection");
        verify(dataManagementFactory, times(1)).getBlobContainerClient(BLOB_CONTAINER_NAME, serviceClient);
        verify(dataManagementFactory, times(1)).getBlobClient(eq(blobContainerClient), any(UUID.class));
    }
}
