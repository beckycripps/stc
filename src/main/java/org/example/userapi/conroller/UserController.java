package org.example.userapi.conroller;

import org.example.userapi.model.PasswordDTO;
import org.example.userapi.model.UserDetailDTO;
import org.example.userapi.model.UserResponseDTO;
import org.example.userapi.service.UserService;
import org.example.userapi.service.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping
    public ResponseEntity<String> createUser(@Valid @RequestBody UserDetailDTO user, BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            // If validation fails, return an error response with details
            StringBuilder errorMessage = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMessage.append(error.getDefaultMessage()).append(". ");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Validation failed: " + errorMessage);
        } else {
            // Validation passed, process the user
            ValidationResult validationResult = userService.validateAndSanitizeUser(user);
            if (validationResult.isValid()) {
                userService.createUser(user);
                return ResponseEntity.ok("User created successfully");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Validation failed: " + validationResult.getErrorMessages());
            }
        }
    }

    @GetMapping("/{email}")
    public ResponseEntity<ResponseWrapper<UserResponseDTO>> getUserDetails(@PathVariable String email) {
        System.out.println("in the getuserdetails");
        UserResponseDTO userResponseDTO = userService.getUserByEmail(email);
        ResponseWrapper<UserResponseDTO> response = new ResponseWrapper<>();
        System.out.println("iuserDetailsDTO:" + userResponseDTO);
        if (userResponseDTO == null) {
            // User not found, set error message in the response
            System.out.println("in the null check of get controller");
            response.setErrorMessage("No such user");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            // User found, set UserDetailsDTO in the response
            response.setData(userResponseDTO);
            return ResponseEntity.ok(response);
        }
    }


    @DeleteMapping("/{email}")
    public ResponseEntity<String> deleteUser(@PathVariable String email, @RequestBody PasswordDTO password) {
        if (userService.deleteUser(email, password.getPassword())) {
            return ResponseEntity.ok("User deleted successfully");
        } else {
            // If the user does not exist or password doesn't match, return a not found response
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access or User not found");
        }
    }

}
