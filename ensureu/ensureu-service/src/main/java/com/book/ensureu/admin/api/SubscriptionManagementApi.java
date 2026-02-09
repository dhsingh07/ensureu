package com.book.ensureu.admin.api;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.book.ensureu.admin.dto.PaperSelectionDto;
import com.book.ensureu.admin.dto.SubscriptionAdminDto;
import com.book.ensureu.admin.dto.SubscriptionCreateDto;
import com.book.ensureu.admin.dto.SubscriptionExtendDto;
import com.book.ensureu.admin.dto.SubscriptionStatsDto;
import com.book.ensureu.admin.dto.SubscriptionUpdateDto;
import com.book.ensureu.admin.service.SubscriptionManagementService;
import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.model.Subscription.SubscriptionState;
import com.book.ensureu.response.dto.Response;

import lombok.extern.slf4j.Slf4j;

/**
 * REST API for Super Admin subscription management
 * All endpoints require SUPERADMIN role
 */
@Slf4j
@RestController
@RequestMapping("/admin/subscription-management")
@CrossOrigin
public class SubscriptionManagementApi {

    @Autowired
    private SubscriptionManagementService subscriptionService;

    // ==========================================
    // List & Get
    // ==========================================

    /**
     * List all subscriptions with filters
     */
    @GetMapping("/list")
    public Response<Page<SubscriptionAdminDto>> listSubscriptions(
            @RequestParam(required = false) PaperType paperType,
            @RequestParam(required = false) PaperCategory paperCategory,
            @RequestParam(required = false) PaperSubCategory paperSubCategory,
            @RequestParam(required = false) TestType testType,
            @RequestParam(required = false) SubscriptionState state,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.info("[SubscriptionManagementApi] List subscriptions - page: {}, size: {}", page, size);

        try {
            Sort sort = sortDir.equalsIgnoreCase("asc")
                    ? Sort.by(sortBy).ascending()
                    : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<SubscriptionAdminDto> result = subscriptionService.listSubscriptions(
                    paperType, paperCategory, paperSubCategory, testType, state, search, pageable);

            return new Response<Page<SubscriptionAdminDto>>()
                    .setStatus(200)
                    .setBody(result)
                    .setMessage("Subscriptions retrieved successfully");
        } catch (Exception e) {
            log.error("[SubscriptionManagementApi] List error: {}", e.getMessage(), e);
            return new Response<Page<SubscriptionAdminDto>>()
                    .setStatus(500)
                    .setMessage("Failed to list subscriptions: " + e.getMessage());
        }
    }

    /**
     * Get subscription by ID
     */
    @GetMapping("/{id}")
    public Response<SubscriptionAdminDto> getSubscription(@PathVariable String id) {
        log.info("[SubscriptionManagementApi] Get subscription: {}", id);

        try {
            SubscriptionAdminDto result = subscriptionService.getSubscription(id);
            return new Response<SubscriptionAdminDto>()
                    .setStatus(200)
                    .setBody(result)
                    .setMessage("Subscription retrieved successfully");
        } catch (IllegalArgumentException e) {
            log.warn("[SubscriptionManagementApi] Not found: {}", id);
            return new Response<SubscriptionAdminDto>()
                    .setStatus(404)
                    .setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("[SubscriptionManagementApi] Get error: {}", e.getMessage(), e);
            return new Response<SubscriptionAdminDto>()
                    .setStatus(500)
                    .setMessage("Failed to get subscription: " + e.getMessage());
        }
    }

    /**
     * Get subscription statistics
     */
    @GetMapping("/stats")
    public Response<SubscriptionStatsDto> getStatistics() {
        log.info("[SubscriptionManagementApi] Get statistics");

        try {
            SubscriptionStatsDto stats = subscriptionService.getStatistics();
            return new Response<SubscriptionStatsDto>()
                    .setStatus(200)
                    .setBody(stats)
                    .setMessage("Statistics retrieved successfully");
        } catch (Exception e) {
            log.error("[SubscriptionManagementApi] Stats error: {}", e.getMessage(), e);
            return new Response<SubscriptionStatsDto>()
                    .setStatus(500)
                    .setMessage("Failed to get statistics: " + e.getMessage());
        }
    }

