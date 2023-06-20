package uk.gov.dwp.health.atw.msclaimbundler.repository;

import com.amazonaws.services.s3.AmazonS3;
import java.net.URL;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.atw.msclaimbundler.config.S3Properties;

@Slf4j
@Service
public class S3Repository {

  final S3Properties s3Properties;
  final AmazonS3 client;

  public S3Repository(S3Properties s3Properties, AmazonS3 client) {
    this.s3Properties = s3Properties;
    this.client = client;
  }

  public URL getUrlForFileId(String fileId) {
    return client.getUrl(s3Properties.getBucketName(), fileId);
  }
}
