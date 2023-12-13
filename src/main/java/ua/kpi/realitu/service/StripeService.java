package ua.kpi.realitu.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Token;
import com.stripe.param.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.kpi.realitu.web.model.PaymentDto;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class StripeService {

    @Value("${realitu.stripe.secret-test-key}")
    private String stripeSecretKey;

    private String getFirstName(String cardholder) {
        String[] names = cardholder.split(" ");
        return names[0];
    }

    private String getLastName(String cardholder) {
        String[] names = cardholder.split(" ");
        return names[1];
    }

    public void createStripeCustomerSaveBusinessPaymentMethodOrChangeItForExistingCustomer(PaymentDto paymentDto) {
        try {

            String customerId = createCustomer(null, getFirstName(paymentDto.getCardholder()), getLastName(paymentDto.getCardholder()), null);
            String paymentMethodId = createPaymentMethod(paymentDto.getToken(), paymentDto.getCardholder());

            attachPaymentMethodToCustomer(paymentMethodId, customerId);

            Double amount = Double.parseDouble(paymentDto.getAmount());
            chargeCustomer(customerId, paymentMethodId, amount, paymentDto.getStoryId());

        } catch (Exception e) {
            throw new RuntimeException("STRIPE :: Can not create Payment");
        }
    }

    public void chargeCustomer(String customerId, String paymentMethodId, Double amountInEur, String storyId) {

        Stripe.apiKey = stripeSecretKey;

        try {
            PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
                    .setAmount((long) (amountInEur * 100))
                    .setCurrency("eur")
                    .putMetadata("id", storyId)
                    .setCustomer(customerId)
                    .setPaymentMethod(paymentMethodId)
                    .setConfirm(true)
                    .build();

            PaymentIntent intent = PaymentIntent.create(createParams);

            if ("succeeded".equals(intent.getStatus())) {
                log.info("STRIPE :: Payment succeeded for Story with ID: " + storyId);
            } else {
                log.error("STRIPE :: Payment failed for Story with ID: " + storyId);
            }

        } catch (StripeException e) {
            log.error("STRIPE :: StripeService (chargeCustomer): StripeException: " + e.getMessage());
            throw new RuntimeException("STRIPE :: Payment failed for Story with ID: " + storyId, e);
        }
    }

    public String createPaymentMethod(String cardToken, String cardholder) {

        Stripe.apiKey = stripeSecretKey;

        try {
            PaymentMethodCreateParams paymentMethodParams = PaymentMethodCreateParams.builder()
                    .setType(PaymentMethodCreateParams.Type.CARD)
                    .setCard(PaymentMethodCreateParams.Token.builder()
                            .setToken(cardToken)
                            .build())
                    .setBillingDetails(PaymentMethodCreateParams.BillingDetails.builder()
                            .setName(cardholder)
                            .build())
                    .build();

            PaymentMethod paymentMethod = PaymentMethod.create(paymentMethodParams);

            log.info("STRIPE :: Payment Method ID: " + paymentMethod.getId());
            log.info("STRIPE :: Payment Method: " + paymentMethod);
            return paymentMethod.getId();

        } catch (StripeException e) {
            log.error("STRIPE :: StripeService: createTestPaymentMethod: StripeException: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public String createToken(String cardNumber, String expMonth, String expYear, String cvc) {

        Stripe.apiKey = stripeSecretKey;

        try {
            TokenCreateParams tokenCreateParams = TokenCreateParams.builder()
                    .setCard(TokenCreateParams.Card.builder()
                            .setNumber(cardNumber)
                            .setExpMonth(expMonth)
                            .setExpYear(expYear)
                            .setCvc(cvc)
                            .build())
                    .build();

            Token token = Token.create(tokenCreateParams);

            log.info("STRIPE :: Token ID: " + token.getId());
            log.info("STRIPE :: Token: " + token);
            return token.getId();

        } catch (StripeException e) {
            log.error("STRIPE :: StripeService (createToken): StripeException: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public String createCustomer(String email, String firstName, String lastName, String phoneNumber) {

        Stripe.apiKey = stripeSecretKey;

        try {
            CustomerCreateParams params = CustomerCreateParams.builder()
                    .setEmail(email)
                    .setName(firstName + " " + lastName)
                    .setPhone(phoneNumber)
                    .build();

            Customer customer = Customer.create(params);

            log.info("STRIPE :: Customer ID: " + customer.getId());
            log.info("STRIPE :: Customer: " + customer);
            return customer.getId();

        } catch (StripeException e) {
            log.error("STRIPE :: StripeService (createCustomer): StripeException: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public void attachPaymentMethodToCustomer(String paymentMethodId, String customerId) {

        Stripe.apiKey = stripeSecretKey;

        try {
            PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
            paymentMethod.attach(PaymentMethodAttachParams.builder()
                    .setCustomer(customerId)
                    .build());

            log.info("STRIPE :: Payment Method attached successfully: " + paymentMethodId);
        } catch (StripeException e) {
            log.error("STRIPE :: StripeService (attachPaymentMethodToCustomer): StripeException: " + e.getMessage());
            throw new RuntimeException("Failed to attach payment method: " + paymentMethodId, e);
        }
    }

    public void detachPaymentMethod(String paymentMethodId) {

        Stripe.apiKey = stripeSecretKey;

        try {
            PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
            paymentMethod.detach();
            log.info("STRIPE :: Payment Method detached successfully: " + paymentMethodId);
        } catch (StripeException e) {
            log.error("STRIPE :: StripeService (detachPaymentMethod): StripeException: " + e.getMessage());
            throw new RuntimeException("Failed to detach payment method: " + paymentMethodId, e);
        }
    }
}
