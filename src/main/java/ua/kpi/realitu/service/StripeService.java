package ua.kpi.realitu.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class StripeService {

    @Value("${realitu.stripe.secret-test-key}")
    private String stripeSecretKey;

    public void createStripeCustomerSaveBusinessPaymentMethodOrChangeItForExistingCustomer(String cardToken, String cardHolder, Double amount, String storyId) {
        try {

            String customerId = createCustomer(null, null, null, null);
            String paymentMethodId = createPaymentMethod(cardToken, cardHolder);

            attachPaymentMethodToCustomer(paymentMethodId, customerId);

            chargeCustomer(customerId, paymentMethodId, amount, storyId);

        } catch (Exception e) {
            throw new RuntimeException("STRIPE: Can not create Payment");
        }
    }

    public void chargeCustomer(String customerId, String paymentMethodId, Double amount, String storyId) {

        Stripe.apiKey = stripeSecretKey;

        try {
            PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
                    .setAmount((long) (amount * 100))
                    .setCurrency("eur")
                    .putMetadata("story_id", storyId)
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

    public String createPaymentMethod(String cardToken, String cardHolder) {
        Stripe.apiKey = stripeSecretKey;

        try {
            Map<String, Object> paymentMethodParams = new HashMap<>();
            paymentMethodParams.put("type", "card");

            Map<String, Object> cardDetails = new HashMap<>();
            cardDetails.put("token", cardToken);
            paymentMethodParams.put("card", cardDetails);

            Map<String, Object> billingDetails = new HashMap<>();
            billingDetails.put("name", cardHolder);
            paymentMethodParams.put("billing_details", billingDetails);

            PaymentMethod paymentMethod = PaymentMethod.create(paymentMethodParams);

            log.info("STRIPE :: Card Token: " + cardToken);
            log.info("STRIPE :: Payment Method ID: " + paymentMethod.getId());
            log.info("STRIPE :: Payment Method: " + paymentMethod);
            return paymentMethod.getId();

        } catch (StripeException e) {
            log.error("STRIPE :: StripeService: createTestPaymentMethod: StripeException: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public void attachPaymentMethodToCustomer(String paymentMethodId, String customerId) {
        try {
            PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
            paymentMethod.attach(Collections.singletonMap("customer", customerId));
        } catch (StripeException e) {
            log.error("STRIPE :: StripeService (attachPaymentMethodToCustomer): StripeException: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public String createCustomer(String email, String firstName, String lastName, String phoneNumber) {

        Stripe.apiKey = stripeSecretKey;

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("email", email);
            params.put("name", firstName + " " + lastName);
            params.put("phone", phoneNumber);

            Customer customer = Customer.create(params);

            log.info("STRIPE :: Customer ID: " + customer.getId());
            log.info("STRIPE :: Customer: " + customer);
            return customer.getId();

        } catch (StripeException e) {
            log.error("STRIPE :: StripeService (createTestCustomerWithTestPaymentMethod): StripeException: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
