package com.github.biconou.sso.cas.tutorial;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    @RequestMapping("/")
    public String index(HttpServletRequest request, Model model) {

        String loggedUserId = request.getRemoteUser();
        model.addAttribute("loggedUserId", loggedUserId);

        return "hello";
    }

}
