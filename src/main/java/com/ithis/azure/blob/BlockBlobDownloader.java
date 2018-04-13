package com.ithis.azure.blob;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlockEntry;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class BlockBlobDownloader {

    private static ExecutorService service = Executors.newCachedThreadPool();
    private final CloudBlobClient client;

    public BlockBlobDownloader() throws InvalidKeyException, IOException, URISyntaxException {
        this.client = BlobClientProvider.getBlobClient();
    }

    public byte[] downloadBlobBlocksAsByteArray(final String containerName, final String blobName) throws URISyntaxException, StorageException, InterruptedException, ExecutionException {

        CloudBlockBlob blockBlob = getCloudBlockBlob(containerName, blobName);
        final BlobBlocks blobBlocks = new BlobBlocks(blockBlob);

        System.out.println("Blob Size " + blobBlocks.getBlobSize());

        List<CompletableFuture> futures = new ArrayList<>();

        long offset = 0;

        for (int blockNum = 0; blockNum < blobBlocks.getNumberOfBlocks(); blockNum++) {
            System.out.println("Downloading block  " + blockNum + ", Block Size " + blobBlocks.getBlockSize(blockNum) + ", Offset " + offset);
            CompletableFuture future = downloadBlockAsync(blockBlob, blobBlocks, blockNum, offset);
            futures.add(future);

            offset += blobBlocks.getBlockSize(blockNum);
        }

        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
        combinedFuture.get();

        return blobBlocks.bytes;
    }

    private CloudBlockBlob getCloudBlockBlob(String containerName, String blobName) throws URISyntaxException, StorageException {
        CloudBlobContainer container = client.getContainerReference(containerName);

        assert (container.exists());

        CloudBlockBlob blockBlob = container.getBlockBlobReference(blobName);
        return blockBlob;
    }

    private CompletableFuture<Void> downloadBlockAsync(CloudBlockBlob blob, BlobBlocks blobBlocks, int blockNumber, long offset) throws InterruptedException {
        CompletableFuture<Void> completableFuture = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                blob.downloadRangeToByteArray(offset, blobBlocks.getBlockSize(blockNumber), blobBlocks.bytes, (int) offset);

                completableFuture.complete(null);

                System.out.println("Completed download for block num " + blockNumber);

            } catch (StorageException e) {
                e.printStackTrace();
            }
        }, service);

        return completableFuture;
    }

    private class BlobBlocks {

        private final ArrayList<BlockEntry> blocks;
        private final Long blobSize;
        private final byte[] bytes;

        public BlobBlocks(CloudBlockBlob blob) throws StorageException {
            this.blocks = blob.downloadBlockList();
            this.blobSize = blob.getProperties().getLength();
            this.bytes = new byte[blobSize.intValue()];
        }

        public Long getBlobSize() {
            return blobSize;
        }

        public int getNumberOfBlocks(){
            return this.blocks.size();
        }

        public Long getBlockSize(int index){
            return this.blocks.get(index).getSize();
        }
    }
}
