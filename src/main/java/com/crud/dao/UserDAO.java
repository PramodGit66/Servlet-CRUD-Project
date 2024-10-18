package com.crud.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.crud.model.User;
import com.crud.util.DBUtil;

// this DAO class provides CRUD database operations for the table in the database
public class UserDAO {
	

	private static final String INSERT_USER_SQL = "INSERT INTO users (name,city) VALUES (?, ?);";
	private static final String SELECT_USER_BY_ID = "SELECT id, name, city FROM users WHERE id = ?";
	private static final String SELECT_ALL_USERS = "SELECT * FROM users";
	private static final String DELETE_USERS_SQL = "DELETE FROM users WHERE id = ?;";
	private static final String UPDATE_USERS_SQL = "UPDATE users SET name = ?, city =? WHERE id = ?;";
	
	
	
	//create user or insert user 
	 public void insertUser(User user) throws SQLException {
	        try (Connection connection = DBUtil.getConnection();
	             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_SQL)) {
	            preparedStatement.setString(1, user.getName());
	            preparedStatement.setString(2, user.getCity());
	            preparedStatement.executeUpdate();
	        } catch (SQLException e) {
	              e.printStackTrace();
	        }
	    }
	 
	//update user
	 public boolean updateUser(User user) throws SQLException {
	        boolean rowUpdated;
	        try (Connection connection = DBUtil.getConnection();
	             PreparedStatement statement = connection.prepareStatement(UPDATE_USERS_SQL);) {
	            statement.setString(1, user.getName());
	            statement.setString(2, user.getCity());
	            statement.setInt(3, user.getId());
	            rowUpdated = statement.executeUpdate() > 0;
	        }
	        return rowUpdated;
	    }

	 
	//select user by id 
	 public User selectUser(int id) {
	        User user = null;
	        try (Connection connection = DBUtil.getConnection();
	             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID);) {
	            preparedStatement.setInt(1, id);
	            ResultSet rs = preparedStatement.executeQuery();

	            while (rs.next()) {
	                String name = rs.getString("name");
	                String city = rs.getString("city");
	                user = new User(id, name, city);
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return user;
	    }
	//select users
	 public List<User> selectAllUsers() {

	        List<User> users = new ArrayList<>();
	        try (Connection connection = DBUtil.getConnection();
	             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS);) {
	            ResultSet rs = preparedStatement.executeQuery();

	            while (rs.next()) {
	                int id = rs.getInt("id");
	                String name = rs.getString("name");
	                String city = rs.getString("city");
	                users.add(new User(id, name, city));
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return users;
	    }
	//delete user 
	 public boolean deleteUser(int id) throws SQLException {
	        boolean rowDeleted;
	        try (Connection connection = DBUtil.getConnection();
	             PreparedStatement statement = connection.prepareStatement(DELETE_USERS_SQL);) {
	            statement.setInt(1, id);
	            rowDeleted = statement.executeUpdate() > 0;
	        }
	        return rowDeleted;
	    }
	
     
}
