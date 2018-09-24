package com.example.com.bookstore;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.com.bookstore.data.BookContract;
import com.example.com.bookstore.data.BookContract.BookEntry;

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
     * EditText field to enter the book's author
     */
    private EditText mAuthorEditText;
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
     * ImageView to increase the quantity of a book in store
     **/
    private ImageView mPlusQuantityImageView;

    /**
     * ImageView to decrease the quantity of a book in store
     **/
    private ImageView mMinusQuantityImageView;

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

    /**
     * Boolean flag that keeps track of whether the book has been edited (true) or not (false)
     */
    private boolean mBookHasChanged = false;

    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
    // the view, and we change the mBookHasChanged boolean to true.

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
            /** If you return false you basically say "Not my problem,
             Someone else will have to take care of this click".
             Then android will pass the event down to other views, which could be under your view.
             In this case, I am saying that from here boolean @mBookHasChanged
             will take care of this touching event
             https://stackoverflow.com/questions/21578476/what-actually-happens-if-i-return-false-in-a-ontouchlistener**/
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        LinearLayout buttons = (LinearLayout) findViewById(R.id.buttons_layout);
        mPlusQuantityImageView = (ImageView) findViewById(R.id.plus)
;
        mMinusQuantityImageView = (ImageView) findViewById(R.id.minus);
        // examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new pat or editing the existing book
        Intent intent = getIntent();
        currentBookUri = intent.getData();

        // IF the intent DOES NOT contain a book content Uri,
        // then we know that we're creating a new book

        if (currentBookUri == null) {
            setTitle(R.string.add_new_book);
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a book that hasn't been created yet.)
            invalidateOptionsMenu();
            buttons.setVisibility(View.GONE);

        } else {
            setTitle(getString(R.string.edit_book));
            buttons.setVisibility(View.VISIBLE);
            mMinusQuantityImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int quantity = Integer.parseInt(mQuantityEditText.getText().toString().trim());
                    if (currentBookUri != null) {

                        if (quantity > 0) {
                            quantity--;
                            ContentValues values = new ContentValues();
                            values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, quantity);
                            getContentResolver().update(currentBookUri, values, null, null);
                        }
                    }
                }
            });

            mPlusQuantityImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int quantity = Integer.parseInt(mQuantityEditText.getText().toString().trim());

                    if (currentBookUri != null) {
                        quantity++;
                        ContentValues values = new ContentValues();
                        values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, quantity);
                        getContentResolver().update(currentBookUri, values, null, null);
                    }
                }
            });
            // Initialize a loader to read the book data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(CURRENT_BOOK_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mAuthorEditText = (EditText) findViewById(R.id.edit_author_name);
        mGenreSpinner = (Spinner) findViewById(R.id.spinner_genre);
        mPriceEditText = (EditText) findViewById(R.id.edit_book_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_quantity);

        mSupplierEditText = (EditText) findViewById(R.id.edit_supplier_name);
        mSupplierPhoneNumberEditText = (EditText) findViewById(R.id.edit_supplier_phone_number);

        /**   Setup OnTouchListeners on all the input fields, so we can determine if the user
         has touched or modified them. This will let us know if there are unsaved changes
         or not, if the user tries to leave the editor without saving**/
        mNameEditText.setOnTouchListener(mTouchListener);
        mAuthorEditText.setOnTouchListener(mTouchListener);
        mGenreSpinner.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneNumberEditText.setOnTouchListener(mTouchListener);


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
    private void saveBook() {
        String nameString = mNameEditText.getText().toString().trim(); /**  .trim erase spaces**/
        String authorString = mAuthorEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();
        String phoneNumberOfSupplierString = mSupplierPhoneNumberEditText.getText().toString().trim();

        // Check if this is supposed to be a new book
        // and check if all the fields in the editor are blank
        if (currentBookUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(authorString)
                && TextUtils.isEmpty(priceString) && TextUtils.isEmpty(quantityString)
                && TextUtils.isEmpty(supplierString) && TextUtils.isEmpty(phoneNumberOfSupplierString)
                && mGenre == BookEntry.GENRE_UNKNOWN) {
            // Since no fields were modified, we can return early without creating a new book.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        if (currentBookUri == null) {
            if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(authorString)
                    || TextUtils.isEmpty(quantityString)) {
                Toast.makeText(this, "Please fill the required fields",
                        Toast.LENGTH_SHORT).show();
            }
        }

        ContentValues values = new ContentValues();

        values.put(BookEntry.COLUMN_BOOK_NAME, nameString);
        values.put(BookEntry.COLUMN_BOOK_AUTHOR, authorString);
        values.put(BookEntry.COLUMN_BOOK_GENRE, mGenre);

        // If the price is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int price = 0;
        if (!TextUtils.isEmpty(priceString)) {
            price = Integer.parseInt(priceString);/**we want to store an integer, not a String.
             parseInt method convert the String into an Integer**/
        }
        values.put(BookEntry.COLUMN_BOOK_PRICE, price);

        // If the quantity is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);/**we want to store an integer, not a String.
             parseInt method convert the String into an Integer**/
        }

        values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER, supplierString);
        values.put(BookEntry.COLUMN_PHONE_NUMBER_OF_SUPPLIER, phoneNumberOfSupplierString);

        // Determine if this is a new or existing book by checking if mCurrentPetUri is null or not
        if (currentBookUri == null) {
            // This is a NEW book, so insert a new book into the provider,
            // returning the content URI for the new book.
            /** One major difference between the SQLiteDatabase insert() method
             and the ContentResolver insert() method is that one returns a row ID,
             while the other returns a Uri, respectively.*/
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

        } else {
            // Otherwise this is an EXISTING book, so update the book with content URI:
            // currentBookUri and pass in the new ContentValues.
            // Pass in null for the selection and selection args because currentBookUri
            // will already identify the correct row in the database that we want to modify.
            int rowsAffected = getContentResolver().update(currentBookUri, values, null, null);
            //Now this returns a number of updated rows.
            // To display whether the update was successful or not,
            // we can check the number of updated rows.
            // If it was 0, then the update was not successful,
            // and we can show an error toast. Otherwise we show a success toast.

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (currentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                //set book to database
                if (isValidBook()) {
                    saveBook();
                    //Exit activity and return to the CatalogActivity
                    finish();
                    return true;
                }
                // Respond to a click on the "Delete" menu option
                // OR if the book wasn't valid after "if (isValidBook())" check
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();/** it will create a dialog
             that calls deleteBook when the delete button is pressed.**/
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the book hasn't changed, continue with navigating up to parent activity
                // which is the {@link MainActivity}.
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isValidBook() {
        String nameString = mNameEditText.getText().toString().trim(); /**  .trim erase spaces**/
        String authorString = mAuthorEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();

        if (TextUtils.isEmpty(nameString) ||
                TextUtils.isEmpty(authorString) ||
                TextUtils.isEmpty(quantityString)) {
            Toast.makeText(this, getString(R.string.book_validation), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all book attributes, define a projection that contains
        // all columns from the book table
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_AUTHOR,
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
            int authorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_AUTHOR);
            int genreColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_GENRE);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER);
            int phoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PHONE_NUMBER_OF_SUPPLIER);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String author = cursor.getString(authorColumnIndex);
            int genre = cursor.getInt(genreColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            String phone = cursor.getString(phoneColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mAuthorEditText.setText(author);
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
        mAuthorEditText.setText("");
        mGenreSpinner.setSelection(0); // GENRE_UNKNOWN
        mQuantityEditText.setText("");
        mSupplierEditText.setText("");
        mSupplierPhoneNumberEditText.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the book hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    /**
     * Prompt the user to confirm that they want to delete this book.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the book in the database.
     */
    private void deleteBook() {
        // Only perform the delete if this is an existing book.
        if (currentBookUri != null) {
            // Call the ContentResolver to delete the book at the given content URI.
            // Pass in null for the selection and selection args because the currentBookUri
            // content URI already identifies the book that we want.
            // The delete method, like update, returns the number of rows deleted.
            int rowsDeleted;
            rowsDeleted = getContentResolver().delete(currentBookUri, null, null);
            Log.v("DeleteBook", String.valueOf(rowsDeleted));


            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                Log.v("DeleteBook", rowsDeleted + " rows deleted from book database");

                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();

    }
}