    /**
     * Get subscriptions expiring within given days
     */
    @GetMapping("/expiring")
    public Response<List<SubscriptionAdminDto>> getExpiringSubscriptions(
            @RequestParam(defaultValue = "30") int days) {

        log.info("[SubscriptionManagementApi] Get expiring subscriptions within {} days", days);

        try {
            List<SubscriptionAdminDto> result = subscriptionService.getExpiringSubscriptions(days);
            return new Response<List<SubscriptionAdminDto>>()
                    .setStatus(200)
                    .setBody(result)
                    .setMessage("Expiring subscriptions retrieved");
        } catch (Exception e) {
            log.error("[SubscriptionManagementApi] Expiring error: {}", e.getMessage(), e);
            return new Response<List<SubscriptionAdminDto>>()
                    .setStatus(500)
                    .setMessage("Failed to get expiring subscriptions: " + e.getMessage());
        }
    }

    // ==========================================
    // Create & Update
    // ==========================================

    /**
     * Create new subscription
     */
    @PostMapping("/create")
    public Response<SubscriptionAdminDto> createSubscription(
            @RequestBody SubscriptionCreateDto dto,
            Principal principal) {

        log.info("[SubscriptionManagementApi] Create subscription: {}", dto.getName());

        try {
            String userId = principal != null ? principal.getName() : "system";
            SubscriptionAdminDto result = subscriptionService.createSubscription(dto, userId, userId);

            return new Response<SubscriptionAdminDto>()
                    .setStatus(201)
                    .setBody(result)
                    .setMessage("Subscription created successfully");
        } catch (IllegalArgumentException e) {
            log.warn("[SubscriptionManagementApi] Validation error: {}", e.getMessage());
            return new Response<SubscriptionAdminDto>()
                    .setStatus(400)
                    .setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("[SubscriptionManagementApi] Create error: {}", e.getMessage(), e);
            return new Response<SubscriptionAdminDto>()
                    .setStatus(500)
                    .setMessage("Failed to create subscription: " + e.getMessage());
        }
    }

    /**
     * Update subscription
     */
    @PutMapping("/{id}")
    public Response<SubscriptionAdminDto> updateSubscription(
            @PathVariable String id,
            @RequestBody SubscriptionUpdateDto dto,
            Principal principal) {

        log.info("[SubscriptionManagementApi] Update subscription: {}", id);

        try {
            String userId = principal != null ? principal.getName() : "system";
            SubscriptionAdminDto result = subscriptionService.updateSubscription(id, dto, userId);

            return new Response<SubscriptionAdminDto>()
                    .setStatus(200)
                    .setBody(result)
                    .setMessage("Subscription updated successfully");
        } catch (IllegalArgumentException e) {
            log.warn("[SubscriptionManagementApi] Update validation error: {}", e.getMessage());
            return new Response<SubscriptionAdminDto>()
                    .setStatus(400)
                    .setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("[SubscriptionManagementApi] Update error: {}", e.getMessage(), e);
            return new Response<SubscriptionAdminDto>()
                    .setStatus(500)
                    .setMessage("Failed to update subscription: " + e.getMessage());
        }
    }

    /**
     * Delete subscription (only DRAFT allowed)
     */
    @DeleteMapping("/{id}")
    public Response<Boolean> deleteSubscription(@PathVariable String id) {
        log.info("[SubscriptionManagementApi] Delete subscription: {}", id);

        try {
            subscriptionService.deleteSubscription(id);
            return new Response<Boolean>()
                    .setStatus(200)
                    .setBody(true)
                    .setMessage("Subscription deleted successfully");
        } catch (IllegalArgumentException e) {
            log.warn("[SubscriptionManagementApi] Delete validation error: {}", e.getMessage());
            return new Response<Boolean>()
                    .setStatus(400)
                    .setBody(false)
                    .setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("[SubscriptionManagementApi] Delete error: {}", e.getMessage(), e);
            return new Response<Boolean>()
                    .setStatus(500)
                    .setBody(false)
                    .setMessage("Failed to delete subscription: " + e.getMessage());
        }
    }

