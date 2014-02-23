package com.scau.baitouwei.xrssfeed.bean;

import android.net.Uri;

public class ReadabilityParserBack {
	private String content;
	private Uri domain;
	private String author;
	private Uri url;
	private Uri short_url;
	private String title;
	private String excerpt;
	private String direction;
	private int word_count;
	private int total_pages;
	private String date_published;
	private String dek;
	private Uri lead_image_url;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Uri getDomain() {
		return domain;
	}

	public void setDomain(Uri domain) {
		this.domain = domain;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Uri getUrl() {
		return url;
	}

	public void setUrl(Uri url) {
		this.url = url;
	}

	public Uri getShort_url() {
		return short_url;
	}

	public void setShort_url(Uri short_url) {
		this.short_url = short_url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getExcerpt() {
		return excerpt;
	}

	public void setExcerpt(String excerpt) {
		this.excerpt = excerpt;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public int getWord_count() {
		return word_count;
	}

	public void setWord_count(int word_count) {
		this.word_count = word_count;
	}

	public int getTotal_pages() {
		return total_pages;
	}

	public void setTotal_pages(int total_pages) {
		this.total_pages = total_pages;
	}

	public String getDate_published() {
		return date_published;
	}

	public void setDate_published(String date_published) {
		this.date_published = date_published;
	}

	public String getDek() {
		return dek;
	}

	public void setDek(String dek) {
		this.dek = dek;
	}

	public Uri getLead_image_url() {
		return lead_image_url;
	}

	public void setLead_image_url(Uri lead_image_url) {
		this.lead_image_url = lead_image_url;
	}

	public int getNext_page_id() {
		return next_page_id;
	}

	public void setNext_page_id(int next_page_id) {
		this.next_page_id = next_page_id;
	}

	public int getRendered_pages() {
		return rendered_pages;
	}

	public void setRendered_pages(int rendered_pages) {
		this.rendered_pages = rendered_pages;
	}

	private int next_page_id;
	private int rendered_pages;
}
