package com.antra.evaluation.reporting_system.service;

import com.antra.evaluation.reporting_system.exception.FileGenerationException;
import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataHeader;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataSheet;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import com.antra.evaluation.reporting_system.repo.ExcelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.amazonaws.services.s3.AmazonS3;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExcelServiceImpl implements ExcelService {

    private static final Logger log = LoggerFactory.getLogger(ExcelServiceImpl.class);

    ExcelRepository excelRepository;

    private ExcelGenerationService excelGenerationService;
    private final AmazonS3 s3Client;

    @Value("${s3.bucket}")
    private String s3Bucket;

    @Autowired
    public ExcelServiceImpl(ExcelRepository excelRepository, ExcelGenerationService excelGenerationService,AmazonS3 s3Client) {
        this.excelRepository = excelRepository;
        this.excelGenerationService = excelGenerationService;
        this.s3Client = s3Client;
    }

    @Override
    public InputStream getExcelBodyById(String id) throws FileNotFoundException {
        Optional<ExcelFile> fileInfo = excelRepository.getFileById(id);
        return new FileInputStream(fileInfo.orElseThrow(FileNotFoundException::new).getFileLocation());
    }

    @Override
    public ExcelFile generateFile(ExcelRequest request, boolean multisheet) {
//        PDFFile file = new PDFFile();
//        file.setId("File-" + UUID.randomUUID().toString());
//        file.setSubmitter(request.getSubmitter());
//        file.setDescription(request.getDescription());
//        file.setGeneratedTime(LocalDateTime.now());
//
//        PDFFile generatedFile= generator.generate(request);
//
//        File temp = new File(generatedFile.getFileLocation());
//        log.debug("Upload temp file to s3 {}", generatedFile.getFileLocation());
//        s3Client.putObject(s3Bucket,file.getId(),temp);
//        log.debug("Uploaded");
//
//        file.setFileLocation(String.join("/",s3Bucket,file.getId()));
//        file.setFileSize(generatedFile.getFileSize());
//        file.setFileName(generatedFile.getFileName());
//        log.debug("Upload temp file to s3 {}", generatedFile.getFileLocation());
//        s3Client.putObject(s3Bucket,file.getId(),temp);
//        log.debug("Uploaded");
        ExcelFile fileInfo = new ExcelFile();
        fileInfo.setFileId(UUID.randomUUID().toString());
        ExcelData data = new ExcelData();
        data.setTitle(request.getDescription());
        data.setFileId(fileInfo.getFileId());
        data.setSubmitter(fileInfo.getSubmitter());

        if(multisheet){
            data.setSheets(generateMultiSheet(request));
        }else {
            data.setSheets(generateSheet(request));
        }
        try {

            File generatedFile = excelGenerationService.generateExcelReport(data);
            File file = new File(generatedFile.getAbsolutePath());
            System.out.println("****generatedFile*****: "+ generatedFile);
            fileInfo.setGeneratedTime(LocalDateTime.now());
            fileInfo.setSubmitter(request.getSubmitter());
            fileInfo.setDescription(request.getDescription());

            log.debug("Upload temp file to s3 {}", generatedFile.getAbsolutePath());
            s3Client.putObject(s3Bucket,fileInfo.getFileId(), file);
            log.debug("Uploaded");

            fileInfo.setFileSize(generatedFile.length());
            fileInfo.setFileName(generatedFile.getName());
            fileInfo.setFileLocation(String.join("/",s3Bucket,fileInfo.getFileId()));
        } catch (IOException e) {
            log.error("Error in generateFile()", e);
            throw new FileGenerationException(e);
        }
        excelRepository.saveFile(fileInfo);
        log.debug("Excel File Generated : {}", fileInfo);
        return fileInfo;

    }

    @Override
    public List<ExcelFile> getExcelList() {
        return excelRepository.getFiles();
    }

    @Override
    public ExcelFile deleteFile(String id) throws FileNotFoundException {
        ExcelFile excelFile = excelRepository.deleteFile(id);
        if (excelFile == null) {
            throw new FileNotFoundException();
        }
        File file = new File(excelFile.getFileLocation());
        file.delete();
        return excelFile;
    }

    private List<ExcelDataSheet> generateSheet(ExcelRequest request) {
        List<ExcelDataSheet> sheets = new ArrayList<>();
        ExcelDataSheet sheet = new ExcelDataSheet();
        sheet.setHeaders(request.getHeaders().stream().map(ExcelDataHeader::new).collect(Collectors.toList()));
        sheet.setDataRows(request.getData().stream().map(listOfString -> (List<Object>) new ArrayList<Object>(listOfString)).collect(Collectors.toList()));
        sheet.setTitle("sheet-1");
        sheets.add(sheet);
        return sheets;
    }
    private List<ExcelDataSheet> generateMultiSheet(ExcelRequest request) {
        List<ExcelDataSheet> sheets = new ArrayList<>();
        int index = request.getHeaders().indexOf(((MultiSheetExcelRequest) request).getSplitBy());
        Map<String, List<List<String>>> splittedData = request.getData().stream().collect(Collectors.groupingBy(row -> (String)row.get(index)));
        List<ExcelDataHeader> headers = request.getHeaders().stream().map(ExcelDataHeader::new).collect(Collectors.toList());
        splittedData.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(
                entry ->{
                    ExcelDataSheet sheet = new ExcelDataSheet();
                    sheet.setHeaders(headers);
                    sheet.setDataRows(entry.getValue().stream().map(listOfString -> {
                        List<Object> listOfObject = new ArrayList<>();
                        listOfString.forEach(listOfObject::add);
                        return listOfObject;
                    }).collect(Collectors.toList()));
                    sheet.setTitle(entry.getKey());
                    sheets.add(sheet);
                }
        );
        return sheets;
    }
}
