package org.paulobichara.prototype.web;

import org.paulobichara.prototype.dto.NewUserDto;
import org.paulobichara.prototype.dto.UpdateUserDto;
import org.paulobichara.prototype.model.User;
import org.paulobichara.prototype.security.annotation.IsAdminOrOwner;
import org.paulobichara.prototype.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.UnsupportedEncodingException;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Api(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
class UserController extends BaseFilteredController<User> {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "Create a new user", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully added"),
            @ApiResponse(code = 400, message = "Bad request") })
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> addNewUser(
            @ApiParam("Required data for registration") @Valid @RequestBody NewUserDto user) {
        return ResponseEntity.ok(userService.addNewUser(user));
    }

    @ApiOperation(value = "Search users", response = User.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Search executed successfully"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 500, message = "Unknown error occurred. Please contact support.")})
    @IsAdminOrOwner
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<User>> searchUsers(
            @ApiParam(SYNTAX) @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        try {
            return ResponseEntity.ok(userService.searchUsers(parseSearchQuery(decodeSearchParameter(search)),
                    PageRequest.of(page, pageSize)));
        } catch (UnsupportedEncodingException exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ApiOperation(value = "Find user by id", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User found"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 403, message = "Forbidden")})
    @IsAdminOrOwner
    @GetMapping(path="/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.findUserById(userId));
    }

    @ApiOperation(value = "Update user", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User updated"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 403, message = "Forbidden")})
    @IsAdminOrOwner
    @PostMapping(path="/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> updateUser(@PathVariable Long userId,
        @ApiParam("Updatable fields") @Valid @RequestBody UpdateUserDto dto) {
        return ResponseEntity.ok(userService.updateUserById(userId, dto));
    }

    @ApiOperation(value = "Remove user", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User removed"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 403, message = "Forbidden")})
    @IsAdminOrOwner
    @DeleteMapping(path="/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> removeUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.removeUser(userId));
    }

}
