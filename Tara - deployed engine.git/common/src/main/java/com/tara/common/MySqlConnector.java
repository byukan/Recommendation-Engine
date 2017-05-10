package com.tara.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class MySqlConnector {

	public static void main(String[] args) {
		MySqlConnector connector = new MySqlConnector();
		// System.out.println(connector.insertCourse("1", "intro to cs"));
		System.out.println(connector.updateCourse("100", "intro to cs",
				"intro-to-cs"));
		// System.out.println(connector.insertRecommendation("123", "21"));
		// System.out.println(connector.markRecommendationTaken("123", "21"));
	}

	public MySqlConnector() {

	}

	private Connection getDatabaseConnection() throws SQLException {
		return DriverManager.getConnection(Configuration.MYSQLDBURL,
				Configuration.MYSQLDBUSERNAME, Configuration.MYSQLDBPASSWORD);
	}

	public int insertCourseTaken(String userId, String courseId) {
		int retId = -1;
		String query = "INSERT INTO takencourses (userId, courseId) VALUES (?,?)";
		Connection connection = null;
		try {

			Class.forName(Configuration.MYSQLJDBCDRIVER).newInstance();

			connection = getDatabaseConnection();

			PreparedStatement statement = connection.prepareStatement(query,
					Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, userId);
			statement.setString(2, courseId);

			int rowsInserted = statement.executeUpdate();
			if (rowsInserted > 0) {
				ResultSet rs = statement.getGeneratedKeys();
				if (rs.next()) {
					retId = rs.getInt(1);
				}
			}
		} catch (Exception exp) {
			exp.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception exp) {

				}
			}
		}
		return retId;

	}

	public Boolean isCourseTaken(String userId, String courseId) {
		String query = "SELECT * FROM takencourses WHERE userId=? AND courseId=?";
		Boolean result = null;
		Connection connection = null;
		try {
			Class.forName(Configuration.MYSQLJDBCDRIVER).newInstance();

			connection = getDatabaseConnection();
			PreparedStatement statement = connection.prepareStatement(query);

			statement.setString(1, userId);
			statement.setString(2, courseId);
			ResultSet resultSet = statement.executeQuery();
			result = resultSet.next();
		} catch (Exception exp) {
			exp.printStackTrace();
			return null;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception exp) {

				}
			}
		}
		return result;
	}

	public int insertRecommendation(String userId, String courseId) {
		int retId = -1;
		String query = "INSERT INTO recommendations (userId, courseId, date,taken) VALUES (?,?,?,?)";
		Connection connection = null;
		try {

			Class.forName(Configuration.MYSQLJDBCDRIVER).newInstance();

			connection = getDatabaseConnection();

			PreparedStatement statement = connection.prepareStatement(query,
					Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, userId);
			statement.setString(2, courseId);
			statement.setLong(3, Utils.getCurrentUtcTimeInMillis());
			statement.setBoolean(4, false);

			int rowsInserted = statement.executeUpdate();
			if (rowsInserted > 0) {
				ResultSet rs = statement.getGeneratedKeys();
				if (rs.next()) {
					retId = rs.getInt(1);
				}
			}
		} catch (Exception exp) {
			exp.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception exp) {

				}
			}
		}
		return retId;
	}

	public Boolean checkLookupCourse(String courseId) {
		String query = "SELECT * FROM lookupcourses WHERE courseId=?";
		Boolean result = null;
		Connection connection = null;
		try {
			Class.forName(Configuration.MYSQLJDBCDRIVER).newInstance();

			connection = getDatabaseConnection();
			PreparedStatement statement = connection.prepareStatement(query);

			statement.setString(1, courseId);
			ResultSet resultSet = statement.executeQuery();
			result = resultSet.next();
		} catch (Exception exp) {
			exp.printStackTrace();
			return null;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception exp) {

				}
			}
		}
		return result;
	}

	public int insertLookupCourse(String id) {
		int retId = -1;
		String query = "INSERT INTO lookupcourses (courseId) VALUES (?)";
		Connection connection = null;
		try {

			Class.forName(Configuration.MYSQLJDBCDRIVER).newInstance();

			connection = getDatabaseConnection();

			PreparedStatement statement = connection.prepareStatement(query,
					Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, id);

			int rowsInserted = statement.executeUpdate();
			if (rowsInserted > 0) {
				retId = 1;
			}
		} catch (Exception exp) {
			exp.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception exp) {

				}
			}
		}
		return retId;
	}

	public int insertCourse(String id, String title) {
		int retId = -1;
		String query = "INSERT INTO courses (title, currentId,allIds,updateCount) VALUES (?,?,?,0)";
		Connection connection = null;
		try {

			Class.forName(Configuration.MYSQLJDBCDRIVER).newInstance();

			connection = getDatabaseConnection();

			PreparedStatement statement = connection.prepareStatement(query,
					Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, title);
			statement.setString(2, id);
			statement.setString(3, id);

			int rowsInserted = statement.executeUpdate();
			if (rowsInserted > 0) {
				retId = 1;
			}
		} catch (Exception exp) {
			exp.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception exp) {

				}
			}
		}
		return retId;
	}

	public String getCourseId(String id) {
		String query = "SELECT allIds FROM courses WHERE allIds Like ? OR allIds Like ? OR allIds Like ?";
		String result = null;
		Connection connection = null;
		try {
			Class.forName(Configuration.MYSQLJDBCDRIVER).newInstance();

			connection = getDatabaseConnection();
			PreparedStatement statement = connection.prepareStatement(query);

			statement.setString(1, "%," + id + ",%");
			statement.setString(2, id + ",%");
			statement.setString(3, "%," + id);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {

				result = resultSet.getString(1);

			}
		} catch (Exception exp) {
			exp.printStackTrace();
			result = null;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception exp) {

				}
			}
		}
		return result;
	}

	public boolean updateCourse(String id, String oldTitle, String newTitle) {
		boolean updated = false;

		String query = "UPDATE courses SET currentId=?,allIds=CONCAT(?,allIds),title=?,updateCount=updateCount+1 WHERE title=?";
		Connection connection = null;
		try {
			Class.forName(Configuration.MYSQLJDBCDRIVER).newInstance();

			connection = getDatabaseConnection();
			PreparedStatement statement = connection.prepareStatement(query);

			statement.setString(1, id);
			statement.setString(2, id + ",");
			statement.setString(3, newTitle);
			statement.setString(4, oldTitle);

			int rowsUpdated = statement.executeUpdate();
			if (rowsUpdated > 0) {
				updated = true;
			}
		} catch (Exception exp) {
			exp.printStackTrace();

			updated = false;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception exp) {

				}
			}
		}
		return updated;
	}

	public boolean addFeedback(String userId, String courseId, String feedback,
			float rating) {
		boolean updated = false;
		String possibleIds = getCourseId(courseId);
		String[] splits = possibleIds.split(",");
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < splits.length; i++) {
			builder.append("?,");
		}

		String query = "UPDATE recommendations SET feedback=? , rating=? WHERE userId=? AND courseId IN ("
				+ builder.deleteCharAt(builder.length() - 1).toString() + ")";
		Connection connection = null;
		try {
			Class.forName(Configuration.MYSQLJDBCDRIVER).newInstance();

			connection = getDatabaseConnection();
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, feedback);
			statement.setFloat(2, rating);
			statement.setString(3, userId);
			statement.setString(4, possibleIds);

			int rowsUpdated = statement.executeUpdate();
			if (rowsUpdated > 0) {
				updated = true;
			}
		} catch (Exception exp) {
			exp.printStackTrace();

			updated = false;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception exp) {
					exp.printStackTrace();
				}
			}
		}
		return updated;
	}

	public boolean markRecommendationTaken(String userId, String courseId) {
		boolean updated = false;
		String possibleIds = getCourseId(courseId);
		System.out.println("for " + courseId + ",possibleIds=" + possibleIds);
		String[] splits = possibleIds.split(",");
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < splits.length; i++) {
			builder.append("?,");
		}

		String query = "UPDATE recommendations SET taken=? WHERE userId=? AND courseId IN ("
				+ builder.deleteCharAt(builder.length() - 1).toString() + ")";
		Connection connection = null;
		try {
			Class.forName(Configuration.MYSQLJDBCDRIVER).newInstance();

			connection = getDatabaseConnection();
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setBoolean(1, true);
			statement.setString(2, userId);
			int index = 3;
			for (Object o : splits) {
				statement.setObject(index++, o);
			}

			int rowsUpdated = statement.executeUpdate();
			if (rowsUpdated > 0) {
				updated = true;
			}
		} catch (Exception exp) {
			exp.printStackTrace();

			updated = false;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception exp) {

				}
			}
		}
		return updated;
	}

	public ArrayList<RecommendationDetail> getRecommendations(long startDate,
			long endDate, boolean includeDateInQuery, String optionalFilter,
			int count) {
		ArrayList<RecommendationDetail> result = new ArrayList<RecommendationDetail>();
		String query = "SELECT userId,courseId,taken FROM recommendations limit ?";
		if (includeDateInQuery) {
			if (!optionalFilter.equals(Constants.INCLUDEALL)) {
				query = "SELECT userId,courseId,taken FROM recommendations WHERE date >=? AND date < ? AND taken=? limit ?";
			} else {
				query = "SELECT userId,courseId,taken FROM recommendations WHERE date >=? AND date < ? limit ?";
			}
		} else {
			if (!optionalFilter.equals(Constants.INCLUDEALL)) {
				query = "SELECT userId,courseId,taken FROM recommendations WHERE taken=? limit ?";
			} else {
				query = "SELECT userId,courseId,taken FROM recommendations limit ?";
			}
		}

		Connection connection = null;
		try {
			Class.forName(Configuration.MYSQLJDBCDRIVER).newInstance();

			connection = getDatabaseConnection();
			PreparedStatement statement = connection.prepareStatement(query);
			if (includeDateInQuery) {
				if (!optionalFilter.equals(Constants.INCLUDEALL)) {
					statement.setLong(1, startDate);
					statement.setLong(2, endDate);
					if (optionalFilter.equals(Constants.INCLUDENOTTAKEN))
						statement.setBoolean(3, false);
					else
						statement.setBoolean(3, true);
					statement.setInt(4, count);

				} else {
					statement.setLong(1, startDate);
					statement.setLong(2, endDate);
					statement.setInt(3, count);
				}
			} else {
				if (!optionalFilter.equals(Constants.INCLUDEALL)) {
					if (optionalFilter.equals(Constants.INCLUDENOTTAKEN))
						statement.setBoolean(1, false);
					else
						statement.setBoolean(1, true);
					statement.setInt(2, count);
				} else {
					statement.setInt(1, count);
				}
			}

			ResultSet recommendations = statement.executeQuery();
			while (recommendations.next()) {
				result.add(new RecommendationDetail(recommendations
						.getString(1), recommendations.getString(2),
						recommendations.getInt(3)));
			}

		} catch (Exception exp) {
			exp.printStackTrace();
			result = null;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception exp) {

				}
			}
		}
		return result;
	}

	public int getRecommendationsCount(long startDate, long endDate,
			boolean includeDateInQuery, String optionalFilter) {
		String query = null;
		int result = -1;
		if (includeDateInQuery) {
			if (!optionalFilter.equals(Constants.INCLUDEALL)) {
				query = "SELECT COUNT(*) FROM recommendations WHERE date >=? AND date < ? AND taken=?";
			} else {
				query = "SELECT COUNT(*) FROM recommendations WHERE date >=? AND date < ?";
			}
		} else {
			if (!optionalFilter.equals(Constants.INCLUDEALL)) {
				query = "SELECT COUNT(*) FROM recommendations WHERE taken=?";
			} else {
				query = "SELECT COUNT(*) FROM recommendations";
			}
		}
		Connection connection = null;
		try {
			Class.forName(Configuration.MYSQLJDBCDRIVER).newInstance();

			connection = getDatabaseConnection();
			PreparedStatement statement = connection.prepareStatement(query);
			if (includeDateInQuery) {
				statement.setLong(1, startDate);
				statement.setLong(2, endDate);
				if (optionalFilter.equals(Constants.INCLUDENOTTAKEN)) {
					statement.setBoolean(3, false);
				} else if (optionalFilter.equals(Constants.INCLUDETAKEN)) {
					statement.setBoolean(3, true);
				}
			} else {
				if (optionalFilter.equals(Constants.INCLUDENOTTAKEN)) {
					statement.setBoolean(1, false);
				} else if (optionalFilter.equals(Constants.INCLUDETAKEN)) {
					statement.setBoolean(1, true);
				}
			}
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				result = resultSet.getInt(1);
			} else {
				result = 0;
			}
		} catch (Exception exp) {
			exp.printStackTrace();
			result = -1;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception exp) {

				}
			}
		}
		return result;
	}

	public ArrayList<Recommendation> getRecommendationsCountByUser(
			long startDate, long endDate, boolean includeDateInQuery,
			String optionalFilter, int count) {
		ArrayList<Recommendation> output = new ArrayList<Recommendation>();
		String query = null;
		if (count == -1) {
			count = Integer.MAX_VALUE;
		}
		if (includeDateInQuery) {
			if (!optionalFilter.equals(Constants.INCLUDEALL)) {
				query = "SELECT userId,COUNT(*) FROM recommendations WHERE date >=? AND date < ? AND taken=? GROUP BY userId limit ?";
			} else {
				query = "SELECT userId,COUNT(*) FROM recommendations WHERE date >=? AND date < ? GROUP BY userId limit ?";
			}
		} else {
			if (!optionalFilter.equals(Constants.INCLUDEALL)) {
				query = "SELECT userId,COUNT(*) FROM recommendations WHERE taken=? GROUP BY userId limit ?";
			} else {
				query = "SELECT userId,COUNT(*) FROM recommendations GROUP BY userId limit ?";
			}
		}

		Connection connection = null;
		try {
			Class.forName(Configuration.MYSQLJDBCDRIVER).newInstance();

			connection = getDatabaseConnection();
			PreparedStatement statement = connection.prepareStatement(query);
			if (includeDateInQuery) {
				statement.setLong(1, startDate);
				statement.setLong(2, endDate);
				if (optionalFilter.equals(Constants.INCLUDENOTTAKEN)) {
					statement.setInt(4, count);
					statement.setBoolean(3, false);
				} else if (optionalFilter.equals(Constants.INCLUDETAKEN)) {
					statement.setInt(4, count);
					statement.setBoolean(3, true);
				}

			} else {
				if (optionalFilter.equals(Constants.INCLUDENOTTAKEN)) {
					statement.setInt(2, count);
					statement.setBoolean(1, false);
				} else if (optionalFilter.equals(Constants.INCLUDETAKEN)) {
					statement.setInt(2, count);
					statement.setBoolean(1, true);
				} else {
					statement.setInt(1, count);
				}

			}

			ResultSet recommendations = statement.executeQuery();
			while (recommendations.next()) {
				output.add(new Recommendation(recommendations.getString(1),
						recommendations.getInt(2)));
			}
		} catch (Exception exp) {
			exp.printStackTrace();
			output = null;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception exp) {

				}
			}
		}
		return output;
	}
}
