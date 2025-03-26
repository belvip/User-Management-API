package com.belvinard.userManagement;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Greetings", description = "Simple greeting endpoints for testing")
public class HelloController {

    @Operation(
            summary = "Get generic greeting",
            description = "Returns a simple welcome message",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully returned greeting"
                    )
            }
    )
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello UserManagement";
    }

    @Operation(
            summary = "Get user greeting",
            description = "Returns a personalized greeting for users",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully returned user greeting"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - authentication required"
                    )
            }
    )
    @GetMapping("/user")
    public String user() {
        return "Hello User";
    }
}