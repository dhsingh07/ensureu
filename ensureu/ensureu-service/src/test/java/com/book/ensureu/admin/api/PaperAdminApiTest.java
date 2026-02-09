
package com.book.ensureu.admin.api;

import com.book.ensureu.admin.constant.PaperStateStatus;
import com.book.ensureu.admin.dto.PaperCollectionDto;
import com.book.ensureu.admin.service.FreePaperCollectionService;
import com.book.ensureu.admin.service.PaidPaperCollectionService;
import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.model.FreePaperCollection;
import com.book.ensureu.security.UserPrincipalService;
import com.ensureu.commons.gcloud.util.GoogleCloudStorageUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for PaperAdminApi controller
 * Tests all endpoints: savePaper, updatePaper, getFreePaperById, getAllPaperFromColl, savePaperImage
 */


@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = PaperAdminApi.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class PaperAdminApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FreePaperCollectionService freePaperCollectionService;

    @MockBean
    private PaidPaperCollectionService paidPaperCollectionService;

    @MockBean
    private UserPrincipalService userPrincipalService;

    @MockBean
    private GoogleCloudStorageUtil googleCloudStorageUtil;

    @MockBean
    private org.springframework.mail.javamail.JavaMailSender javaMailSender;

    private PaperCollectionDto testPaperDto;
    private FreePaperCollection testFreePaper;

    @BeforeEach
    void setUp() {
        // Initialize test data
        testPaperDto = new PaperCollectionDto();
        testPaperDto.setId("test-paper-123");
        testPaperDto.setPaperType(PaperType.SSC);
        testPaperDto.setPaperName("Sample Math Test");
        testPaperDto.setPaperSubCategory(PaperSubCategory.SSC_CGL_TIER1);
        testPaperDto.setPaperCategory(PaperCategory.SSC_CGL);
        testPaperDto.setTestType(TestType.FREE);
        testPaperDto.setTotalQuestionCount(50);
        testPaperDto.setTotalScore(100.0);
        testPaperDto.setTotalTime(3600000L); // 1 hour in milliseconds
        testPaperDto.setNegativeMarks(-0.25);
        testPaperDto.setPerQuestionScore(2.0);
        testPaperDto.setPaperStateStatus(PaperStateStatus.ACTIVE);

        testFreePaper = new FreePaperCollection();
        testFreePaper.setId("test-paper-123");
        testFreePaper.setPaperName("Sample Math Test");
        testFreePaper.setPaperType(PaperType.SSC);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testSavePaper_Success_FreePaper() throws Exception {
        // Arrange
        doNothing().when(freePaperCollectionService).createFreePaperInCollection(any(FreePaperCollection.class));

        // Act & Assert
        mockMvc.perform(post("/admin/paper")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPaperDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Paper saved successfully"));

        // Verify
        ArgumentCaptor<FreePaperCollection> captor = ArgumentCaptor.forClass(FreePaperCollection.class);
        verify(freePaperCollectionService, times(1)).createFreePaperInCollection(captor.capture());

        FreePaperCollection savedPaper = captor.getValue();
        assertEquals("Sample Math Test", savedPaper.getPaperName());
        assertEquals(PaperType.SSC, savedPaper.getPaperType());
        assertEquals(50, savedPaper.getTotalQuestionCount());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testSavePaper_Success_PaidPaper() throws Exception {
        // Arrange
        testPaperDto.setPaperType(PaperType.BANK);
        testPaperDto.setTestType(TestType.PAID);
        doNothing().when(paidPaperCollectionService).createPaidPaperInCollection(any());

        // Act & Assert
        mockMvc.perform(post("/admin/paper")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPaperDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Paper saved successfully"));

        // Verify
        verify(paidPaperCollectionService, times(1)).createPaidPaperInCollection(any());
        verify(freePaperCollectionService, never()).createFreePaperInCollection(anyList());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testSavePaper_InitializesMissingDates() throws Exception {
        // Arrange
        testPaperDto.setCreateDateTime(null);
        testPaperDto.setValidityRangeStartDateTime(null);
        testPaperDto.setValidityRangeEndDateTime(null);

        doNothing().when(freePaperCollectionService).createFreePaperInCollection(any(FreePaperCollection.class));

        // Act
        mockMvc.perform(post("/admin/paper")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPaperDto)))
                .andExpect(status().isOk());

        // Verify - dates should be initialized
        ArgumentCaptor<FreePaperCollection> captor = ArgumentCaptor.forClass(FreePaperCollection.class);
        verify(freePaperCollectionService).createFreePaperInCollection(captor.capture());

        FreePaperCollection savedPaper = captor.getValue();
        assertNotNull(savedPaper.getCreateDateTime());
        assertNotNull(savedPaper.getValidityRangeStartDateTime());
        assertNotNull(savedPaper.getValidityRangeEndDateTime());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdatePaper_Success_FreePaper() throws Exception {
        // Arrange
        when(freePaperCollectionService.getFreePaperCollectionEntityById(anyString()))
                .thenReturn(testFreePaper);
        doNothing().when(freePaperCollectionService).createFreePaperInCollection(any(FreePaperCollection.class));

        testPaperDto.setPaperName("Updated Math Test");

        // Act & Assert
        mockMvc.perform(put("/admin/paper")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPaperDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Paper updated successfully"));

        // Verify
        verify(freePaperCollectionService, times(1)).getFreePaperCollectionEntityById("test-paper-123");
        verify(freePaperCollectionService, times(1)).createFreePaperInCollection(anyList());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdatePaper_PaperNotFound() throws Exception {
        // Arrange
        when(freePaperCollectionService.getFreePaperCollectionEntityById(anyString()))
                .thenReturn(null);

        // Act & Assert
        mockMvc.perform(put("/admin/paper")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPaperDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string("Paper not found with id: test-paper-123"));

        // Verify - update should not be called
        verify(freePaperCollectionService, never()).createFreePaperInCollection(anyList());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetFreePaperById_Success() throws Exception {
        // Arrange
        when(freePaperCollectionService.getFreePaperCollectionById("test-paper-123"))
                .thenReturn(testPaperDto);

        // Act & Assert
        mockMvc.perform(get("/admin/paper/{id}", "test-paper-123")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("test-paper-123")))
                .andExpect(jsonPath("$.paperName", is("Sample Math Test")))
                .andExpect(jsonPath("$.paperType", is("SSC")))
                .andExpect(jsonPath("$.totalQuestionCount", is(50)))
                .andExpect(jsonPath("$.totalScore", is(100.0)));

        // Verify
        verify(freePaperCollectionService, times(1)).getFreePaperCollectionById("test-paper-123");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetFreePaperById_NotFound() throws Exception {
        // Arrange
        when(freePaperCollectionService.getFreePaperCollectionById("non-existent-id"))
                .thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/admin/paper/{id}", "non-existent-id")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        // Verify
        verify(freePaperCollectionService, times(1)).getFreePaperCollectionById("non-existent-id");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllPaperFromColl_Success_FreePapers() throws Exception {
        // Arrange
        PaperCollectionDto paper1 = new PaperCollectionDto();
        paper1.setId("paper-1");
        paper1.setPaperName("Math Test 1");
        paper1.setPaperType(PaperType.SSC);

        PaperCollectionDto paper2 = new PaperCollectionDto();
        paper2.setId("paper-2");
        paper2.setPaperName("Math Test 2");
        paper2.setPaperType(PaperType.SSC);

        List<PaperCollectionDto> papers = Arrays.asList(paper1, paper2);
        Page<PaperCollectionDto> page = new PageImpl<>(papers, PageRequest.of(0, 10), 2);

        when(freePaperCollectionService.getAllFreePaperColl(
                eq(PaperType.SSC),
                any(Pageable.class)))
                .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/admin/paper")
                        .with(csrf())
                        .param("paperType", "SSC")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is("paper-1")))
                .andExpect(jsonPath("$.content[0].paperName", is("Math Test 1")))
                .andExpect(jsonPath("$.content[1].id", is("paper-2")))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(1)));

        // Verify
        verify(freePaperCollectionService, times(1)).getAllFreePaperColl(
                eq(PaperType.SSC),
                any(Pageable.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllPaperFromColl_Success_PaidPapers() throws Exception {
        // Arrange
        PaperCollectionDto paper1 = new PaperCollectionDto();
        paper1.setId("paid-paper-1");
        paper1.setPaperName("Advanced Math Test");
        paper1.setPaperType(PaperType.BANK);

        List<PaperCollectionDto> papers = Arrays.asList(paper1);
        Page<PaperCollectionDto> page = new PageImpl<>(papers, PageRequest.of(0, 10), 1);

        when(paidPaperCollectionService.getAllPaidPaperCollection(
                eq(PaperType.BANK),
                any(Pageable.class)))
                .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/admin/paper")
                        .with(csrf())
                        .param("paperType", "BANK")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is("paid-paper-1")))
                .andExpect(jsonPath("$.content[0].paperType", is("BANK")));

        // Verify
        verify(paidPaperCollectionService, times(1)).getAllPaidPaperCollection(
                eq(PaperType.BANK),
                any(Pageable.class));
        verify(freePaperCollectionService, never()).getAllFreePaperColl(any(), any());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllPaperFromColl_EmptyResult() throws Exception {
        // Arrange
        Page<PaperCollectionDto> emptyPage = new PageImpl<>(Arrays.asList(), PageRequest.of(0, 10), 0);

        when(freePaperCollectionService.getAllFreePaperColl(
                eq(PaperType.SSC),
                any(Pageable.class)))
                .thenReturn(emptyPage);

        // Act & Assert
        mockMvc.perform(get("/admin/paper")
                        .with(csrf())
                        .param("paperType", "SSC")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testSavePaperImage_Success() throws Exception {
        // Arrange
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "test-image.png",
                MediaType.IMAGE_PNG_VALUE,
                "test image content".getBytes()
        );

        String expectedImageUrl = "https://storage.googleapis.com/bucket/images/test-image.png";
        when(googleCloudStorageUtil.uploadObjectInBucket(any(), any(), anyString(), anyString())).thenReturn(expectedImageUrl);

        // Act & Assert
        mockMvc.perform(multipart("/admin/paper/image")
                        .file(imageFile)
                        .with(csrf())
                        .param("paperId", "test-paper-123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(expectedImageUrl));

        // Verify
        verify(googleCloudStorageUtil, times(1)).uploadObjectInBucket(any(), any(), anyString(), anyString());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testSavePaperImage_NoFileProvided() throws Exception {
        // Act & Assert
        mockMvc.perform(multipart("/admin/paper/image")
                        .with(csrf())
                        .param("paperId", "test-paper-123"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Verify - upload should not be called
        verify(googleCloudStorageUtil, never()).uploadObjectInBucket(any(), any(), anyString(), anyString());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testSavePaperImage_EmptyFile() throws Exception {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile(
                "image",
                "empty.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[0]
        );

        // Act & Assert
        mockMvc.perform(multipart("/admin/paper/image")
                        .file(emptyFile)
                        .with(csrf())
                        .param("paperId", "test-paper-123"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Verify - upload should not be called for empty files
        verify(googleCloudStorageUtil, never()).uploadObjectInBucket(any(), any(), anyString(), anyString());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testSavePaper_ValidationError_MissingPaperName() throws Exception {
        // Arrange
        testPaperDto.setPaperName(null);

        // Act & Assert
        mockMvc.perform(post("/admin/paper")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPaperDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Verify - service should not be called
        verify(freePaperCollectionService, never()).createFreePaperInCollection(anyList());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testSavePaper_ValidationError_MissingPaperType() throws Exception {
        // Arrange
        testPaperDto.setPaperType(null);

        // Act & Assert
        mockMvc.perform(post("/admin/paper")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPaperDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Verify - service should not be called
        verify(freePaperCollectionService, never()).createFreePaperInCollection(anyList());
    }
}

