package com.example.barcodescan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class BookListActivity extends Activity {
	
	private static User user;
	private static boolean want;
	
	private static Map<String, List<Book>> bookMap;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_list);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setHomeButtonEnabled(true);
		}
		setupActionBar();
		
		//Have-Book List
		final ListView haveList = (ListView)findViewById(R.id.listview1);		
		haveList.setClickable(true);
		
		haveList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

		  @Override
		  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		  	openDetailView(haveList, position, false);
		  }
		});
		
		haveList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				return removeBook(haveList, position, false);
			}
		});
		
		//Want-Book List
		final ListView wantList = (ListView)findViewById(R.id.listview2);
		wantList.setClickable(true);
		
		wantList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

		  @Override
		  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		  	openDetailView(wantList, position, true);
		  }
		});
		
		wantList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				return removeBook(wantList, arg2, true);
			}
		});
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		
		try {
			user = (User)bundle.getSerializable("user");
		}catch(Exception e){
			  AppPreferences prefs = new AppPreferences(this);
				user = new User(prefs.getEmail(), prefs.getId(), prefs.getAPIKey());
		}
		
		JSONObject response = WebService.getBookList(this,user);
		
		List<?>[] listsH = instantiateBookLists(response, false);
		List<Book> booksHave = (List<Book>)listsH[0];
		List<String> booksHaveTitle = (List<String>)listsH[1];
		
		final StableArrayAdapter adapter1 = new StableArrayAdapter(this,android.R.layout.simple_list_item_1, booksHaveTitle);	
		haveList.setAdapter(adapter1);
		
		List<?>[] listsW = instantiateBookLists(response, true);
		List<Book> booksWant = (List<Book>)listsW[0];
		List<String> booksWantTitle = (List<String>)listsW[1];
			
		final StableArrayAdapter adapter2 = new StableArrayAdapter(this,android.R.layout.simple_list_item_1, booksWantTitle);			
		wantList.setAdapter(adapter2);
		
		bookMap = new HashMap<String,List<Book>>();
		
		bookMap.put("have", booksHave);
		bookMap.put("want", booksWant);		
		
		//BUTTONS
		Button haveBtn = (Button) findViewById(R.id.btnHave);
		haveBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				scanISBN(false);
			}
		});
		Button wantBtn = (Button) findViewById(R.id.btnWant);
		wantBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				scanISBN(true);
			}
		});
	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.display_message, menu);
		return true;
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.logout:
			AppPreferences prefs = new AppPreferences(this);
			prefs.logOut();
			Intent intent = new Intent(this, BarcodeScanActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public List<?>[] instantiateBookLists(JSONObject response, boolean want) {
		
		try {
			List<Book> books = new ArrayList<Book>();
			List<String> booksTitle = new ArrayList<String>();
			
			JSONArray arrayH = response.getJSONArray(want ? "wanted_books" : "owned_books");
			for (int i = 0; i < arrayH.length(); i++) {
				JSONObject bookObject = arrayH.getJSONObject(i);
				books.add(new Book(bookObject.getString("isbn"),
						bookObject.getString("name"),
						Integer.valueOf(bookObject.getString("edition")),
						bookObject.getString("author"),
						bookObject.getString("description"),
						Double.valueOf(bookObject.getString("price"))));
				booksTitle.add(bookObject.getString("name"));
			}
			return new List<?>[]{books,booksTitle};
		}catch (JSONException e) {
			e.printStackTrace();
			return null;
		}	
	}
	
	public boolean removeBook(ListView view, int arg2, final boolean want) {
		final Object o = view.getItemAtPosition(arg2);
		AlertDialog.Builder adb=new AlertDialog.Builder(BookListActivity.this);
		adb.setTitle("Delete?");
		adb.setMessage("Are you sure you want to delete this listing?");
		adb.setNegativeButton("Cancel", null);
    adb.setPositiveButton("Ok",new AlertDialog.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
      	List<Book> list = bookMap.get(want ? "want" : "have");
      	Book book = null;
      	
      	for (Book b : list) {
      		if (b.getName().equals(o.toString())){
      			book = b;
      			break;
      		}
      	}
      	
      	WebService.removeBook(book);
      	
      }});
    adb.show();
		return false;
	}
	
	public void openDetailView(ListView view, int position, boolean want) {
    Object o = view.getItemAtPosition(position);
    String bookName = o.toString();
    List<Book> list = bookMap.get(want ? "want" : "have");
    
    for (Book book : list) {
    	if (book.getName().equals(bookName)) {
    		Intent intent = new Intent(this, BookDetailActivity.class);
    		Bundle bundle = new Bundle();
				bundle.putSerializable("book", book);
				bundle.putBoolean("wanted", want);
				intent.putExtras(bundle);
				startActivity(intent);
    	}
    }
	}
	
	public void scanISBN(boolean want) {
		BookListActivity.want = want;
		IntentIntegrator.initiateScan(this);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		switch(requestCode) {
		
			case IntentIntegrator.REQUEST_CODE: {
				if (resultCode != RESULT_CANCELED) {
					IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
					
					if (scanResult != null) {
						String UPC = scanResult.getContents();
						Intent intent = new Intent(this, BookDetailActivity.class);
						
						if (UPC.length() == 13) {
							JSONObject response = WebService.getBookDetail(this, UPC);
							
							String isbn = "";
							String author = "";
							String name = "";
							String descr = "";
							String price = "";
							try {
								isbn = response.getString("isbn");
								author = response.getString("author");
								name = response.getString("name");
								descr = response.getString("description");
								price = response.getString("price");
							} catch (JSONException e) {
								e.printStackTrace();
							}
							price = (price.equals("")) ? "0.0" : price;
							Book book = new Book(isbn,name,0,author,descr,Double.valueOf(price));
						
							if (book.isValid()) {
								Bundle bundle = new Bundle();
								bundle.putSerializable("book", book);
								bundle.putBoolean("wanted", want);
								intent.putExtras(bundle);
							}
						} else {
							(Toast.makeText(this, "Not a valid ISBN13", Toast.LENGTH_SHORT)).show();
						}
				
						startActivity(intent);
					}
				}
				
				break;
			}
		}
	}

	private class StableArrayAdapter extends ArrayAdapter<String>{
		
		HashMap<String,Integer> mIdMap;

		public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
			super(context, textViewResourceId,objects);
			
			mIdMap = new HashMap<String,Integer>();
			
			for (int i = 0; i < objects.size(); i++) {
				mIdMap.put(objects.get(i), i);
			}
		}
		
		@Override
		public long getItemId(int position) {
			String item = getItem(position);
			return mIdMap.get(item);
		}
		
		@Override
		public boolean hasStableIds() {
			return true;
		}
	}

}
