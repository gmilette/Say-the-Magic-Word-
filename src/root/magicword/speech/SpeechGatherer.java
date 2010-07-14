/**
 * this is free software 
 * It is under the The MIT License http://www.opensource.org/licenses/mit-license.php
 */
package root.magicword.speech;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;
import android.util.Log;

/**
 * 
 * @author GregM gregorym@gmail.com
 *
 */
public class SpeechGatherer
{
    private static final String D_LOG = SpeechGatherer.class.getName();
    
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    private final List<String> NOTHING_HEARD = new ArrayList<String>();
    private List<String> lastThingHeard = NOTHING_HEARD;    
    
    private boolean lastResultOk = true;
    private int lastResultCode;
    
    private String prompt;

    public SpeechGatherer(Context context, String prompt)
    {
        setup(context);
        this.prompt = prompt;
    }
    
    public List<String> getLastThingsHeard()
    {
        return lastThingHeard;
    }
    
    public boolean isLastResultOk()
    {
        return lastResultOk;
    }
    
    public int getLastResultCode()
    {
        return lastResultCode;
    }

    /**
     * Called with the activity is first created.
     */
    public void setup(Context context)
    {
        // Check to see if a recognition activity is present
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        
        if (activities.size() != 0) 
        {
            Log.e("main", "Ready");
        } else {
            Log.e("main", "Not Ready");
        }
    }


    public Intent getRecognizeIntent()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, prompt);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 100);
        return intent;
    }
    
    public void clearLastThingHeard()
    {
        lastThingHeard = NOTHING_HEARD;
    }
    
    public boolean isSpeechCode(int requestCode)
    {
        return (requestCode == SpeechGatherer.VOICE_RECOGNITION_REQUEST_CODE);
    }
    
    public void handleRecognition(int requestCode, int resultCode, Intent data, int resultOk)
    {
        if (isSpeechCode(requestCode)
            && resultCode == resultOk) 
        {
            if (resultCode == resultOk)
            {
                lastResultOk = true;
                Log.d("main", "the speech is done!");
                // Fill the list view with the strings the recognizer thought it could have heard
                ArrayList<String> matches = data
                                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                Log.d("main", "matches: ");
                lastThingHeard = new ArrayList<String>(); 
                for (String match : matches)
                {
                    Log.d("main", match);
                    lastThingHeard.add(match);
                }
            }
            else //result was not ok
            {
                lastResultOk = false;
                lastResultCode = resultCode;
            }
        }
            
    }
}