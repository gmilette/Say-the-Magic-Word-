/**
 * this is free software 
 * It is under the The MIT License http://www.opensource.org/licenses/mit-license.php
 */
package root.magicword;

import java.util.List;

import root.magicword.speech.SpeechGatheringActivity;
import root.magicword.speech.TextSpeakerAndroid;
import android.os.Bundle;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MagicWord extends SpeechGatheringActivity implements OnUtteranceCompletedListener
{
    private TextSpeakerAndroid speaker;
    
    private TextView result;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        speaker = new TextSpeakerAndroid(this);
        
        Button speak = (Button)findViewById(R.id.bt_speak);
        speak.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               gatherSpeech();
            }
        });
        
        result = (TextView)findViewById(R.id.tv_result);
    }
    
    @Override
    public void onUtteranceCompleted(String utteranceId)
    {
        //done speaking, execute some ui updates on the UIthread
    }
    
    public void receiveWhatWasHeard(List<String> lastThingsHeard)
    {
        if (lastThingsHeard.size() == 0)
        {
            speaker.say("Heard nothing", this);
        }
        else
        {
            String mostLikelyThingHeard = lastThingsHeard.get(0);
            String magicWord = this.getResources().getString(R.string.magicword);
            if (mostLikelyThingHeard.equals(magicWord))
            {
                speaker.say("You said the magic word!", this);
            }
            else
            {
                speaker.say("It's not " + mostLikelyThingHeard + " try again", this);
            }
        }
        result.setText("heard: " + lastThingsHeard);
    }

    
}