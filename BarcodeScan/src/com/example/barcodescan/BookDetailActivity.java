package com.example.barcodescan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class BookDetailActivity extends Activity{
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_detail);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setHomeButtonEnabled(true);
		}
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		
		final boolean want = (Boolean)bundle.getBoolean("want");
		final Book book = (Book)bundle.getSerializable("book");
		
		final EditText bookTitle = (EditText)findViewById(R.id.BookTitle);
		bookTitle.setText(book.getName());
		
		final EditText bookEdition = (EditText)findViewById(R.id.BookEdition);
		if (book.getEdition() != 0) {
			bookEdition.setText(book.getEdition()+"");
		}
		
		final EditText bookISBN = (EditText)findViewById(R.id.BookIsbn);
		bookISBN.setText(book.getISBN());
		
		final EditText bookAuthor = (EditText)findViewById(R.id.BookAuthor);
		bookAuthor.setText(book.getAuthor());
		
		final EditText bookPrice = (EditText)findViewById(R.id.BookPrice);
		bookPrice.setText("$"+book.getPrice());
		
		Button bookSubmit = (Button)findViewById(R.id.BookSubmitButton);
		
		final BookDetailActivity context = this;
		bookSubmit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Integer edition = 0;
				try {edition = Integer.valueOf((String)(bookEdition.getText().toString()));}catch(NumberFormatException e){};
				
				WebService.submitBook(context,new Book(bookISBN.getText().toString(),
																							 bookTitle.getText().toString(),
																							 edition,
																							 bookAuthor.getText().toString(),
																							 "",
																							 Double.valueOf(bookPrice.getText().toString().substring(1))),want);
				context.goBackToListView();
			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.logout:
			AppPreferences prefs = new AppPreferences(this);
			prefs.logOut();
			Intent intent = new Intent(this, BarcodeScanActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void goBackToListView() {
		AppPreferences prefs = new AppPreferences(this);
		String key = prefs.getAPIKey();
		
		String id = prefs.getId();
		String email = prefs.getEmail();
		String apiKey = key;
		
		User user = new User(email,id,apiKey);	
		
		Bundle bundle = new Bundle();
		bundle.putSerializable("user", user);
		
		Intent intent = new Intent(this, BookListActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}

}
