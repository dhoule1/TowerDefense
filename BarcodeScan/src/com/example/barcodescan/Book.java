package com.example.barcodescan;

import java.io.Serializable;

public class Book implements Serializable{
	private String ISBN;
	private String name;
	private Integer edition;
	private String author;
	private String description;
	private Double price;
	
	public Book(String ISBN, String name, Integer edition, String author, String description, Double price) {
		this.ISBN = ISBN;
		this.name = name;
		this.edition = edition;
		this.author = author;
		this.description = description;
		this.price = price;
	}
	
	public boolean isValid() {
		return !(ISBN.isEmpty() ||
				name.isEmpty() ||
				author.isEmpty());
	}
	
	public String getISBN() {
		return ISBN;
	}
	public void setISBN(String iSBN) {
		ISBN = iSBN;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getEdition() {
		return edition;
	}
	public void setEdition(Integer edition) {
		this.edition = edition;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}
	
	

}
