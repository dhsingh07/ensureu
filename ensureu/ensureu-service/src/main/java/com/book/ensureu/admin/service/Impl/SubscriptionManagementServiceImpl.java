package com.book.ensureu.admin.service.Impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.book.ensureu.admin.constant.PaperStateStatus;
import com.book.ensureu.admin.dto.PaperSelectionDto;
import com.book.ensureu.admin.dto.PriceMetadataDto;
import com.book.ensureu.admin.dto.SubscriptionAdminDto;
import com.book.ensureu.admin.dto.SubscriptionCreateDto;
import com.book.ensureu.admin.dto.SubscriptionExtendDto;
import com.book.ensureu.admin.dto.SubscriptionStatsDto;
import com.book.ensureu.admin.dto.SubscriptionUpdateDto;
import com.book.ensureu.admin.service.SubscriptionManagementService;
import com.book.ensureu.constant.CounterEnum;
import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.SubscriptionType;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.model.FreePaperCollection;
import com.book.ensureu.model.PaidPaperCollection;
import com.book.ensureu.model.Subscription;
import com.book.ensureu.model.Subscription.PriceMetadata;
import com.book.ensureu.model.Subscription.SubscriptionState;
import com.book.ensureu.repository.FreePaperCollectionRepository;
import com.book.ensureu.repository.PaidPaperCollectionRepository;
import com.book.ensureu.repository.SubscriptionRepository;
import com.book.ensureu.service.CounterService;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of SubscriptionManagementService for Super Admin subscription management
 */
