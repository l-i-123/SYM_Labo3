package ch.heigvd.iict.sym.a3dcompassapp;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import static android.content.ContentValues.TAG;

public class NFC extends AppCompatActivity implements CommunicationEventListenerString {

    private NfcAdapter mNfcAdapter = null;
    TextView text = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc);

        text = findViewById(R.id.textView3);

    }

    @Override
    public void onResume(){
        super.onResume();

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if(mNfcAdapter == null)
            return;
        final Intent intent = new Intent(this.getApplicationContext(),
                this.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent pendingIntent =
                PendingIntent.getActivity(this.getApplicationContext(), 0, intent, 0);
        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};
        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            Log.e("TAG", "MalformedMimeTypeException", e);
        }
        mNfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, techList);
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // getIntent() should always return the most recent
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        new NdefReaderTask(NFC.this).execute(tag);
        setIntent(intent);
    }

    //Retour de la réponse à afficher sur l'interface graphique
    @Override
    public void handleServerResponse(String response) {
        text.setText(response);
    }


}

class NdefReaderTask extends AsyncTask<Tag, Void, String> {

    NdefReaderTask(CommunicationEventListenerString l){
        cel = l;
    }

    CommunicationEventListenerString cel = null;

    @Override
    protected String doInBackground(Tag... params) {
        Tag tag = params[0];

        Ndef ndef = Ndef.get(tag);
        if (ndef == null) {
            // NDEF is not supported by this Tag.
            return null;
        }

        NdefMessage ndefMessage = ndef.getCachedNdefMessage();

        NdefRecord[] records = ndefMessage.getRecords();
        for (NdefRecord ndefRecord : records) {
            if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                try {
                    return readText(ndefRecord);
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "Unsupported Encoding", e);
                }
            }
        }

        return null;
    }

    private String readText(NdefRecord record) throws UnsupportedEncodingException {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

        byte[] payload = record.getPayload();

        // Get the Text Encoding
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

        // Get the Language Code
        int languageCodeLength = payload[0] & 0063;

        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
        // e.g. "en"

        // Get the Text
        cel.handleServerResponse(new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding));
        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
    }

    @Override
    protected void onPostExecute(String result) {

        if (result != null) {
            //cel.handleServerResponse(result);
        }
    }
}