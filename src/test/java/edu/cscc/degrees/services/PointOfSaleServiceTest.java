package edu.cscc.degrees.services;

import edu.cscc.degrees.model.Customer;
import edu.cscc.degrees.model.Offer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointOfSaleServiceTest {

    @Mock
    private CreditBureauService creditBureauService;

    @InjectMocks
    private PointOfSaleService pointOfSaleService;

    @Test
    @DisplayName("No offers made if customer has no credit score")
    void test01() throws NoCreditScoreAvailableException {
        when(creditBureauService.getCreditScore(any(String.class)))
                .thenThrow(new NoCreditScoreAvailableException());
        assertTrue(pointOfSaleService.getOffers(
                new Customer("123-45-6789", "Bart", "Simpson")).isEmpty());
        verify(creditBureauService, times(1)).getCreditScore(any(String.class));
    }

    @DisplayName("It offers the correct rate based on the credit score")
    @ParameterizedTest(name = "Customer with SSN of {0} and credit score {1} is given rate {2}")
    @MethodSource("offersWithArgsList")
    void test02(String ssn, int score, String rate) throws NoCreditScoreAvailableException {
        when(creditBureauService.getCreditScore(ssn)).thenReturn(score);
        assertEquals(pointOfSaleService.getOffers(
                new Customer(ssn, "first", "last")).get(0).getAmount(),rate);
        verify(creditBureauService, times(1)).getCreditScore(ssn);
        verifyNoMoreInteractions(creditBureauService);
    }

    private static List<Arguments> offersWithArgsList() {
        return Arrays.asList(
                Arguments.of("123-45-1000", 825, "5%"),
                Arguments.of("123-45-1001", 775, "10%"),
                Arguments.of("123-45-1002", 700, "15%"),
                Arguments.of("123-45-1003", 600, "20%"),
                Arguments.of("123-45-1003", 400, "25%"));
    }
}