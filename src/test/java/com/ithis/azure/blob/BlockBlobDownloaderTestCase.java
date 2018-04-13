package com.ithis.azure.blob;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;


public class BlockBlobDownloaderTestCase {

    @Test
    public void testBlockBlobDownload() throws Exception {
        BlockBlobDownloader blockBlobDownloader = new BlockBlobDownloader();
        byte[] bytes = blockBlobDownloader.downloadBlobBlocksAsByteArray("largeblobtest", "testBlobPeak");
        FileUtils.writeByteArrayToFile(new File("block-blob-demo.txt"), bytes);

    }
}
