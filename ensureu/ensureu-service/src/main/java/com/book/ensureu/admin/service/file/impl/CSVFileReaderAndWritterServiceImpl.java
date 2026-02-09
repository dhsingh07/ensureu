package com.book.ensureu.admin.service.file.impl;

import java.io.File;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.book.ensureu.admin.service.Impl.SSCPaperAggregatorServiceImpl;
import com.book.ensureu.admin.service.file.FileReaderAndWritterService;
import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.SectionType;
import com.book.ensureu.dto.CsvBean;
import com.book.ensureu.dto.CsvColumnBean;
import com.book.ensureu.model.PaidPaperCollection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;

@Service
public class CSVFileReaderAndWritterServiceImpl implements FileReaderAndWritterService {

	private final static Logger LOGGER = LoggerFactory.getLogger(CSVFileReaderAndWritterServiceImpl.class.getName());

	@Value("${spring.file.csv}")
	private String csvFolderPath;

	@Override
	public <T> List<T> readFile(String file, T clazz) {

		LOGGER.info("input file " + file);
		Reader reader = null;
		List<Class> cl = null;

		List<CsvBean> csvColl = null;

		try {

			if (clazz instanceof CsvBean) {

				// Path path = Paths.get(ClassLoader.getSystemResource(file).toURI());
				File fileR = new File(file);
				reader = Files.newBufferedReader(fileR.toPath());

				List<String[]> listVal = readAll(reader);
				csvColl = new LinkedList<>();
				LOGGER.info("CSV FIle row Size : {}", listVal.size());
				for (int i = 1; i < listVal.size(); i++) {
					String[] str = listVal.get(i);
					LOGGER.info("CSV FIle Column Size : {}" , str.length);
					CsvBean csvBean = new CsvColumnBean();
					csvBean.setPaperType(PaperType.valueOf(str[0].toString()));
					csvBean.setPaperCategory(PaperCategory.valueOf(str[1].toString()));
					csvBean.setPaperSubCategory(PaperSubCategory.valueOf(str[2].toString()));
					CsvColumnBean csvColumnBean = (CsvColumnBean) csvBean;

					csvColumnBean.setPaperName(str[3]); 
					LOGGER.info("CSV Column sectionName :{} ", str[4]);
					csvColumnBean.setSectionName(str[4]);
					csvColumnBean.setSectionType(SectionType.valueOf(str[5]));
					LOGGER.info("CSV Column subSectionName : {}" , str[6]);
					System.out.println("subSectionName--> " + str[6]);
					csvColumnBean.setSubSectionName(str[6]);
					LOGGER.info("CSV Column QuestionNumber : {}" , Integer.valueOf(str[7]));
					csvColumnBean.setQuestionNumber(Integer.valueOf(str[7]));
					csvColumnBean.setQuestion(str[8]);
					LOGGER.info("CSV Column QuestionImage : {}", str[9]);
					csvColumnBean.setQuestionImage(str[9]);
					csvColumnBean.setOption1(str[10]);
					csvColumnBean.setOption1_Image(str[11]);
					csvColumnBean.setOption2(str[12]);
					csvColumnBean.setOption2_Image(str[13]);
					csvColumnBean.setOption3(str[14]);
					csvColumnBean.setOption3_Image(str[15]);
					csvColumnBean.setOption4(str[16]);
					csvColumnBean.setOption4_Image(str[17]);
					csvColumnBean.setCorrectOption(Integer.valueOf(str[18]));
					csvColumnBean.setAnswerDescription1(str[19]);
					csvColumnBean.setAnswerDescription2(str[20]);
					csvColumnBean.setAnswerDescriptionImage(str[21]);
					csvColumnBean.setComplexityLevel(str[22]);
					
					if(str[23]!=null && !str[23].isEmpty()) {
						csvColumnBean.setComplexityScore(Integer.valueOf(str[23]));
					}else {
						csvColumnBean.setComplexityScore(0);
					}
					
					csvColumnBean.setType(str[24]);
					csvColl.add(csvBean);

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return (List<T>) csvColl;

	}

	@Override
	public void writeFile(String path) {

	}

	public static void main(String arg[]) throws URISyntaxException, JsonProcessingException {
		CSVFileReaderAndWritterServiceImpl abc = new CSVFileReaderAndWritterServiceImpl();
		String file = "C://eutest/csv/sscCglTier1Paper-2.csv";
		// Path path =
		// Paths.get(ClassLoader.getSystemResource("csv/SSCPapersTier1.csv").toURI());
		List<CsvBean> csvBeanData = abc.readFile(file, new CsvBean());
		SSCPaperAggregatorServiceImpl testApp = new SSCPaperAggregatorServiceImpl();
		PaidPaperCollection paidPaperCollection = testApp.converCsvBeanToPaperCollection(csvBeanData);
		ObjectMapper objectMapper = new ObjectMapper();

		String val = objectMapper.writeValueAsString(paidPaperCollection);
		System.out.println(val);

	}

	public List<String[]> readAll(Reader reader) throws Exception {
		CSVReader csvReader = new CSVReader(reader);
		List<String[]> list = new ArrayList<>();
		list = csvReader.readAll();
		reader.close();
		csvReader.close();
		return list;
	}

	// read file from folder and create paper...
	@Override
	public <T> List<T> readFile(T clazz) {
		return null;
	}
}
