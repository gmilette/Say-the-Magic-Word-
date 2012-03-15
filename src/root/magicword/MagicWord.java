/**
 * this is free software 
 * It is under the The MIT License http://www.opensource.org/licenses/mit-license.php
 */
package root.magicword;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MagicWord extends Activity implements OnInitListener
{
    private static final String TAG = "MagicWord";
    
    private TextView result;
    
    private TextToSpeech tts;
    
    private Button speak;
    
    private int SPEECH_REQUEST_CODE = 1234;
    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        speak = (Button)findViewById(R.id.bt_speak);
        speak.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendRecognizeIntent();
            }
        });
        
        speak.setEnabled(false);
        result = (TextView)findViewById(R.id.tv_result);
        
        tts = new TextToSpeech(this, this);
    }
    
    @Override
    public void onInit(int status)
    {
        if (status == TextToSpeech.SUCCESS)
        {
            speak.setEnabled(true);
        }
        else
        {
            //failed to init
            finish();
        }
        
    }
    
    private void sendRecognizeIntent()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say the magic word");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 100);
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override
    protected void
            onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == SPEECH_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                
                if (matches.size() == 0)
                {
                    tts.speak("Heard nothing", TextToSpeech.QUEUE_FLUSH, null);
                }
                else
                {
                    String mostLikelyThingHeard = matches.get(0);
                    String magicWord = this.getResources().getString(R.string.magicword);
                    if (mostLikelyThingHeard.equals(magicWord))
                    {
                        tts.speak("You said the magic word!", TextToSpeech.QUEUE_FLUSH, null);
                    }
                    else
                    {
                        tts.speak("The magic word is not " + mostLikelyThingHeard + " try again", TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
                result.setText("heard: " + matches);
            }
            else
            {
                Log.d(TAG, "result NOT ok");
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    protected void onDestroy()
    {
        if (tts != null)
        {
            tts.shutdown();
        }
        super.onDestroy();
    }
}