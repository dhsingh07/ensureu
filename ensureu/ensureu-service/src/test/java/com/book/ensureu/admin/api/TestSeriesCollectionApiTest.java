package com.book.ensureu.admin.api;

import com.book.ensureu.admin.service.TestSeriesService;
import com.book.ensureu.common.dto.TestSeriesDto;
import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for TestSeriesCollectionApi controller
 * Tests endpoints: fetchTestSeries, createTestSeries, patchTestSeries
 */


@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = TestSeriesCollectionApi.class,
    excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
    })
@ActiveProfiles("test")
class TestSeriesCollectionApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TestSeriesService testSeriesService;

    @MockBean
    private org.springframework.mail.javamail.JavaMailSender javaMailSender;

    private TestSeriesDto testSeriesDto;
    private SimpleDateFormat dateFormat;

    @BeforeEach
    void setUp() {
        dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

        // Initialize test data
        TestSeriesDto.paperSubCategoryInfoDto subCategoryInfo1 = TestSeriesDto.paperSubCategoryInfoDto.builder()
                .paperSubCategory(PaperSubCategory.SSC_CGL_TIER1)
                .paperCount(10)
                .build();

        TestSeriesDto.paperSubCategoryInfoDto subCategoryInfo2 = TestSeriesDto.paperSubCategoryInfoDto.builder()
                .paperSubCategory(PaperSubCategory.SSC_CGL_TIER1)
                .paperCount(8)
                .build();

        testSeriesDto = TestSeriesDto.builder()
                .uuid("test-series-123")
                .description("Complete JEE Main Test Series")
                .price(2999.0)
                .discountedPrice(1999.0)
                .discountedPercentage(33.34)
                .validity(7776000000L) // 90 days in milliseconds
                .active(true)
                .paperCategory("OBJECTIVE")
                .paperType("PAID")
                .paperSubCategoryInfoList(Arrays.asList(subCategoryInfo1, subCategoryInfo2))
                .build();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testFetchTestSeries_Success() throws Exception {
        // Arrange
        TestSeriesDto series1 = TestSeriesDto.builder()
                .uuid("series-1")
                .description("JEE Main Series")
                .price(2999.0)
                .active(true)
                .build();

        TestSeriesDto series2 = TestSeriesDto.builder()
                .uuid("series-2")
                .description("NEET Series")
                .price(3499.0)
                .active(true)
                .build();

        List<TestSeriesDto> testSeriesList = Arrays.asList(series1, series2);

        Date crDate = new Date(System.currentTimeMillis());
        Date validity = new Date(System.currentTimeMillis() + 7776000000L); // 90 days later

        when(testSeriesService.fetchTestSeries(
                eq(PaperCategory.SSC_CGL),
                any(Date.class),
                any(Date.class)))
                .thenReturn(testSeriesList);

        // Act & Assert
        mockMvc.perform(get("/admin/testSeries/get")
                        .with(csrf())
                        .header("paperCategory", "OBJECTIVE")
                        .header("crDate", dateFormat.format(crDate))
                        .header("validity", dateFormat.format(validity))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].uuid", is("series-1")))
                .andExpect(jsonPath("$[0].description", is("JEE Main Series")))
                .andExpect(jsonPath("$[0].price", is(2999.0)))
                .andExpect(jsonPath("$[1].uuid", is("series-2")))
                .andExpect(jsonPath("$[1].description", is("NEET Series")));

        // Verify
        verify(testSeriesService, times(1)).fetchTestSeries(
                eq(PaperCategory.SSC_CGL),
                any(Date.class),
                any(Date.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testFetchTestSeries_EmptyResult() throws Exception {
        // Arrange
        Date crDate = new Date();
        Date validity = new Date(System.currentTimeMillis() + 7776000000L);

        when(testSeriesService.fetchTestSeries(
                eq(PaperCategory.SSC_CGL),
                any(Date.class),
                any(Date.class)))
                .thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/admin/testSeries/get")
                        .with(csrf())
                        .header("paperCategory", "OBJECTIVE")
                        .header("crDate", dateFormat.format(crDate))
                        .header("validity", dateFormat.format(validity))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        // Verify
        verify(testSeriesService, times(1)).fetchTestSeries(
                eq(PaperCategory.SSC_CGL),
                any(Date.class),
                any(Date.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testFetchTestSeries_MissingHeaders() throws Exception {
        // Act & Assert - Missing paperCategory header
        mockMvc.perform(get("/admin/testSeries/get")
                        .with(csrf())
                        .header("crDate", dateFormat.format(new Date()))
                        .header("validity", dateFormat.format(new Date()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Verify - service should not be called
        verify(testSeriesService, never()).fetchTestSeries(any(), any(), any());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateTestSeries_Success() throws Exception {
        // Arrange
        doNothing().when(testSeriesService).createTestSeries(any(TestSeriesDto.class));

        // Act & Assert
        mockMvc.perform(post("/admin/testSeries/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testSeriesDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string("Created"));

        // Verify
        verify(testSeriesService, times(1)).createTestSeries(any(TestSeriesDto.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateTestSeries_WithSubCategories() throws Exception {
        // Arrange
        doNothing().when(testSeriesService).createTestSeries(any(TestSeriesDto.class));

        // Act & Assert
        mockMvc.perform(post("/admin/testSeries/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testSeriesDto)))
                .andDo(print())
                .andExpect(status().isCreated());

        // Verify the service was called with correct data
        verify(testSeriesService, times(1)).createTestSeries(argThat(dto ->
                dto.getDescription().equals("Complete JEE Main Test Series") &&
                        dto.getPrice() == 2999.0 &&
                        dto.getDiscountedPrice() == 1999.0 &&
                        dto.getPaperSubCategoryInfoList().size() == 2
        ));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateTestSeries_InvalidData() throws Exception {
        // Arrange - Empty description
        testSeriesDto.setDescription("");

        // Act & Assert
        mockMvc.perform(post("/admin/testSeries/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testSeriesDto)))
                .andDo(print())
                .andExpect(status().isCreated()); // Note: Controller doesn't validate, service layer should

        verify(testSeriesService, times(1)).createTestSeries(any(TestSeriesDto.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateTestSeries_MissingRequestBody() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/admin/testSeries/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Verify - service should not be called
        verify(testSeriesService, never()).createTestSeries(any());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testPatchTestSeries_Success() throws Exception {
        // Arrange
        testSeriesDto.setDescription("Updated JEE Main Test Series");
        testSeriesDto.setDiscountedPrice(1499.0);

        doNothing().when(testSeriesService).patchTestSeries(any(TestSeriesDto.class));

        // Act & Assert
        mockMvc.perform(patch("/admin/testSeries/patch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testSeriesDto)))
                .andDo(print())
                .andExpect(status().isAccepted())
                .andExpect(content().string("Created"));

        // Verify
        verify(testSeriesService, times(1)).patchTestSeries(any(TestSeriesDto.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testPatchTestSeries_UpdatePrice() throws Exception {
        // Arrange - Update only price fields
        testSeriesDto.setDiscountedPrice(1799.0);
        testSeriesDto.setDiscountedPercentage(40.0);

        doNothing().when(testSeriesService).patchTestSeries(any(TestSeriesDto.class));

        // Act & Assert
        mockMvc.perform(patch("/admin/testSeries/patch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testSeriesDto)))
                .andDo(print())
                .andExpect(status().isAccepted());

        // Verify the service received the updated DTO
        verify(testSeriesService, times(1)).patchTestSeries(argThat(dto ->
                dto.getDiscountedPrice() == 1799.0 &&
                        dto.getDiscountedPercentage() == 40.0
        ));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testPatchTestSeries_ToggleActiveStatus() throws Exception {
        // Arrange - Deactivate test series
        testSeriesDto.setActive(false);

        doNothing().when(testSeriesService).patchTestSeries(any(TestSeriesDto.class));

        // Act & Assert
        mockMvc.perform(patch("/admin/testSeries/patch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testSeriesDto)))
                .andDo(print())
                .andExpect(status().isAccepted());

        // Verify
        verify(testSeriesService, times(1)).patchTestSeries(argThat(dto ->
                !dto.isActive()
        ));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testPatchTestSeries_MissingRequestBody() throws Exception {
        // Act & Assert
        mockMvc.perform(patch("/admin/testSeries/patch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Verify - service should not be called
        verify(testSeriesService, never()).patchTestSeries(any());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testPatchTestSeries_NullUuid() throws Exception {
        // Arrange - UUID is null (should be handled by service layer)
        testSeriesDto.setUuid(null);

        doNothing().when(testSeriesService).patchTestSeries(any(TestSeriesDto.class));

        // Act & Assert
        mockMvc.perform(patch("/admin/testSeries/patch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testSeriesDto)))
                .andDo(print())
                .andExpect(status().isAccepted());

        // Verify - service is called (validation should happen at service layer)
        verify(testSeriesService, times(1)).patchTestSeries(any(TestSeriesDto.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testFetchTestSeries_DifferentPaperCategories() throws Exception {
        // Arrange
        List<TestSeriesDto> subjectiveSeriesList = Arrays.asList(
                TestSeriesDto.builder()
                        .uuid("subjective-1")
                        .description("Subjective Test Series")
                        .paperCategory("SUBJECTIVE")
                        .build()
        );

        Date crDate = new Date();
        Date validity = new Date(System.currentTimeMillis() + 7776000000L);

        when(testSeriesService.fetchTestSeries(
                eq(PaperCategory.SSC_CGL),
                any(Date.class),
                any(Date.class)))
                .thenReturn(subjectiveSeriesList);

        // Act & Assert
        mockMvc.perform(get("/admin/testSeries/get")
                        .with(csrf())
                        .header("paperCategory", "SUBJECTIVE")
                        .header("crDate", dateFormat.format(crDate))
                        .header("validity", dateFormat.format(validity))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].paperCategory", is("SUBJECTIVE")));

        // Verify
        verify(testSeriesService, times(1)).fetchTestSeries(
                eq(PaperCategory.SSC_CGL),
                any(Date.class),
                any(Date.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateTestSeries_WithMultipleSubCategories() throws Exception {
        // Arrange - Test series with many paper subcategories
        TestSeriesDto.paperSubCategoryInfoDto subCat1 = TestSeriesDto.paperSubCategoryInfoDto.builder()
                .paperSubCategory(PaperSubCategory.SSC_CGL_TIER1)
                .paperCount(15)
                .build();

        TestSeriesDto.paperSubCategoryInfoDto subCat2 = TestSeriesDto.paperSubCategoryInfoDto.builder()
                .paperSubCategory(PaperSubCategory.SSC_CGL_TIER1)
                .paperCount(12)
                .build();

        TestSeriesDto.paperSubCategoryInfoDto subCat3 = TestSeriesDto.paperSubCategoryInfoDto.builder()
                .paperSubCategory(PaperSubCategory.SSC_CGL_TIER1)
                .paperCount(10)
                .build();

        testSeriesDto.setPaperSubCategoryInfoList(Arrays.asList(subCat1, subCat2, subCat3));

        doNothing().when(testSeriesService).createTestSeries(any(TestSeriesDto.class));

        // Act & Assert
        mockMvc.perform(post("/admin/testSeries/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testSeriesDto)))
                .andDo(print())
                .andExpect(status().isCreated());

        // Verify
        verify(testSeriesService, times(1)).createTestSeries(argThat(dto ->
                dto.getPaperSubCategoryInfoList().size() == 3 &&
                        dto.getPaperSubCategoryInfoList().stream()
                                .mapToInt(TestSeriesDto.paperSubCategoryInfoDto::getPaperCount)
                                .sum() == 37
        ));
    }
}