package com.ttd.cain.timetodie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.ttd.cain.timetodie.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user clicks the Send button */
    public void sendMessage(View view){
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void onButtonTap(View v){
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        Toast myToast = Toast.makeText(
                getApplicationContext(),
                //"Ouch!",
                message,
                Toast.LENGTH_LONG);
        myToast.show();
    }
}
