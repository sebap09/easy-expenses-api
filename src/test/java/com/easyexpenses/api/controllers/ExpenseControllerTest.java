package com.easyexpenses.api.controllers;

import com.easyexpenses.api.builders.ExpenseFixtureBuilder;
import com.easyexpenses.api.dtos.AddNewExpenseRequest;
import com.easyexpenses.api.dtos.ExpenseResponse;
import com.easyexpenses.api.entities.UserProfile;
import com.easyexpenses.api.errors.ResourceNotFoundException;
import com.easyexpenses.api.fixtures.ExpenseFixture;
import com.easyexpenses.api.services.ExpenseService;
import com.easyexpenses.api.services.UserProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ExpenseController.class)
@Import(TestSecurityConfiguration.class)
public class ExpenseControllerTest {

    private static final String EXPENSES_ENDPOINT = "/api/v1/expenses";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExpenseService expenseService;

    @MockitoBean
    private UserProfileService userProfileService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnCreatedExpenseWhenRequestIsValid() throws Exception {
        ExpenseFixture expenseFixture = new ExpenseFixtureBuilder().build();
        double value = 100d;
        Date date = new Date();
        String comment = "comment";

        AddNewExpenseRequest addNewExpenseRequest = new AddNewExpenseRequest(
                expenseFixture.getUserExpenseCategory().getId(),
                expenseFixture.getUserExpenseSubCategory().getId(),
                expenseFixture.getUserPaymentMethod().getId(),
                date,
                value,
                comment
        );

        ExpenseResponse expenseResponse = new ExpenseResponse(
                1L,
                expenseFixture.getUserExpenseCategory().getId(),
                expenseFixture.getUserExpenseSubCategory().getId(),
                expenseFixture.getUserPaymentMethod().getId(),
                date,
                value,
                comment
        );

        when(userProfileService.findOrCreateUser(any(Jwt.class)))
                .thenReturn(expenseFixture.getUserProfile());

        when(expenseService.addNewExpense(any(UserProfile.class), any(AddNewExpenseRequest.class)))
                .thenReturn(expenseResponse);


        MvcResult result = mockMvc.perform(
                        post(EXPENSES_ENDPOINT)
                                .with(jwt())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(addNewExpenseRequest))

                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseBody =
                result.getResponse().getContentAsString();

        ExpenseResponse actualResponse =
                objectMapper.readValue(
                        responseBody,
                        ExpenseResponse.class
                );


        assertThat(actualResponse)
                .usingRecursiveComparison()
                .isEqualTo(expenseResponse);

        verify(expenseService)
                .addNewExpense(expenseFixture.getUserProfile(), addNewExpenseRequest);
    }

    @Test
    void shouldReturnListOfExpensesWhenExpensesExist() throws Exception {
        ExpenseFixture expenseFixture = new ExpenseFixtureBuilder().build();
        ExpenseResponse expenseResponse = new ExpenseResponse(
                1L,
                expenseFixture.getUserExpenseCategory().getId(),
                expenseFixture.getUserExpenseSubCategory().getId(),
                expenseFixture.getUserPaymentMethod().getId(),
                new Date(),
                100d,
                "comment"
        );

        List<ExpenseResponse> expenseResponses = List.of(expenseResponse);

        when(expenseService.getAllExpenses())
                .thenReturn(expenseResponses);


        MvcResult result = mockMvc.perform(
                        get(EXPENSES_ENDPOINT)
                                .with(jwt())

                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseBody =
                result.getResponse().getContentAsString();

        List<ExpenseResponse> actualExpenseResponses =
                objectMapper.readValue(
                        responseBody,
                        new TypeReference<List<ExpenseResponse>>() {}
                );

        assertThat(actualExpenseResponses)
                .usingRecursiveComparison()
                .isEqualTo(expenseResponses);

        verify(expenseService)
                .getAllExpenses();
    }

    @Test
    void shouldReturn404WhenInvalidConfigurationProvided() throws Exception {
        ExpenseFixture expenseFixture = new ExpenseFixtureBuilder().build();
        String errorMessage = "Resource Not Found";
        AddNewExpenseRequest addNewExpenseRequest = new AddNewExpenseRequest(
                expenseFixture.getUserExpenseCategory().getId(),
                expenseFixture.getUserExpenseSubCategory().getId(),
                expenseFixture.getUserPaymentMethod().getId(),
                new Date(),
                100d,
                "comment"
        );

        when(userProfileService.findOrCreateUser(any(Jwt.class)))
                .thenReturn(expenseFixture.getUserProfile());

        when(expenseService.addNewExpense(expenseFixture.getUserProfile(), addNewExpenseRequest))
                .thenThrow(new ResourceNotFoundException(errorMessage));


        mockMvc.perform(
                        post(EXPENSES_ENDPOINT)
                                .with(jwt())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(addNewExpenseRequest))
                )
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value(errorMessage))
                .andExpect(jsonPath("$.instance").value(EXPENSES_ENDPOINT))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Not Found"));

        verify(expenseService)
                .addNewExpense(expenseFixture.getUserProfile(), addNewExpenseRequest);
    }

    @Test
    void shouldReturn400WhenNoBodyProvided() throws Exception {
        mockMvc.perform(
                        post(EXPENSES_ENDPOINT)
                )
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Request body is missing"))
                .andExpect(jsonPath("$.instance").value(EXPENSES_ENDPOINT))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"));
    }

    @Test
    void shouldReturn400WhenWrongBodyParametersProvided() throws Exception {
        mockMvc.perform(
                        post(EXPENSES_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "XcategoryId": 1,
                                          "XsubCategoryId": 1,
                                          "XuserPaymentMethodId": 1,
                                          "Xdate": "2026-04-17",
                                          "Xvalue": 100.19,
                                          "Xcomment": "comment"
                                        }
                                        """)
                )
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Request contains invalid fields"))
                .andExpect(jsonPath("$.errors.categoryId").value("must not be null"))
                .andExpect(jsonPath("$.errors.subCategoryId").value("must not be null"))
                .andExpect(jsonPath("$.errors.userPaymentMethodId").value("must not be null"))
                .andExpect(jsonPath("$.errors.date").value("must not be null"))
                .andExpect(jsonPath("$.errors.value").value("must not be null"))
                .andExpect(jsonPath("$.errors.comment").value("must not be blank"))
                .andExpect(jsonPath("$.instance").value(EXPENSES_ENDPOINT))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"));
    }
}
