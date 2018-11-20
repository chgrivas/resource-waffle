package com.chgrivas.example.resourcewaffle.resource;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.Md5Utils;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
    resources.forEach(resource -> {
      LOG.info("Synchronizing resource " + resource.getFilename());
      try {
        if (!isUploaded(resource.getFile())) {
          LOG.info("Uploading resource " + resource.getFilename() + "...");
          uploadFileTos3bucket(resource.getFilename(), resource.getFile());
        }
      } catch (IOException e) {
        LOG.error("Could not upload file!", e);
      }
    });
  }

  private boolean isUploaded(File file) throws IOException {
    try {
      String localFileHash = String.valueOf(Hex.encodeHex(Md5Utils.computeMD5Hash(file)));
      String remoteFileHash = Optional.ofNullable(getObjectMetadata(file.getName()))
        .map(ObjectMetadata::getETag)
        .orElse("");

      return localFileHash.equals(remoteFileHash);
    } catch (AmazonServiceException e) {
      return false;
    }
  }

  private ObjectMetadata getObjectMetadata(String key) {
    return s3client.getObjectMetadata(new GetObjectMetadataRequest(bucketName, key));
  }

  private void uploadFileTos3bucket(String fileName, File file) {
    s3client.putObject(new PutObjectRequest(bucketName, fileName, file)
      .withCannedAcl(CannedAccessControlList.PublicRead));
  }
}
