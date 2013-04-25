package com.example.barcodescan;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONObject;

import android.content.Context;


public class WebService {
	
  public static JSONObject sendLogin(Context c, String email, String password) {
    JSONObject jsonResponse = null;

    MultipartEntity multipartEntity = new MultipartEntity();
    try {
        multipartEntity.addPart("user[email]", new StringBody(email));
        multipartEntity.addPart("user[password]", new StringBody(password));

        JSONParser jsonParser = new JSONParser(c);
        jsonResponse = jsonParser.postForJSON((c.getResources().getString(R.string.root_url))+"api/login", multipartEntity, null);
    } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
    }

    return jsonResponse;
  }
  
  public static JSONObject getBookList(Context c, User user) {
  	
  	//Header[] headers = {new BasicHeader("Token", user.getApiKey())};
  	
  	JSONParser parser = new JSONParser(c);
  	return parser.getForJSON((c.getResources().getString(R.string.root_url))+"users/"+user.getId()+"/books", null, null);
  	
  }
  
  public static JSONObject getBookDetail(Context c, String ISBN) {
  	
  	JSONParser parser = new JSONParser(c);
  	return parser.getForJSON((c.getResources().getString(R.string.root_url))+"books/lookup/"+ISBN, null, null);
  }
  
  public static JSONObject submitBook(Context c, Book book, boolean want) {
    JSONObject jsonResponse = null;

    MultipartEntity multipartEntity = new MultipartEntity();
    try {
        multipartEntity.addPart("book[name]", new StringBody(book.getName()));
        multipartEntity.addPart("book[edition]", new StringBody(String.valueOf(book.getEdition())));
        multipartEntity.addPart("book[isbn]", new StringBody(book.getISBN()));
        multipartEntity.addPart("book[author]", new StringBody(book.getAuthor()));
        multipartEntity.addPart("book[price]", new StringBody(String.valueOf(book.getPrice())));
        multipartEntity.addPart("book[want]", new StringBody(String.valueOf(want)));

        JSONParser jsonParser = new JSONParser(c);
        jsonResponse = jsonParser.postForJSON((c.getResources().getString(R.string.root_url))+"books", multipartEntity, null);
    } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
    }

    return jsonResponse;
  }
  
  public static JSONObject removeBook(Book book) {
  	return new JSONObject();
  }

}
