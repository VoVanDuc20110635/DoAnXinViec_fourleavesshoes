package com.data.filtro.controller;

import com.data.filtro.exception.AuthenticationAccountException;
import com.data.filtro.model.*;
import com.data.filtro.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

import static com.data.filtro.service.InputService.containsAllowedCharacters;

@Controller
@RequestMapping("/login")
public class LoginController {

    private String csrfToken;

    private final CartService cartService;
    private final UserService userService;

    private final AuthenticationService authenticationService;


    @Autowired
    private ProductService productService;

    @Autowired
    public LoginController(UserService userService, CartService cartService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.cartService = cartService;
        this.authenticationService = authenticationService;
    }

    @GetMapping
    public String show(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            String _csrfToken = generateRandomString();
            csrfToken = _csrfToken;
//        System.out.println("csrfToken:" + _csrfToken);
            model.addAttribute("_csrfToken", _csrfToken);
            return "user/boot1/login";
        }
        else {
            return "redirect:/";
        }
    }

    @PostMapping
    public String login(@RequestParam("accountName") String accountName,
                        @RequestParam("password") String password,
                        @RequestParam("_csrfParameterName") String csrfTokenForm,
                        HttpSession session,
                        Model model) {
//        System.out.println("Da vao ham post logging");
        if(!containsAllowedCharacters(accountName) || !containsAllowedCharacters(password)){
            String message = "Username and password can only contain lowercase letters, and the characters (), @.";
            model.addAttribute("errorMessage", message);
//            throw new InputNotInvalidException("Tên tài khoản, mật khẩu chỉ được chứa các ký tự thường và dấu (), @");
            return "redirect:/login";
        }
//        System.out.println("Sau khi nhan nut dang ky thi csrf token la: " + csrfToken);
        if (!csrfTokenForm.equals(csrfToken)) {
            String message = "Anti-CSRF token is not correct!";
            model.addAttribute("errorMessage", message);
            return "redirect:/login";
        }
        try {
//            System.out.println("dang nhap");
            AuthenticateResponse authenticateResponse = authenticationService.authenticate(accountName, password, session);
//            User user = userService.authenticateUser(accountName, password);
//            System.out.println(user.getName());
            session.setAttribute("user", authenticateResponse.getUser());
            session.setAttribute("token", authenticateResponse.getAccessToken());
            System.out.println(authenticateResponse.getAccessToken());
            System.out.println(authenticateResponse.getUser().getName() + " " + authenticateResponse.getUser().getPhoneNumber());
            Cart cart = (Cart) session.getAttribute("cart");
            GuestCart guestCart = (GuestCart) session.getAttribute("guestCart");
            if (guestCart != null) {
                cart = cartService.convertGuestCartToCart(guestCart,  authenticateResponse.getUser());
                session.removeAttribute("guestCart");
            }
            return "redirect:/";
        } catch (AuthenticationAccountException exception) {
            exception.printStackTrace();
//            System.out.println(exception.getMessage());
            model.addAttribute("message", exception.getMessage());
        }
        return "user/boot1/login";
    }

    @GetMapping("/session")
    public String check(HttpSession session) {
//        Account account = (Account) session.getAttribute("account");
//        System.out.println("session lay duoc la: " + account.getAccountName());
        return "session";
    }
    public String generateRandomString() {
        return UUID.randomUUID().toString();
    }
}
