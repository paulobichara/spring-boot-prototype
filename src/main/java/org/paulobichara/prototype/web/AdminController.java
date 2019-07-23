package org.paulobichara.prototype.web;

import org.paulobichara.prototype.dto.NewUserDto;
import org.paulobichara.prototype.model.User;
import org.paulobichara.prototype.service.UserService;
import org.paulobichara.prototype.security.annotation.IsAdmin;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "/api/admins", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/admins")
class AdminController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "Create a new admin", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully added"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 403, message = "Forbidden") })
    @IsAdmin
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> addNewAdmin(
            @ApiParam("Required data for registration") @Valid @RequestBody NewUserDto user) {
        return ResponseEntity.ok(userService.addNewAdmin(user));
    }

}
