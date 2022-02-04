package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.CompensationService;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {

    private String postCompensationUrl;
    private String getCompensationUrl;

    @Autowired
    private CompensationService compensationService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        postCompensationUrl = "http://localhost:" + port + "/compensation";
        getCompensationUrl = "http://localhost:" + port + "/compensation/{id}";
    }

    @Test
    public void testCreateRead() {
        Compensation compensation = new Compensation();
        compensation.setEmployeeId("37dd8233-7c33-44eb-8c52-d8e661615841");
        compensation.setSalary("1000000");
        compensation.setEffectiveDate("2022-04-02");

        // Create checks
        Compensation postResult = restTemplate.postForObject(postCompensationUrl, compensation, Compensation.class);

        assertNotNull(postResult);
        assertNotNull(postResult.getCompensationId());
        assertEquals(compensation.getEmployeeId(), postResult.getEmployeeId());
        assertEquals(compensation.getSalary(), postResult.getSalary());
        assertEquals(compensation.getEffectiveDate(), postResult.getEffectiveDate());

        // Read checks
        Compensation getResult = restTemplate.getForObject(getCompensationUrl, Compensation.class, postResult.getCompensationId());
        assertNotNull(getResult);
        assertNotNull(getResult.getCompensationId());
        assertEquals(compensation.getEmployeeId(), getResult.getEmployeeId());
        assertEquals(compensation.getSalary(), getResult.getSalary());
        assertEquals(compensation.getEffectiveDate(), getResult.getEffectiveDate());
    }
}
