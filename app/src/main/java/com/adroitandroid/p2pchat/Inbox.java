package com.adroitandroid.p2pchat;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Random;

import android.media.MediaPlayer;
import android.media.MediaRecorder;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;

import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import com.adroitandroid.near.connect.NearConnect;
import com.adroitandroid.near.model.Host;

import io.reactivex.disposables.Disposable;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Inbox extends AppCompatActivity {

    private NearConnect mNearConnect;
    private Snackbar mDiscoveryInProgressSnackbar;
    private Host mParticipant;
    private Disposable mStatusDisposable;
    private ChatAdapter mChatAdapter;
    public static final String BUNDLE_PARTICIPANT = "bundle_participant";
    public static final int RequestPermissionCode = 1;
    String AudioSavePathInDevice = "";
    MediaRecorder mediaRecorder ;
    Random random ;
    MediaPlayer mediaPlayer ;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    ImageButton mSend, mRecord;
    EditText mText;
    ListView listview;
    ArrayList<String> list;
    ArrayAdapter<String> arrayAdapter;
    ArrayAdapter<String> adapter;

    private ArrayList<String> mFileName = new ArrayList<>();
    private static final String  TAG = "Inbox";

    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        if(getSupportActionBar()!=null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }
        mSend = (ImageButton) findViewById(R.id.sendbutton);
        mRecord = (ImageButton) findViewById(R.id.voicerecord);
        listview = (ListView) findViewById(R.id.listview_chat);
        mText = (EditText) findViewById(R.id.msg_et);
        list = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,list);
        mParticipant = getIntent().getParcelableExtra(BUNDLE_PARTICIPANT);
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mText.getText().toString();
                list.add(text);
                listview.setAdapter(arrayAdapter);
                arrayAdapter.notifyDataSetChanged();
            }
        });

        random = new Random();
        if(checkPermission()) {
            mRecord.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        AudioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath()
                                + "/" + CreateRandomAudioFileName(5) + "AudioRecording.mp3";
                        MediaRecorderReady();
                        try {
                            mediaRecorder.prepare();
                            mediaRecorder.start();
                        } catch (IllegalStateException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        Toast.makeText(Inbox.this, "Recording!", Toast.LENGTH_LONG).show();
                    }
                    else if (event.getAction() == MotionEvent.ACTION_UP) {
                        mediaRecorder.stop();
                        Toast.makeText(Inbox.this, "Recording stopped!", Toast.LENGTH_LONG).show();
                        try{
//                            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(AudioSavePathInDevice)));
//
                            String line;
                            ArrayList<String> lines = new ArrayList<>();
                            lines.add(AudioSavePathInDevice);
                            adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, lines);
                            listview.setAdapter(adapter);
//                            br.close();

                        }catch (Exception e){
                            e.printStackTrace();

                        }

                        File audioFile = new File(AudioSavePathInDevice);
                        byte[] audioFileByteStream  = new byte[(int) audioFile.length()];
                        try{
//                            convertFileToByte(AudioSavePathInDevice);
                            FileInputStream audioFileInputStream = new FileInputStream(audioFile);
                            audioFileInputStream.read(audioFileByteStream);
                            audioFileInputStream.close();

                            for(int ctr = 0; ctr < audioFileByteStream.length; ctr++ ){
                                System.out.println("Byte:"+(char)audioFileByteStream[ctr] );
                            }

                            writeToFile(audioFileByteStream,AudioSavePathInDevice);

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                    return true;
                }
            });
        } else {
            requestPermission();
        }

//        mFileName.add(AudioSavePathInDevice);
//        initRecyclerView();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String file = listview.getItemAtPosition(position).toString();

                Toast.makeText(getApplicationContext(),file, Toast.LENGTH_SHORT).show();

                MediaPlayer mp = new MediaPlayer();
                try {
                    mp.setDataSource(getApplicationContext(), Uri.parse(file));
                } catch (Exception e) {}
                try {
                    mp.prepare();
                } catch (Exception e) {}
                mp.start();
            }
        });
    }

//    public void initRecyclerView(){
//        Log.d(TAG,"initRecyclerView: init recyclerview.");
//
//        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.chat_inbox);
//        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mFileName);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//    }
    public void writeToFile(byte[] data, String fileName) throws IOException {
        FileOutputStream out = new FileOutputStream(fileName+"received");
        out.write(data);
        out.close();
        Toast.makeText(Inbox.this, "Saved to Device!: "+fileName, Toast.LENGTH_LONG).show();
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(Inbox.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }


    public String CreateRandomAudioFileName(int string){
        StringBuilder stringBuilder = new StringBuilder( string );
        int i = 0 ;
        while(i < string ) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));

            i++ ;
        }
        return stringBuilder.toString();
    }
    public void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }
    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(Inbox.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(Inbox.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
