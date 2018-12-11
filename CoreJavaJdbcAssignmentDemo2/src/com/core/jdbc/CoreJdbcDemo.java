package com.core.jdbc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.core.jdbc.dao.IJdbcDAO;
import com.core.jdbc.dao.JdbcDAOImpl;
import com.projo.Book;
import com.projo.Subject;
import com.utils.Utils;

public class CoreJdbcDemo {

	private static final String EXCEPTION_WHILE_READING_A_DATA_FROM_INPUT_OUTPUT_SYSTEM = "Exception while reading a data from input/output system.....";
	private static final String FAILED_TO_DELETE_BOTH_SUBJECT_AND_ASSOCIATED_BOOKS_FOR_THE_SUBJECT_ID = "Failed to delete both Subject and associated Books for the Subject ID.....";
	private static final String THE_PROVIDED_SUBJECT_ID_IS_NOT_AVAILABLE_IN_DATABASE = "The provided Subject ID is not available in database.....";
	private static final String SUCCESSFULLY_DELETED_BOTH_SUBJECT_AND_ASSOCIATED_BOOKS_FOR_THE_SUBJECT_ID = "Successfully deleted both Subject and associated Books for the Subject ID.....";

	public static void main(String[] args) throws ClassNotFoundException, SQLException {

		new CoreJdbcDemo().processMenuDrivenOperation();
	}

	/**
	 * Menu option to read a input from USER.....
	 */
	private void processMenuDrivenOperation() {

		BufferedReader br = new BufferedReader(new InputStreamReader((System.in)));

		boolean stopExecutionInd = true;
		while (stopExecutionInd) {
			System.out.println("-----Select any option(a or b or c or d or e or f or g or h or i) in the below list------");

			System.out.println("a. Add a Book");
			System.out.println("b. Delete a Subject");
			System.out.println("c. Delete a book");
			System.out.println("d. Search for a book");
			System.out.println("e. Search for a subject");
			System.out.println("f. Sort Book By Title");
			System.out.println("g. Sort Subject By Subject Title ");
			System.out.println("h. Sort Books by publish Date");
			System.out.println("i. Exit");

			System.out.println("---------------------------------------------");

			try {
				String userInput = br.readLine();
				if (isStrEmpty(userInput)) {
					System.out.println("The input option shouldn't be empty - please enter valid option...");
					processMenuDrivenOperation();
				}
				switch (userInput.toLowerCase()) {

				case "a":
					// Add a new Book
					Subject subject = addBook(br);
					IJdbcDAO jdbcDAO = new JdbcDAOImpl();
					jdbcDAO.addBook(subject);

					break;
				case "b":
					// Delete Subject
					deleteSubject(br);

					break;
				case "c":
					// Delete Book
					deleteBook(br);
					break;
				case "d":
					// Search a Book
					searchBook(br);

					break;
				case "e":
					// Search Subject
					searchSubject(br);
					break;
				case "f":
					// Sort Book By Title
					sortBookByTitle();
					break;

				case "g":
					// sortSubjectByTitle
					sortSubjectByTitle();
					break;

				case "h":
					// Sort Book by published date
					sortBookByPublishedDate();
					break;

				case "i":
					// Exit
					System.out.println("As per userinput - Exiting......... ");
					stopExecutionInd = false;
					break;
				default:
					System.out.println(
							"No option selected in the list..Select any of the options like a or b or c or d or e or f ");
					processMenuDrivenOperation();
					break;

				}
				System.out.println("");

				if (stopExecutionInd) {
					System.out.println("Do you want to continue with main menu again....Y/N ");
					String inputOption = br.readLine();
					// Terminate the process If user provided 'N'
					if ("N".equalsIgnoreCase(inputOption)) {
						stopExecutionInd = false;
						System.out.println("Terminating the program.....");
					}
				}

			} catch (IOException e) {
				System.out.println("Exception while reading an input from user..." + e.getMessage());
			}
		}

	}

