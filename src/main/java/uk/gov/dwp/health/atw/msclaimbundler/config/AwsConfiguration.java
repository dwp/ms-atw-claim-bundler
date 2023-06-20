package uk.gov.dwp.health.atw.msclaimbundler.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Slf4j
@Configuration
public class AwsConfiguration {

  private final S3Properties s3Properties;

  public AwsConfiguration(S3Properties s3Properties) {
    this.s3Properties = s3Properties;
  }

  @Primary
  @Bean
  public AmazonS3 amazonS3() {
    if (s3Properties.getS3Endpoint().trim().isEmpty()) {
      log.debug("AWS config no endpoint");
      return AmazonS3ClientBuilder.standard()
          .withRegion(s3Properties.getAwsRegion())
          .withPathStyleAccessEnabled(true)
          .build();
    } else {
      log.warn("Localstack mode");
      AwsClientBuilder.EndpointConfiguration endpoint = new AwsClientBuilder.EndpointConfiguration(
          s3Properties.getS3Endpoint(), s3Properties.getAwsRegion());

      return AmazonS3ClientBuilder.standard().withEndpointConfiguration(endpoint)
          .withCredentials(new AWSStaticCredentialsProvider(
              new BasicAWSCredentials(s3Properties.getAwsAccessKey(),
                  s3Properties.getAwsSecretAccessKey())))
          .withPathStyleAccessEnabled(true).build();
    }
  }
}
