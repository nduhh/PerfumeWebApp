package com.perfumes.perfumeswebapp.Services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Service;

@Service
public class CreditCardValidationService {
    public boolean validateCreditCard(String cardNumber, String expiryDate, String cvv) {

        if (!isValidExpiryDate(cardNumber)) {
            return false;
        }

        if (!isValidCVV(cardNumber, cvv)) {
            return false;
        }

        if (!isValidCardNumber(cardNumber)) {
            return false;
        }

        return true;
    }

    private static boolean isValidExpiryDate(String expiryDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("mm/yy");
            Date expDate = sdf.parse(expiryDate);
            Date currentDate = new Date();
            // The expiry date of the credit card (year and month) must be AFTER present
            // time
            return expDate.after(currentDate);
        } catch (ParseException e) {
            return false;
        }
    }

    private static boolean isValidCVV(String cardNumber, String cvv) {
        if (cardNumber.startsWith("5") || cardNumber.startsWith("4")) {
            // Unless it’s an American Express card, in which case the CVV must be exactly 4
            // digits long
            return cvv.length() == 3;
        }
        return true;
    }

    private static boolean isValidCardNumber(String cardNumber) {
        if (cardNumber.startsWith("5") || cardNumber.startsWith("4")) {

            if (cardNumber.length() < 15 || cardNumber.length() > 19) {
                return false;
            }
        } else {
            // The PAN (card number) is between 16 and 19 digits long
            if (cardNumber.length() < 16 || cardNumber.length() > 19) {
                return false;
            }
        }
        return true;
    }

}
