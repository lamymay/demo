package com.arc.demo;

import com.arc.demo.server.service.FileService;
import com.arc.demo.server.RestServerApplication;
import com.arc.util.YmlPropertiesLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = RestServerApplication.class)
@AutoConfigureMockMvc
class RestServerApplicationTest {

    private static final Logger log = LoggerFactory.getLogger(RestServerApplicationTest.class);


    @Autowired
    private MockMvc mockMvc;

    @Mock
    private FileService fileService;

    @InjectMocks
    private RestServerApplication restServerApplication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReadFile() throws Exception {
        // Arrange
        String filePath = "application.yml";
        Map<String, Object> ymlContent = YmlPropertiesLoader.loadConfig(filePath);
        log.info("read {} ymlContent={}", filePath, ymlContent);
        when(fileService.readFile(filePath)).thenReturn(ymlContent);

        // Act & Assert
        mockMvc.perform(post("/readFile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"path\":\"" + filePath + "\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"from\":\"resources_folder_in_jar_test\"}")); // 确保与上面的期望值一致
    }


    @Test
    void testReadFile_InvalidPath() throws Exception {
        // Arrange
        String filePath = "/invalid/path.txt";

        when(fileService.readFile(filePath)).thenReturn(null); // 假设返回 null 表示文件不存在

        // Act & Assert
        mockMvc.perform(post("/readFile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"path\":\"" + filePath + "\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{}")); // 假设返回空 JSON 对象表示文件不存在
    }

    @Test
    void testReadFile_NoPathProvided() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/readFile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{}")); // 期望返回空 JSON 对象
    }
}
