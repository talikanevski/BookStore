package com.example.com.bookstore;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.com.bookstore.data.BookContract.BookEntry;
import com.example.com.bookstore.data.BookDBHelper;

/**
 * Allows user to create a new book or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity {

    /**
     * EditText field to enter the book's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the book's genre
     */
    private Spinner mGenreSpinner;

    /**
     * EditText field to enter the book's price
     */
    private EditText mPriceEditText;

    /**
     * EditText field to enter the quantity of books in store
     */
    private EditText mQuantityEditText;

    /**
     * EditText field to enter the name of book's supplier
     */
    private EditText mSupplierEditText;

    /**
     * EditText field to enter the name of book's supplier
     */
    private EditText mSupplierPhoneNumberEditText;

    /**
     * Genre of the book.
     */
    private int mGenre = BookEntry.GENRE_UNKNOWN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mGenreSpinner = (Spinner) findViewById(R.id.spinner_genre);
        mPriceEditText = (EditText) findViewById(R.id.edit_book_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_quantity);
        mSupplierEditText = (EditText) findViewById(R.id.edit_supplier_name);
        mSupplierPhoneNumberEditText = (EditText) findViewById(R.id.edit_supplier_phone_number);

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the genre of the book.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genreSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_genre_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genreSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenreSpinner.setAdapter(genreSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.genre_fiction))) {
                        mGenre = BookEntry.GENDER_FICTION;
                    } else if (selection.equals(getString(R.string.genre_science_fiction))) {
                        mGenre = BookEntry.GENDER_SCIENCE_FICTION;
                    } else if (selection.equals(getString(R.string.genre_non_fiction))) {
                        mGenre = BookEntry.GENDER_NON_FICTION;
                    } else if
                            (selection.equals(getString(R.string.genre_action_and_Adventure))) {
                        mGenre = BookEntry.GENDER_ACTION_AND_ADVENTURE;
                    } else if (selection.equals(getString(R.string.genre_satire))) {
                        mGenre = BookEntry.GENDER_SATIRE;
                    } else if (selection.equals(getString(R.string.genre_drama))) {
                        mGenre = BookEntry.GENDER_DRAMA;
                    } else if (selection.equals(getString(R.string.genre_tragedy))) {
                        mGenre = BookEntry.GENDER_TRAGEDY;
                    } else if
                            (selection.equals(getString(R.string.genre_romance))) {
                        mGenre = BookEntry.GENDER_ROMANCE;
                    } else if (selection.equals(getString(R.string.genre_poetry))) {
                        mGenre = BookEntry.GENDER_POETRY;
                    } else if (selection.equals(getString(R.string.genre_history))) {
                        mGenre = BookEntry.GENDER_HISTORY;
                    } else mGenre = BookEntry.GENRE_UNKNOWN;
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGenre = BookEntry.GENRE_UNKNOWN;
            }
        });
    }

    /**
     * get user input and save new book into database
     **/
    private void insertBook() {
        String nameString = mNameEditText.getText().toString().trim(); /**  .trim erase spaces**/
        String priceString = mPriceEditText.getText().toString().trim();
        int price = Integer.parseInt(priceString);/**we want to store an integer, not a String.
         parseInt method convert the String into an Integer**/
        String quantityString = mQuantityEditText.getText().toString().trim();
        int quantity = Integer.parseInt(quantityString);
        String supplierString = mSupplierEditText.getText().toString().trim();
        String phoneNumberOfSupplierString = mSupplierPhoneNumberEditText.getText().toString().trim();

        ContentValues values = new ContentValues();

        values.put(BookEntry.COLUMN_BOOK_NAME, nameString);
        values.put(BookEntry.COLUMN_BOOK_GENRE, mGenre);
        values.put(BookEntry.COLUMN_BOOK_PRICE, price);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER, supplierString);
        values.put(BookEntry.COLUMN_PHONE_NUMBER_OF_SUPPLIER, phoneNumberOfSupplierString);

        // Insert a new row for book in the database, returning the ID of that new row.
        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.editor_insert_book_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_insert_book_successful),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                //set book to database
                insertBook();
                //Exit activity and return to the CatalogActivity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}