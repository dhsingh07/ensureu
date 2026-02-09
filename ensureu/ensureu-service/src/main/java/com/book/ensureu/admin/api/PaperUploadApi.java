package com.book.ensureu.admin.api;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.book.ensureu.admin.service.PaidPaperCollectionService;
import com.book.ensureu.admin.service.file.FileReaderAndWritterService;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.dto.CsvBean;
import com.book.ensureu.service.PaperAggregatorService;
import com.ensureu.commons.gcloud.util.GoogleCloudStorageUtil;

@RestController
@RequestMapping("/admin/upload")
public class PaperUploadApi {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(PaperUploadApi.class);

	@Autowired
	PaidPaperCollectionService testPaperCollectionService;

	@Autowired
	public PaperAggregatorService<CsvBean> paperAggregatorService;
	
	@Autowired
	FileReaderAndWritterService CsvFileReaderAndWritterService;

	@Value("${spring.static.image}")
	private String staticContentLoc;

	@Value("${spring.file.csv}")
	private String csvFileLoc;
	
	@Value("${spring.free.file.csv}")
	private String csvFreeFileLoc;
	

	@CrossOrigin
	@RequestMapping(value = "/create/paid", method = RequestMethod.POST)
	public void createTestPaper() {
		LOGGER.info("createPaidPaper ===");
		try {
			List<CsvBean> csvBeanList = null;
			File file = new File(csvFileLoc);
			String pathValue = file.getPath();
			LOGGER.info("FolderPath " + pathValue);
			fileArchive(pathValue);
			File newFolderFile = new File(pathValue);
			for (File newFolderFileVal : newFolderFile.listFiles()) {
				if (newFolderFileVal.getName().endsWith(".csv")) {
					LOGGER.info("File processing.... " + newFolderFileVal.getName());
					try {
						csvBeanList = CsvFileReaderAndWritterService
								.readFile(pathValue + "/" + newFolderFileVal.getName(), new CsvBean());
						paperAggregatorService.createPaidPaper(csvBeanList, pathValue,staticContentLoc);
						Files.move(Paths.get(pathValue + "/" + newFolderFileVal.getName()),
								Paths.get(pathValue + "/archive", newFolderFileVal.getName()),
								StandardCopyOption.REPLACE_EXISTING);
						LOGGER.info("File processing Finish.... " + newFolderFileVal.getName());
					} catch (Exception e) {
						LOGGER.error("Error while paid paper {}", e);
					}
				}
			}

		} catch (Exception ex) {
			LOGGER.error("createTestPaper ", ex);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/create/free", method = RequestMethod.POST)
	public void createFreedPaper() {
		LOGGER.info("createTestPaper ===Free ");
		try {
			List<CsvBean> csvBeanList = null;
			File file = new File(csvFreeFileLoc);
			String pathValue = file.getPath();
			LOGGER.info("FolderPath " + pathValue);
			fileArchive(pathValue);
			File newFolderFile = new File(pathValue);
			for (File newFolderFileVal : newFolderFile.listFiles()) {
				if (newFolderFileVal.isDirectory()) {
					continue;
				}
				if (newFolderFileVal.getName().endsWith(".csv")) {
					LOGGER.info("File processing.... " + newFolderFileVal.getName());
					try {
						csvBeanList = CsvFileReaderAndWritterService
								.readFile(pathValue + "/" + newFolderFileVal.getName(), new CsvBean());
						paperAggregatorService.createFreeTypePaper(csvBeanList, TestType.FREE, pathValue,staticContentLoc);
						Files.move(Paths.get(pathValue + "/" + newFolderFileVal.getName()),
								Paths.get(pathValue + "/archive", newFolderFileVal.getName()),
								StandardCopyOption.REPLACE_EXISTING);
						LOGGER.info("File processing Finish.... " + newFolderFileVal.getName());
					} catch (Exception e) {
						LOGGER.error("While process Free paper ", e);
					}
				}
			}
		} catch (Exception ex) {
			LOGGER.error("createTestPaper ", ex);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/create/{testType}", method = RequestMethod.POST)
	public void createPaper(@PathVariable(value = "testType") String testType,
			@RequestParam(value = "path") String path,@RequestParam(value="imagePath") String imagePath) {
		LOGGER.info("Start Processing "+ testType+ " path "+path +" imagePath "+imagePath);
		try {
			List<CsvBean> csvBeanList = null;
			LOGGER.info("FolderPath " + path);
			fileArchive(path);
			File newFolderFile = new File(path);
			for (File newFolderFileVal : newFolderFile.listFiles()) {
				if (newFolderFileVal.isDirectory()) {
					continue;
				}
				if (newFolderFileVal.getName().endsWith(".csv")) {
					LOGGER.info("File processing.... " + newFolderFileVal.getName());
					try {
						csvBeanList = CsvFileReaderAndWritterService.readFile(path + "/" + newFolderFileVal.getName(),
								new CsvBean());
						switch (testType.toUpperCase()) {
						case "FREE":
							paperAggregatorService.createFreeTypePaper(csvBeanList,
									TestType.valueOf(testType.toUpperCase()), path, imagePath);
							break;
						case "PAID":
							paperAggregatorService.createPaidPaper(csvBeanList, path,imagePath);
							break;
						case "PRACTICE":
							paperAggregatorService.createPrecticePaper(csvBeanList,
									TestType.valueOf(testType.toUpperCase()), path,imagePath);
							break;
						case "QUIZ":
							paperAggregatorService.createQuizPaper(csvBeanList,
									TestType.valueOf(testType.toUpperCase()), path, imagePath);
							break;
						case "PASTPAPER":
							paperAggregatorService.createPastPaper(csvBeanList,
									TestType.valueOf(testType.toUpperCase()), path, imagePath);
							break;
						default:
							throw new IllegalArgumentException("TestType is not valid");
						}

						Files.move(Paths.get(path + "/" + newFolderFileVal.getName()),
								Paths.get(path + "/archive", newFolderFileVal.getName()),
								StandardCopyOption.REPLACE_EXISTING);
						LOGGER.info("File processing Finish.... " + newFolderFileVal.getName());

					} catch (Exception e) {
                     LOGGER.error("Error while Reading file ",e);
					}
				}
			}
		} catch (Exception ex) {
			LOGGER.error("createFreePaper ", ex);
		}
	}
	
	private void fileArchive(String csvLoc) {

		File archiveDir = new File(csvLoc + "/archive");
		if (!archiveDir.exists()) {
			boolean dir = archiveDir.mkdirs();
			LOGGER.info("Archive dir created : " + dir);
		}
	}

}
