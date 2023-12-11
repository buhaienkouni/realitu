package ua.kpi.realitu.web;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.kpi.realitu.domain.UserEntity;
import ua.kpi.realitu.domain.enums.Category;
import ua.kpi.realitu.service.ArticleService;
import ua.kpi.realitu.service.ImageService;
import ua.kpi.realitu.service.StripeService;
import ua.kpi.realitu.service.UserService;
import ua.kpi.realitu.web.model.ArticleDto;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class PaymentController {

    @Autowired
    private UserService userService;

    @Autowired
    private StripeService stripeService;

    @Autowired
    private ArticleService articleService;

    @Value("${realitu.stripe.publishable-test-key}")
    private String stripePublishableKey;

    @GetMapping("/donate/{historyId}")
    public String getPaymentSettings(@PathVariable("historyId") UUID historyId, Authentication authentication, Model model) {
        ArticleDto history = articleService.getArticleDtoById(historyId);

        currentUser(model, authentication);
        model.addAttribute("history", history);
        model.addAttribute("stripePublishableKey", stripePublishableKey);
        return "payment";
    }

    @PostMapping("/donate/pay")
    public ResponseEntity<String> donateFromCard(@RequestParam("token") String token, Authentication authentication, Model model) {
        currentUser(model, authentication);
        try {
            stripeService.createStripeCustomerSaveBusinessPaymentMethodOrChangeItForExistingCustomer(token, cardHolder, amount, storyId);
            return ResponseEntity.ok("Payment succeeded.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Payment failed. Please try again.");
        }
    }

    private void currentUser(Model model, Authentication authentication) {
        Optional.ofNullable(authentication).ifPresentOrElse(
                auth -> model.addAttribute("currentUser", userService.getUserByUsername(authentication.getName())),
                () -> model.addAttribute("currentUser", null)
        );
    }
}