    // ==========================================
    // State Management
    // ==========================================

    /**
     * Change subscription state (activate/deactivate)
     */
    @PatchMapping("/{id}/state")
    public Response<SubscriptionAdminDto> changeState(
            @PathVariable String id,
            @RequestParam SubscriptionState state,
            @RequestParam(defaultValue = "false") boolean force) {

        log.info("[SubscriptionManagementApi] Change state for {}: {}", id, state);

        try {
            SubscriptionAdminDto result = subscriptionService.changeState(id, state, force);
            return new Response<SubscriptionAdminDto>()
                    .setStatus(200)
                    .setBody(result)
                    .setMessage("Subscription state changed to " + state);
        } catch (IllegalArgumentException e) {
            log.warn("[SubscriptionManagementApi] State change error: {}", e.getMessage());
            return new Response<SubscriptionAdminDto>()
                    .setStatus(400)
                    .setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("[SubscriptionManagementApi] State change error: {}", e.getMessage(), e);
            return new Response<SubscriptionAdminDto>()
                    .setStatus(500)
                    .setMessage("Failed to change state: " + e.getMessage());
        }
    }

    /**
     * Activate subscription
     */
    @PostMapping("/{id}/activate")
    public Response<SubscriptionAdminDto> activateSubscription(@PathVariable String id) {
        log.info("[SubscriptionManagementApi] Activate subscription: {}", id);

        try {
            SubscriptionAdminDto result = subscriptionService.activateSubscription(id);
            return new Response<SubscriptionAdminDto>()
                    .setStatus(200)
                    .setBody(result)
                    .setMessage("Subscription activated successfully");
        } catch (IllegalArgumentException e) {
            log.warn("[SubscriptionManagementApi] Activate error: {}", e.getMessage());
            return new Response<SubscriptionAdminDto>()
                    .setStatus(400)
                    .setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("[SubscriptionManagementApi] Activate error: {}", e.getMessage(), e);
            return new Response<SubscriptionAdminDto>()
                    .setStatus(500)
                    .setMessage("Failed to activate: " + e.getMessage());
        }
    }

    /**
     * Deactivate subscription
     */
    @PostMapping("/{id}/deactivate")
    public Response<SubscriptionAdminDto> deactivateSubscription(
            @PathVariable String id,
            @RequestParam(defaultValue = "false") boolean force) {

        log.info("[SubscriptionManagementApi] Deactivate subscription: {}", id);

        try {
            SubscriptionAdminDto result = subscriptionService.deactivateSubscription(id, force);
            return new Response<SubscriptionAdminDto>()
                    .setStatus(200)
                    .setBody(result)
                    .setMessage("Subscription deactivated successfully");
        } catch (IllegalArgumentException e) {
            log.warn("[SubscriptionManagementApi] Deactivate error: {}", e.getMessage());
            return new Response<SubscriptionAdminDto>()
                    .setStatus(400)
                    .setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("[SubscriptionManagementApi] Deactivate error: {}", e.getMessage(), e);
            return new Response<SubscriptionAdminDto>()
                    .setStatus(500)
                    .setMessage("Failed to deactivate: " + e.getMessage());
        }
    }

    // ==========================================
    // Validity Management
    // ==========================================

    /**
     * Extend subscription validity
     */
    @PatchMapping("/{id}/extend")
    public Response<SubscriptionAdminDto> extendValidity(
            @PathVariable String id,
            @RequestBody SubscriptionExtendDto dto,
            Principal principal) {

        log.info("[SubscriptionManagementApi] Extend validity for: {}", id);

        try {
            String userId = principal != null ? principal.getName() : "system";
            SubscriptionAdminDto result = subscriptionService.extendValidity(id, dto, userId);
            return new Response<SubscriptionAdminDto>()
                    .setStatus(200)
                    .setBody(result)
                    .setMessage("Subscription validity extended");
        } catch (IllegalArgumentException e) {
            log.warn("[SubscriptionManagementApi] Extend error: {}", e.getMessage());
            return new Response<SubscriptionAdminDto>()
                    .setStatus(400)
                    .setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("[SubscriptionManagementApi] Extend error: {}", e.getMessage(), e);
            return new Response<SubscriptionAdminDto>()
                    .setStatus(500)
                    .setMessage("Failed to extend validity: " + e.getMessage());
        }
    }

