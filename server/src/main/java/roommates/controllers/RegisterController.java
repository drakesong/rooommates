package com.roommates.roommates;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.json.*;

import javax.servlet.http.*;

import java.sql.*;
import java.io.*;
import java.util.*;
import java.security.*;

@RestController
public class RegisterController {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/Roommates";
    static Connection conn = null;

    @RequestMapping(value="/register", method=RequestMethod.POST)
    public ResponseEntity<String> register(@RequestBody String body, HttpServletRequest request) {
        PreparedStatement ps = null;
        HttpHeaders responseHeader = new HttpHeaders();

        responseHeader.set("Content-Type", "application/json");
        initializeDBConnection();

		try {
			JSONObject bodyObj = new JSONObject(body);
            String hashedPassword = hashPassword(bodyObj.getString("password"));

            if (checkIfUserExists(bodyObj.getString("email"))) {
                return new ResponseEntity("{\"message\": \"Email is already registered.\"}", responseHeader, HttpStatus.BAD_REQUEST);
            }

            String query = "INSERT INTO Users (email, password, first_name, last_name) VALUES (?, ?, ?, ?)";
            ps = conn.prepareStatement(query);

            ps.setString(1, bodyObj.getString("email"));
            ps.setString(2, hashedPassword);
            ps.setString(3, bodyObj.getString("first_name"));
            ps.setString(4, bodyObj.getString("last_name"));

            ps.executeUpdate();
            ps.close();

            return new ResponseEntity("{\"message\": \"User has been registered.\"}", responseHeader, HttpStatus.OK);
		} catch(JSONException e) {
			e.printStackTrace();
        } catch(SQLException e) {
            e.printStackTrace();
        }

		return new ResponseEntity("{\"message\": \"Error.\"}", responseHeader, HttpStatus.BAD_REQUEST);
	}

    public static void initializeDBConnection() {
        try {
            File file = new File("../src/main/java/roommates/configs/dbparams.txt");
            Scanner sc = new Scanner(file);
            final String USER = sc.nextLine();
            final String PASSWORD = sc.nextLine();
            sc. close();

            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public String hashPassword(String password) {
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

    public static String bytesToHex(byte[] in) {
		StringBuilder builder = new StringBuilder();
		for(byte b: in) {
			builder.append(String.format("%02x", b));
		}
		return builder.toString();
	}

    public Boolean checkIfUserExists(String email) {
        try {
            String query = "SELECT email FROM Users WHERE email=?";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return false;
            } else {
                return true;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return true;
    }
}
