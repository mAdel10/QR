package com.toastdemoapp.qrdemoapp.utilities;

import android.content.Context;
import android.util.Patterns;
import android.widget.EditText;

public class InputValidator {

    @SuppressWarnings("ConstantConditions")
    public static boolean registerValidation(Context context, EditText firstNameEdt, EditText lastNameEdt,
                                             EditText emailEdt, EditText passwordEdt, EditText confirmPasswordEdt,
                                             EditText phoneEdt) {

        String firstName = firstNameEdt.getText().toString().trim();
        String lastName = lastNameEdt.getText().toString().trim();
        String email = emailEdt.getText().toString().trim();
        String password = passwordEdt.getText().toString().trim();
        String confirmPassword = confirmPasswordEdt.getText().toString().trim();
        String phone = phoneEdt.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
                || !Patterns.EMAIL_ADDRESS.matcher(email).matches() || phone.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty() || password.length() < 6
                || !password.equals(confirmPassword)) {

            if (firstName.isEmpty()) {
                firstNameEdt.setError("First Name is require");
            }
            if (lastName.isEmpty()) {
                lastNameEdt.setError("Last Name is require");
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEdt.setError("Enter Valid Email");
            }
            if (email.isEmpty()) {
                emailEdt.setError("Email is require");
            }
            if (phone.isEmpty()) {
                phoneEdt.setError("Phone is require");
            }
            if (password.length() < 6) {
                passwordEdt.setError("Password should be at least 6 number");
            }
            if (password.isEmpty()) {
                passwordEdt.setError("Password is require");
            }
            if (confirmPassword.isEmpty()) {
                confirmPasswordEdt.setError("Confirm Password is require");
            }
            if (!password.equals(confirmPassword)) {
                passwordEdt.setError("Password don't matched");
                confirmPasswordEdt.setError("Confirm Password don't matched");
            }
            return false;
        }
        return true;
    }
    public static boolean centerRegisterValidation(Context context, EditText nameEdt,
                                                   EditText emailEdt, EditText passwordEdt, EditText confirmPasswordEdt,
                                                   EditText phoneEdt, EditText latitudeEdt, EditText longitudeEdt) {

        String name = nameEdt.getText().toString().trim();
        String email = emailEdt.getText().toString().trim();
        String password = passwordEdt.getText().toString().trim();
        String confirmPassword = confirmPasswordEdt.getText().toString().trim();
        String phone = phoneEdt.getText().toString().trim();
        String latitude = latitudeEdt.getText().toString().trim();
        String longitude = longitudeEdt.getText().toString().trim();

        if (name.isEmpty() ||  email.isEmpty()
                || !Patterns.EMAIL_ADDRESS.matcher(email).matches() || phone.isEmpty()
                || latitude.isEmpty() || longitude.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty() || password.length() < 6
                || !password.equals(confirmPassword)) {

            if (name.isEmpty()) {
                nameEdt.setError("Name is require");
            }
            if (latitude.isEmpty()){
                latitudeEdt.setError("Latitude is require");
            }
            if (longitude.isEmpty()){
                latitudeEdt.setError("Longitude is require");
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEdt.setError("Enter Valid Email");
            }
            if (email.isEmpty()) {
                emailEdt.setError("Email is require");
            }
            if (phone.isEmpty()) {
                phoneEdt.setError("Phone is require");
            }
            if (password.length() < 6) {
                passwordEdt.setError("Password should be at least 6 number");
            }
            if (password.isEmpty()) {
                passwordEdt.setError("Password is require");
            }
            if (confirmPassword.isEmpty()) {
                confirmPasswordEdt.setError("Confirm Password is require");
            }
            if (!password.equals(confirmPassword)) {
                passwordEdt.setError("Password don't matched");
                confirmPasswordEdt.setError("Confirm Password don't matched");
            }
            return false;
        }
        return true;
    }

    public static boolean loginValidation(Context context, EditText usernameEdt, EditText passwordEdt) {
        String username = usernameEdt.getText().toString().trim();
        String password = passwordEdt.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() ) {
            if (username.isEmpty()) {
                usernameEdt.setError("User Name is required");
            }

            if (password.isEmpty()) {
                passwordEdt.setError("Password is required");
            }
            return false;
        }
        return true;
    }

    public static boolean updateProfileValidation(Context context, EditText firstNameEdt, EditText lastNameEdt, EditText phoneEdt) {
        String firstName = firstNameEdt.getText().toString().trim();
        String lastName = lastNameEdt.getText().toString().trim();
        String phone = phoneEdt.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty()) {
            if (firstName.isEmpty()) {
                firstNameEdt.setError("");
            }
            if (lastName.isEmpty()) {
                lastNameEdt.setError("");
            }
            if (phone.isEmpty()) {
                phoneEdt.setError("");
            }
            return false;
        }
        return true;
    }

    public static boolean changePassValidation(Context context, EditText oldPasswordEdt, EditText newPasswordEdt, EditText confirmPasswordEdt) {
        String oldPassword = oldPasswordEdt.getText().toString().trim();
        String newPassword = newPasswordEdt.getText().toString().trim();
        String confirmPassword = confirmPasswordEdt.getText().toString().trim();

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()
                || newPassword.length() < 6 || oldPassword.length() < 6 || confirmPassword.length() < 6
                || !newPassword.equals(confirmPassword)) {
            if (oldPassword.length() < 6) {
                oldPasswordEdt.setError("");
            }
            if (oldPassword.isEmpty()) {
                oldPasswordEdt.setError("");
            }
            if (newPassword.length() < 6) {
                newPasswordEdt.setError("");
            }
            if (newPassword.isEmpty()) {
                newPasswordEdt.setError("");
            }
            if (confirmPassword.length() < 6) {
                confirmPasswordEdt.setError("");
            }
            if (confirmPassword.isEmpty()) {
                confirmPasswordEdt.setError("");
            }
            if (!newPassword.equals(confirmPassword)) {
                newPasswordEdt.setError("");
                confirmPasswordEdt.setError("");
            }
            return false;
        }
        return true;
    }

    public static boolean emailValidation(Context context, EditText emailEdt) {
        String email = emailEdt.getText().toString().trim();
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEdt.setError("");
            }
            if (email.isEmpty()) {
                emailEdt.setError("");
            }
            return false;
        }
        return true;
    }
}
