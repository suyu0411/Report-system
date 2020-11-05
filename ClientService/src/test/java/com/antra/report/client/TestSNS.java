package com.antra.report.client;

import com.antra.report.client.entity.ReportRequestEntity;
import com.antra.report.client.pojo.request.ReportRequest;
import com.antra.report.client.repository.ReportRequestRepo;
import com.antra.report.client.service.ReportService;
import com.antra.report.client.service.SNSService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.mockito.Mockito;
import org.springframework.util.Assert;

import java.util.List;

import static io.restassured.RestAssured.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestSNS {

    @Autowired
    SNSService snsService;

    @Autowired
    ReportService reportService;

    @Autowired
    ReportRequestRepo reportRequestRepo;


    @Value("http://localhost:8080}/report")
    private String REST_SERVICE_URI ;

    @Test
    public void testReportService() {
        ReportRequest request = new ReportRequest();
        String submitter = "YuSuuuuu";
        String description = "This is just a test1";
        request.setSubmitter(submitter);
        request.setDescription(description);
        request.setHeaders(List.of("Id","Name","Age"));
        request.setData(List.of(List.of("1","Dd","23"),List.of("2","AJ","32")));
        reportService.generateReportsAsync(request);

//        try {
//            Thread.sleep(8000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        Mockito.when(reportRequestRepo.findDistinctBySubmitter(submitter).getDescription()).equals(description);
//        when().
//                get(REST_SERVICE_URI).peek().
//                then().assertThat()
//                .statusCode(200)
//                .body("submitter",Matchers.equalTo(submitter));



    }
    @Test
    public void testSNSSend() {
        ReportRequest request = new ReportRequest();
        request.setSubmitter("YuSu_test");
        request.setDescription("This is just a test");
        request.setHeaders(List.of("Id","Name","Age"));
        request.setData(List.of(List.of("1","Dd","23"),List.of("2","AJ","32")));
        snsService.sendReportNotification(request);
    }
}
