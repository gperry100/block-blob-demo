# block-blob-demo

Simple demo illustrating parallel download of blob block from Azure Blob Storage

## Usage

To download blob blobs in parallel use the `BlockBlobDownloader` and call `downloadBlobBlocksAsByteArray`

```java
BlockBlobDownloader blockBlobDownloader = new BlockBlobDownloader();
byte[] bytes = blockBlobDownloader.downloadBlobBlocksAsByteArray("containerName", "blobName");
```
## About Blob Blobs

For block blobs it's easy enough to download to the blob via an input stream, or the whole blob to a byte array:

```java
blockBlob.downloadToByteArray(bytes, 0);
```

This works fine, but can often be slow. [Azure block blobs](https://docs.microsoft.com/en-us/rest/api/storageservices/understanding-block-blobs--append-blobs--and-page-blobs)  are comprised of blocks, each of which is identified by a block ID. These can be downloaded in parallel to optimise the download time (and upload) of the blob in its entirety, by providing offsets and block size to the method:

```java
blob.downloadRangeToByteArray(offset, blockSize, bytes, (int) offset);
```

By using this and combining this with CompletedFutures we can easily download the blocks in parallel.
