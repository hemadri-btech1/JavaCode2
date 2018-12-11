package com.core.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.core.jdbc.JdbcConnectionFactory;
import com.projo.Book;
import com.projo.Subject;
import com.utils.Utils;

public class JdbcDAOImpl implements IJdbcDAO {

	private static final String BOOK_DETAILS_ARE_SAVED_SUCCESSFULLY_WITH_BOOK_ID = "Book details are saved successfully with Book-ID : ";
	private static final String INSERT_INTO_BOOK_TITLE_PRICE_VOLUME_PUBLISH_DATE_FK_SUBJECT_ID_VALUES = "INSERT INTO Book (title, price,volume,publishDate,Fk_Subject_Id) values (?, ?, ?, ?, ?)";
	private static final String SUBJECT_DETAILS_ARE_SAVED_SUCCESSFULLY_WITH_ID = "Subject details are saved successfully With Subject-ID : ";
	private static final String INSERT_INTO_SUBJECT_TITLE_DURATION_IN_HOURS_VALUES = " INSERT INTO Subject (title, durationInHours) values (?,?) ";

	@Override
	public String addBook(Subject subject) {

		Connection connection = JdbcConnectionFactory.getConnection();

		try {
			// Insert parent component - Subject
			int subjectId = insertSubject(subject, connection);

			// If Subject is non zero - the Subject is saved successfully
			if (subjectId > 0) {
				for (Book book : subject.getBookList()) {
					insertBook(book, connection, subjectId);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					System.out.println("Exception while closing the connection....." + e.getMessage());
				}
			}
		}

		return null;
	}

	private void insertBook(Book book, Connection connection, int subjectId) throws SQLException {
		PreparedStatement pstmt;
		ResultSet rs;
		pstmt = connection.prepareStatement(INSERT_INTO_BOOK_TITLE_PRICE_VOLUME_PUBLISH_DATE_FK_SUBJECT_ID_VALUES);
		pstmt.setString(1, book.getTitle());
		pstmt.setDouble(2, book.getPrice());
		pstmt.setInt(3, book.getVolume());
		java.sql.Date sqlDate = java.sql.Date.valueOf(book.getPublishDate());
		pstmt.setDate(4, sqlDate);
		pstmt.setInt(5, subjectId);

		pstmt.executeUpdate();
		rs = pstmt.getGeneratedKeys();
		while (rs.next()) {
			System.out.println(BOOK_DETAILS_ARE_SAVED_SUCCESSFULLY_WITH_BOOK_ID + rs.getInt(1));
		}
	}

	/**
	 * 
	 * @param subject
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	private int insertSubject(Subject subject, Connection connection) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int subjectId = 0;
		try {
			pstmt = connection.prepareStatement(INSERT_INTO_SUBJECT_TITLE_DURATION_IN_HOURS_VALUES);

			pstmt.setString(1, subject.getTitle());
			pstmt.setInt(2, subject.getDurationInHours());

			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();

			while (rs.next()) {
				subjectId = rs.getInt(1);
				System.out.println(SUBJECT_DETAILS_ARE_SAVED_SUCCESSFULLY_WITH_ID + subjectId);

			}
		} catch (SQLException e) {
			System.out.println("SQLException while inserting Subject details....." + e.getMessage());
		} finally {
			closeAllResources(null, pstmt, rs);
		}
		return subjectId;
	}

	@Override
	public String deleteSubject(Subject subject) {

		Connection connection = JdbcConnectionFactory.getConnection();
		String operationStatus = null;

		// To Delete a subject - we should first delete child component BOOK
		// Retrieve BOOK ID for the given title
		int bookDeleteCount = deleteBookBySubjectId(connection, subject.getSubjectId());
		if (bookDeleteCount >= 0) {
			PreparedStatement pstmt = null;
			// Delete Subject
			int subDeleteCount = 0;
			try {
				pstmt = connection.prepareStatement(" DELETE FROM Subject where subjectid = ? ");
				pstmt.setInt(1, subject.getSubjectId());

				subDeleteCount = pstmt.executeUpdate();
			} catch (SQLException ex) {
				System.out.println("SQLException while deleting recordd.....");
				operationStatus = Utils.FAIL;
			} finally {
				closeAllResources(connection, pstmt, null);
			}

			operationStatus = getOperationStatus(subDeleteCount, operationStatus);

		}

		return operationStatus;
	}

	/**
	 * Delete book by subjectID
	 * 
	 * @param connection
	 * @param subjectId
	 * @return
	 */
	private int deleteBookBySubjectId(Connection connection, Integer subjectId) {
		int bookDeleteCount = 0;
		PreparedStatement pstmt = null;
		try {
			pstmt = connection.prepareStatement(" DELETE FROM BOOK where Fk_Subject_Id = ? ");
			pstmt.setInt(1, subjectId);

			bookDeleteCount = pstmt.executeUpdate();
		} catch (Exception ex) {

		} finally {
			closeAllResources(null, pstmt, null);
		}
		return bookDeleteCount;

	}

