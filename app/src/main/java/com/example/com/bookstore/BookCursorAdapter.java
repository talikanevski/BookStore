package com.example.com.bookstore;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.com.bookstore.data.BookContract;

/**
 * {@link BookCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of book data as its data source. This adapter knows
 * how to create list items for each row of book data in the {@link Cursor}.
 */
public class BookCursorAdapter extends CursorAdapter {
    /**
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }
    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the book data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current book can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        // Find fields to populate in inflated template
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView author = (TextView) view.findViewById(R.id.author);
        final TextView quantity = (TextView) view.findViewById(R.id.quantity);
        TextView price = (TextView) view.findViewById(R.id.price);
        // Find sale button
        Button sale = view.findViewById(R.id.sellButton);

        // Find the columns of pet attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_NAME);
        int authorColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_AUTHOR);
        int quantityColumnIndex = cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_BOOK_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_BOOK_PRICE);

        // Read the book attributes from the Cursor for the current book
        String bookName = cursor.getString(nameColumnIndex);
        String bookAuthor = cursor.getString(authorColumnIndex);
        String booksInStock = cursor.getString(quantityColumnIndex);
        final int bookQuantity = cursor.getInt(quantityColumnIndex);
        String priceOfTheBook = cursor.getString(priceColumnIndex);
        final int bookPrice = cursor.getInt(priceColumnIndex);

        if (bookQuantity == 0) {
            sale.setEnabled(false);
            sale.setText("Out of stock");

        } else {
            sale.setEnabled(true);
            sale.setText(R.string.sell);
            final String id = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry._ID));

            sale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Uri currentBookUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, Long.parseLong(id));
                    ContentValues values = new ContentValues();
                    values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, bookQuantity - 1);
                    context.getContentResolver().update(currentBookUri, values, null, null);
                    swapCursor(cursor);
                    // Check if out of stock to display toast
                    if (bookQuantity == 1) {
                        Toast.makeText(context, R.string.toast_out_of_stock, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        // Update the TextViews with the attributes for the current book
        name.setText(bookName);
        author.setText(bookAuthor);
        quantity.setText(booksInStock);
        price.setText(priceOfTheBook);
    }
}