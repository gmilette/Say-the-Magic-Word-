/**
 * this is free software 
 * It is under the The MIT License http://www.opensource.org/licenses/mit-license.php
 */

package root.magicword.speech;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

/**
 * abstract class for getting speech
 * @author gmilette
 */
public abstract class SpeechGatheringActivity extends Activity
{
    private static final String D_LOG = "speechAct";
    
    public static final String PROMPT_DATA = "promptdata";
    private SpeechGatherer recognizer;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);

        String message = getMessage(this.getIntent());  //StringResourceUtil.getMessage(this, R.string.SpeechHeader, R.array.SpeechInstructions, false);

        recognizer = new SpeechGatherer(this, message);
        Log.d("main", "done creating voice");
        
    }

    private static String getMessage(Intent intent)
    {
        String message = intent.getStringExtra(PROMPT_DATA);
        return message;
    }

    /**
     * calling class can use this to create the appropriate intent
     */
    public static Intent createIntent(String message, Context context)
    {
        Intent intent = new Intent(context, SpeechGatheringActivity.class);
        intent.putExtra(PROMPT_DATA, message);
        return intent;
    }
    
    public void gatherSpeech()
    {
        Intent recognizeIntent = recognizer.getRecognizeIntent();
        recognizer.clearLastThingHeard();
        startActivityForResult(recognizeIntent, SpeechGatherer.VOICE_RECOGNITION_REQUEST_CODE);
    }
    
    protected void doGatherSpeech()
    {
        gatherSpeech();
    }
    /**
     * Handle the results from the recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Speech", "GOT SPEECH RESULT " + resultCode + " req: " + requestCode);

        if (recognizer.isSpeechCode(requestCode))
        {
            recognizer.handleRecognition(requestCode, resultCode, data,
                RESULT_OK);
            
            boolean heardSomething = recognizer.getLastThingsHeard().size() > 0;

            if (heardSomething)
            {
                //after capture it report what was heard
                Log.d("Speech", "I heard: " + recognizer.getLastThingsHeard());
            }
            receiveWhatWasHeard(recognizer.getLastThingsHeard());

            //Log.d(D_LOG, "UNLOCK: unlocking collect lock for speech");
            //executeState.getCollectLock().unlock();
        }
        else
        {
            Log.d(D_LOG, "speech activity: unrecogonized result");
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    abstract public void receiveWhatWasHeard(List<String> lastThingsHeard);
}