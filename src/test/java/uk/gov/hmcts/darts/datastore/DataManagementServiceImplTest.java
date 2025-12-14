package uk.gov.hmcts.darts.datastore;

import com.azure.core.http.rest.Response;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.DeleteSnapshotsOptionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.darts.datastore.azure.DataManagementAzureClientFactory;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataManagementServiceImplTest {

    private static final String BLOB_CONTAINER_NAME = "dummy_container";
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
        lenient().when(dataManagementFactory.getBlobContainerClient(Mockito.anyString(), Mockito.notNull())).thenReturn(blobContainerClient);
        lenient().when(dataManagementFactory.getBlobServiceClient(Mockito.notNull())).thenReturn(serviceClient);
        lenient().when(dataManagementConfiguration.getBlobStorageAccountConnectionString()).thenReturn("connection");
        lenient().when(dataManagementFactory.getBlobClient(any(), any())).thenReturn(blobClient);
    }

    @Test
    void testSaveBlobDataViaInputStream() {
        // Given
        when(dataManagementConfiguration.getBlobClientBlockSizeBytes()).thenReturn(1L);
        when(dataManagementConfiguration.getBlobClientMaxSingleUploadSizeBytes()).thenReturn(1L);
        when(dataManagementConfiguration.getBlobClientMaxConcurrency()).thenReturn(1);
        when(dataManagementConfiguration.getBlobClientTimeout()).thenReturn(Duration.ofMinutes(1));

        // When
        UUID uuid = dataManagementService.saveBlobData(BLOB_CONTAINER_NAME,
                                                       new ByteArrayInputStream(TEST_BINARY_STRING.getBytes(StandardCharsets.UTF_8)),
                                                       Map.of("key", "value"));

        // Then
        assertNotNull(uuid);
        verify(dataManagementFactory, times(1)).getBlobServiceClient("connection");
        verify(dataManagementFactory, times(1)).getBlobContainerClient(BLOB_CONTAINER_NAME, serviceClient);
        verify(dataManagementFactory, times(1)).getBlobClient(eq(blobContainerClient), any(UUID.class));
        verify(blobClient, never()).deleteIfExistsWithResponse(any(), any(), any(), any());
    }

    @Test
    void deleteBlobData_whenAzureIsDisabled_NoActionShouldBeTaken() {
        when(dataManagementConfiguration.isDisableUpload()).thenReturn(true);
        dataManagementService.deleteBlobData(BLOB_CONTAINER_NAME, UUID.randomUUID());
        verifyNoMoreInteractions(dataManagementFactory);
    }

    @Test
    @SuppressWarnings("unchecked")
    void deleteBlobData_shouldDeleteItem_whenUsingValidData() {
        Response<Boolean> response = mock(Response.class);
        when(response.getStatusCode()).thenReturn(200);
        when(blobClient.deleteIfExistsWithResponse(any(), any(), any(), any())).thenReturn(response);

        Duration deleteTimeout = Duration.ofMinutes(1);
        when(dataManagementConfiguration.getBlobClientDeleteTimeout()).thenReturn(deleteTimeout);
        UUID blobId = UUID.randomUUID();
        dataManagementService.deleteBlobData(BLOB_CONTAINER_NAME, blobId);

        verify(blobClient).deleteIfExistsWithResponse(
            DeleteSnapshotsOptionType.INCLUDE, null, deleteTimeout, null
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void deleteBlobData_whenAInvalidStatusCodeIsReturned_noExceptionsShouldBeThrown() {
        Response<Boolean> response = mock(Response.class);
        when(response.getStatusCode()).thenReturn(400);
        when(blobClient.deleteIfExistsWithResponse(any(), any(), any(), any())).thenReturn(response);

        Duration deleteTimeout = Duration.ofMinutes(1);
        when(dataManagementConfiguration.getBlobClientDeleteTimeout()).thenReturn(deleteTimeout);
        UUID blobId = UUID.randomUUID();
        dataManagementService.deleteBlobData(BLOB_CONTAINER_NAME, blobId);

        verify(blobClient).deleteIfExistsWithResponse(
            DeleteSnapshotsOptionType.INCLUDE, null, deleteTimeout, null
        );
    }

    @Test
    void deleteBlobData_whenTheApiFails_noExceptionsShouldBeThrown() {
        RuntimeException exception = new RuntimeException("Test");
        when(blobClient.deleteIfExistsWithResponse(any(), any(), any(), any())).thenThrow(exception);

        Duration deleteTimeout = Duration.ofMinutes(1);
        when(dataManagementConfiguration.getBlobClientDeleteTimeout()).thenReturn(deleteTimeout);
        UUID blobId = UUID.randomUUID();
        dataManagementService.deleteBlobData(BLOB_CONTAINER_NAME, blobId);

        verify(blobClient).deleteIfExistsWithResponse(
            DeleteSnapshotsOptionType.INCLUDE, null, deleteTimeout, null
        );
    }
}