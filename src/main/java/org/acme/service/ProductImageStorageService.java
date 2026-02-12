package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@ApplicationScoped
public class ProductImageStorageService {

    @Inject
    S3Client s3Client;

    @ConfigProperty(name = "app.object-storage.bucket", defaultValue = "plm-files")
    String bucket;

    @ConfigProperty(name = "app.object-storage.endpoint", defaultValue = "http://localhost:9000")
    String endpoint;

    private volatile boolean bucketReady;

    public String putProductImage(UUID productId, byte[] imageBytes) {
        ensureBucketExists();
        String objectKey = objectKey(productId);

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(objectKey)
                        .contentType("application/octet-stream")
                        .build(),
                RequestBody.fromBytes(imageBytes)
        );

        return objectKey;
    }

    public void deleteByReference(String imageReference) {
        if (imageReference == null || imageReference.isBlank()) {
            return;
        }
        s3Client.deleteObject(builder -> builder.bucket(bucket).key(imageReference));
    }

    public String imageUrl(String imageReference) {
        if (imageReference == null || imageReference.isBlank()) {
            return null;
        }
        String normalizedEndpoint = endpoint.endsWith("/") ? endpoint.substring(0, endpoint.length() - 1) : endpoint;
        return normalizedEndpoint + "/" + bucket + "/" + encodePath(imageReference);
    }

    private String objectKey(UUID productId) {
        return "products/" + productId + "/image.bin";
    }

    private synchronized void ensureBucketExists() {
        if (bucketReady) {
            return;
        }
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
        } catch (S3Exception exception) {
            if (exception.statusCode() == 404) {
                s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
            } else {
                throw exception;
            }
        }
        bucketReady = true;
    }

    private String encodePath(String path) {
        StringBuilder builder = new StringBuilder();
        for (char c : path.toCharArray()) {
            if (isUnreserved(c) || c == '/') {
                builder.append(c);
            } else {
                byte[] bytes = String.valueOf(c).getBytes(StandardCharsets.UTF_8);
                for (byte b : bytes) {
                    builder.append('%');
                    builder.append(String.format("%02X", b));
                }
            }
        }
        return builder.toString();
    }

    private boolean isUnreserved(char c) {
        return (c >= 'a' && c <= 'z')
                || (c >= 'A' && c <= 'Z')
                || (c >= '0' && c <= '9')
                || c == '-' || c == '_' || c == '.' || c == '~';
    }
}
