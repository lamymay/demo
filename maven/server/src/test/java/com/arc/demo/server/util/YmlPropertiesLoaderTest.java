package com.arc.demo.server.util;

import com.arc.util.YmlPropertiesLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class YmlPropertiesLoaderTest {

    @Mock
    private InputStream mockInputStream;

    private YmlPropertiesLoader loader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loader = new YmlPropertiesLoader();
    }

    @Test
    void testLoadConfigFromLocal_ValidPropertiesFile() throws IOException {
        // Arrange
        File tempFile = File.createTempFile("test", ".properties");
        Properties properties = new Properties();
        properties.setProperty("key1", "value1");
        properties.setProperty("key2", "value2");
        try (var output = new FileOutputStream(tempFile)) {
            properties.store(output, null);
        }

        // Act
        Map<String, Object> result = loader.loadConfigFromLocal(tempFile);

        // Assert
        assertEquals("value1", result.get("key1"));
        assertEquals("value2", result.get("key2"));

        // Clean up
        tempFile.delete();
    }

    @Test
    void testLoadConfigFromLocal_ValidYmlFile() throws IOException {
        // Arrange
        File tempFile = File.createTempFile("test", ".yml");
        String yamlContent = "key1: value1\nkey2: value2";
        try (var output = new FileOutputStream(tempFile)) {
            output.write(yamlContent.getBytes());
        }

        // Act
        Map<String, Object> result = loader.loadConfigFromLocal(tempFile);

        // Assert
        assertEquals("value1", result.get("key1"));
        assertEquals("value2", result.get("key2"));

        // Clean up
        tempFile.delete();
    }

    @Test
    void testLoadConfigFromLocal_InvalidFile() {
        // Arrange
        File tempFile = new File("nonexistent.properties");

        // Act
        Map<String, Object> result = loader.loadConfigFromLocal(tempFile);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testLoadConfigFromJar_ValidPropertiesFile() {
        // Arrange
        // Assume you have a properties file in src/test/resources
        String path = "test/PropertiesReadTest.properties"; // Ensure this file exists for the test

        // Act
        Map<String, Object> result = loader.loadConfigFromJar(path);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testLoadConfigFromJar_ValidYmlFile() {
        // Arrange
        // Assume you have a yml file in src/test/resources
        String path = "test/YmlReadTest.yml"; // Ensure this file exists for the test

        // Act
        Map<String, Object> result = loader.loadConfigFromJar(path);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testLoadConfig_ValidFilePath() {
        // Arrange
        String path = "test/PropertiesReadTest.properties"; // Ensure this file exists for the test

        // Act
        Map<String, Object> result = loader.loadConfig(path);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testLoadConfig_InvalidFilePath() {
        // Act
        Map<String, Object> result = loader.loadConfig("invalid/path/to/file.properties");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testLoadConfig_NullOrEmptyPath() {
        // Act
        Map<String, Object> result = loader.loadConfig(null);
        assertTrue(result.isEmpty());

        result = loader.loadConfig("");
        assertTrue(result.isEmpty());
    }
}
