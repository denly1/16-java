package com.example.db2;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddBookActivity extends AppCompatActivity {
    private EditText editTextName, editTextAuthor;
    private Button addButton, updateButton, deleteButton;
    private DataBaseHelper dbHelper;
    private int bookId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        editTextName = findViewById(R.id.editTextName);
        editTextAuthor = findViewById(R.id.editTextAuthor);
        addButton = findViewById(R.id.add);
        updateButton = findViewById(R.id.edit);
        deleteButton = findViewById(R.id.delete);


        dbHelper = new DataBaseHelper(this);


        Intent intent = getIntent();
        bookId = intent.getIntExtra("BOOK_ID", -1);


        if (bookId != -1) {
            loadBookData();
            addButton.setVisibility(View.GONE);
            updateButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            updateButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        }

        addButton.setOnClickListener(v -> addBookToDatabase());
        updateButton.setOnClickListener(v -> updateBook());
        deleteButton.setOnClickListener(v -> deleteBook());
    }

    private void loadBookData() {
        Cursor cursor = dbHelper.getAllBooks();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_ID));
                if (id == bookId) {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_NAME));
                    String author = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_AUTHOR));
                    editTextName.setText(name);
                    editTextAuthor.setText(author);
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void addBookToDatabase() {
        String bookName = editTextName.getText().toString().trim();
        String bookAuthor = editTextAuthor.getText().toString().trim();

        if (bookName.isEmpty() || bookAuthor.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        long result = dbHelper.addBook(bookName, bookAuthor);

        if (result > 0) {
            Toast.makeText(this, "Книга добавлена", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddBookActivity.this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Ошибка добавления книги", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateBook() {
        String newName = editTextName.getText().toString();
        String newAuthor = editTextAuthor.getText().toString();

        if (dbHelper.updateBook(bookId, newName, newAuthor) > 0) {
            Toast.makeText(this, "Книга обновлена успешно", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Ошибка при обновлении книги", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteBook() {
        if (dbHelper.deleteBookById(bookId) > 0) {
            Toast.makeText(this, "Книга удалена успешно", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Ошибка при удалении книги", Toast.LENGTH_SHORT).show();
        }
    }
}

