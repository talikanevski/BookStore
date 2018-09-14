package com.example.com.bookstore.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.com.bookstore.data.BookContract.BookEntry;


public class BookProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = BookProvider.class.getSimpleName();

    /**
     * database helper object
     **/
    private BookDBHelper mDbHelper;

    /**
     * URI matcher code for the content URI for the books table
     */
    private static final int BOOKS = 100;

    /**
     * URI matcher code for the content URI for a single book in the books table
     */
    private static final int BOOK_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "#", BOOK_ID);

    }

    /**
     * Initialize the provider.
     */
    @Override
    public boolean onCreate() {

        /** Initialize database helper object mDbHelper**/
        mDbHelper = new BookDBHelper(getContext());

        return true;
    }

    /**
     * Perform the query for the given URI.
     * Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor = null;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // For the BOOKS code, query the books table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the books table.
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOK_ID:
                // For the BOOK_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.books/books/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the books table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a book into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertBook(Uri uri, ContentValues values) {

        /** Sanity checking.**/
        // Check that the name of the book is not null
        String name = values.getAsString(BookEntry.COLUMN_BOOK_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Book requires a name");
        }

        /** Sanity checking.**/
        // Check that the genre of the book. It is stored as an integer.
        Integer genre = values.getAsInteger(BookEntry.COLUMN_BOOK_GENRE);
        if (genre != null || !BookEntry.isValidGenre(genre)) {
            throw new IllegalArgumentException("Book requires valid genre");
        }
        /** Sanity checking.**/
        // If the price is provided, check that it's greater than or equal to $0
        // If the price is null, that’s fine
        Integer price = values.getAsInteger(BookEntry.COLUMN_BOOK_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Book requires valid price");
        }
        /** Sanity checking.**/
        // check that quantity is not null and that it is greater than or equal to 0 books.
        Integer quantity = values.getAsInteger(BookEntry.COLUMN_BOOK_QUANTITY);
        if (quantity == null || price < 0) {
            throw new IllegalArgumentException("Book requires valid quantity");
        }
        /** Sanity checking.**/
        // Check that the supplier's name is not null.
        String supplier = values.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER);
        if (supplier == null) {
            throw new IllegalArgumentException("Book requires a supplier's name");
        }
        /** Sanity checking.**/
        // Check that the supplier's phone is not null.
        String phone = values.getAsString(BookEntry.COLUMN_PHONE_NUMBER_OF_SUPPLIER);
        if (phone == null) {
            throw new IllegalArgumentException("Book requires a supplier's phone");
        }

        /**After the sanity check, let’s start by getting a database object.
         Should it be a readable or writable database?
         Well, we are editing the data source by adding a new book,
         so we need to write changes to the database.
         Get writable database**/
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        /**  Once we have a database object, we can call the insert() method on it,
         * passing in the pet table name and the ContentValues object.
         * The return value is the ID of the new row that was just created,
         * in the form of a long data type (which can store numbers larger than the int data type).**/

        long id = database.insert(BookEntry.TABLE_NAME, null, values);


        /**Based on the ID, we can determine if the database operation went smoothly or not.
         If the ID is -1, then the insertion failed. Log an error and return null.**/
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        /**Once we know the ID of the new row in the table,
         return the new URI with the ID appended to the end of it*/
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                /** For the BOOK_ID code, extract out the ID from the URI,
                 so we know which row to update. Selection will be "_id=?" and selection
                 arguments will be a String array containing the actual ID.*/
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update books in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more books).
     * Return the number of rows that were successfully updated.
     */
    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        /** Sanity checking.**/
        // If the {@link BookEntry.COLUMN_BOOK_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(BookEntry.COLUMN_BOOK_NAME)) {
            String name = values.getAsString(BookEntry.COLUMN_BOOK_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Book requires a name");
            }
        }
        /** Sanity checking.**/
        // If the {@link BookEntry.COLUMN_BOOK_GENRE)} key is present,
        // check that the genre value is not null.
        if (values.containsKey(BookEntry.COLUMN_BOOK_GENRE)) {
            Integer genre = values.getAsInteger(BookEntry.COLUMN_BOOK_GENRE);
            if (genre == null || !BookEntry.isValidGenre(genre)) {
                throw new IllegalArgumentException("Book requires valid genre");
            }
        }
        /** Sanity checking.**/
        // If the {@link BookEntry.COLUMN_BOOK_PRICE} key is present,
        // check that the price value is not < 0.
        if (values.containsKey(BookEntry.COLUMN_BOOK_PRICE)) {
            Integer price = values.getAsInteger(BookEntry.COLUMN_BOOK_PRICE);
            if (price < 0) {
                throw new IllegalArgumentException("Book's price should be >=0");
            }
        }
        /** Sanity checking.**/
        // If the {@link BookEntry.COLUMN_BOOK_QUANTITY} key is present,
        // check that the quantity value is not < 0.
        if (values.containsKey(BookEntry.COLUMN_BOOK_QUANTITY)) {
            Integer quantity = values.getAsInteger(BookEntry.COLUMN_BOOK_QUANTITY);
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("Book requires valid quantity");
            }
        }
        /** Sanity checking.**/
        // If the {@link BookEntry.COLUMN_BOOK_SUPPLIER} key is present,
        // check that the supplier's name value is not null.
        if (values.containsKey(BookEntry.COLUMN_BOOK_SUPPLIER)) {
            String supplier = values.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER);
            if (supplier == null) {
                throw new IllegalArgumentException("Book requires a supplier's name");
            }
        }
        /** Sanity checking.**/
        // If the {@link BookEntry.COLUMN_PHONE_NUMBER_OF_SUPPLIER} key is present,
        // check that the supplier's phone value is not null.
        if (values.containsKey(BookEntry.COLUMN_PHONE_NUMBER_OF_SUPPLIER)) {
            String phone = values.getAsString(BookEntry.COLUMN_PHONE_NUMBER_OF_SUPPLIER);
            if (phone == null) {
                throw new IllegalArgumentException("Book requires a supplier's phone");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        return database.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
      @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Delete all rows that match the selection and selection args
                return database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
            case BOOK_ID:
                // Delete a single row given by the ID in the URI
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    /**
     * Returns the MIME type (media type) of data for the content URI.
     * https://stackoverflow.com/questions/7157129/what-is-the-mimetype-attribute-in-data-used-for
     * <p>
     * In our app we have essentially two types of URIs.
     * The first is the URI is “content://com.example.android.books/books/”,
     * which references the entire books table. Basically it represents a list of books.
     * In MIME type terms, this is known as a directory of data.
     * The second URI is “content://com.example.android.books/books/#”,
     * which represents a single book. In MIME type terms, a single row of data is an item of data.
     * <p>
     * content://com.example.android.books/books → Returns directory MIME type
     * content://com.example.android.books/books/# → Returns item MIME type
     * <p>
     * Directory MIME type: vnd.android.cursor.dir/com.example.android.books/books
     * <p>
     * Item MIME type: vnd.android.cursor.item/com.example.android.books/books/#
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}