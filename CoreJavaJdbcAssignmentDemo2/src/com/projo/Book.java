package com.projo;

import java.time.LocalDate;

public class Book {

	private String title;
	private double price;
	private int volume;
	private LocalDate publishDate;
	private int bookid;

	public int getBookid() {
		return bookid;
	}

	public void setBookid(int bookid) {
		this.bookid = bookid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public LocalDate getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(LocalDate publishDate) {
		this.publishDate = publishDate;
	}

	@Override
	public String toString() {
		StringBuilder bookBuilder = new StringBuilder();

		bookBuilder.append("Book Title=").append(title).append(",").append("Book Price=").append(price).append(",")
				.append("Book Volume=").append(volume).append(",").append("PublishDate=").append(publishDate);

		return bookBuilder.toString();
	}

}
