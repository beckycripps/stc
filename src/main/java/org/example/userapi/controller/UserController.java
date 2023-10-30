package org.example.userapi.controller;

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

/**
 *
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    /**
     * Handles the creation of a new user based on the provided UserDetailDTO.
     *
     * @param user          The UserDetailDTO object containing user information to be created.
     * @param bindingResult Represents the result of validation and data binding for the user object.
     * @return ResponseEntity<String> A response entity indicating the status of the user creation process.
     */
    @PostMapping
    public ResponseEntity<String> createUser(@Valid @RequestBody UserDetailDTO user, BindingResult bindingResult) {

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
                try {
                    userService.createUser(user);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("User Creation failed: " + e.getMessage());
                }
                return ResponseEntity.ok("User created successfully");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Validation failed: " + validationResult.getErrorMessages());
            }
        }
    }

    /**
     * Handles the update of a user based on the provided UserDetailDTO.
     *
     * @param user          The UserDetailDTO object containing user information to be created.
     * @param bindingResult Represents the result of validation and data binding for the user object.
     * @return ResponseEntity<String> A response entity indicating the status of the user creation process.
     */
    @PutMapping
    public ResponseEntity<String> updateUser(@Valid @RequestBody UserDetailDTO user, BindingResult bindingResult) {

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
                try {
                    userService.updateUserDetails(user);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Unauthorized access or update failed: " + e.getMessage());
                }
                return ResponseEntity.ok("User updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Validation failed: " + validationResult.getErrorMessages());
            }
        }
    }

    /**
     * Retrieves user details based on the provided email address.
     *
     * @param email The email address of the user to retrieve details for.
     * @return ResponseEntity<ResponseWrapper < UserResponseDTO>> A response entity containing the user details
     * wrapped in a ResponseWrapper or an error message if the user is not found.
     */
    @GetMapping("/{email}")
    public ResponseEntity<ResponseWrapper<UserResponseDTO>> getUserDetails(@PathVariable String email, @RequestBody PasswordDTO password) {
        // Attempt to retrieve user details by email from the userService
        UserResponseDTO userResponseDTO = userService.getUserByEmail(email, password.getPassword());

        // Create a ResponseWrapper object to wrap the user details or error message
        ResponseWrapper<UserResponseDTO> response = new ResponseWrapper<>();
        if (userResponseDTO == null) {
            // User not found, set error message in the response
            response.setErrorMessage("No such user or password incorrect");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            // User found, set UserDetailsDTO in the response
            response.setData(userResponseDTO);
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Deletes a user based on the provided email address and password.
     *
     * @param email    The email address of the user to be deleted.
     * @param password The PasswordDTO containing the user's password for authentication.
     * @return ResponseEntity<String> A response entity indicating the status of the user deletion process.
     * Returns a success message if the user is deleted, or an error message if the user is not found
     * or the provided password doesn't match the user's password.
     */
    @DeleteMapping("/{email}")
    public ResponseEntity<String> deleteUser(@PathVariable String email, @RequestBody PasswordDTO password) {
        // Attempt to delete the user using the provided email and password
        if (userService.deleteUser(email, password.getPassword())) {
            // User deleted successfully, return a success response
            return ResponseEntity.ok("User deleted successfully");
        } else {
            // If the user does not exist or password doesn't match, return an unauthorized access response
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access or User not found");
        }
    }
}
