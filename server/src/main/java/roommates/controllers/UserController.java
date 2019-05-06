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
			String query = "SELECT first_name, last_name, gender, zipcode, birthdate, description, picture, sleep, eat, neat, social, desired_zipcode, desired_gender, desired_rent, user_id FROM Users WHERE email=?";
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
            response.put("userId", rs_profile.getString("user_id"));

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

    @RequestMapping(value="/explore", method=RequestMethod.GET)
    public ResponseEntity<String> explore(HttpServletRequest request) {
        PreparedStatement ps_explore = null;
        HttpHeaders responseHeader = new HttpHeaders();
        JSONObject responseBody = new JSONObject();

        responseHeader.set("Content-Type", "application/json");
        initializeDBConnection();

        String desired_gender = request.getParameter("desired_gender");
        String gender = request.getParameter("gender");
        String desired_zipcode = request.getParameter("desired_zipcode");
        String desired_rent = request.getParameter("desired_rent");

        try {
            String query = "SELECT user_id, first_name, last_name, description, picture, sleep, eat, neat, social FROM users WHERE gender=? AND desired_gender=? AND abs(desired_zipcode-?) < 1000 AND abs(desired_rent-?) < 1000";
            ps_explore = conn.prepareStatement(query);

            ps_explore.setString(1, desired_gender);
            ps_explore.setString(2, gender);
            ps_explore.setString(3, desired_zipcode);
            ps_explore.setString(4, desired_rent);

            ResultSet rs_explore = ps_explore.executeQuery();
            JSONArray response = new JSONArray();

            while(rs_explore.next()) {
                JSONObject temp = new JSONObject();
                temp.put("userId", rs_explore.getString("user_id") == null ? "" : rs_explore.getString("user_id"));
                temp.put("firstName", rs_explore.getString("first_name") == null ? "" : rs_explore.getString("first_name"));
                temp.put("lastName", rs_explore.getString("last_name") == null ? "" : rs_explore.getString("last_name"));
                temp.put("description", rs_explore.getString("description") == null ? "" : rs_explore.getString("description"));
                temp.put("picture", rs_explore.getString("picture") == null ? "" : rs_explore.getString("picture"));
                temp.put("sleep", rs_explore.getString("sleep") == null ? "" : rs_explore.getString("sleep"));
                temp.put("eat", rs_explore.getString("eat") == null ? "" : rs_explore.getString("eat"));
                temp.put("neat", rs_explore.getString("neat") == null ? "" : rs_explore.getString("neat"));
                temp.put("social", rs_explore.getString("social") == null ? "" : rs_explore.getString("social"));

                response.put(temp);
            }

            ps_explore.close();
            rs_explore.close();
            conn.close();

            return new ResponseEntity(response.toString(), responseHeader, HttpStatus.OK);
        } catch(SQLException e) {
            e.printStackTrace();
            responseBody.put("message", "SQLException");
        }

        return new ResponseEntity(responseBody.toString(), responseHeader, HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value="/getlikes", method=RequestMethod.GET)
    public ResponseEntity<String> getLikes(HttpServletRequest request) {
        PreparedStatement ps_likes = null;
        HttpHeaders responseHeader = new HttpHeaders();
        JSONObject responseBody = new JSONObject();

        responseHeader.set("Content-Type", "application/json");
        initializeDBConnection();

        String id = request.getParameter("user_id");

        try {
			String query = "SELECT user2_id FROM Likes WHERE user1_id=?";
            ps_likes = conn.prepareStatement(query);

            ps_likes.setString(1, id);

            ResultSet rs_likes = ps_likes.executeQuery();

            JSONArray response = new JSONArray();
            while(rs_likes.next()) {
                response.put(rs_likes.getString("user2_id"));
            }

            ps_likes.close();
            rs_likes.close();
            conn.close();

            return new ResponseEntity(response.toString(), responseHeader, HttpStatus.OK);
		} catch(SQLException e) {
            e.printStackTrace();
            responseBody.put("message", "SQLException");
        }

        return new ResponseEntity(responseBody.toString(), responseHeader, HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value="/getdislikes", method=RequestMethod.GET)
    public ResponseEntity<String> getDislikes(HttpServletRequest request) {
        PreparedStatement ps_dislikes = null;
        HttpHeaders responseHeader = new HttpHeaders();
        JSONObject responseBody = new JSONObject();

        responseHeader.set("Content-Type", "application/json");
        initializeDBConnection();

        String id = request.getParameter("user_id");

        try {
			String query = "SELECT user2_id FROM Dislikes WHERE user1_id=?";
            ps_dislikes = conn.prepareStatement(query);

            ps_dislikes.setString(1, id);

            ResultSet rs_dislikes = ps_dislikes.executeQuery();

            JSONArray response = new JSONArray();
            while(rs_dislikes.next()) {
                response.put(rs_dislikes.getString("user2_id"));
            }

            ps_dislikes.close();
            rs_dislikes.close();
            conn.close();

            return new ResponseEntity(response.toString(), responseHeader, HttpStatus.OK);
		} catch(SQLException e) {
            e.printStackTrace();
            responseBody.put("message", "SQLException");
        }

        return new ResponseEntity(responseBody.toString(), responseHeader, HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value="/like", method=RequestMethod.POST)
    public ResponseEntity<String> like(@RequestBody String body, HttpServletRequest request) {
        PreparedStatement ps_like = null;
        HttpHeaders responseHeader = new HttpHeaders();
        JSONObject responseBody = new JSONObject();

        responseHeader.set("Content-Type", "application/json");
        initializeDBConnection();

        String id = request.getParameter("user_id");

        try {
			String query = "INSERT INTO Likes SET user1_id=?, user2_id=?";
            ps_like = conn.prepareStatement(query);

            ps_like.setString(1, id);
            ps_like.setString(2, body);

            ps_like.executeUpdate();
            responseBody.put("message", "User has been liked.");

            ps_like.close();
            conn.close();

            return new ResponseEntity(responseBody.toString(), responseHeader, HttpStatus.OK);
		} catch(SQLException e) {
            e.printStackTrace();
            responseBody.put("message", "SQLException");
        }

        return new ResponseEntity(responseBody.toString(), responseHeader, HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value="/dislike", method=RequestMethod.POST)
    public ResponseEntity<String> dislike(@RequestBody String body, HttpServletRequest request) {
        PreparedStatement ps_dislike = null;
        HttpHeaders responseHeader = new HttpHeaders();
        JSONObject responseBody = new JSONObject();

        responseHeader.set("Content-Type", "application/json");
        initializeDBConnection();

        String id = request.getParameter("user_id");

        try {
			String query = "INSERT INTO Dislikes SET user1_id=?, user2_id=?";
            ps_dislike = conn.prepareStatement(query);

            ps_dislike.setString(1, id);
            ps_dislike.setString(2, body);

            ps_dislike.executeUpdate();
            responseBody.put("message", "User has been disliked.");

            ps_dislike.close();
            conn.close();

            return new ResponseEntity(responseBody.toString(), responseHeader, HttpStatus.OK);
		} catch(SQLException e) {
            e.printStackTrace();
            responseBody.put("message", "SQLException");
        }

        return new ResponseEntity(responseBody.toString(), responseHeader, HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value="/match", method=RequestMethod.POST)
    public ResponseEntity<String> match(@RequestBody String body, HttpServletRequest request) {
        PreparedStatement ps_match = null;
        HttpHeaders responseHeader = new HttpHeaders();
        JSONObject responseBody = new JSONObject();

        responseHeader.set("Content-Type", "application/json");
        initializeDBConnection();

        try {
            JSONObject bodyObj = new JSONObject(body);
            String user1_id = bodyObj.getString("user1_id");
            String user2_id = bodyObj.getString("user2_id");

            String query = "INSERT INTO Matches SET user1_id=?, user2_id=?";
            ps_match = conn.prepareStatement(query);

            ps_match.setString(1, user1_id);
            ps_match.setString(2, user2_id);

            ps_match.executeUpdate();
            responseBody.put("message", "You have a match!");

            ps_match.close();
            conn.close();

            return new ResponseEntity(responseBody.toString(), responseHeader, HttpStatus.OK);
        } catch(SQLException e) {
            e.printStackTrace();
            responseBody.put("message", "SQLException");
        }

        return new ResponseEntity(responseBody.toString(), responseHeader, HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value="/getmatches", method=RequestMethod.GET)
    public ResponseEntity<String> getMatches(HttpServletRequest request) {
        PreparedStatement ps_matches1 = null;
        PreparedStatement ps_matches2 = null;
        HttpHeaders responseHeader = new HttpHeaders();
        JSONObject responseBody = new JSONObject();

        responseHeader.set("Content-Type", "application/json");
        initializeDBConnection();

        String id = request.getParameter("user_id");

        try {
            String query1 = "SELECT user2_id, first_name, picture, chat_id FROM matches, users WHERE user1_id=? AND user2_id=user_id";
            ps_matches1 = conn.prepareStatement(query1);

            ps_matches1.setString(1, id);

            ResultSet rs_matches1 = ps_matches1.executeQuery();
            JSONArray response = new JSONArray();

            while(rs_matches1.next()) {
                JSONObject temp = new JSONObject();
                temp.put("userId", rs_matches1.getString("user2_id"));
                temp.put("firstName", rs_matches1.getString("first_name"));
                temp.put("picture", rs_matches1.getString("picture"));
                temp.put("chatId", rs_matches1.getString("chat_id"));

                response.put(temp);
            }

            ps_matches1.close();
            rs_matches1.close();

            String query = "SELECT user1_id, first_name, picture, chat_id FROM matches, users WHERE user2_id=? AND user1_id=user_id";
            ps_matches2 = conn.prepareStatement(query);

            ps_matches2.setString(1, id);

            ResultSet rs_matches2 = ps_matches2.executeQuery();

            while(rs_matches2.next()) {
                JSONObject temp = new JSONObject();
                temp.put("userId", rs_matches2.getString("user1_id"));
                temp.put("firstName", rs_matches2.getString("first_name"));
                temp.put("picture", rs_matches2.getString("picture"));
                temp.put("chatId", rs_matches2.getString("chat_id"));

                response.put(temp);
            }

            ps_matches2.close();
            rs_matches2.close();
            conn.close();

            return new ResponseEntity(response.toString(), responseHeader, HttpStatus.OK);
        } catch(SQLException e) {
            e.printStackTrace();
            responseBody.put("message", "SQLException");
        }

        return new ResponseEntity(responseBody.toString(), responseHeader, HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value="/message", method=RequestMethod.POST)
    public ResponseEntity<String> message(@RequestBody String body, HttpServletRequest request) {
        PreparedStatement ps_message = null;
        PreparedStatement ps_message_id = null;
        PreparedStatement ps_chatroom = null;
        HttpHeaders responseHeader = new HttpHeaders();
        JSONObject responseBody = new JSONObject();

        responseHeader.set("Content-Type", "application/json");
        initializeDBConnection();

        try {
            JSONObject bodyObj = new JSONObject(body);
			String query_message = "INSERT INTO Messages SET user_id=?, message=?";
            ps_message = conn.prepareStatement(query_message);

            ps_message.setInt(1, Integer.parseInt(bodyObj.getString("user_id")));
            ps_message.setString(2, bodyObj.getString("message"));

            ps_message.executeUpdate();
            ps_message.close();

            String query_message_id = "SELECT message_id FROM Messages WHERE user_id=? AND message=?";
            ps_message_id = conn.prepareStatement(query_message_id);

            ps_message_id.setInt(1, Integer.parseInt(bodyObj.getString("user_id")));
            ps_message_id.setString(2, bodyObj.getString("message"));

            ResultSet rs_message_id = ps_message_id.executeQuery();
            rs_message_id.next();
            int message_id = rs_message_id.getInt("message_id");

            ps_message_id.close();
            rs_message_id.close();

            String query_chatroom = "INSERT INTO Chatrooms SET chat_id=?, message_id=?";
            ps_chatroom = conn.prepareStatement(query_chatroom);

            ps_chatroom.setInt(1, Integer.parseInt(bodyObj.getString("chat_id")));
            ps_chatroom.setInt(2, message_id);

            ps_chatroom.executeUpdate();
            ps_chatroom.close();

            responseBody.put("message", "Message has been saved.");
            conn.close();

            return new ResponseEntity(responseBody.toString(), responseHeader, HttpStatus.OK);
		} catch(SQLException e) {
            e.printStackTrace();
            responseBody.put("message", "SQLException");
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
