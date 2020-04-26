package pl.adamswiatkowski.sm_p2;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

public class EditActivity extends Activity {
    private EditText marka;
    private EditText model;
    private EditText android;
    private EditText url;
    private Button zapisz;
    private Button anuluj;
    private Button urlButton;
    private long rowID;
    boolean markaOkej=false;
    boolean modelOkej=false;
    boolean androidOkej=false;
    boolean urlOkej=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        marka=(EditText)findViewById(R.id.Marka);
        model=(EditText)findViewById(R.id.Model);
        android=(EditText)findViewById(R.id.Android);
        url=(EditText)findViewById(R.id.WWW);
        zapisz=(Button)findViewById(R.id.zapisz);
        zapisz.setEnabled(false);
        anuluj=(Button)findViewById(R.id.anuluj);
        urlButton=(Button)findViewById(R.id.wwwButton);
        urlButton.setEnabled(false);

        url.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String text = marka.getText().toString();
                        if (text.isEmpty()) {
                            Toast tost = Toast.makeText(getApplicationContext(), "url nie może być puste", Toast.LENGTH_SHORT);
                            tost.show();
                            urlOkej = false;
                        } else {
                            urlOkej = true;
                            urlButton.setEnabled(true);
                        }
                        czyGotowe();
                    }
                }
        );

        android.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String text = android.getText().toString();
                        if (text.isEmpty()) {
                            Toast tost = Toast.makeText(getApplicationContext(), "Android nie może być pusty", Toast.LENGTH_SHORT);
                            tost.show();
                            androidOkej = false;
                        } else {
                            androidOkej = true;
                        }
                        czyGotowe();
                    }
                }
        );

        marka.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String text = marka.getText().toString();
                        if (text.isEmpty()) {
                            Toast tost = Toast.makeText(getApplicationContext(), "Marka nie może być pusta", Toast.LENGTH_SHORT);
                            tost.show();
                            markaOkej = false;
                        } else {
                            markaOkej = true;
                        }
                        czyGotowe();
                    }
                }
        );

        model.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String text = model.getText().toString();
                        if (text.isEmpty()) {
                            Toast tost = Toast.makeText(getApplicationContext(), "Model nie może być pusty", Toast.LENGTH_SHORT);
                            tost.show();
                            modelOkej = false;
                        } else {
                            modelOkej = true;
                        }
                        czyGotowe();
                    }
                }
        );

        rowID = -1;
        if(savedInstanceState != null) {
            rowID = savedInstanceState.getLong(PomocnikBD.ID);
        } else {
            Bundle tobolek = getIntent().getExtras();
            if(tobolek != null) {
                rowID = tobolek.getLong(PomocnikBD.ID);
            }
        }
        if (rowID != -1) {
            String[] projekcja = {PomocnikBD.MARKA, PomocnikBD.MODEL, PomocnikBD.ANDROID, PomocnikBD.WWW};
            Cursor kursor = getContentResolver().query(ContentUris.withAppendedId(Provider.URI_ZAWARTOSCI, rowID), projekcja,null,null,null);
            kursor.moveToFirst();
            int indeks = kursor.getColumnIndexOrThrow(PomocnikBD.MARKA);
            String tekst = kursor.getString(indeks);
            marka.setText(tekst);
            model.setText(kursor.getString(kursor.getColumnIndexOrThrow(PomocnikBD.MODEL)));
            android.setText(kursor.getString(kursor.getColumnIndexOrThrow(PomocnikBD.ANDROID)));
            url.setText(kursor.getString(kursor.getColumnIndexOrThrow(PomocnikBD.WWW)));
            kursor.close();
        }

        zapisz.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ContentValues wartosci = new ContentValues();
                        wartosci.put(PomocnikBD.MARKA,marka.getText().toString());
                        wartosci.put(PomocnikBD.MODEL,model.getText().toString());
                        wartosci.put(PomocnikBD.ANDROID,android.getText().toString());
                        wartosci.put(PomocnikBD.WWW,url.getText().toString());
                        if(rowID == -1) {
                            Uri uri = getContentResolver().insert(Provider.URI_ZAWARTOSCI, wartosci);
                            rowID = Integer.parseInt(uri.getLastPathSegment());
                        } else {
                            getContentResolver().update(ContentUris.withAppendedId(Provider.URI_ZAWARTOSCI, rowID), wartosci,null,null);
                        }
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
        );

        anuluj.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                }
        );
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(PomocnikBD.ID, rowID);
    }

    public void otworzUrl(View view) {
        String temp = url.getText().toString();
        if(!temp.startsWith("http://")){
            temp="http://"+url;
        }
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(temp));
        startActivity(intent);
    }

    public void czyGotowe() {
        if(markaOkej & modelOkej & androidOkej & urlOkej) {
            zapisz.setEnabled(true);
        }
    }
}
