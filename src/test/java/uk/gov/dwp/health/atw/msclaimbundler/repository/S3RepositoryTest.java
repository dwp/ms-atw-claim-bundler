package uk.gov.dwp.health.atw.msclaimbundler.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.AmazonS3;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.dwp.health.atw.msclaimbundler.config.S3Properties;

@SpringBootTest(classes = S3Repository.class)
class S3RepositoryTest {
  @Autowired
  S3Repository repository;

  @MockBean
  private AmazonS3 amazonS3;

  @MockBean
  private S3Properties s3Properties;

  @Test
  @DisplayName("upload to S3 bucket")
  void uploadToS3() throws MalformedURLException {
    when(amazonS3.getUrl(any(String.class), any(String.class))).thenReturn(
        new URL("http://localhost:8080/bucket/key"));

    when(s3Properties.getBucketName()).thenReturn("bucket_name");

    assertEquals("http://localhost:8080/bucket/key",
        repository.getUrlForFileId("file_id").toString());

    verify(amazonS3).getUrl("bucket_name", "file_id");
  }
}
