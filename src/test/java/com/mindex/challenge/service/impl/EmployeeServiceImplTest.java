package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
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
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String employeeReadReportingStructureUrl;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        employeeReadReportingStructureUrl = "http://localhost:" + port + "/employee/{id}/reporting";
    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);


        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);


        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }

    @Test
    public void testReadReportingStructure() {
        Employee randal = new Employee();
        randal.setFirstName("Randal");
        randal.setLastName("Lindsey");
        randal.setDepartment("Engineering");
        randal.setPosition("Developer");

        randal = restTemplate.postForEntity(employeeUrl, randal, Employee.class).getBody();
        assertNotNull(randal);

        Employee lee = new Employee();
        lee.setFirstName("Lee");
        lee.setLastName("Chapman");
        lee.setDepartment("Engineering");
        lee.setPosition("Manager");
        lee.setDirectReports(List.of(randal));

        lee = restTemplate.postForEntity(employeeUrl, lee, Employee.class).getBody();
        assertNotNull(lee);

        Employee harriet = new Employee();
        harriet.setFirstName("Harriet");
        harriet.setLastName("Austin");
        harriet.setDepartment("Management");
        harriet.setPosition("CEO");
        harriet.setDirectReports(List.of(lee));

        harriet = restTemplate.postForEntity(employeeUrl, harriet, Employee.class).getBody();
        assertNotNull(harriet);

        // Test no reports.
        ReportingStructure structure = restTemplate.getForObject(employeeReadReportingStructureUrl, ReportingStructure.class, randal.getEmployeeId());
        assertEquals(randal.getEmployeeId(), structure.getEmployee().getEmployeeId());
        assertEquals(0, structure.getNumberOfReports());

        // Test one direct report.
        structure = restTemplate.getForObject(employeeReadReportingStructureUrl, ReportingStructure.class, lee.getEmployeeId());
        assertEquals(lee.getEmployeeId(), structure.getEmployee().getEmployeeId());
        assertEquals(1, structure.getNumberOfReports());

        // Test one direct report and one indirect report.
        structure = restTemplate.getForObject(employeeReadReportingStructureUrl, ReportingStructure.class, harriet.getEmployeeId());
        assertEquals(harriet.getEmployeeId(), structure.getEmployee().getEmployeeId());
        assertEquals(2, structure.getNumberOfReports());
    }


    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }
}
