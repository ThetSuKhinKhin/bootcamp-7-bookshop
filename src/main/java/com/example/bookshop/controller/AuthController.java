package com.example.bookshop.controller;

import com.example.bookshop.entity.Customer;
import com.example.bookshop.entity.Order;
import com.example.bookshop.entity.PaymentMethod;
import com.example.bookshop.service.AuthService;
import com.example.bookshop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final CartService cartService;

    @RequestMapping("/register")
    public String register(Model model) {
        model.addAttribute("customer", new Customer());
        return "register";
    }
    //you can see order Class
    @PostMapping("/save-customer")
    public String saveCustomer(@RequestParam("billingAddress")String billingAddress,
                               @RequestParam("shippingAddress")String shippingAddress,
                               @RequestParam("payment")PaymentMethod method,
                               @ModelAttribute("totalPrice")double totalPrice,
                               Customer customer,
                               BindingResult result) {
        Order order = new Order(
                LocalDate.now(),
                billingAddress,
                shippingAddress,
                method,
                totalPrice
        );
        if (result.hasErrors()) {
            return "register";
        }

        /*System.out.println("Billing Address" + billingAddress);
        System.out.println("Payment Method" + method);
        System.out.println("================" + customer);*/

        authService.register(customer, order);
        this.customer = customer;
        return "redirect:/auth/info";
    }

    private Customer customer; //trick way -> customer order to order item


    /*@GetMapping("/info")//Model = ModelMap = Map are the same
    public String checkoutInfo(Map map, @ModelAttribute("totalPrice")double totalPrice) {
        map.put("cartItems", cartService.getCartItem());
        map.put("totalPrice", totalPrice);
        return "info";
    }*/

    /*@GetMapping("/info")
    public ModelAndView checkoutInfo(ModelMap map, @ModelAttribute("totalPrice")double totalPrice) {
        return new ModelAndView("info",
                "cartItems", cartService.getCartItem());
    }*/

    // with upper two methods are the same
    @GetMapping("/info")//Model = ModelMap = Map are the same
    public ModelAndView checkoutInfo(ModelMap map,
                                     @ModelAttribute("totalPrice")double totalPrice) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("cartItems", cartService.getCartItem());
        mv.addObject("totalPrice", totalPrice);
        mv.addObject("customerInfo",
                authService.findCustomerInfoByCustomerName(customer.getCustomerName()));
        mv.setViewName("info");
        return mv;
    }


    //auth/login
    @GetMapping("/login")
    public String login() {

        return "login";
    }

    @ModelAttribute("totalPrice")
    public double totalAmount() {
        return cartService
                .getCartItem()
                .stream()
                .map(c -> c.getQuantity() * c.getPrice())
                .reduce((a,b) -> a + b)
                .get();
    }
}