	private void sortBookByTitle() {
		// Retrieve all the books.
		List<Book> bookList = getJdbcDAO().retrieveAllTheBooks();
		System.out.println("Before sorting........");
		bookList.forEach(System.out::println);

		System.out.println("After sorting........");
		// Sort
		bookList.stream().sorted((book1,book2) -> book1.getTitle().compareTo(book2.getTitle())).forEach(System.out::println);

	}

	
	private void sortSubjectByTitle() {
		// Retrieve all the Subjects.
		List<Subject> subjectList = getJdbcDAO().retrieveAllTheSubjects();
		System.out.println("Before sorting........");
		subjectList.forEach(System.out::println);
		// Sort
		System.out.println("After sorting........");

		subjectList.stream().sorted((subject1,subject2) -> subject1.getTitle().compareTo(subject2.getTitle())).forEach(System.out::println);
		
	}
	
	private void sortBookByPublishedDate() {
		// Retrieve all the books.
		List<Book> bookList = getJdbcDAO().retrieveAllTheBooks();
		System.out.println("Before sorting........");
		bookList.forEach(System.out::println);

		System.out.println("After sorting........");
		// Sort
		bookList.stream().sorted((book1,book2) -> book1.getPublishDate().compareTo(book2.getPublishDate())).forEach(System.out::println);

	}
	
	private static void searchSubject(BufferedReader br) throws IOException {
		System.out.println("Input a subjectId to be searched......\n");
		String subjectIdForSearch = br.readLine();
		Subject subjectForSearch = new Subject();
		int subjectId = Integer.parseInt(subjectIdForSearch);
		IJdbcDAO jdbcDAO = getJdbcDAO();
		subjectForSearch.setSubjectId(subjectId);
		Subject subjectResult = jdbcDAO.searchSubject(subjectForSearch);
		if (subjectResult == null) {
			System.out.println("Requested book is not available....");
		} else {
			System.out.println("The Requested Book is : " + subjectResult);
		}
	}

	private static IJdbcDAO getJdbcDAO() {
		IJdbcDAO jdbcDAO = new JdbcDAOImpl();
		return jdbcDAO;
	}

	private static void deleteBook(BufferedReader br) throws IOException {
		System.out.println("Userinput - Delete a book ");
		System.out.println("Input a bookid to be deleted......\n");
		String bookIdStr = br.readLine();
		Book book = new Book();
		while (isStrEmpty(bookIdStr)) {
			System.out.println("Please enter bookId as number/integer ");
			bookIdStr = br.readLine();

		}
		int bookid = Integer.parseInt(bookIdStr);
		IJdbcDAO jdbcDAO1 = new JdbcDAOImpl();
		book.setBookid(bookid);
		jdbcDAO1.deleteBook(book);
	}

	private static void deleteSubject(BufferedReader br) throws IOException {

		System.out.println(
				"Caution - The associated books with Subject will also be deleted as books are stored in DB with Foriegn key relation....  ");
		System.out.println("Do you want to continue Y/N...");
		String alert = br.readLine();

		if ("Y".equalsIgnoreCase(alert)) {
			System.out.println("Input a subjectId to be deleted......\n");
			String subjectIdForSearch = br.readLine();
			Subject subjectForSearch = new Subject();
			while (isStrEmpty(subjectIdForSearch)) {
				System.out.println("Please enter valid bookid in number/integer...");
				subjectIdForSearch = br.readLine();
			}
			int subjectId = Integer.parseInt(subjectIdForSearch);
			IJdbcDAO jdbcDAO2 = new JdbcDAOImpl();
			subjectForSearch.setSubjectId(subjectId);
			String opeationStatus = jdbcDAO2.deleteSubject(subjectForSearch);
			if (Utils.SUCCESS.equalsIgnoreCase(opeationStatus)) {
				System.out
						.println(SUCCESSFULLY_DELETED_BOTH_SUBJECT_AND_ASSOCIATED_BOOKS_FOR_THE_SUBJECT_ID + subjectId);
			} else if (Utils.NOT_FOUND.equalsIgnoreCase(opeationStatus)) {
				System.out.println(THE_PROVIDED_SUBJECT_ID_IS_NOT_AVAILABLE_IN_DATABASE + subjectId);

			} else if (Utils.FAIL.equalsIgnoreCase(opeationStatus)) {
				System.out.println(FAILED_TO_DELETE_BOTH_SUBJECT_AND_ASSOCIATED_BOOKS_FOR_THE_SUBJECT_ID + subjectId);
			}

		}
	}

