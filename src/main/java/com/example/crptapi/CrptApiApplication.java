package com.example.crptapi;

import com.example.crptapi.demo.CrptApi;
import com.example.crptapi.entity.Description;
import com.example.crptapi.entity.Document;
import com.example.crptapi.entity.Product;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class CrptApiApplication implements CommandLineRunner {

    private final CrptApi crptApi;

    public CrptApiApplication() {

        this.crptApi = new CrptApi(TimeUnit.MINUTES, 10, "string");
    }

    public static void main(String[] args) {
        SpringApplication.run(CrptApiApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        Document document = new Document();
        document.setDescription(new Description());
        document.getDescription().setParticipantInn("1234567890");
        document.setDocId("DOC123456");
        document.setDocStatus("Draft");
        document.setDocType("LP_INTRODUCE_GOODS");
        document.setImportRequest(true);
        document.setOwnerInn("0987654321");
        document.setParticipantInn("1234567890");
        document.setProducerInn("1122334455");
        document.setProductionDate("2024-08-16");
        document.setProductionType("ExampleType");
        document.setProducts(new Product[] {
                new Product() {{
                    setCertificateDocument("CertDoc001");
                    setCertificateDocumentDate("2024-08-16");
                    setCertificateDocumentNumber("Cert001");
                    setOwnerInn("0987654321");
                    setProducerInn("1122334455");
                    setProductionDate("2024-08-16");
                    setTnvedCode("1234");
                    setUitCode("5678");
                    setUituCode("9101");
                }}
        });
        document.setRegDate("2024-08-16");
        document.setRegNumber("Reg001");


        crptApi.createDocument(document);
    }

}
