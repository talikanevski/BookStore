package com.example.com.bookstore.data;

import android.provider.BaseColumns;

public final class BookContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private BookContract() {
    }

    /**
     * Inner class that defines constant values for the books database table.
     * Each entry in the table represents a single book.
     */
    public static final class BookEntry implements BaseColumns {

        public static final String TABLE_NAME = "books";

        public static final String _ID = BaseColumns._ID;
        public final static String COLUMN_BOOK_NAME = "product_name";
        public final static String COLUMN_BOOK_GENRE = "genre";
        public final static String COLUMN_BOOK_PRICE = "price";
        public final static String COLUMN_BOOK_QUANTITY = "quantity";
        public final static String COLUMN_BOOK_SUPPLIER = "supplier";
        public final static String COLUMN_PHONE_NUMBER_OF_SUPPLIER = "phone";


        /**
         * Possible values for the genre of the book.
         */
        public static final int GENRE_UNKNOWN = 0;
        public static final int GENDER_FICTION = 1;
        public static final int GENDER_SCIENCE_FICTION = 2;
        public static final int GENDER_NON_FICTION = 3;
        public static final int GENDER_ACTION_AND_ADVENTURE = 4;
        public static final int GENDER_SATIRE = 5;
        public static final int GENDER_DRAMA = 6;
        public static final int GENDER_TRAGEDY = 7;
        public static final int GENDER_ROMANCE = 8;
        public static final int GENDER_POETRY = 9;
        public static final int GENDER_HISTORY = 10;
    }
}


