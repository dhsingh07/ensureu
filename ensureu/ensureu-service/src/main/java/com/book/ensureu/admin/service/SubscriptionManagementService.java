package com.book.ensureu.admin.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.book.ensureu.admin.dto.PaperSelectionDto;
import com.book.ensureu.admin.dto.SubscriptionAdminDto;
import com.book.ensureu.admin.dto.SubscriptionCreateDto;
import com.book.ensureu.admin.dto.SubscriptionExtendDto;
import com.book.ensureu.admin.dto.SubscriptionStatsDto;
import com.book.ensureu.admin.dto.SubscriptionUpdateDto;
import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.model.Subscription.SubscriptionState;

/**
 * Service interface for Super Admin subscription management
 */
public interface SubscriptionManagementService {

    // ==========================================
    // CRUD Operations
    // ==========================================

    /**
     * List all subscriptions with filters
     */
    Page<SubscriptionAdminDto> listSubscriptions(
            PaperType paperType,
            PaperCategory paperCategory,
            PaperSubCategory paperSubCategory,
            TestType testType,
            SubscriptionState state,
            String search,
            Pageable pageable);

    /**
     * Get subscription by ID
     */
    SubscriptionAdminDto getSubscription(String id);

    /**
     * Create new subscription
     */
    SubscriptionAdminDto createSubscription(SubscriptionCreateDto dto, String createdBy, String createdByName);

    /**
     * Update existing subscription
     */
    SubscriptionAdminDto updateSubscription(String id, SubscriptionUpdateDto dto, String updatedBy);

    /**
     * Delete subscription (only DRAFT allowed)
     */
    void deleteSubscription(String id);

    // ==========================================
    // State Management
    // ==========================================

    /**
     * Change subscription state (DRAFT <-> ACTIVE)
     */
    SubscriptionAdminDto changeState(String id, SubscriptionState newState, boolean force);

    /**
     * Activate subscription - sets taken=true on all papers
     */
    SubscriptionAdminDto activateSubscription(String id);

    /**
     * Deactivate subscription - sets taken=false on all papers
     */
    SubscriptionAdminDto deactivateSubscription(String id, boolean force);

    // ==========================================
    // Validity Management
    // ==========================================

    /**
     * Extend subscription validity
     */
    SubscriptionAdminDto extendValidity(String id, SubscriptionExtendDto dto, String extendedBy);

    /**
     * Bulk extend multiple subscriptions
     */
    List<SubscriptionAdminDto> bulkExtendValidity(List<String> ids, SubscriptionExtendDto dto, String extendedBy);

    // ==========================================
    // Paper Selection
    // ==========================================

    /**
     * Get available papers for subscription (not taken, approved/active)
     */
    Page<PaperSelectionDto> getAvailablePapers(
            TestType testType,
            PaperSubCategory paperSubCategory,
            String excludeSubscriptionId,
            String search,
            Pageable pageable);

    /**
     * Get papers currently in a subscription
     */
    List<PaperSelectionDto> getSubscriptionPapers(String subscriptionId);

    /**
     * Check if papers are available (not taken by other subscriptions)
     */
    List<PaperSelectionDto> checkPapersAvailability(List<String> paperIds, TestType testType, String excludeSubscriptionId);

    // ==========================================
    // Statistics
    // ==========================================

    /**
     * Get subscription statistics
     */
    SubscriptionStatsDto getStatistics();

    /**
     * Get subscriptions expiring within given days
     */
    List<SubscriptionAdminDto> getExpiringSubscriptions(int days);
}
