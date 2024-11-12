package uk.gov.hmcts.darts.datastore;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@Getter
public final class DataManagementConfiguration {

    @Value("${darts-gateway.storage.blob.client.connection-string}")
    private String blobStorageAccountConnectionString;

    @Value("${darts-gateway.storage.blob.client.block-size-bytes}")
    private long blobClientBlockSizeBytes;

    @Value("${darts-gateway.storage.blob.client.max-single-upload-size-bytes}")
    private long blobClientMaxSingleUploadSizeBytes;

    @Value("${darts-gateway.storage.blob.client.max-concurrency}")
    private int blobClientMaxConcurrency;

    @Value("${darts-gateway.storage.blob.client.disable-upload}")
    private boolean disableUpload;

    @Value("${darts-gateway.storage.blob.client.timeout}")
    private Duration blobClientTimeout;

    @Value("${darts-gateway.storage.blob.container-name.inbound}")
    private String inboundContainerName;
}