package com.ithis.azure.blob;


import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import static org.junit.Assert.assertNotNull;

public class BlobClientProviderTestCase {

    @Test
    public void testBlobClientProvider() throws InvalidKeyException, IOException, URISyntaxException {
        assertNotNull(BlobClientProvider.getBlobClient());
    }
}
