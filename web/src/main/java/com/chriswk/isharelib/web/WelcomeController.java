package com.chriswk.isharelib.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WelcomeController {

    @RequestMapping("/")
    public String welcome(ModelMap modelMap) {
        modelMap.put("welcome", "Hello world");
        return "welcome";
    }
}