    /**
     * Bulk extend multiple subscriptions
     */
    @PostMapping("/bulk-extend")
    public Response<List<SubscriptionAdminDto>> bulkExtendValidity(
            @RequestParam List<String> ids,
            @RequestBody SubscriptionExtendDto dto,
            Principal principal) {

        log.info("[SubscriptionManagementApi] Bulk extend {} subscriptions", ids.size());

        try {
            String userId = principal != null ? principal.getName() : "system";
            List<SubscriptionAdminDto> result = subscriptionService.bulkExtendValidity(ids, dto, userId);
            return new Response<List<SubscriptionAdminDto>>()
                    .setStatus(200)
                    .setBody(result)
                    .setMessage("Extended " + result.size() + " subscriptions");
        } catch (Exception e) {
            log.error("[SubscriptionManagementApi] Bulk extend error: {}", e.getMessage(), e);
            return new Response<List<SubscriptionAdminDto>>()
                    .setStatus(500)
                    .setMessage("Failed to bulk extend: " + e.getMessage());
        }
    }

    // ==========================================
    // Paper Selection
    // ==========================================

    /**
     * Get available papers for subscription (not taken)
     */
    @GetMapping("/available-papers")
    public Response<Page<PaperSelectionDto>> getAvailablePapers(
            @RequestParam TestType testType,
            @RequestParam PaperSubCategory paperSubCategory,
            @RequestParam(required = false) String excludeSubscriptionId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("[SubscriptionManagementApi] Get available papers - testType: {}, subCategory: {}",
                testType, paperSubCategory);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<PaperSelectionDto> result = subscriptionService.getAvailablePapers(
                    testType, paperSubCategory, excludeSubscriptionId, search, pageable);

            return new Response<Page<PaperSelectionDto>>()
                    .setStatus(200)
                    .setBody(result)
                    .setMessage("Available papers retrieved");
        } catch (Exception e) {
            log.error("[SubscriptionManagementApi] Available papers error: {}", e.getMessage(), e);
            return new Response<Page<PaperSelectionDto>>()
                    .setStatus(500)
                    .setMessage("Failed to get available papers: " + e.getMessage());
        }
    }

    /**
     * Get papers in a subscription
     */
    @GetMapping("/{id}/papers")
    public Response<List<PaperSelectionDto>> getSubscriptionPapers(@PathVariable String id) {
        log.info("[SubscriptionManagementApi] Get papers for subscription: {}", id);

        try {
            List<PaperSelectionDto> result = subscriptionService.getSubscriptionPapers(id);
            return new Response<List<PaperSelectionDto>>()
                    .setStatus(200)
                    .setBody(result)
                    .setMessage("Subscription papers retrieved");
        } catch (Exception e) {
            log.error("[SubscriptionManagementApi] Subscription papers error: {}", e.getMessage(), e);
            return new Response<List<PaperSelectionDto>>()
                    .setStatus(500)
                    .setMessage("Failed to get subscription papers: " + e.getMessage());
        }
    }

    /**
     * Check paper availability
     */
    @PostMapping("/check-papers")
    public Response<List<PaperSelectionDto>> checkPapersAvailability(
            @RequestBody List<String> paperIds,
            @RequestParam TestType testType,
            @RequestParam(required = false) String excludeSubscriptionId) {

        log.info("[SubscriptionManagementApi] Check availability for {} papers", paperIds.size());

        try {
            List<PaperSelectionDto> result = subscriptionService.checkPapersAvailability(
                    paperIds, testType, excludeSubscriptionId);
            return new Response<List<PaperSelectionDto>>()
                    .setStatus(200)
                    .setBody(result)
                    .setMessage("Paper availability checked");
        } catch (Exception e) {
            log.error("[SubscriptionManagementApi] Check papers error: {}", e.getMessage(), e);
            return new Response<List<PaperSelectionDto>>()
                    .setStatus(500)
                    .setMessage("Failed to check papers: " + e.getMessage());
        }
    }
}