@Slf4j
@Service
public class SubscriptionManagementServiceImpl implements SubscriptionManagementService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private FreePaperCollectionRepository freePaperRepository;

    @Autowired
    private PaidPaperCollectionRepository paidPaperRepository;

    @Autowired
    private CounterService counterService;

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final List<PaperStateStatus> AVAILABLE_STATUSES =
            Arrays.asList(PaperStateStatus.APPROVED, PaperStateStatus.ACTIVE);

    // ==========================================
    // CRUD Operations
    // ==========================================

    @Override
    public Page<SubscriptionAdminDto> listSubscriptions(
            PaperType paperType,
            PaperCategory paperCategory,
            PaperSubCategory paperSubCategory,
            TestType testType,
            SubscriptionState state,
            String search,
            Pageable pageable) {

        log.info("[SubscriptionManagement] Listing subscriptions with filters");

        Query query = new Query();

        if (paperType != null) {
            query.addCriteria(Criteria.where("paperType").is(paperType));
        }
        if (paperCategory != null) {
            query.addCriteria(Criteria.where("paperCategory").is(paperCategory));
        }
        if (paperSubCategory != null) {
            query.addCriteria(Criteria.where("paperSubCategory").is(paperSubCategory));
        }
        if (testType != null) {
            query.addCriteria(Criteria.where("testType").is(testType));
        }
        if (state != null) {
            query.addCriteria(Criteria.where("state").is(state));
        }
        if (StringUtils.hasText(search)) {
            query.addCriteria(new Criteria().orOperator(
                    Criteria.where("name").regex(search, "i"),
                    Criteria.where("description").regex(search, "i")
            ));
        }

        long total = mongoTemplate.count(query, Subscription.class);
        query.with(pageable);
        List<Subscription> subscriptions = mongoTemplate.find(query, Subscription.class);

        List<SubscriptionAdminDto> dtos = subscriptions.stream()
                .map(this::toAdminDto)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, total);
    }

    @Override
    public SubscriptionAdminDto getSubscription(String id) {
        log.info("[SubscriptionManagement] Getting subscription: {}", id);

        Long subscriptionId = Long.parseLong(id);
        Optional<Subscription> optSub = subscriptionRepository.findById(subscriptionId);

        if (optSub.isEmpty()) {
            throw new IllegalArgumentException("Subscription not found: " + id);
        }

        SubscriptionAdminDto dto = toAdminDto(optSub.get());

        // Load full paper details
        dto.setPapers(getSubscriptionPapers(id));

        return dto;
    }

    @Override
    @Transactional
    public SubscriptionAdminDto createSubscription(SubscriptionCreateDto dto, String createdBy, String createdByName) {
        log.info("[SubscriptionManagement] Creating subscription: {}", dto.getName());

        // Validate
        validateCreateDto(dto);

        // Check paper availability
        List<PaperSelectionDto> paperCheck = checkPapersAvailability(dto.getPaperIds(), dto.getTestType(), null);
        List<String> takenPapers = paperCheck.stream()
                .filter(p -> Boolean.TRUE.equals(p.getTaken()))
                .map(PaperSelectionDto::getId)
                .collect(Collectors.toList());

        if (!takenPapers.isEmpty()) {
            throw new IllegalArgumentException("Papers already in use: " + String.join(", ", takenPapers));
        }

        // Generate ID
        Long newId = counterService.increment(CounterEnum.SUBSCRIPTION);

        // Build subscription
        Subscription subscription = Subscription.builder()
                .id(newId)
                .subscriptionId(newId)
                .name(dto.getName())
                .description(dto.getDescription())
                .paperType(dto.getPaperType())
                .paperCategory(dto.getPaperCategory())
                .paperSubCategory(dto.getPaperSubCategory())
                .testType(dto.getTestType())
                .paperIds(dto.getPaperIds())
                .createdDate(dto.getCreatedDate() != null ? dto.getCreatedDate() : System.currentTimeMillis())
                .validity(dto.getValidity())
                .state(dto.getState() != null ? dto.getState() : SubscriptionState.DRAFT)
                .priceMap(toPriceMetadataMap(dto.getPricing()))
                .createdBy(createdBy)
                .createdByName(createdByName)
                .createdAt(System.currentTimeMillis())
                .amendmentNo(0)
                .subscriberCount(0)
                .totalRevenue(0.0)
                .build();

        // Save
        Subscription saved = subscriptionRepository.save(subscription);

        // If ACTIVE, mark papers as taken
        if (saved.getState() == SubscriptionState.ACTIVE) {
            setPapersTakenFlag(saved.getPaperIds(), saved.getTestType(), true);
            saved.setActiveDate(System.currentTimeMillis());
            subscriptionRepository.save(saved);
        }

        log.info("[SubscriptionManagement] Created subscription: {}", saved.getId());
        return toAdminDto(saved);
    }

    @Override
    @Transactional
    public SubscriptionAdminDto updateSubscription(String id, SubscriptionUpdateDto dto, String updatedBy) {
        log.info("[SubscriptionManagement] Updating subscription: {}", id);

        Long subscriptionId = Long.parseLong(id);
        Optional<Subscription> optSub = subscriptionRepository.findById(subscriptionId);

        if (optSub.isEmpty()) {
            throw new IllegalArgumentException("Subscription not found: " + id);
        }

        Subscription subscription = optSub.get();

        // Update fields if provided
        if (StringUtils.hasText(dto.getName())) {
            subscription.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            subscription.setDescription(dto.getDescription());
        }
        if (dto.getCreatedDate() != null) {
            subscription.setCreatedDate(dto.getCreatedDate());
        }
        if (dto.getValidity() != null) {
            subscription.setValidity(dto.getValidity());
        }
        if (dto.getPricing() != null) {
            subscription.setPriceMap(toPriceMetadataMap(dto.getPricing()));
        }

        // Handle paper changes
        if (dto.getPaperIds() != null && !dto.getPaperIds().isEmpty()) {
            List<String> oldPaperIds = subscription.getPaperIds();
            List<String> newPaperIds = dto.getPaperIds();

            // Check availability of new papers (excluding current subscription)
            List<String> addedPapers = newPaperIds.stream()
                    .filter(p -> !oldPaperIds.contains(p))
                    .collect(Collectors.toList());

            if (!addedPapers.isEmpty()) {
                List<PaperSelectionDto> paperCheck = checkPapersAvailability(addedPapers, subscription.getTestType(), id);
                List<String> takenPapers = paperCheck.stream()
                        .filter(p -> Boolean.TRUE.equals(p.getTaken()))
                        .map(PaperSelectionDto::getId)
                        .collect(Collectors.toList());

                if (!takenPapers.isEmpty()) {
                    throw new IllegalArgumentException("Papers already in use: " + String.join(", ", takenPapers));
                }
            }

            // If subscription is ACTIVE, update taken flags
            if (subscription.getState() == SubscriptionState.ACTIVE) {
                // Release removed papers
                List<String> removedPapers = oldPaperIds.stream()
                        .filter(p -> !newPaperIds.contains(p))
                        .collect(Collectors.toList());
                if (!removedPapers.isEmpty()) {
                    setPapersTakenFlag(removedPapers, subscription.getTestType(), false);
                }

                // Mark added papers as taken
                if (!addedPapers.isEmpty()) {
                    setPapersTakenFlag(addedPapers, subscription.getTestType(), true);
                }
            }

            subscription.setPaperIds(newPaperIds);
        }

        subscription.setUpdatedBy(updatedBy);
        subscription.setUpdatedAt(System.currentTimeMillis());

        Subscription saved = subscriptionRepository.save(subscription);
        log.info("[SubscriptionManagement] Updated subscription: {}", saved.getId());

        return toAdminDto(saved);
    }

    @Override
    @Transactional
    public void deleteSubscription(String id) {
        log.info("[SubscriptionManagement] Deleting subscription: {}", id);

        Long subscriptionId = Long.parseLong(id);
        Optional<Subscription> optSub = subscriptionRepository.findById(subscriptionId);

        if (optSub.isEmpty()) {
            throw new IllegalArgumentException("Subscription not found: " + id);
        }

        Subscription subscription = optSub.get();

        if (subscription.getState() == SubscriptionState.ACTIVE) {
            throw new IllegalArgumentException("Cannot delete active subscription. Deactivate first.");
        }

        subscriptionRepository.delete(subscription);
        log.info("[SubscriptionManagement] Deleted subscription: {}", id);
    }

    // ==========================================
    // State Management
    // ==========================================

    @Override
    @Transactional
    public SubscriptionAdminDto changeState(String id, SubscriptionState newState, boolean force) {
        log.info("[SubscriptionManagement] Changing state for {}: new state = {}", id, newState);

        if (newState == SubscriptionState.ACTIVE) {
            return activateSubscription(id);
        } else {
            return deactivateSubscription(id, force);
        }
    }

    @Override
    @Transactional
    public SubscriptionAdminDto activateSubscription(String id) {
        log.info("[SubscriptionManagement] Activating subscription: {}", id);

        Long subscriptionId = Long.parseLong(id);
        Optional<Subscription> optSub = subscriptionRepository.findById(subscriptionId);

        if (optSub.isEmpty()) {
            throw new IllegalArgumentException("Subscription not found: " + id);
        }

        Subscription subscription = optSub.get();

        if (subscription.getState() == SubscriptionState.ACTIVE) {
            throw new IllegalArgumentException("Subscription is already active");
        }

        // Check paper availability
        List<PaperSelectionDto> paperCheck = checkPapersAvailability(
                subscription.getPaperIds(), subscription.getTestType(), id);
        List<String> takenPapers = paperCheck.stream()
                .filter(p -> Boolean.TRUE.equals(p.getTaken()))
                .map(PaperSelectionDto::getId)
                .collect(Collectors.toList());

        if (!takenPapers.isEmpty()) {
            throw new IllegalArgumentException("Cannot activate: Papers already in use: " + String.join(", ", takenPapers));
        }

        // Mark papers as taken
        setPapersTakenFlag(subscription.getPaperIds(), subscription.getTestType(), true);

        // Update subscription
        subscription.setState(SubscriptionState.ACTIVE);
        subscription.setActiveDate(System.currentTimeMillis());

        Subscription saved = subscriptionRepository.save(subscription);
        log.info("[SubscriptionManagement] Activated subscription: {}", id);

        return toAdminDto(saved);
    }

    @Override
    @Transactional
    public SubscriptionAdminDto deactivateSubscription(String id, boolean force) {
        log.info("[SubscriptionManagement] Deactivating subscription: {}", id);

        Long subscriptionId = Long.parseLong(id);
        Optional<Subscription> optSub = subscriptionRepository.findById(subscriptionId);

        if (optSub.isEmpty()) {
            throw new IllegalArgumentException("Subscription not found: " + id);
        }

        Subscription subscription = optSub.get();

        if (subscription.getState() == SubscriptionState.DRAFT) {
            throw new IllegalArgumentException("Subscription is already a draft");
        }

        // Check for active subscribers (if not forcing)
        if (!force && subscription.getSubscriberCount() != null && subscription.getSubscriberCount() > 0) {
            throw new IllegalArgumentException(
                    "Cannot deactivate: " + subscription.getSubscriberCount() + " active subscribers. Use force=true to override.");
        }

        // Release papers
        setPapersTakenFlag(subscription.getPaperIds(), subscription.getTestType(), false);

        // Update subscription
        subscription.setState(SubscriptionState.DRAFT);

        Subscription saved = subscriptionRepository.save(subscription);
        log.info("[SubscriptionManagement] Deactivated subscription: {}", id);

        return toAdminDto(saved);
    }

    // ==========================================
    // Validity Management
    // ==========================================

    @Override
    @Transactional
    public SubscriptionAdminDto extendValidity(String id, SubscriptionExtendDto dto, String extendedBy) {
        log.info("[SubscriptionManagement] Extending validity for subscription: {}", id);

        Long subscriptionId = Long.parseLong(id);
        Optional<Subscription> optSub = subscriptionRepository.findById(subscriptionId);

        if (optSub.isEmpty()) {
            throw new IllegalArgumentException("Subscription not found: " + id);
        }

        Subscription subscription = optSub.get();
        Long currentValidity = subscription.getValidity();
        Long newValidity;

        if (dto.getNewValidity() != null) {
            newValidity = dto.getNewValidity();
        } else if (dto.getExtendDays() != null) {
            long extensionMs = dto.getExtendDays() * 24L * 60L * 60L * 1000L;
            newValidity = (currentValidity != null ? currentValidity : System.currentTimeMillis()) + extensionMs;
        } else {
            throw new IllegalArgumentException("Either extendDays or newValidity must be provided");
        }

        if (newValidity <= System.currentTimeMillis()) {
            throw new IllegalArgumentException("New validity must be in the future");
        }

        subscription.setValidity(newValidity);
        subscription.setUpdatedBy(extendedBy);
        subscription.setUpdatedAt(System.currentTimeMillis());

        Subscription saved = subscriptionRepository.save(subscription);
        log.info("[SubscriptionManagement] Extended validity for subscription {} to {}", id, newValidity);

        return toAdminDto(saved);
    }

    @Override
    @Transactional
    public List<SubscriptionAdminDto> bulkExtendValidity(List<String> ids, SubscriptionExtendDto dto, String extendedBy) {
        log.info("[SubscriptionManagement] Bulk extending {} subscriptions", ids.size());

        return ids.stream()
                .map(id -> extendValidity(id, dto, extendedBy))
                .collect(Collectors.toList());
    }

    // ==========================================
    // Paper Selection
    // ==========================================

    @Override
    public Page<PaperSelectionDto> getAvailablePapers(
            TestType testType,
            PaperSubCategory paperSubCategory,
            String excludeSubscriptionId,
            String search,
            Pageable pageable) {

        log.info("[SubscriptionManagement] Getting available papers for {} / {}", testType, paperSubCategory);

        if (testType == TestType.FREE) {
            Page<FreePaperCollection> papers;
            if (StringUtils.hasText(search)) {
                papers = freePaperRepository.findAvailablePapersWithSearch(
                        paperSubCategory, false, AVAILABLE_STATUSES, search, pageable);
            } else {
                papers = freePaperRepository.findByPaperSubCategoryAndTakenAndPaperStateStatusIn(
                        paperSubCategory, false, AVAILABLE_STATUSES, pageable);
            }
            return papers.map(this::toFreePaperSelectionDto);
        } else {
            Page<PaidPaperCollection> papers;
            if (StringUtils.hasText(search)) {
                papers = paidPaperRepository.findAvailablePapersWithSearch(
                        paperSubCategory, false, AVAILABLE_STATUSES, search, pageable);
            } else {
                papers = paidPaperRepository.findByPaperSubCategoryAndTakenAndPaperStateStatusIn(
                        paperSubCategory, false, AVAILABLE_STATUSES, pageable);
            }
            return papers.map(this::toPaidPaperSelectionDto);
        }
    }

    @Override
    public List<PaperSelectionDto> getSubscriptionPapers(String subscriptionId) {
        log.info("[SubscriptionManagement] Getting papers for subscription: {}", subscriptionId);

        Long subId = Long.parseLong(subscriptionId);
        Optional<Subscription> optSub = subscriptionRepository.findById(subId);

        if (optSub.isEmpty()) {
            return new ArrayList<>();
        }

        Subscription subscription = optSub.get();
        List<String> paperIds = subscription.getPaperIds();

        if (paperIds == null || paperIds.isEmpty()) {
            return new ArrayList<>();
        }

        if (subscription.getTestType() == TestType.FREE) {
            List<FreePaperCollection> papers = freePaperRepository.findByIdIn(paperIds);
            return papers.stream()
                    .map(p -> {
                        PaperSelectionDto dto = toFreePaperSelectionDto(p);
                        dto.setIsSelected(true);
                        return dto;
                    })
                    .collect(Collectors.toList());
        } else {
            List<PaidPaperCollection> papers = paidPaperRepository.findByIdIn(paperIds);
            return papers.stream()
                    .map(p -> {
                        PaperSelectionDto dto = toPaidPaperSelectionDto(p);
                        dto.setIsSelected(true);
                        return dto;
                    })
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<PaperSelectionDto> checkPapersAvailability(List<String> paperIds, TestType testType, String excludeSubscriptionId) {
        log.info("[SubscriptionManagement] Checking availability for {} papers", paperIds.size());

        List<PaperSelectionDto> result = new ArrayList<>();

        for (String paperId : paperIds) {
            PaperSelectionDto dto = new PaperSelectionDto();
            dto.setId(paperId);

            // Check if paper is taken by another subscription
            List<Subscription> usingSubs = subscriptionRepository.findActiveByPaperId(paperId);

            // Filter out current subscription if editing
            if (excludeSubscriptionId != null) {
                Long excludeId = Long.parseLong(excludeSubscriptionId);
                usingSubs = usingSubs.stream()
                        .filter(s -> !s.getId().equals(excludeId))
                        .collect(Collectors.toList());
            }

            if (!usingSubs.isEmpty()) {
                dto.setTaken(true);
                dto.setTakenBySubscriptionId(usingSubs.get(0).getId().toString());
                dto.setTakenBySubscriptionName(usingSubs.get(0).getName());
            } else {
                dto.setTaken(false);
            }

            // Load paper details
            if (testType == TestType.FREE) {
                freePaperRepository.findById(paperId).ifPresent(p -> {
                    dto.setPaperName(p.getPaperName());
                    dto.setPaperStateStatus(p.getPaperStateStatus());
                });
            } else {
                paidPaperRepository.findById(paperId).ifPresent(p -> {
                    dto.setPaperName(p.getPaperName());
                    dto.setPaperStateStatus(p.getPaperStateStatus());
                });
            }

            result.add(dto);
        }

        return result;
    }

    // ==========================================
    // Statistics
    // ==========================================

    @Override
    public SubscriptionStatsDto getStatistics() {
        log.info("[SubscriptionManagement] Getting statistics");

        long totalActive = subscriptionRepository.countByState(SubscriptionState.ACTIVE);
        long totalDraft = subscriptionRepository.countByState(SubscriptionState.DRAFT);
        long total = totalActive + totalDraft;

        // Count expiring subscriptions
        long now = System.currentTimeMillis();
        long in7Days = now + (7L * 24L * 60L * 60L * 1000L);
        long in30Days = now + (30L * 24L * 60L * 60L * 1000L);

        List<Subscription> expiringIn7 = subscriptionRepository.findExpiringBetween(now, in7Days);
        List<Subscription> expiringIn30 = subscriptionRepository.findExpiringBetween(now, in30Days);

        // Count by test type
        long freeTotal = subscriptionRepository.countByStateAndTestType(SubscriptionState.ACTIVE, TestType.FREE)
                + subscriptionRepository.countByStateAndTestType(SubscriptionState.DRAFT, TestType.FREE);
        long freeActive = subscriptionRepository.countByStateAndTestType(SubscriptionState.ACTIVE, TestType.FREE);

        long paidTotal = subscriptionRepository.countByStateAndTestType(SubscriptionState.ACTIVE, TestType.PAID)
                + subscriptionRepository.countByStateAndTestType(SubscriptionState.DRAFT, TestType.PAID);
        long paidActive = subscriptionRepository.countByStateAndTestType(SubscriptionState.ACTIVE, TestType.PAID);

        // Count available papers
        long availableFreePapers = freePaperRepository.countByPaperSubCategoryAndTakenAndPaperStateStatusIn(
                null, false, AVAILABLE_STATUSES);
        long availablePaidPapers = paidPaperRepository.countByPaperSubCategoryAndTakenAndPaperStateStatusIn(
                null, false, AVAILABLE_STATUSES);

        return SubscriptionStatsDto.builder()
                .totalSubscriptions(total)
                .activeSubscriptions(totalActive)
                .draftSubscriptions(totalDraft)
                .availablePapers(availableFreePapers + availablePaidPapers)
                .expiringIn7Days((long) expiringIn7.size())
                .expiringIn30Days((long) expiringIn30.size())
                .freeStats(SubscriptionStatsDto.TestTypeStats.builder()
                        .total(freeTotal)
                        .active(freeActive)
                        .build())
                .paidStats(SubscriptionStatsDto.TestTypeStats.builder()
                        .total(paidTotal)
                        .active(paidActive)
                        .build())
                .build();
    }

    @Override
    public List<SubscriptionAdminDto> getExpiringSubscriptions(int days) {
        log.info("[SubscriptionManagement] Getting subscriptions expiring in {} days", days);

        long now = System.currentTimeMillis();
        long futureDate = now + (days * 24L * 60L * 60L * 1000L);

        List<Subscription> expiring = subscriptionRepository.findExpiringBetween(now, futureDate);

        return expiring.stream()
                .map(this::toAdminDto)
                .collect(Collectors.toList());
    }

    // ==========================================
    // Helper Methods
    // ==========================================

    private void validateCreateDto(SubscriptionCreateDto dto) {
        if (dto.getPaperType() == null) {
            throw new IllegalArgumentException("Paper type is required");
        }
        if (dto.getPaperCategory() == null) {
            throw new IllegalArgumentException("Paper category is required");
        }
        if (dto.getPaperSubCategory() == null) {
            throw new IllegalArgumentException("Paper sub-category is required");
        }
        if (dto.getTestType() == null) {
            throw new IllegalArgumentException("Test type is required");
        }
        if (!StringUtils.hasText(dto.getName())) {
            throw new IllegalArgumentException("Subscription name is required");
        }
        if (dto.getPaperIds() == null || dto.getPaperIds().isEmpty()) {
            throw new IllegalArgumentException("At least one paper is required");
        }
        if (dto.getValidity() == null) {
            throw new IllegalArgumentException("Validity date is required");
        }
        if (dto.getTestType() == TestType.PAID && (dto.getPricing() == null || dto.getPricing().isEmpty())) {
            throw new IllegalArgumentException("Pricing is required for paid subscriptions");
        }
    }

    private void setPapersTakenFlag(List<String> paperIds, TestType testType, boolean taken) {
        if (paperIds == null || paperIds.isEmpty()) {
            return;
        }

        String collectionName = testType == TestType.FREE ? "freePaperCollection" : "paidPaperCollection";

        Query query = new Query(Criteria.where("_id").in(paperIds));
        Update update = new Update().set("taken", taken);

        mongoTemplate.updateMulti(query, update, collectionName);

        log.info("[SubscriptionManagement] Updated taken={} for {} papers in {}", taken, paperIds.size(), collectionName);
    }

    private SubscriptionAdminDto toAdminDto(Subscription subscription) {
        long now = System.currentTimeMillis();
        Long validity = subscription.getValidity();
        boolean isExpired = validity != null && validity < now;
        int remainingDays = 0;

        if (validity != null && validity > now) {
            remainingDays = (int) ((validity - now) / (24L * 60L * 60L * 1000L));
        }

        return SubscriptionAdminDto.builder()
                .id(subscription.getId().toString())
                .subscriptionId(subscription.getSubscriptionId())
                .paperType(subscription.getPaperType())
                .paperCategory(subscription.getPaperCategory())
                .paperSubCategory(subscription.getPaperSubCategory())
                .testType(subscription.getTestType())
                .name(subscription.getName())
                .description(subscription.getDescription())
                .paperCount(subscription.getPaperIds() != null ? subscription.getPaperIds().size() : 0)
                .paperIds(subscription.getPaperIds())
                .createdDate(subscription.getCreatedDate())
                .validity(subscription.getValidity())
                .validityDays(remainingDays)
                .isExpired(isExpired)
                .pricing(toPriceMetadataDtoMap(subscription.getPriceMap()))
                .state(subscription.getState())
                .subscriberCount(subscription.getSubscriberCount())
                .totalRevenue(subscription.getTotalRevenue())
                .createdBy(subscription.getCreatedBy())
                .createdByName(subscription.getCreatedByName())
                .createdAt(subscription.getCreatedAt())
                .updatedBy(subscription.getUpdatedBy())
                .updatedAt(subscription.getUpdatedAt())
                .build();
    }

    private PaperSelectionDto toFreePaperSelectionDto(FreePaperCollection paper) {
        return PaperSelectionDto.builder()
                .id(paper.getId())
                .paperName(paper.getPaperName())
                .paperType(paper.getPaperType())
                .paperCategory(paper.getPaperCategory())
                .paperSubCategory(paper.getPaperSubCategory())
                .testType(paper.getTestType())
                .paperSubCategoryName(paper.getPaperSubCategoryName())
                .totalQuestionCount(paper.getTotalQuestionCount())
                .totalScore(paper.getTotalScore())
                .negativeMarks(paper.getNegativeMarks())
                .totalTime(paper.getTotalTime())
                .paperStateStatus(paper.getPaperStateStatus())
                .createDateTime(paper.getCreateDateTime())
                .taken(paper.isTaken())
                .isSelected(false)
                .build();
    }

    private PaperSelectionDto toPaidPaperSelectionDto(PaidPaperCollection paper) {
        return PaperSelectionDto.builder()
                .id(paper.getId())
                .paperName(paper.getPaperName())
                .paperType(paper.getPaperType())
                .paperCategory(paper.getPaperCategory())
                .paperSubCategory(paper.getPaperSubCategory())
                .testType(paper.getTestType())
                .paperSubCategoryName(paper.getPaperSubCategoryName())
                .totalQuestionCount(paper.getTotalQuestionCount())
                .totalScore(paper.getTotalScore())
                .negativeMarks(paper.getNegativeMarks())
                .totalTime(paper.getTotalTime())
                .paperStateStatus(paper.getPaperStateStatus())
                .createDateTime(paper.getCreateDateTime())
                .taken(paper.isTaken())
                .isSelected(false)
                .build();
    }

    private Map<SubscriptionType, PriceMetadata> toPriceMetadataMap(Map<SubscriptionType, PriceMetadataDto> dtoMap) {
        if (dtoMap == null) {
            return null;
        }
        Map<SubscriptionType, PriceMetadata> result = new HashMap<>();
        dtoMap.forEach((key, value) -> {
            result.put(key, PriceMetadata.builder()
                    .originalPrice(value.getOriginalPrice())
                    .discountedPrice(value.getDiscountedPrice())
                    .discountPercentage(value.getDiscountPercentage())
                    .isActive(value.getIsActive())
                    .build());
        });
        return result;
    }

    private Map<SubscriptionType, PriceMetadataDto> toPriceMetadataDtoMap(Map<SubscriptionType, PriceMetadata> metadataMap) {
        if (metadataMap == null) {
            return null;
        }
        Map<SubscriptionType, PriceMetadataDto> result = new HashMap<>();
        metadataMap.forEach((key, value) -> {
            result.put(key, PriceMetadataDto.builder()
                    .originalPrice(value.getOriginalPrice())
                    .discountedPrice(value.getDiscountedPrice())
                    .discountPercentage(value.getDiscountPercentage())
                    .isActive(value.getIsActive())
                    .build());
        });
        return result;
    }
}
