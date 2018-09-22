package com.example.com.bookstore.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class BookContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private BookContract() {
    }
    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website. A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.The same as that from the AndroidManifest:
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.books";

    /**
     * Next, concatenate the CONTENT_AUTHORITY constant with the scheme
     * “content://”
     * create the BASE_CONTENT_URI which will be shared by every URI associated with BookContract:
     * "content://" + CONTENT_AUTHORITY
     * The parse method make this a usable URI. The parse method takes in a URI string and returns a Uri.
     **/
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
    * This constants stores the path for each of the tables which will be appended to the base content URI.
    **/
    public static final String PATH_BOOKS = "books";

    /**
     * Inner class that defines constant values for the books database table.
     * Each entry in the table represents a single book.
     */
    public static final class BookEntry implements BaseColumns {

        /**
         * Inside each of the Entry classes in the contract, we create a full URI
         * for the class as a constant called CONTENT_URI.
         * The Uri.withAppendedPath() method appends the BASE_CONTENT_URI
         * (which contains the scheme and the content authority) to the path segment.
         **/
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of books.
         * <p>
         * we’re making use of the constants defined in the ContentResolver class:
         * CURSOR_DIR_BASE_TYPE (which maps to the constant "vnd.android.cursor.dir")
         * and CURSOR_ITEM_BASE_TYPE (which maps to the constant “vnd.android.cursor.item”).
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single book.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public static final String TABLE_NAME = "books";

        public static final String _ID = BaseColumns._ID;
        public final static String COLUMN_BOOK_NAME = "book_name";
        public final static String COLUMN_BOOK_AUTHOR = "author";
        public final static String COLUMN_BOOK_GENRE = "genre";
        public final static String COLUMN_BOOK_PRICE = "price";
        public final static String COLUMN_BOOK_QUANTITY = "quantity";
        public final static String COLUMN_BOOK_SUPPLIER = "supplier";
        public final static String COLUMN_PHONE_NUMBER_OF_SUPPLIER = "phone";

        /**
         * Possible values for the genre of the book.
         */
        public static final int GENRE_UNKNOWN = 0;
        public static final int GENRE_FICTION = 1;
        public static final int GENRE_SCIENCE_FICTION = 2;
        public static final int GENRE_NON_FICTION = 3;
        public static final int GENRE_ACTION_AND_ADVENTURE = 4;
        public static final int GENRE_SATIRE = 5;
        public static final int GENRE_DRAMA = 6;
        public static final int GENRE_TRAGEDY = 7;
        public static final int GENRE_ROMANCE = 8;
        public static final int GENRE_POETRY = 9;
        public static final int GENRE_HISTORY = 10;

        public static boolean isValidGenre(Integer genre) {
            if (genre == GENRE_UNKNOWN ||
                    genre == GENRE_ACTION_AND_ADVENTURE ||
                    genre == GENRE_DRAMA ||
                    genre == GENRE_FICTION ||
                    genre == GENRE_HISTORY ||
                    genre == GENRE_NON_FICTION ||
                    genre == GENRE_POETRY ||
                    genre == GENRE_ROMANCE ||
                    genre == GENRE_SATIRE ||
                    genre == GENRE_SCIENCE_FICTION ||
                    genre == GENRE_TRAGEDY) {
                return true;
            }
            return false;
        }
    }
}


