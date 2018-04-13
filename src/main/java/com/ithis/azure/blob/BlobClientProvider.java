package com.ithis.azure.blob;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.Properties;


public class BlobClientProvider {

    public static CloudBlobClient getBlobClient() throws RuntimeException, IOException, URISyntaxException, InvalidKeyException {

        Properties prop = new Properties();
        try {
            InputStream propertyStream = BlobClientProvider.class.getClassLoader().getResourceAsStream("config.properties");
            if (propertyStream != null) {
                prop.load(propertyStream);
            }
            else {
                throw new RuntimeException();
            }
        } catch (RuntimeException|IOException e) {
            System.out.println("\nFailed to load config.properties file.");
            throw e;
        }

        CloudStorageAccount storageAccount;
        try {
            System.out.println(prop.getProperty("storage.connection.string"));
            storageAccount = CloudStorageAccount.parse(prop.getProperty("storage.connection.string"));
        }
        catch (IllegalArgumentException|URISyntaxException e) {
            System.out.println("\nConnection string specifies an invalid URI.");
            System.out.println("Please confirm the connection string is in the Azure connection string format.");
            throw e;
        }
        catch (InvalidKeyException e) {
            System.out.println("\nConnection string specifies an invalid key.");
            System.out.println("Please confirm the AccountName and AccountKey in the connection string are valid.");
            throw e;
        }

        return storageAccount.createCloudBlobClient();
    }
}
