package com.springvue.app.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Tag(name = "首页模块")
@Controller
@RequestMapping("/")
public class IndexController {

    @Operation(summary = "首页")
    @GetMapping("")
    public String index() {
        return "index.html";
    }

    @Parameter(name = "name", description = "姓名", required = true)
    @Operation(summary = "向客人问好")
    @GetMapping("/sayHi")
    @ResponseBody
    public ResponseEntity<String> sayHi(@RequestParam(value = "name") String name) {
        return ResponseEntity.ok("Hi:" + name);
    }
}
