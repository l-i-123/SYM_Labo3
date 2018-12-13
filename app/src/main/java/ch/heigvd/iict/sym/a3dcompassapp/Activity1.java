package ch.heigvd.iict.sym.a3dcompassapp;

/**
 * Created by Maxime Vulliens and Elie N'djoli
 * on 20.11.18
 * Description : Activity 1
 */

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Activity1 extends AppCompatActivity {

    private NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity1);

        final TextView errorText = findViewById(R.id.ErrorText);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;

        }

        Button mConnectButton = findViewById(R.id.connectButton);

        mConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mNfcAdapter.isEnabled()){
                    errorText.setText("NFC is disabled.");
                }else{
                    errorText.setText("");
                    Intent intent = new Intent(Activity1.this, NFC.class);
                    startActivity(intent);
                }

            }
        });

    }


}
