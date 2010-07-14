/**
 * this is free software
 * It is under the The MIT License http://www.opensource.org/licenses/mit-license.php
 */
package root.magicword.speech;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import android.app.Activity;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;


public class TextSpeakerAndroid implements OnInitListener
{
    private static final String D_LOG = TextSpeakerAndroid.class.getName();

    private TextToSpeech tts;
    
    public static final int SPEECH_DATA_CHECK_CODE = 123555;
    
    private Activity activity;

    private static HashMap<String, String> DUMMY_PARAMS = new HashMap<String, String>();
    
    static 
    {
        DUMMY_PARAMS.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "theUtId");
    }
    
    private ReentrantLock waitForInitLock = new ReentrantLock();

    public TextSpeakerAndroid(Activity parentActivity)
    {
        activity = parentActivity;

        //TODO: move this to the right place
        createTts(activity);

        Log.d(D_LOG, "LOCK: WAIT INIT ");
        waitForInitLock.lock();
    }
    
    public void onInit(int version)
    {
        Log.d(D_LOG, "speech initialized");

        //unlock it
        waitForInitLock.unlock();
        Log.d(D_LOG, "UNLOCK: WAIT INIT ");
    }

    private void createTts(Activity activity)
    {
        tts = new TextToSpeech(activity, this);
        tts.setPitch(0.2f);
    }

    public boolean isSpeaking()
    {
        return tts.isSpeaking();
    }

    public void say(String say)
    {
        tts.speak(say, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void say(String say, OnUtteranceCompletedListener whenTextDone)
    {
        if (waitForInitLock.isLocked())
        {
            try
            {
                Log.d(D_LOG, "WAIT: WAIT INIT ");
                waitForInitLock.tryLock(180, TimeUnit.SECONDS);
            }
            catch (InterruptedException e)
            {
                Log.e(D_LOG, "interruped");
            }
            //unlock it here so that it is never locked again
            waitForInitLock.unlock();
            Log.d(D_LOG, "UNLOCK: (after waiting) WAIT INIT ");
        }
        
        Log.d(D_LOG, "saying: " + whenTextDone.getClass().getName());
        int result = tts.setOnUtteranceCompletedListener(whenTextDone);
        if (result == TextToSpeech.ERROR)
        {
            Log.e(D_LOG, "failed to add utterance listener");
        }
        tts.speak(say, TextToSpeech.QUEUE_FLUSH, DUMMY_PARAMS);
    }
    
    public void done()
    {
        tts.shutdown();
    }
}
