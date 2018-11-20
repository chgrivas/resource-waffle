package com.chgrivas.example.resourcewaffle.resource;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class AwsResourceSynchronizationService implements ResourceSynchronizationService {

  private static final Logger LOG = LoggerFactory.getLogger(AwsResourceSynchronizationService.class);

  private AmazonS3 s3client;

  @Value("${aws.endpointUrl}")
  private String endpointUrl;

  @Value("${aws.accessKey}")
  private String accessKey;

  @Value("${aws.secretKey}")
  private String secretKey;

  @Value("${aws.bucketName}")
  private String bucketName;

  @PostConstruct
  private void initializeAmazon() {
    AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
    this.s3client = new AmazonS3Client(credentials);
  }

  @Override
  public void sync(List<Resource> resources) {
    resources.forEach(r -> {
      LOG.info("Synchronizing resource " + r.getFilename());
      try {
        uploadFileTos3bucket(r.getFilename(), r.getFile());
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  private void uploadFileTos3bucket(String fileName, File file) {
    s3client.putObject(new PutObjectRequest(bucketName, fileName, file)
      .withCannedAcl(CannedAccessControlList.PublicRead));
  }
}
