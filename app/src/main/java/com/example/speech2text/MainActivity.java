package com.example.speech2text;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    protected static final int RESULT_SPEECH=1;
    protected ImageButton btnSpeak;
    private TextView tvText;
    private TextToSpeech tts;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        tvText=findViewById(R.id.tvText);
        btnSpeak=findViewById(R.id.btnSpeak);
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"en-US");
                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    tvText.setText("");

                }catch(ActivityNotFoundException e){
                    Toast.makeText(getApplicationContext(),"your device doesn't support speech to text",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }

        });
        initTextToSpeech();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void initTextToSpeech(){
        tts=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(tts.getEngines().size()==0){
                    Toast.makeText(MainActivity.this,"Engine is not Availavle",Toast.LENGTH_SHORT).show();
                }
                else{
                    String s=wishMe();
                    speak("hi ,I am Jarvis ai "+s);
                }
            }
        });


    }
    private  String wishMe(){
        String s="";
        Calendar c=Calendar.getInstance();
        int time=c.get(Calendar.HOUR_OF_DAY);
        if(time >=6 && time<12){
            s="Good morning sir";

        }
        else if(time>=12&& time<16){
            s="Good Afternoon  sir";
        }
        else if(time >=16 && time<22){
            s="Good Evening sir";
        }
        else if(time>=22 && time<6){
            s="Good Night";
        }
        return s;
    }


    private void response(String str){
        String strs=str.toLowerCase(Locale.ROOT);
        if(strs.indexOf("hi")!=-1){
            speak("Helo Sir,jarvis at your service please tell me how can i help you?");
        }

        if(strs.indexOf("time")!=-1){
            Date date=new Date();
            String time= DateUtils.formatDateTime(this,date.getTime(),DateUtils.FORMAT_SHOW_TIME);
            speak(time);
        }
        if(strs.indexOf("date")!=-1){
            SimpleDateFormat dt=new SimpleDateFormat("dd mm  yyyy");
            Calendar cal=Calendar.getInstance();
            String todays_Date=dt.format(cal.getTime());
            speak(" the date today is"+todays_Date);

        }

        if(strs.indexOf("google")!=-1) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
            startActivity(intent);
        }
        if(strs.indexOf("youtube")!=-1) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com"));
            startActivity(intent);
        }
        if(strs.indexOf("instagram")!=-1) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com"));
            startActivity(intent);
        }
        if(strs.indexOf("search")!=-1){
            Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.google.com/search?q="+strs.replace("search"," ")));
            startActivity(intent);
        }
        if(strs.indexOf("play")!=-1){
            Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.youtube.com/search?q"+strs));
            
            startActivity(intent);
        }
        if(strs.indexOf("remember")!=-1){
            speak("okay sir i'll remember that for you");
            writeToFile(strs.replace("jarvis remember that"," "));

        }
        if(strs.indexOf("know")!=-1){
            String data =readFromFile();
            speak("yes sir you told me to rember that"+data);
        }


        }
        private String readFromFile(){
    String ret="";
    try{
        InputStream inputStream=openFileInput("data.txt");
        if(inputStream!=null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String Receivestr = "";
            StringBuilder stringBuilder = new StringBuilder();
            while ((Receivestr = bufferedReader.readLine()) != null) {
                stringBuilder.append("\n").append(Receivestr);
            }
            inputStream.close();
            ret = stringBuilder.toString();

        }
        }
    catch(FileNotFoundException e){
        Log.e("Exception","File not found"+e.toString());

    }
    catch (IOException e){
        Log.e("Exception","cannot read file"+e.toString());
    }
    return ret;
    }
    private void writeToFile(String data){
        try{
            OutputStreamWriter outputStreamWriter=new OutputStreamWriter(openFileOutput("data.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch(IOException e){
            Log.e("Exception","File Write Failed"+e.toString());
        }
    }

    private  void speak(String msg){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){

            tts.speak(msg,TextToSpeech.QUEUE_FLUSH,null,null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_SPEECH:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    tvText.setText(text.get(0));
                    response(text.get(0));
                }
                break;
           }
        }
    }
