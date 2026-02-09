
package com.book.ensureu.admin.api;

import com.book.ensureu.admin.constant.PaperStateStatus;
import com.book.ensureu.admin.dto.PaperCollectionDto;
import com.book.ensureu.admin.service.PaidPaperCollectionService;
import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.dto.PaperInfo;
import com.book.ensureu.model.PaidPaperCollection;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
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
 * Unit tests for PaidPaperCollectionApi controller
 * Tests endpoints: saveTestPaper, updatePaper, getPaidPaperById, getAllPaidPaper, fetchPaperInfoListBySubCategory
 */


@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = PaidPaperCollectionApi.class,
    excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
    })
@ActiveProfiles("test")
class PaidPaperCollectionApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaidPaperCollectionService testPaperCollectionService;


    @MockBean
    private org.springframework.mail.javamail.JavaMailSender javaMailSender;

    private PaperCollectionDto testPaidPaperDto;
    private PaidPaperCollection testPaidPaper;

    @BeforeEach
    void setUp() {
        // Initialize test data
        testPaidPaperDto = new PaperCollectionDto();
        testPaidPaperDto.setId("paid-paper-456");
        testPaidPaperDto.setPaperType(PaperType.SSC);
        testPaidPaperDto.setPaperName("Advanced JEE Main Mock Test");
        testPaidPaperDto.setPaperSubCategory(PaperSubCategory.SSC_CGL_TIER1);
        testPaidPaperDto.setPaperCategory(PaperCategory.SSC_CGL);
        testPaidPaperDto.setTestType(TestType.FREE);
        testPaidPaperDto.setTotalQuestionCount(75);
        testPaidPaperDto.setTotalScore(150.0);
        testPaidPaperDto.setTotalTime(5400000L); // 1.5 hours
        testPaidPaperDto.setNegativeMarks(-0.33);
        testPaidPaperDto.setPerQuestionScore(2.0);
        testPaidPaperDto.setPaperStateStatus(PaperStateStatus.ACTIVE);

        testPaidPaper = new PaidPaperCollection();
        testPaidPaper.setId("paid-paper-456");
        testPaidPaper.setPaperName("Advanced JEE Main Mock Test");
        testPaidPaper.setPaperType(PaperType.SSC);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testSaveTestPaper_Success() throws Exception {
        // Arrange
        doNothing().when(testPaperCollectionService).createPaidPaperInCollection(any(PaidPaperCollection.class));

        // Act & Assert
        mockMvc.perform(post("/admin/paidpapercoll/save")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPaidPaperDto)))
                .andDo(print())
                .andExpect(status().isOk());

        // Verify
        ArgumentCaptor<PaidPaperCollection> captor = ArgumentCaptor.forClass(PaidPaperCollection.class);
        verify(testPaperCollectionService, times(1)).createPaidPaperInCollection(captor.capture());

        PaidPaperCollection savedPaper = captor.getValue();
        assertEquals("Advanced JEE Main Mock Test", savedPaper.getPaperName());
        assertEquals(PaperType.SSC, savedPaper.getPaperType());
        assertEquals(75, savedPaper.getTotalQuestionCount());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testSaveTestPaper_InitializesMissingDates() throws Exception {
        // Arrange
        testPaidPaperDto.setCreateDateTime(null);
        testPaidPaperDto.setValidityRangeStartDateTime(null);
        testPaidPaperDto.setValidityRangeEndDateTime(null);

        doNothing().when(testPaperCollectionService).createPaidPaperInCollection(any(PaidPaperCollection.class));

        // Act
        mockMvc.perform(post("/admin/paidpapercoll/save")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPaidPaperDto)))
                .andExpect(status().isOk());

        // Verify - dates should be initialized
        ArgumentCaptor<PaidPaperCollection> captor = ArgumentCaptor.forClass(PaidPaperCollection.class);
        verify(testPaperCollectionService).createPaidPaperInCollection(captor.capture());

        PaidPaperCollection savedPaper = captor.getValue();
        assertNotNull(savedPaper.getCreateDateTime());
        assertNotNull(savedPaper.getValidityRangeStartDateTime());
        assertNotNull(savedPaper.getValidityRangeEndDateTime());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testSaveTestPaper_HandlesException() throws Exception {
        // Arrange
        doThrow(new MongoException("Database error"))
                .when(testPaperCollectionService).createPaidPaperInCollection(any(PaidPaperCollection.class));

        // Act & Assert - Exception is caught and logged, no error response
        mockMvc.perform(post("/admin/paidpapercoll/save")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPaidPaperDto)))
                .andDo(print())
                .andExpect(status().isOk()); // Controller catches exception

        // Verify
        verify(testPaperCollectionService, times(1)).createPaidPaperInCollection(any());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdatePaper_Success() throws Exception {
        // Arrange
        doNothing().when(testPaperCollectionService).updatePaidPaperState(anyString(), any(PaperStateStatus.class));

        // Act & Assert
        mockMvc.perform(put("/admin/paidpapercoll/save")
                        .with(csrf())
                        .param("id", "paid-paper-456")
                        .param("paperState", "INACTIVE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Verify
        verify(testPaperCollectionService, times(1))
                .updatePaidPaperState("paid-paper-456", PaperStateStatus.DRAFT);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdatePaper_ToActiveState() throws Exception {
        // Arrange
        doNothing().when(testPaperCollectionService).updatePaidPaperState(anyString(), any(PaperStateStatus.class));

        // Act & Assert
        mockMvc.perform(put("/admin/paidpapercoll/save")
                        .with(csrf())
                        .param("id", "paid-paper-123")
                        .param("paperState", "ACTIVE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Verify
        verify(testPaperCollectionService, times(1))
                .updatePaidPaperState("paid-paper-123", PaperStateStatus.ACTIVE);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdatePaper_MissingId() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/admin/paidpapercoll/save")
                        .with(csrf())
                        .param("paperState", "ACTIVE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Verify - service should not be called
        verify(testPaperCollectionService, never()).updatePaidPaperState(anyString(), any());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdatePaper_HandlesMongoException() throws Exception {
        // Arrange
        doThrow(new MongoException("Update failed"))
                .when(testPaperCollectionService).updatePaidPaperState(anyString(), any(PaperStateStatus.class));

        // Act & Assert - Exception is caught and logged
        mockMvc.perform(put("/admin/paidpapercoll/save")
                        .with(csrf())
                        .param("id", "paid-paper-456")
                        .param("paperState", "ACTIVE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Verify
        verify(testPaperCollectionService, times(1))
                .updatePaidPaperState("paid-paper-456", PaperStateStatus.ACTIVE);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetPaidPaperById_Success() throws Exception {
        // Arrange
        when(testPaperCollectionService.getTestPaperCollectionById("paid-paper-456"))
                .thenReturn(testPaidPaperDto);

        // Act & Assert
        mockMvc.perform(get("/admin/paidpapercoll/getbyid/{id}", "paid-paper-456")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("paid-paper-456")))
                .andExpect(jsonPath("$.paperName", is("Advanced JEE Main Mock Test")))
                .andExpect(jsonPath("$.paperType", is("PAID")))
                .andExpect(jsonPath("$.totalQuestionCount", is(75)))
                .andExpect(jsonPath("$.totalScore", is(150.0)));

        // Verify
        verify(testPaperCollectionService, times(1)).getTestPaperCollectionById("paid-paper-456");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetPaidPaperById_NotFound() throws Exception {
        // Arrange
        when(testPaperCollectionService.getTestPaperCollectionById("non-existent-id"))
                .thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/admin/paidpapercoll/getbyid/{id}", "non-existent-id")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        // Verify
        verify(testPaperCollectionService, times(1)).getTestPaperCollectionById("non-existent-id");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetPaidPaperById_HandlesException() throws Exception {
        // Arrange
        when(testPaperCollectionService.getTestPaperCollectionById(anyString()))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert - Exception is caught and logged, returns null
        mockMvc.perform(get("/admin/paidpapercoll/getbyid/{id}", "paid-paper-456")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        // Verify
        verify(testPaperCollectionService, times(1)).getTestPaperCollectionById("paid-paper-456");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllPaidPaper_Success() throws Exception {
        // Arrange
        PaperCollectionDto paper1 = new PaperCollectionDto();
        paper1.setId("paid-paper-1");
        paper1.setPaperName("Paid Test 1");
        paper1.setPaperType(PaperType.SSC);

        PaperCollectionDto paper2 = new PaperCollectionDto();
        paper2.setId("paid-paper-2");
        paper2.setPaperName("Paid Test 2");
        paper2.setPaperType(PaperType.SSC);

        List<PaperCollectionDto> papers = Arrays.asList(paper1, paper2);
        Page<PaperCollectionDto> page = new PageImpl<>(papers, PageRequest.of(0, 10), 2);

        when(testPaperCollectionService.getAllPaidPaperCollection(
                eq(PaperType.SSC),
                any(Pageable.class)))
                .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/admin/paidpapercoll/list/{paperType}", "PAID")
                        .with(csrf())
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is("paid-paper-1")))
                .andExpect(jsonPath("$.content[0].paperName", is("Paid Test 1")))
                .andExpect(jsonPath("$.content[1].id", is("paid-paper-2")))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(1)));

        // Verify
        verify(testPaperCollectionService, times(1)).getAllPaidPaperCollection(
                eq(PaperType.SSC),
                any(Pageable.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllPaidPaper_EmptyResult() throws Exception {
        // Arrange
        Page<PaperCollectionDto> emptyPage = new PageImpl<>(Arrays.asList(), PageRequest.of(0, 10), 0);

        when(testPaperCollectionService.getAllPaidPaperCollection(
                eq(PaperType.SSC),
                any(Pageable.class)))
                .thenReturn(emptyPage);

        // Act & Assert
        mockMvc.perform(get("/admin/paidpapercoll/list/{paperType}", "PAID")
                        .with(csrf())
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
    void testGetAllPaidPaper_Pagination() throws Exception {
        // Arrange - Test different page sizes
        List<PaperCollectionDto> papers = Arrays.asList(
                createPaperDto("p1", "Test 1"),
                createPaperDto("p2", "Test 2"),
                createPaperDto("p3", "Test 3"),
                createPaperDto("p4", "Test 4"),
                createPaperDto("p5", "Test 5")
        );
        Page<PaperCollectionDto> page = new PageImpl<>(papers, PageRequest.of(1, 5), 15);

        when(testPaperCollectionService.getAllPaidPaperCollection(
                eq(PaperType.SSC),
                any(Pageable.class)))
                .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/admin/paidpapercoll/list/{paperType}", "PAID")
                        .with(csrf())
                        .param("page", "1")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.totalElements", is(15)))
                .andExpect(jsonPath("$.totalPages", is(3)))
                .andExpect(jsonPath("$.number", is(1)));

        // Verify
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(testPaperCollectionService).getAllPaidPaperCollection(eq(PaperType.SSC), pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(1, capturedPageable.getPageNumber());
        assertEquals(5, capturedPageable.getPageSize());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllPaidPaper_HandlesException() throws Exception {
        // Arrange
        when(testPaperCollectionService.getAllPaidPaperCollection(any(), any()))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert - Exception is caught and logged, returns null
        mockMvc.perform(get("/admin/paidpapercoll/list/{paperType}", "PAID")
                        .with(csrf())
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testFetchPaperInfoListBySubCategory_Success() throws Exception {
        // Arrange
        PaperInfo paper1 = PaperInfo.builder()
                .id("paper-1")
                .paperName("Maths Paper 1")
                .createdDate(System.currentTimeMillis())
                .validity(7776000000L)
                .build();

        PaperInfo paper2 = PaperInfo.builder()
                .id("paper-2")
                .paperName("Maths Paper 2")
                .createdDate(System.currentTimeMillis())
                .validity(7776000000L)
                .build();

        List<PaperInfo> paperInfoList = Arrays.asList(paper1, paper2);

        when(testPaperCollectionService.fetchPaperInfoList(
                eq(PaperSubCategory.SSC_CGL_TIER1),
                any(Pageable.class),
                eq(false)))
                .thenReturn(paperInfoList);

        // Act & Assert
        mockMvc.perform(get("/admin/paidpapercoll/list/{paperSubCategory}", "MATHS")
                        .with(csrf())
                        .param("page", "0")
                        .param("size", "10")
                        .param("taken", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("paper-1")))
                .andExpect(jsonPath("$[0].paperName", is("Maths Paper 1")))
                .andExpect(jsonPath("$[1].id", is("paper-2")))
                .andExpect(jsonPath("$[1].paperName", is("Maths Paper 2")));

        // Verify
        verify(testPaperCollectionService, times(1)).fetchPaperInfoList(
                eq(PaperSubCategory.SSC_CGL_TIER1),
                any(Pageable.class),
                eq(false));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testFetchPaperInfoListBySubCategory_FilterByTaken() throws Exception {
        // Arrange - Fetch only taken papers
        PaperInfo takenPaper = PaperInfo.builder()
                .id("taken-paper")
                .paperName("Completed Maths Test")
                .createdDate(System.currentTimeMillis())
                .validity(7776000000L)
                .build();

        List<PaperInfo> takenPapers = Arrays.asList(takenPaper);

        when(testPaperCollectionService.fetchPaperInfoList(
                eq(PaperSubCategory.SSC_CGL_TIER1),
                any(Pageable.class),
                eq(true)))
                .thenReturn(takenPapers);

        // Act & Assert
        mockMvc.perform(get("/admin/paidpapercoll/list/{paperSubCategory}", "PHYSICS")
                        .with(csrf())
                        .param("page", "0")
                        .param("size", "20")
                        .param("taken", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].paperName", is("Completed Maths Test")));

        // Verify
        verify(testPaperCollectionService, times(1)).fetchPaperInfoList(
                eq(PaperSubCategory.SSC_CGL_TIER1),
                any(Pageable.class),
                eq(true));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testFetchPaperInfoListBySubCategory_EmptyResult() throws Exception {
        // Arrange
        when(testPaperCollectionService.fetchPaperInfoList(
                any(PaperSubCategory.class),
                any(Pageable.class),
                anyBoolean()))
                .thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/admin/paidpapercoll/list/{paperSubCategory}", "CHEMISTRY")
                        .with(csrf())
                        .param("page", "0")
                        .param("size", "10")
                        .param("taken", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testFetchPaperInfoListBySubCategory_MissingRequiredParam() throws Exception {
        // Act & Assert - Missing 'taken' parameter
        mockMvc.perform(get("/admin/paidpapercoll/list/{paperSubCategory}", "MATHS")
                        .with(csrf())
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Verify - service should not be called
        verify(testPaperCollectionService, never()).fetchPaperInfoList(any(), any(), anyBoolean());
    }

    // Helper method
    private PaperCollectionDto createPaperDto(String id, String name) {
        PaperCollectionDto dto = new PaperCollectionDto();
        dto.setId(id);
        dto.setPaperName(name);
        dto.setPaperType(PaperType.SSC);
        return dto;
    }
}