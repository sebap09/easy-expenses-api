package com.easyexpenses.api.controllers;

import com.easyexpenses.api.builders.ExpenseFixtureBuilder;
import com.easyexpenses.api.dtos.AddNewExpenseRequest;
import com.easyexpenses.api.dtos.ExpenseResponse;
import com.easyexpenses.api.errors.ErrorCode;
import com.easyexpenses.api.errors.ResourceNotFoundException;
import com.easyexpenses.api.errors.ValidationException;
import com.easyexpenses.api.fixtures.ExpenseFixture;
import com.easyexpenses.api.services.ExpenseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnCreatedExpenseWhenRequestIsValid() throws Exception {
        ExpenseFixture expenseFixture = new ExpenseFixtureBuilder().build();
        double value = 100d;
        Date date = new Date();
        String comment = "comment";

        AddNewExpenseRequest addNewExpenseRequest = new AddNewExpenseRequest(
                expenseFixture.getUserProfile().getUserId(),
                expenseFixture.getUserExpenseCategory().getId(),
                expenseFixture.getUserExpenseSubCategory().getId(),
                expenseFixture.getUserPaymentMethod().getId(),
                date,
                value,
                comment
        );

        ExpenseResponse expenseResponse = new ExpenseResponse(
                1L,
                expenseFixture.getUserProfile().getUserId(),
                expenseFixture.getUserExpenseCategory().getId(),
                expenseFixture.getUserExpenseSubCategory().getId(),
                expenseFixture.getUserPaymentMethod().getId(),
                date,
                value,
                comment
        );

        when(expenseService.addNewExpense(addNewExpenseRequest))
                .thenReturn(expenseResponse);


        MvcResult result = mockMvc.perform(
                        post(EXPENSES_ENDPOINT)
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
                .addNewExpense(addNewExpenseRequest);
    }

    @Test
    void shouldReturnListOfExpensesWhenExpensesExist() throws Exception {
        ExpenseFixture expenseFixture = new ExpenseFixtureBuilder().build();
        ExpenseResponse expenseResponse = new ExpenseResponse(
                1L,
                expenseFixture.getUserProfile().getUserId(),
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
                expenseFixture.getUserProfile().getUserId(),
                expenseFixture.getUserExpenseCategory().getId(),
                expenseFixture.getUserExpenseSubCategory().getId(),
                expenseFixture.getUserPaymentMethod().getId(),
                new Date(),
                100d,
                "comment"
        );

        when(expenseService.addNewExpense(addNewExpenseRequest))
                .thenThrow(new ResourceNotFoundException(errorMessage));


        mockMvc.perform(
                        post(EXPENSES_ENDPOINT)
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
                .addNewExpense(addNewExpenseRequest);
    }

    @Test
    void shouldReturn400WhenInvalidUserRelationshipExceptionWasThrown() throws Exception {
        ExpenseFixture expenseFixture = new ExpenseFixtureBuilder().build();
        String errorMessage = "User Id does not match across domain data";
        ErrorCode errorCode = ErrorCode.INVALID_USER_RELATIONSHIP;
        AddNewExpenseRequest addNewExpenseRequest = new AddNewExpenseRequest(
                expenseFixture.getUserProfile().getUserId(),
                expenseFixture.getUserExpenseCategory().getId(),
                expenseFixture.getUserExpenseSubCategory().getId(),
                expenseFixture.getUserPaymentMethod().getId(),
                new Date(),
                100d,
                "comment"
        );

        when(expenseService.addNewExpense(addNewExpenseRequest))
                .thenThrow(new ValidationException(errorMessage, errorCode));


        mockMvc.perform(
                        post(EXPENSES_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(addNewExpenseRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value(errorMessage))
                .andExpect(jsonPath("$.instance").value(EXPENSES_ENDPOINT))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()));

        verify(expenseService)
                .addNewExpense(addNewExpenseRequest);
    }

    @Test
    void shouldReturn400WhenSubcategoryCategoryMismatchExceptionWasThrown() throws Exception {
        ExpenseFixture expenseFixture = new ExpenseFixtureBuilder().build();
        String errorMessage = "Sub-category assignment not correct";
        ErrorCode errorCode = ErrorCode.SUBCATEGORY_CATEGORY_MISMATCH;
        AddNewExpenseRequest addNewExpenseRequest = new AddNewExpenseRequest(
                expenseFixture.getUserProfile().getUserId(),
                expenseFixture.getUserExpenseCategory().getId(),
                expenseFixture.getUserExpenseSubCategory().getId(),
                expenseFixture.getUserPaymentMethod().getId(),
                new Date(),
                100d,
                "comment"
        );

        when(expenseService.addNewExpense(addNewExpenseRequest))
                .thenThrow(new ValidationException(errorMessage, errorCode));


        mockMvc.perform(
                        post(EXPENSES_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(addNewExpenseRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value(errorMessage))
                .andExpect(jsonPath("$.instance").value(EXPENSES_ENDPOINT))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()));

        verify(expenseService)
                .addNewExpense(addNewExpenseRequest);
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
                                          "XuserId": 2,
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
                .andExpect(jsonPath("$.errors.userId").value("must not be null"))
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
