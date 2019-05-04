package com.roommates.roommates;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.json.*;

import javax.servlet.http.*;

import java.sql.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.security.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class UserController {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/Roommates";
    static Connection conn = null;

    @RequestMapping(value="/register", method=RequestMethod.POST)
    public ResponseEntity<String> register(@RequestBody String body, HttpServletRequest request) {
        PreparedStatement ps_register = null;
        HttpHeaders responseHeader = new HttpHeaders();
        JSONObject responseBody = new JSONObject();

        responseHeader.set("Content-Type", "application/json");
        initializeDBConnection();

		try {
			JSONObject bodyObj = new JSONObject(body);
            String hashedPassword = hashPassword(bodyObj.getString("password"));

            if (!checkPasswords(bodyObj.getString("password"), bodyObj.getString("confirmPassword"))) {
                return new ResponseEntity("{\"message\": \"Passwords do not match.\"}", responseHeader, HttpStatus.BAD_REQUEST);
            }

            if (checkIfUserExists(bodyObj.getString("email"))) {
                return new ResponseEntity("{\"message\": \"Email is already registered.\"}", responseHeader, HttpStatus.BAD_REQUEST);
            }

            String query = "INSERT INTO Users (email, password, first_name, last_name) VALUES (?, ?, ?, ?)";
            ps_register = conn.prepareStatement(query);

            ps_register.setString(1, bodyObj.getString("email"));
            ps_register.setString(2, hashedPassword);
            ps_register.setString(3, bodyObj.getString("firstName"));
            ps_register.setString(4, bodyObj.getString("lastName"));

            ps_register.executeUpdate();
            ps_register.close();
            conn.close();

            return new ResponseEntity("{\"message\": \"User has been registered.\"}", responseHeader, HttpStatus.OK);
		} catch(JSONException e) {
			e.printStackTrace();
            responseBody.put("message", "JSONException");
        } catch(SQLException e) {
            e.printStackTrace();
            responseBody.put("message", "SQLException");
        }

		return new ResponseEntity(responseBody.toString(), responseHeader, HttpStatus.BAD_REQUEST);
	}

    @RequestMapping(value="/login", method=RequestMethod.POST)
    public ResponseEntity<String> login(@RequestBody String body, HttpServletRequest request) {
        PreparedStatement ps_login = null;
        HttpHeaders responseHeader = new HttpHeaders();
        JSONObject responseBody = new JSONObject();

        responseHeader.set("Content-Type", "application/json");
        initializeDBConnection();


        try {
            JSONObject bodyObj = new JSONObject(body);
            String email = bodyObj.getString("email");
            String password = hashPassword(bodyObj.getString("password"));

            if (!checkIfUserExists(email)) {
                return new ResponseEntity("{\"message\": \"Email is not registered.\"}", responseHeader, HttpStatus.BAD_REQUEST);
            }

			String query = "SELECT user_id FROM Users WHERE email=? AND password=?";
            ps_login = conn.prepareStatement(query);

            ps_login.setString(1, email);
            ps_login.setString(2, password);

            ResultSet rs_login = ps_login.executeQuery();

            if (rs_login.next()) {
                return new ResponseEntity("{\"message\": \"User has been logged in.\",\"access_token\": \"" + email + "\"}", responseHeader, HttpStatus.OK);
            }
            ps_login.close();
            rs_login.close();
            conn.close();

            return new ResponseEntity("{\"message\": \"Wrong email/password.\"}", responseHeader, HttpStatus.BAD_REQUEST);
		} catch(SQLException e) {
            e.printStackTrace();
            responseBody.put("message", "SQLException");
        }

        return new ResponseEntity(responseBody.toString(), responseHeader, HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value="/profile", method=RequestMethod.GET)
    public ResponseEntity<String> profile(HttpServletRequest request) {
        PreparedStatement ps_profile = null;
        HttpHeaders responseHeader = new HttpHeaders();
        JSONObject responseBody = new JSONObject();

        responseHeader.set("Content-Type", "application/json");
        initializeDBConnection();

        String email = request.getParameter("email");

        try {
			String query = "SELECT first_name, last_name, gender, zipcode, birthdate, description, picture, sleep, eat, neat, social, desired_zipcode, desired_gender, desired_rent FROM Users WHERE email=?";
            ps_profile = conn.prepareStatement(query);

            ps_profile.setString(1, email);

            ResultSet rs_profile = ps_profile.executeQuery();
            rs_profile.next();

            JSONObject response = new JSONObject();
            response.put("email", email);
            response.put("firstName", rs_profile.getString("first_name") == null ? "" : rs_profile.getString("first_name"));
            response.put("lastName", rs_profile.getString("last_name") == null ? "" : rs_profile.getString("last_name"));
            response.put("gender", rs_profile.getString("gender") == null ? "" : rs_profile.getString("gender"));
            response.put("zipcode", rs_profile.getString("zipcode") == null ? "" : rs_profile.getString("zipcode"));
            response.put("birthdate", rs_profile.getString("birthdate") == null ? "" : rs_profile.getString("birthdate"));
            response.put("description", rs_profile.getString("description") == null ? "" : rs_profile.getString("description"));
            response.put("picture", rs_profile.getString("picture") == null ? "" : rs_profile.getString("picture"));
            response.put("sleep", rs_profile.getString("sleep") == null ? "" : rs_profile.getString("sleep"));
            response.put("eat", rs_profile.getString("eat") == null ? "" : rs_profile.getString("eat"));
            response.put("neat", rs_profile.getString("neat") == null ? "" : rs_profile.getString("neat"));
            response.put("social", rs_profile.getString("social") == null ? "" : rs_profile.getString("social"));
            response.put("desiredZipcode", rs_profile.getString("desired_zipcode") == null ? "" : rs_profile.getString("desired_zipcode"));
            response.put("desiredGender", rs_profile.getString("desired_gender") == null ? "" : rs_profile.getString("desired_gender"));
            response.put("desiredRent", rs_profile.getString("desired_rent") == null ? "" : rs_profile.getString("desired_rent"));

            ps_profile.close();
            rs_profile.close();
            conn.close();

            return new ResponseEntity(response.toString(), responseHeader, HttpStatus.OK);
		} catch(SQLException e) {
            e.printStackTrace();
            responseBody.put("message", "SQLException");
        }

        return new ResponseEntity(responseBody.toString(), responseHeader, HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value="/edit", method=RequestMethod.POST)
    public ResponseEntity<String> edit(@RequestBody String body, HttpServletRequest request) {
        PreparedStatement ps_edit = null;
        HttpHeaders responseHeader = new HttpHeaders();
        JSONObject responseBody = new JSONObject();

        responseHeader.set("Content-Type", "application/json");
        initializeDBConnection();

		try {
			JSONObject bodyObj = new JSONObject(body);
            String email = bodyObj.getString("email");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date date = format.parse(bodyObj.getString("birthdate"));
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());

            String query = "UPDATE Users SET first_name=?, last_name=?, gender=?, zipcode=?, birthdate=?, description=?, picture=?, sleep=?, eat=?, neat=?, social=?, desired_zipcode=?, desired_gender=?, desired_rent=? WHERE email=?";
            ps_edit = conn.prepareStatement(query);

            ps_edit.setString(1, bodyObj.getString("firstName"));
            ps_edit.setString(2, bodyObj.getString("lastName"));
            ps_edit.setString(3, bodyObj.getString("gender"));
            ps_edit.setString(4, bodyObj.getString("zipcode"));
            ps_edit.setDate(5, sqlDate);
            ps_edit.setString(6, bodyObj.getString("description"));
            ps_edit.setString(7, bodyObj.getString("picture"));
            ps_edit.setInt(8, bodyObj.getInt("sleep"));
            ps_edit.setInt(9, bodyObj.getInt("eat"));
            ps_edit.setInt(10, bodyObj.getInt("neat"));
            ps_edit.setInt(11, bodyObj.getInt("social"));
            ps_edit.setString(12, bodyObj.getString("desiredZipcode"));
            ps_edit.setString(13, bodyObj.getString("desiredGender"));
            ps_edit.setInt(14, bodyObj.getInt("desiredRent"));
            ps_edit.setString(15, email);

            ps_edit.executeUpdate();
            ps_edit.close();
            conn.close();

            return new ResponseEntity("{\"message\": \"Profile has been edited.\"}", responseHeader, HttpStatus.OK);
		} catch(JSONException e) {
			e.printStackTrace();
            responseBody.put("message", "JSONException");
        } catch(SQLException e) {
            e.printStackTrace();
            responseBody.put("message", "SQLException");
        } catch(ParseException e) {
            e.printStackTrace();
            responseBody.put("message", "ParseException");
        }

		return new ResponseEntity(responseBody.toString(), responseHeader, HttpStatus.BAD_REQUEST);
    }


    private static void initializeDBConnection() {
        try {
            File file = new File("../src/main/java/roommates/configs/dbparams.txt");
            Scanner sc = new Scanner(file);
            try {
                final String USER = sc.nextLine();
                final String PASSWORD = sc.nextLine();

                Class.forName(JDBC_DRIVER);
                conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            } catch(NoSuchElementException e) {
                e.printStackTrace();
            }

            sc.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private String hashPassword(String password) {
        MessageDigest digest = null;
		String hashedPassword = null;

		try {
			digest = MessageDigest.getInstance("SHA-256");
			hashedPassword = bytesToHex(digest.digest(password.getBytes("UTF-8")));

		} catch(UnsupportedEncodingException e) {
            e.printStackTrace();
		} catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
		}

        return hashedPassword;
    }

    private static String bytesToHex(byte[] in) {
		StringBuilder builder = new StringBuilder();
		for(byte b: in) {
			builder.append(String.format("%02x", b));
		}
		return builder.toString();
	}

    private Boolean checkIfUserExists(String email) {
        try {
            String query = "SELECT email FROM Users WHERE email=?";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, email);

            ResultSet rs_check = ps.executeQuery();
            if (!rs_check.next()) {
                return false;
            } else {
                return true;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    private Boolean checkPasswords(String password, String confirmPassword) {
        if (password.equals(confirmPassword)) {
            return true;
        }

        return false;
    }
}
