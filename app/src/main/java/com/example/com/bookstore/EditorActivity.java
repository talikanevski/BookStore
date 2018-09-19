package com.example.com.bookstore;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
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
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Identifies a particular Loader being used in this component
    private static final int CURRENT_BOOK_LOADER = 0;

    /**
     * Content URI for the existing book (null if it's a new book)
     */
    private Uri currentBookUri;
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

        // examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new pat or editing the existing pet
        Intent intent = getIntent();
        currentBookUri = intent.getData();

        // IF the intent DOES NOT contain a book content Uri,
        // then we know that we're creating a new book

        if (currentBookUri == null) {
            setTitle(R.string.add_new_book);
        } else {
            setTitle(getString(R.string.edit_book));

            // Initialize a loader to read the pet data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(CURRENT_BOOK_LOADER, null, this);
        }

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
                        mGenre = BookEntry.GENRE_FICTION;
                    } else if (selection.equals(getString(R.string.genre_science_fiction))) {
                        mGenre = BookEntry.GENRE_SCIENCE_FICTION;
                    } else if (selection.equals(getString(R.string.genre_non_fiction))) {
                        mGenre = BookEntry.GENRE_NON_FICTION;
                    } else if
                            (selection.equals(getString(R.string.genre_action_and_Adventure))) {
                        mGenre = BookEntry.GENRE_ACTION_AND_ADVENTURE;
                    } else if (selection.equals(getString(R.string.genre_satire))) {
                        mGenre = BookEntry.GENRE_SATIRE;
                    } else if (selection.equals(getString(R.string.genre_drama))) {
                        mGenre = BookEntry.GENRE_DRAMA;
                    } else if (selection.equals(getString(R.string.genre_tragedy))) {
                        mGenre = BookEntry.GENRE_TRAGEDY;
                    } else if
                            (selection.equals(getString(R.string.genre_romance))) {
                        mGenre = BookEntry.GENRE_ROMANCE;
                    } else if (selection.equals(getString(R.string.genre_poetry))) {
                        mGenre = BookEntry.GENRE_POETRY;
                    } else if (selection.equals(getString(R.string.genre_history))) {
                        mGenre = BookEntry.GENRE_HISTORY;
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all book attributes, define a projection that contains
        // all columns from the book table
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_GENRE,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_SUPPLIER,
                BookEntry.COLUMN_PHONE_NUMBER_OF_SUPPLIER};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                currentBookUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // first move the cursor to it’s first item position.
        // Even though it only has one item, it st  arts at position -1.
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            //Then I’ll get the data out of the cursor by getting the index of each data item,
            //and then using the indexes and the get methods to grab the actual integers and strings.
            // Find the columns of book attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
            int genreColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_GENRE);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER);
            int phoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PHONE_NUMBER_OF_SUPPLIER);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int genre = cursor.getInt(genreColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            String phone = cursor.getString(phoneColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mSupplierEditText.setText(supplier);
            mSupplierPhoneNumberEditText.setText(phone);

            // genre is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options.
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (genre) {
                case BookEntry.GENRE_FICTION:
                    mGenreSpinner.setSelection(1);
                    break;
                case BookEntry.GENRE_SCIENCE_FICTION:
                    mGenreSpinner.setSelection(2);
                    break;
                case BookEntry.GENRE_NON_FICTION:
                    mGenreSpinner.setSelection(3);
                    break;
                case BookEntry.GENRE_ACTION_AND_ADVENTURE:
                    mGenreSpinner.setSelection(4);
                    break;
                case BookEntry.GENRE_SATIRE:
                    mGenreSpinner.setSelection(5);
                    break;
                case BookEntry.GENRE_DRAMA:
                    mGenreSpinner.setSelection(6);
                    break;
                case BookEntry.GENRE_TRAGEDY:
                    mGenreSpinner.setSelection(7);
                    break;
                case BookEntry.GENRE_ROMANCE:
                    mGenreSpinner.setSelection(8);
                    break;
                case BookEntry.GENRE_POETRY:
                    mGenreSpinner.setSelection(9);
                    break;
                case BookEntry.GENRE_HISTORY:
                    mGenreSpinner.setSelection(10);
                    break;
                default:
                    mGenreSpinner.setSelection(0); // GENRE_UNKNOWN
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mGenreSpinner.setSelection(0); // GENRE_UNKNOWN
        mQuantityEditText.setText("");
        mSupplierEditText.setText("");
        mSupplierPhoneNumberEditText.setText("");
    }
}