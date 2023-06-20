package uk.gov.dwp.health.atw.msclaimbundler.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class S3Properties {

  @Value("${cloud.aws.region.static}")
  private String awsRegion;

  @Value("${cloud.aws.credentials.accessKey:}")
  private String awsAccessKey;

  @Value("${cloud.aws.credentials.secretKey:}")
  private String awsSecretAccessKey;

  @Value("${aws.s3.default-endpoint}")
  private String s3Endpoint;

  @Value("${aws.s3.bucket-name}")
  private String bucketName;

  @Value("${aws.encryption.enabled}")
  private boolean isEncryptionEnabled;
}