	public static Book searchBook(BufferedReader br) throws IOException {

		System.out.println("Input a bookid to be searched......\n");
		String bookIdForSearch = br.readLine();
		Book bookForSearch = new Book();
		int bookid1 = Integer.parseInt(bookIdForSearch);
		IJdbcDAO jdbcDAO2 = getJdbcDAO();
		bookForSearch.setBookid(bookid1);
		Book bookresult = jdbcDAO2.searchBook(bookForSearch);
		if (bookresult == null) {
			System.out.println("Requested book is not available....");
		} else {
			System.out.println("The Requested Book is : " + bookresult);
		}
		return bookresult;
	}

	private static boolean isStrEmpty(String userInput) {
		return userInput == null || userInput.length() == 0;
	}

	private Subject addBook(BufferedReader br) {

		Subject subject = new Subject();

		try {

			// Read Subject data from user
			readSubjectFromUser(subject, br);

			String noOfBooks = br.readLine();
			while (isStrEmpty(noOfBooks)) {
				System.out.println("Please enter number of books in integer.....");
				noOfBooks = br.readLine();
			}
			int noOfBooksInt = Integer.parseInt(noOfBooks);

			// Read Books data from user
			Set<Book> bookList = readBookListFromUser(br, noOfBooksInt);

			subject.setBookList(bookList);

			System.out.println("The input data from User is : " + subject);

		} catch (IOException e) {
			System.out.println(EXCEPTION_WHILE_READING_A_DATA_FROM_INPUT_OUTPUT_SYSTEM + e.getMessage());
		}

		return subject;
	}

	private Set<Book> readBookListFromUser(BufferedReader br, int noOfBooksInt) throws IOException {
		int i = 1;

		Set<Book> bookList = new HashSet<>();

		while (noOfBooksInt >= 1) {

			System.out.println("Enter the Book Number : " + i);
			Book book = new Book();
			System.out.println("Input a Book Title....");

			String bookTitle = br.readLine();

			while (isStrEmpty(bookTitle)) {
				System.out.println("Title should not be empty/null...");
				bookTitle = br.readLine();
			}
			System.out.println("Input a Book price in (double)....");

			String priceInStr = br.readLine();
			while (isStrEmpty(priceInStr)) {
				System.out.println("Price should not be empty/null.. It should be double number...");
				priceInStr = br.readLine();
			}
			double price = Double.parseDouble(priceInStr);

			System.out.println("Input a Book volume in integer....");

			String volumeStr = br.readLine();
			while (isStrEmpty(volumeStr)) {
				System.out.println("Volume should not be empty/null.. It should be integer number...");
				volumeStr = br.readLine();
			}
			int volume = Integer.parseInt(volumeStr);

			Date date = readAndParseDate(br);

			System.out.println("Date is : " + date);
			LocalDate publishDate = convertToLocalDateViaInstant(date);

			book.setPrice(price);
			book.setPublishDate(publishDate);
			book.setTitle(bookTitle);
			book.setVolume(volume);
			bookList.add(book);

			noOfBooksInt--;
			i++;
		}
		return bookList;
	}

	private Date readAndParseDate(BufferedReader br) throws IOException {
		System.out.println("Input a Book published date in the format dd/mm/yyyy....");
		String publishedDateStr = br.readLine();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		sdf.setLenient(false);
		Date date = null;
		try {
			date = sdf.parse(publishedDateStr);
		} catch (ParseException e) {
			System.out.println("Exception in the date format....");
			readAndParseDate(br);
			// throw new RuntimeException("Exception in the date format....");
		}
		return date;
	}

	private void readSubjectFromUser(Subject subject, BufferedReader br) throws IOException {
		System.out.println("Input a Subject Title....");

		String subjectTile = br.readLine();

		System.out.println("Input a durationInHours in number....");

		int durationInHours = 0;
		String durationInHoursStr = br.readLine();
		while (isStrEmpty(durationInHoursStr)) {

			System.out.println("Please input a durationInHours in number....");

			durationInHoursStr = br.readLine();
		}
		durationInHours = Integer.parseInt(durationInHoursStr);

		subject.setDurationInHours(durationInHours);
		subject.setTitle(subjectTile);

		System.out.println("How many books you wanted to add for the Subject...." + subjectTile);
	}

	public static LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
		return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
}
