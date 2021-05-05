package edu.cscc.degrees.services;

import edu.cscc.degrees.model.Cart;
import edu.cscc.degrees.model.Customer;
import edu.cscc.degrees.model.Offer;

import java.util.ArrayList;
import java.util.List;

public class PointOfSaleService {

    CreditBureauService creditBureauService;
    CreditCardService creditCardService;

    public PointOfSaleService(CreditBureauService creditBureauService, CreditCardService creditCardService) {
        this.creditBureauService = creditBureauService;
        this.creditCardService = creditCardService;
    }

    public List<Offer> getOffers(Customer customer) {
        List<Offer> offers = new ArrayList<>();
        int score = 0;

        try {
            score = creditBureauService.getCreditScore(customer.getTaxID());
        } catch (NoCreditScoreAvailableException e) {
            // Nothing to do, just don't offer them credit
        }
        if (score >= 800) {
            offers.add(new Offer("Cougars Visa Card", "5%"));
        } else if (score >= 740) {
            offers.add(new Offer("Cougars Visa Card", "10%"));
        } else if (score >= 670) {
            offers.add(new Offer("Cougars Visa Card", "15%"));
        } else if (score >= 580) {
            offers.add(new Offer("Cougars Visa Card", "20%"));
        } else if (score >= 300) {
            offers.add(new Offer("Cougars Visa Card", "25%"));
        }

        return offers;
    }

    public double checkout(String cardNumber, Cart cart) throws ChargeDeclinedException {
        double totalAmount = cart.getTotal();
        creditCardService.chargeCard(cardNumber, totalAmount);
        return totalAmount;
    }


}