	@Override
	public String deleteBook(Book book) {
		Connection connection = JdbcConnectionFactory.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int noOfRows = 0;
		String operationStatus = "";

		try {
			pstmt = connection.prepareStatement("DELETE FROM Book where bookid = ?");
			pstmt.setInt(1, book.getBookid());
			noOfRows = pstmt.executeUpdate();
			if (noOfRows > 0) {
				System.out.println("Successfully deleted book for the id : " + book.getBookid() + " -> " + noOfRows);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			operationStatus = Utils.FAIL;
		} finally {
			closeAllResources(connection, pstmt, rs);
		}

		getOperationStatus(noOfRows, operationStatus);

		return null;
	}

	private String getOperationStatus(int noOfRows, String operationStatus) {
		// if (operationStatus != null && operationStatus.isEmpty()) {
		if (noOfRows > 0) {
			operationStatus = Utils.SUCCESS;
		} else {
			operationStatus = Utils.NOT_FOUND;
		}

		// }
		return operationStatus;
	}

	/**
	 * This is o make-sure that all the JDBC resources are closed.
	 * 
	 * @param connection
	 * @param pstmt
	 * @param rs
	 */
	private void closeAllResources(Connection connection, PreparedStatement pstmt, ResultSet rs) {

		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (pstmt != null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Book searchBook(Book inputBook) {
		Connection connection = JdbcConnectionFactory.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Book bookResult = null;
		try {
			pstmt = connection.prepareStatement("SELECT * FROM BOOK WHERE BOOKID = ? ");
			pstmt.setInt(1, inputBook.getBookid());

			rs = pstmt.executeQuery();
			while (rs.next()) {
				bookResult = new Book();
				bookResult.setPrice(rs.getDouble("price"));
				bookResult.setPublishDate(rs.getDate("publishDate").toLocalDate());
				bookResult.setTitle(rs.getString("title"));
				bookResult.setVolume(rs.getInt("volume"));
				bookResult.setBookid(rs.getInt("bookid"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return bookResult;
	}

	/**
	 * This is to search subject alone...
	 */
	@Override
	public Subject searchSubject(Subject subjectForSearch) {
		Connection connection = JdbcConnectionFactory.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Subject subject = null;
		try {
			pstmt = connection.prepareStatement("SELECT * FROM Subject WHERE subjectid = ? ");
			pstmt.setInt(1, subjectForSearch.getSubjectId());

			rs = pstmt.executeQuery();
			while (rs.next()) {
				subject = new Subject();
				subject.setTitle(rs.getString("title"));
				subject.setDurationInHours(rs.getInt("durationInHours"));
				subject.setSubjectId(rs.getInt("subjectid"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return subject;
	}

	@Override
	public List<Book> retrieveAllTheBooks() {
		Connection connection = JdbcConnectionFactory.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Book> bookList = new ArrayList<>();
		try {
			pstmt = connection.prepareStatement("SELECT * FROM Book ");
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Book bookResult = new Book();
				bookResult.setPrice(rs.getDouble("price"));
				bookResult.setPublishDate(rs.getDate("publishDate").toLocalDate());
				bookResult.setTitle(rs.getString("title"));
				bookResult.setVolume(rs.getInt("volume"));
				bookResult.setBookid(rs.getInt("bookid"));

				bookList.add(bookResult);
			}

		} catch (SQLException e) {
			System.out.println("Exception while retrieving all the Books... " + e.getMessage());
		}
		return bookList;
	}

	@Override
	public List<Subject> retrieveAllTheSubjects() {
		Connection connection = JdbcConnectionFactory.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Subject> subjectList = new ArrayList<>();
		try {
			pstmt = connection.prepareStatement("SELECT * FROM Subject ");

			rs = pstmt.executeQuery();
			while (rs.next()) {
				Subject subject = new Subject();
				subject.setTitle(rs.getString("title"));
				subject.setDurationInHours(rs.getInt("durationInHours"));
				subject.setSubjectId(rs.getInt("subjectid"));
				subjectList.add(subject);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return subjectList;
	}

}
