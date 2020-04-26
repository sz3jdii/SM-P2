package com.example.sm_p2;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Provider extends ContentProvider {

    private PomocnikBD pomocnikBD;
    private static final int CALA_TABELA = 1;
    private static final int WYBRANY_WIERSZ = 2;
    private static final String IDENTYFIKATOR = "com.example.sm_p2.Provider";
    public static final Uri URI_ZAWARTOSCI = Uri.parse("content://" + IDENTYFIKATOR + "/" + PomocnikBD.NAZWA_TABELI);
    private static final UriMatcher dopasowanieUri = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        dopasowanieUri.addURI(IDENTYFIKATOR, PomocnikBD.NAZWA_TABELI, CALA_TABELA);
        dopasowanieUri.addURI(IDENTYFIKATOR, PomocnikBD.NAZWA_TABELI + "/#", WYBRANY_WIERSZ);
    }

    @Override
    public boolean onCreate() {
        pomocnikBD = new PomocnikBD(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int uriType = dopasowanieUri.match(uri);
        SQLiteDatabase bd = pomocnikBD.getWritableDatabase();
        Cursor kursor = null;
        switch(uriType) {
            case CALA_TABELA:
                kursor = bd.query(false, PomocnikBD.NAZWA_TABELI, projection, selection, selectionArgs, null, null, sortOrder, null, null);
                break;

            case WYBRANY_WIERSZ:
                kursor = bd.query(false, PomocnikBD.NAZWA_TABELI, projection, addID(selection, uri), selectionArgs, null, null, sortOrder, null, null);
                break;

            default:
                throw new IllegalArgumentException("Nieznane URI: " + uri);
        }
        kursor.setNotificationUri(getContext().getContentResolver(), uri);
        return kursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int uriType = dopasowanieUri.match(uri);
        SQLiteDatabase bd = pomocnikBD.getWritableDatabase();
        long added = 0;
        switch(uriType) {
            case CALA_TABELA:
                added = bd.insert(PomocnikBD.NAZWA_TABELI, null, values);
                break;

            default:
                throw new IllegalArgumentException("Nieznane URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(PomocnikBD.NAZWA_TABELI + "/" + added);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int uriType = dopasowanieUri.match(uri);
        SQLiteDatabase bd = pomocnikBD.getWritableDatabase();
        int deleted = 0;
        switch(uriType) {
            case CALA_TABELA:
                deleted = bd.delete(PomocnikBD.NAZWA_TABELI, selection, selectionArgs);
                break;

            case WYBRANY_WIERSZ:
                deleted = bd.delete(PomocnikBD.NAZWA_TABELI, addID(selection, uri), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Nieznane URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return deleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int uriType = dopasowanieUri.match(uri);
        SQLiteDatabase bd = pomocnikBD.getWritableDatabase();
        int updated = 0;
        switch(uriType) {
            case CALA_TABELA:
                updated = bd.update(PomocnikBD.NAZWA_TABELI, values, selection, selectionArgs);
                break;

            case WYBRANY_WIERSZ:
                updated = bd.update(PomocnikBD.NAZWA_TABELI, values, addID(selection, uri), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Nieznane URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return updated;
    }

    private String addID(String selection, Uri uri) {
        if(selection != null && !selection.equals("")) {
            selection = selection + " and " + PomocnikBD.ID + "=" + uri.getLastPathSegment();
        } else {
            selection = PomocnikBD.ID + "=" + uri.getLastPathSegment();
        }
        return selection;
    }
}
