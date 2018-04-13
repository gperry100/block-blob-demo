# block-blob-demo
Download azure blob blocks in parallel

Simple demo illustrating parallel download of blob block from Azure Blob Storage

```java
BlockBlobDownloader blockBlobDownloader = new BlockBlobDownloader();
byte[] bytes = blockBlobDownloader.downloadBlobBlocksAsByteArray("largeblobtest", "testBlobPeak");
```
