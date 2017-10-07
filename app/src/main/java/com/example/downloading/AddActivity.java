package com.example.downloading;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;



public class AddActivity extends AppCompatActivity {
    EditText url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        url = (EditText) findViewById(R.id.url);
        Button Ok = (Button) findViewById(R.id.ok);

        Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("data_return", url.getText().toString() );
                Log.i("TAT", "onClick: "+ url.getText());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }



}
