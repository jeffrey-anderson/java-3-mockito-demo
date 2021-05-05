package edu.cscc.degrees.services;

import edu.cscc.degrees.model.Cart;
import edu.cscc.degrees.model.Customer;
import edu.cscc.degrees.model.LineItem;
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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointOfSaleServiceTest {

    @Mock
    private CreditBureauService creditBureauService;

    @Mock
    private CreditCardService creditCardService;

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

        List<Offer> offers = pointOfSaleService.getOffers(
                new Customer(ssn, "first", "last"));
        assertEquals(1, offers.size());
        assertEquals(rate, offers.get(0).getAmount());

        verify(creditBureauService, times(1)).getCreditScore(ssn);
        verifyNoMoreInteractions(creditBureauService);
    }

    @Test
    @DisplayName("It throws ChargeDeclinedException when card is declined")
    void test03() throws ChargeDeclinedException {
        when(creditCardService.chargeCard(any(String.class), any(Double.TYPE)))
                .thenThrow(new ChargeDeclinedException());
        Cart cart = new Cart();
        assertThrows(ChargeDeclinedException.class, () -> pointOfSaleService.checkout("", cart));

        verify(creditCardService, times(1)).chargeCard("", 0.00);
        verifyNoMoreInteractions(creditCardService);
        verifyNoInteractions(creditBureauService);
    }

    @Test
    @DisplayName("It returns the total amount when the card is not declined")
    void test04() throws ChargeDeclinedException {
        when(creditCardService.chargeCard(any(String.class), any(Double.TYPE)))
                .thenReturn("Confirmation 12345");
        Cart cart = new Cart();
        cart.addItem(new LineItem("SMOKED TURKEY GRINDER", 1, 12.00));
        cart.addItem(new LineItem("SHRIMP ‘N GRITS", 1, 13.00));

        assertEquals(25.00,pointOfSaleService.checkout("12-34-56", cart));

        verify(creditCardService, times(1)).chargeCard("12-34-56", 25.00);
        verifyNoMoreInteractions(creditCardService);
        verifyNoInteractions(creditBureauService);
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