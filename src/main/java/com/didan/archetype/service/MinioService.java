package com.didan.archetype.service;

import io.minio.ObjectWriteResponse;
import io.minio.Result;
import io.minio.StatObjectResponse;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

public interface MinioService {

  void createBucket(String bucketName);

  boolean bucketExists(String bucketName);

  String getBucketPolicy(String bucketName);

  List<Bucket> getAllBuckets();

  Optional<Bucket> getBucket(String bucketName);

  void removeBucket(String bucketName);

  boolean isObjectExists(String bucketName, String objectName);

  boolean isFolderExists(String bucketName, String folderName);

  List<Item> getAllObjectsByPrefix(String bucketName, String prefix, boolean recursive);

  InputStream getObject(String bucketName, String objectName);

  InputStream getObject(String bucketName, String objectName, long offset, long length);

  Iterable<Result<Item>> listObjects(String bucketName, String prefix, boolean recursive);

  ObjectWriteResponse uploadFile(String bucketName, MultipartFile file, String objectName, String contentType);

  ObjectWriteResponse uploadFile(String bucketName, String objectName, String contentType);

  ObjectWriteResponse uploadFile(String bucketName, String objectName, InputStream inputStream);

  ObjectWriteResponse uploadImage(String bucketName, String imageBase64, String imageName);

  InputStream base64ToInputStream(String base64);

  ObjectWriteResponse createDir(String bucketName, String objectName);

  StatObjectResponse getFileStatusInfo(String bucketName, String objectName);

  ObjectWriteResponse copyFile(String bucketName, String objectName, String srcBucketName, String srcObjectName);

  void removeFile(String bucketName, String objectName);

  void removeFile(String bucketName, List<String> keys);

  String getPresignedObjectUrl(String bucketName, String objectName);

  String getUTF8ByURLDecoder(String str);
}
