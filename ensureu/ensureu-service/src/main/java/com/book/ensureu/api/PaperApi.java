package com.book.ensureu.api;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.book.ensureu.exception.GenericException;
import com.book.ensureu.exception.RuntimeEUException;
import com.book.ensureu.flow.analytics.service.AnalyticsDataIngestionService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperStatus;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.dto.PaperDto;
import com.book.ensureu.dto.PaperAesDto;
import com.book.ensureu.model.JwtUser;
import com.book.ensureu.response.dto.Response;
import com.book.ensureu.security.UserPrincipalService;
import com.book.ensureu.service.PaperService;
import com.book.ensureu.service.impl.PaperFactory;
import com.book.ensureu.util.AesEncDecUtil;
import com.book.ensureu.util.HashUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/paper")
@Slf4j
public class PaperApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaperApi.class.getName());
    @Autowired
    PaperFactory paperFactory;

    PaperService paperService;

    @Autowired
    UserPrincipalService userPrincipal;

    @Value("${aes.encryption.salt}")
    private String salt;

    @Value("${aes.encryption.iv}")
    private String iv;

    @Value("${aes.encryption.passphrase}")
    private String passphrase;

    @Autowired
    private AnalyticsDataIngestionService analyticsDataIngestionService;

    private ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @CrossOrigin
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public void savePaper(@RequestBody PaperDto paperDto) {

        if (paperDto != null) {
            try {
                LOGGER.info("savePaper  " + paperDto.getPaperId() + " testType " + paperDto.getTestType());
                paperService = paperFactory.getPaperService(paperDto.getTestType().toString());
                paperService.savePaper(paperDto);
                analyticsDataIngestionService.saveUserPaperStatFromPaperDto(paperDto);

            } catch (Exception ex) {
                LOGGER.error("savePaper  " + paperDto.getPaperId() + " testType " + paperDto.getTestType(), ex);
                throw ex;
            }
        }

    }

    @CrossOrigin
    @RequestMapping(value = "/v1/save", method = RequestMethod.POST)
    public void savePaperV1(@RequestBody PaperAesDto<String> paperAesDto) {

        if (paperAesDto != null && paperAesDto.getBody() != null) {
            PaperDto paperDto = null;
            try {
                AesEncDecUtil aesEncDecUtil = new AesEncDecUtil();
                String passPhressEnr = HashUtil.setPassphraseEnrichment(passphrase);
                LOGGER.info("passPhressEnr  " + passPhressEnr);
                String paperDtoStr = aesEncDecUtil.decrypt(salt, iv, passPhressEnr, paperAesDto.getBody());
                paperDto = objectMapper.readValue(paperDtoStr, PaperDto.class);
                if (paperDto != null) {
                    LOGGER.info("savePaper  " + paperDto.getPaperId() + " testType " + paperDto.getTestType());
                    paperService = paperFactory.getPaperService(paperDto.getTestType().toString());
                    paperService.savePaper(paperDto);
                    analyticsDataIngestionService.saveUserPaperStatFromPaperDto(paperDto);
                }
            } catch (Exception ex) {
                if (paperDto != null) {
                    LOGGER.error("savePaper  " + paperDto.getPaperId() + " testType " + paperDto.getTestType(), ex);
                } else {
                    LOGGER.error("[savePaperV1] Exception occurred while decryption", ex);
                    throw new RuntimeEUException("Exception occurred while decryption");

                }
            }
        } else {
            throw new IllegalArgumentException("Can't be null");
        }

    }

    @CrossOrigin
    @RequestMapping(value = "/{testType}/{paperId}", method = RequestMethod.GET)
    public List<PaperDto> getPaperByPaperId(@PathVariable(value = "paperId") final String paperId,
                                            @PathVariable(value = "testType") String testType) {

        try {
            LOGGER.info("getPaperByPaperId  " + paperId + " testType " + testType);
            paperService = paperFactory.getPaperService(testType);
            return paperService.getPaperByPaperId(paperId, TestType.valueOf(testType.toUpperCase()));

        } catch (Exception ex) {
            LOGGER.error("getPaperByPaperId  " + paperId + " testType " + testType, ex);
            throw ex;
        }

    }

    @CrossOrigin
    @RequestMapping(value = "/user/{testType}/{paperId}", method = RequestMethod.GET)
    public PaperDto getPaperByPaperIdAndUser(@PathVariable(value = "paperId") final String paperId,
                                             @PathVariable(value = "testType") String testType) {

        JwtUser jwtUser = null;
        try {
            jwtUser = userPrincipal.getCurrentUserDetails();
            LOGGER.info("getPaperByPaperIdAndUser  " + paperId + " testType " + testType + " UserId "
                    + jwtUser.getUsername());
            paperService = paperFactory.getPaperService(testType);
            return paperService.getPaperByPaperIdAndUserId(paperId, jwtUser.getUsername(),
                    TestType.valueOf(testType.toUpperCase()));

        } catch (Exception ex) {
            LOGGER.error("getPaperByPaperIdAndUser  " + paperId + " testType " + testType + " UserId "
                    + jwtUser.getUsername(), ex);
            throw ex;
        }

    }

    @CrossOrigin
    @RequestMapping(value = "/user/{testType}", method = RequestMethod.GET)
    public List<PaperDto> getPaperByUserAndPaperType(@PathVariable(value = "testType") String testType,
                                                     @RequestParam(value = "paperType") String paperType) {

        JwtUser jwtUser = null;
        try {
            jwtUser = userPrincipal.getCurrentUserDetails();
            LOGGER.info("getPaperByUser testType " + testType + " UserId " + jwtUser.getUsername());
            paperService = paperFactory.getPaperService(testType);
            return paperService.getPaperByUserIdAndPaperType(jwtUser.getUsername(),
                    PaperType.valueOf(paperType.toUpperCase()));

        } catch (Exception ex) {
            LOGGER.error("getPaperByUser testType " + testType + " UserId " + jwtUser.getUsername(), ex);
            throw ex;
        }

    }

    /**
     * service to mapped with user and saved collection to userPaper collection when
     * status is start other wise just return from userPaper
     *
     * @param testType
     * @param paperStatus
     * @param paperId
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/user/mapping/{testType}/{paperStatus}/{paperId}", method = RequestMethod.GET)
    public PaperDto getPaperAndUserByTestStatus(@PathVariable(value = "testType") String testType,
                                                @PathVariable(value = "paperStatus") String paperStatus, @PathVariable(value = "paperId") String paperId) {

        JwtUser jwtUser = null;
        try {
            jwtUser = userPrincipal.getCurrentUserDetails();
            LOGGER.info("getPaperByUser testType " + testType + " UserId " + jwtUser.getUsername());
            paperService = paperFactory.getPaperService(testType);
            return paperService.paperMappedUserByPaperStatus(jwtUser.getUsername(),
                    TestType.valueOf(testType.toUpperCase()), PaperStatus.valueOf(paperStatus.toUpperCase()), paperId);

        } catch (Exception ex) {
            LOGGER.error("getPaperAndUserByTestStatus testType " + testType + " UserId " + jwtUser.getUsername()
                    + "paperId " + paperId, ex);
            throw ex;
        }

    }

    /**
     * service to mapped with user and saved collection to userPaper collection when
     * status is start other wise just return from userPaper
     *
     * @param testType
     * @param paperStatus
     * @param paperId     encrypted paper
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/v1/user/mapping/{testType}/{paperStatus}/{paperId}", method = RequestMethod.GET)
    public Response<String> getPaperAndUserByTestStatusEnc(@PathVariable(value = "testType") String testType,
                                                           @PathVariable(value = "paperStatus") String paperStatus, @PathVariable(value = "paperId") String paperId) {

        JwtUser jwtUser = null;
        try {
            jwtUser = userPrincipal.getCurrentUserDetails();
            LOGGER.info("getPaperByUser testType " + testType + " UserId " + jwtUser.getUsername());
            paperService = paperFactory.getPaperService(testType);
            PaperDto paperDto = paperService.paperMappedUserByPaperStatus(jwtUser.getUsername(),
                    TestType.valueOf(testType.toUpperCase()), PaperStatus.valueOf(paperStatus.toUpperCase()), paperId);

            AesEncDecUtil aesEncDecUtil = new AesEncDecUtil();
            String passPhressEnr = HashUtil.setPassphraseEnrichment(passphrase);
            LOGGER.info("passPhressEnr  " + passPhressEnr);
            String paperSdoStr = objectMapper.writeValueAsString(paperDto);
            String paperDtoEncStr = aesEncDecUtil.encrypt(salt, iv, passPhressEnr, paperSdoStr);

            return new Response<String>().setBody(paperDtoEncStr).setStatus(200).setMessage("Success");

        } catch (Exception ex) {
            LOGGER.error("getPaperAndUserByTestStatus testType " + testType + " UserId " + jwtUser.getUsername()
                    + "paperId " + paperId, ex);
        }
        return null;

    }

    /**
     * Admin use only..
     *
     * @param paperId
     * @param userId
     * @param testType
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/user/{testType}/{paperId}/{userId}", method = RequestMethod.GET)
    public PaperDto getPaperByPaperIdAndUserId(@PathVariable(value = "paperId") final String paperId,
                                               @PathVariable(value = "userId") final String userId, @PathVariable(value = "testType") String testType) {

        try {
            LOGGER.info("getPaperByPaperIdAndUserId  " + paperId + " testType " + testType + " UserId " + userId);
            paperService = paperFactory.getPaperService(testType);
            return paperService.getPaperByPaperIdAndUserId(paperId, userId, TestType.valueOf(testType.toUpperCase()));

        } catch (Exception ex) {
            LOGGER.error("getPaperByPaperIdAndUserId  " + paperId + " testType " + testType + " UserId " + userId, ex);
            throw ex;
        }

    }

    /**
     * it is used for getting for all status paper by paperId..
     *
     * @param testType
     * @param paperIds
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "user/list/{testType}", method = RequestMethod.GET)
    public List<PaperDto> getUserPapereTestStatusByIds(@PathVariable(value = "testType") String testType,
                                                       @RequestParam(value = "paperIds") List<String> paperIds) {

        JwtUser jwtUser = null;
        try {
            jwtUser = userPrincipal.getCurrentUserDetails();
            LOGGER.info("getUserPapereTestStatusByIds testType " + testType + " UserId " + jwtUser.getUsername());
            paperService = paperFactory.getPaperService(testType);
            return paperService.getPaperStatusDetailsByPaperIds(jwtUser.getUsername(), paperIds,
                    TestType.valueOf(testType.toUpperCase()));

        } catch (Exception ex) {
            LOGGER.error("getUserPapereTestStatusByIds  testType " + testType + " UserId " + jwtUser.getUsername(), ex);
        }
        return null;

    }

    /**
     * it is used for getting all paper(Free and Paid)
     *
     * @param testType
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "user/list/paperType/{paperType}", method = RequestMethod.GET)
    public List<PaperDto> getUserPapereTestStatus(@PathVariable(value = "paperType") String paperType,
                                                  @RequestParam(value = "paperCategory", required = true) String paperCategory,
                                                  @RequestParam(value = "testType", required = false) String testType) {

        JwtUser jwtUser = null;
        List<PaperDto> result = null;
        try {
            jwtUser = userPrincipal.getCurrentUserDetails();
            LOGGER.info("getUserPapereTestStatus testType " + testType + " UserId " + jwtUser.getUsername());
            PaperCategory paperCate = paperCategory != null ? PaperCategory.valueOfByAnyString(paperCategory) : null;
            if (testType != null && !testType.isEmpty()) {
                paperService = paperFactory.getPaperService(testType);
                return paperService.getPaperStatusDetailsByPaperCateoryORTestType(jwtUser.getUsername(),
                        TestType.valueOf(testType.toUpperCase()), PaperType.valueOf(paperType.toUpperCase()), paperCate);
            } else {
                // Get FREE papers based on user's FREE_SUBSCRIPTION entitlements
                paperService = paperFactory.getPaperService(TestType.FREE.toString());
                List<PaperDto> paperDtoFree = paperService.getPaperStatusDetailsByPaperCateoryORTestType(
                        jwtUser.getUsername(), TestType.FREE, PaperType.valueOf(paperType.toUpperCase()), paperCate);

                // Get PAID papers based on user's SUBSCRIPTION entitlements
                paperService = paperFactory.getPaperService(TestType.PAID.toString());
                List<PaperDto> paperDtoPaid = paperService.getPaperStatusDetailsByPaperCateoryORTestType(
                        jwtUser.getUsername(), TestType.PAID, PaperType.valueOf(paperType.toUpperCase()), paperCate);

                if (paperDtoPaid != null && paperDtoFree != null) {
                    result = Stream.of(paperDtoPaid, paperDtoFree).flatMap(x -> x.stream())
                            .collect(Collectors.toList());
                } else if (paperDtoPaid != null) {
                    return paperDtoPaid;
                } else if (paperDtoFree != null) {
                    return paperDtoFree;
                } else {
                    return null;
                }

                return result;

            }

        } catch (Exception ex) {
            LOGGER.error("getUserPapereTestStatusByIds  testType " + testType + " UserId " + jwtUser.getUsername(), ex);
        }

        return null;

    }

    /**
     * @param paperType
     * @param paperStatus
     * @param testType
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "user/paperType/{paperType}", method = RequestMethod.GET)
    public List<PaperDto> getUserPapereByStatus(@PathVariable(value = "paperType") String paperType,
                                                @RequestParam(value = "paperStatus", required = true) String paperStatus,
                                                @RequestParam(value = "paperCategory", required = false) String paperCategory,
                                                @RequestParam(value = "testType", required = false) String testType) {

        JwtUser jwtUser = null;
        List<PaperDto> result = null;
        try {
            jwtUser = userPrincipal.getCurrentUserDetails();
            LOGGER.info("getUserPapereByStatus testType " + testType + " UserId " + jwtUser.getUsername());

            PaperCategory paperCate = paperCategory != null ? PaperCategory.valueOfByAnyString(paperCategory) : null;
            if (testType != null && !testType.isEmpty()) {
                paperService = paperFactory.getPaperService(testType);
                return paperService.getPaperStatusDetailsByStatusAndPaperType(jwtUser.getUsername(),
                        TestType.valueOf(testType.toUpperCase()), PaperType.valueOf(paperType.toUpperCase()),
                        PaperStatus.valueOf(paperStatus.toUpperCase()), paperCate);

            } else {
                paperService = paperFactory.getPaperService(TestType.FREE.toString());
                List<PaperDto> paperDtoFree = paperService.getPaperStatusDetailsByStatusAndPaperType(
                        jwtUser.getUsername(), TestType.FREE, PaperType.valueOf(paperType.toUpperCase()),
                        PaperStatus.valueOf(paperStatus.toUpperCase()), paperCate);
                paperService = paperFactory.getPaperService(TestType.PAID.toString());
                List<PaperDto> paperDtoPaid = paperService.getPaperStatusDetailsByStatusAndPaperType(
                        jwtUser.getUsername(), TestType.PAID, PaperType.valueOf(paperType.toUpperCase()),
                        PaperStatus.valueOf(paperStatus.toUpperCase()), paperCate);

                if (paperDtoPaid != null && paperDtoFree != null) {
                    result = Stream.of(paperDtoPaid, paperDtoFree).flatMap(x -> x.stream())
                            .collect(Collectors.toList());
                } else if (paperDtoPaid != null) {
                    return paperDtoPaid;
                } else if (paperDtoFree != null) {
                    return paperDtoFree;
                } else {
                    return null;
                }

                return result;

            }

        } catch (Exception ex) {
            LOGGER.error("getUserPapereByStatus  testType " + testType + " UserId " + jwtUser.getUsername(), ex);
        }
        return null;

    }

    // MissedPaper paper list on analysis
    @CrossOrigin
    @RequestMapping(value = "user/missed/paperType/{paperType}", method = RequestMethod.GET)
    public List<PaperDto> getUserMissedPapers(@PathVariable(value = "paperType") String paperType,
                                              @RequestParam(value = "testType", required = true) String testType,
                                              @RequestParam(value = "paperCategory", required = false) String paperCategory) {

        JwtUser jwtUser = null;
        try {
            jwtUser = userPrincipal.getCurrentUserDetails();
            LOGGER.info("getUserMissedPaperes  testType " + testType + " UserId " + jwtUser.getUsername());
            paperService = paperFactory.getPaperService(testType);

            PaperCategory paperCategory2 = paperCategory != null ? PaperCategory.valueOf(paperCategory) : null;

            return paperService.getMissedPapersByUsers(jwtUser.getUsername(), TestType.valueOf(testType),
                    PaperType.valueOf(paperType), paperCategory2);

        } catch (Exception ex) {
            LOGGER.error("getUserPapereTestStatusByIds  testType " + testType + " UserId " + jwtUser.getUsername(), ex);
        }
        return null;

    }

    @CrossOrigin
    @RequestMapping(value = "/count/{testType}", method = RequestMethod.GET)
    public long getPaperCount(@PathVariable(value = "testType", required = true) String testType,
                              @RequestParam(value = "paperType", required = true) String paperType,
                              @RequestParam(value = "paperCategory", required = false) String paperCategory,
                              @RequestParam(value = "paperSubCategory", required = false) String paperSubCategory) {
        try {
            LOGGER.info("getPaperCount testType " + testType + " paperType " + paperType + " paperCategory "
                    + paperCategory + " paperSubCategory " + paperSubCategory);
            paperService = paperFactory.getPaperService(testType);
            if (paperCategory != null && paperSubCategory != null) {
                return paperService.getPaperCountByPaperTypeAndTestTypeAndPaperCategoryAndPaperSubCategory(
                        PaperType.valueOf(paperType), TestType.valueOf(testType), PaperCategory.valueOf(paperCategory),
                        PaperSubCategory.valueOf(paperSubCategory));
            } else if (paperCategory != null) {
                return paperService.getPaperCountByPaperTypeAndTestTypeAndPaperCategory(PaperType.valueOf(paperType),
                        TestType.valueOf(testType), PaperCategory.valueOf(paperCategory));
            } else {
                return paperService.getPaperCountByPaperTypeAndTestType(PaperType.valueOf(paperType),
                        TestType.valueOf(testType));
            }

        } catch (Exception ex) {
            LOGGER.error("getPaperCount testType " + testType + " paperType " + paperType + " paperCategory "
                    + paperCategory + " paperSubCategory " + paperSubCategory, ex);
        }
        return 0;
    }

    @CrossOrigin
    @RequestMapping(value = "/v1/dec", method = RequestMethod.POST)
    public PaperDto getDecPaperDto(
            @RequestParam(value = "passphreaseV", required = false) String passphreaseV,
            @RequestParam(value = "saltV", required = false) String saltV,
            @RequestParam(value = "ivV", required = false) String ivV,
            @RequestBody PaperAesDto<String> paperAesDto) {
        PaperDto paperDto = null;
        try {
            if (ivV != null) {
                iv = ivV;
            }
            if (saltV != null) {
                salt = saltV;
            }
            if (passphreaseV != null) {
                passphrase = passphreaseV;
            } else {
                passphrase = HashUtil.setPassphraseEnrichment(passphrase);
            }

            LOGGER.info("getDecPaperDto salt " + salt + " iv" + iv + " passphrase " + passphrase);
            AesEncDecUtil aesEncDecUtil = new AesEncDecUtil();
            String paperDtoStr = aesEncDecUtil.decrypt(salt, iv, passphrase, paperAesDto.getBody());
            LOGGER.info("paperDtoStr  " + paperDtoStr);
            paperDto = objectMapper.readValue(paperDtoStr, PaperDto.class);
            return paperDto;

        } catch (Exception ex) {
            LOGGER.error("getDecPaperDto salt " + salt + " iv " + iv + " passphrease " + passphreaseV, ex);
            ex.printStackTrace();
        }
        return null;
    }

    @CrossOrigin
    @RequestMapping(value = "/v1/enc", method = RequestMethod.POST)
    public Response<String> getEncPaperDto(@RequestParam(value = "passphreaseV", required = false) String passphreaseV,
                                           @RequestParam(value = "saltV", required = false) String saltV,
                                           @RequestParam(value = "ivV", required = false) String ivV,
                                           @RequestBody PaperDto paperDto) {

        try {
            LOGGER.info("getEncPaperDto salt " + salt + " iv" + iv + " passphreaseV " + passphreaseV);

            if (ivV != null) {
                iv = ivV;
            }
            if (saltV != null) {
                salt = saltV;
            }
            if (passphreaseV != null) {
                passphrase = passphreaseV;
            } else {
                passphrase = HashUtil.setPassphraseEnrichment(passphrase);
            }
            AesEncDecUtil aesEncDecUtil = new AesEncDecUtil();
            LOGGER.info("passPhressEnr  " + passphrase);
            String paperSdoStr = objectMapper.writeValueAsString(paperDto);
            String paperDtoEncStr = aesEncDecUtil.encrypt(salt, iv, passphrase, paperSdoStr);
            return new Response<String>().setBody(paperDtoEncStr).setStatus(200).setMessage("Success");

        } catch (Exception ex) {
            LOGGER.error("getEncPaperDto salt " + salt + " iv " + iv + " passphrease " + passphreaseV, ex);
            ex.printStackTrace();
        }
        return null;
    }

}
