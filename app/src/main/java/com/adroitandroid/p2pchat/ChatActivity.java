package com.adroitandroid.p2pchat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.adroitandroid.near.connect.NearConnect;
import com.adroitandroid.near.model.Host;
import com.adroitandroid.p2pchat.databinding.ActivityChatBinding;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;


import java.io.File;
import java.io.FileInputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

// for buttons

public class ChatActivity extends AppCompatActivity {

    public static final String BUNDLE_PARTICIPANT = "bundle_participant";
    public static final String MSG_PREFIX = "msg:";
    public static final String RCD_PREFIX = "rcd:";
    private static final String STATUS_TYPING = "status:typing";
    private static final String STATUS_STOPPED_TYPING = "status:stopped_typing";
    private static final String STATUS_EXIT_CHAT = "status:exit_chat";
    private ActivityChatBinding binding;
    private NearConnect mNearConnect;
    private Host mParticipant;
    private Disposable mStatusDisposable;
    private ChatAdapter mChatAdapter;
    ImageButton mRecord;
    String AudioSavePathInDevice = "";
    MediaRecorder mediaRecorder ;
    Random random ;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer ;
    String outputString;
    String AES = "AES";
    String key = "VibeMessengerS1cUr!tyc0d3";
    ArrayList myList;
    String files = "";
    MediaPlayer mp;
    ListView listView;
    ArrayAdapter adapter;
    ImageButton btnSend;
    EditText txtMessage;
    ListView listview;


    RecyclerView recyclerView;
    ArrayList<String> Recordings;
    public static void start(Activity activity, Host participant) {
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra(BUNDLE_PARTICIPANT, participant);
        activity.startActivityForResult(intent, 1234);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        binding.chatRv.setLayoutManager(new LinearLayoutManager(this));
        mChatAdapter = new ChatAdapter();
        binding.chatRv.setAdapter(mChatAdapter);

//        listview = (ListView) findViewById(R.id.chat_lv);
        recyclerView = (RecyclerView) findViewById(R.id.chat_rv);
        Recordings = new ArrayList<String>();

        mChatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                binding.chatRv.scrollToPosition(positionStart);
            }
        });

        mParticipant = getIntent().getParcelableExtra(BUNDLE_PARTICIPANT);
        setTitle(mParticipant.getName());

        if(getSupportActionBar()!=null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        btnSend = (ImageButton) findViewById(R.id.send_btn);
        mRecord = (ImageButton) findViewById(R.id.start_record);
        txtMessage = (EditText) findViewById(R.id.msg_et);
        btnSend.setVisibility(View.GONE);
        mRecord.setVisibility(View.VISIBLE);
        txtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String message = txtMessage.getText().toString().trim();
                if(!message.isEmpty()){
                    mRecord.setVisibility(View.GONE);
                    btnSend.setVisibility(View.VISIBLE);
                    btnSend.setEnabled(true);
                }
                else{
                    mRecord.setVisibility(View.VISIBLE);
                    btnSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        initNearConnect();
        initMessagingUi();

    }

    public void writeToFile(byte[] data, String fileName) throws IOException {
        FileOutputStream out = new FileOutputStream(fileName);
        out.write(data);
        out.close();
        Toast.makeText(ChatActivity.this, "Saved to Device!: "+fileName, Toast.LENGTH_LONG).show();
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

    private void requestPermission() {
        ActivityCompat.requestPermissions(ChatActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
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
                        Toast.makeText(ChatActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ChatActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }


    private void initNearConnect() {
        ArraySet<Host> peers = new ArraySet<>();
        peers.add(mParticipant);
        mNearConnect = new NearConnect.Builder()
                .forPeers(peers)
                .setContext(this)
                .setListener(getNearConnectListener(), Looper.getMainLooper()).build();
        mNearConnect.startReceiving();
    }

    @NonNull
    private NearConnect.Listener getNearConnectListener() {
        return new NearConnect.Listener() {
            @Override
            public void onReceive(byte[] bytes, Host sender) throws IOException {
                if (bytes != null) {
                    String data = new String(bytes);
                    byte[] record = bytes;
                    switch (data) {
                        case STATUS_TYPING:
                            binding.statusTv.setVisibility(View.VISIBLE);
                            mStatusDisposable.dispose();
                            mStatusDisposable = Observable.timer(1, TimeUnit.SECONDS)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<Long>() {
                                        @Override
                                        public void accept(@io.reactivex.annotations.NonNull Long aLong) throws Exception {
                                            binding.statusTv.setVisibility(View.INVISIBLE);
                                        }
                                    });
                            break;
                        case STATUS_STOPPED_TYPING:
                            binding.statusTv.setVisibility(View.INVISIBLE);
                            break;
                        case STATUS_EXIT_CHAT:
                            binding.statusTv.setVisibility(View.INVISIBLE);
                            binding.msgLl.setVisibility(View.GONE);
                            new AlertDialog.Builder(ChatActivity.this)
                                    .setMessage(String.format("%s has left the chat", sender.getName()))
                                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            mNearConnect.stopReceiving(true);
                                            ChatActivity.this.setResult(RESULT_OK);
                                            ChatActivity.this.finish();
                                        }
                                    }).create().show();
                            break;
                        default:
                            if (data.startsWith(MSG_PREFIX)) {
                                mChatAdapter.addMessage(data.substring(4), sender.getName());
                                Toast.makeText(ChatActivity.this, "Data starts with MSG+PREFIX", Toast.LENGTH_LONG).show();
                            } //Receieve voice message
                            else{
//                                AudioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath();
//                                FileOutputStream out = new FileOutputStream(AudioSavePathInDevice);
//                                out.write(bytes);
//                                out.close();
                                random = new Random();
                                String fileName = Environment.getExternalStorageDirectory().getAbsolutePath()
                                        + "/" + CreateRandomAudioFileName(5) + "AudioRecording.mp3";
                                mChatAdapter.addMessage(fileName, sender.getName());
                                writeToFile(record,fileName);
                                Toast.makeText(ChatActivity.this, "File has been saved!", Toast.LENGTH_LONG).show();
                                MediaPlayer mp = new MediaPlayer();
                                try {
                                    mp.setDataSource(getApplicationContext(), Uri.parse(fileName));
                                } catch (Exception e) {}
                                try {
                                    mp.prepare();
                                } catch (Exception e) {

                                }
                                mp.start();
                            }
                            break;
                    }
                }
            }

            @Override
            public void onSendComplete(long jobId) {
//                update UI with sent status if necessary
            }

            @Override
            public void onSendFailure(Throwable e, long jobId) {

            }

            @Override
            public void onStartListenFailure(Throwable e) {

            }
        };
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initMessagingUi() {

//        binding.statusTv.setText(String.format("%s is typing...", mParticipant.getName()));
        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNearConnect.send(STATUS_STOPPED_TYPING.getBytes(), mParticipant);
                String message = binding.msgEt.getText().toString();
                String mEncrypttion = "";
                try{
                    mEncrypttion = encrypt(message,key);
                }catch (Exception e){
                    e.printStackTrace();
                }
                mNearConnect.send((MSG_PREFIX + mEncrypttion).getBytes(), mParticipant);
                mChatAdapter.addMessage(mEncrypttion, "You");
                binding.msgEt.setText("");
            }
        });
        RxTextView.textChanges(binding.msgEt)
                .debounce(200, TimeUnit.MILLISECONDS)
                .subscribe(new Observer<CharSequence>() {
                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CharSequence charSequence) {
                        if (charSequence.length() > 0) {
                            mNearConnect.send(STATUS_TYPING.getBytes(), mParticipant);
                        } else {
                            mNearConnect.send(STATUS_STOPPED_TYPING.getBytes(), mParticipant);
                        }
                    }
                });
        mStatusDisposable = Observable.timer(0, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Long aLong) throws Exception {
                        binding.statusTv.setVisibility(View.INVISIBLE);
                    }
                });


        //voice button
        if(checkPermission()) {
            binding.startRecord.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        mNearConnect.send(STATUS_STOPPED_TYPING.getBytes(), mParticipant);
                        random = new Random();
                        AudioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath()
                                + "/" + CreateRandomAudioFileName(5) + "AudioRecording.mp3";

                        MediaRecorderReady();
                        try {
                            mediaRecorder.prepare();
                            mediaRecorder.start();
                        } catch (IllegalStateException | IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }else if (event.getAction() == MotionEvent.ACTION_UP) {
                        mediaRecorder.stop();
                        String mEncrypttion = "";
                        try {
                            mEncrypttion = encrypt(AudioSavePathInDevice, key);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        File audioFile = new File(AudioSavePathInDevice);
                        byte[] audioFileByteStream  = new byte[(int) audioFile.length()];
                        try{
                            FileInputStream audioFileInputStream = new FileInputStream(audioFile);
                            audioFileInputStream.read(audioFileByteStream);
                            audioFileInputStream.close();
                        }catch(Exception e){
                            e.printStackTrace();
                        }

                        mNearConnect.send(audioFileByteStream, mParticipant);
                        mChatAdapter.addMessage(mEncrypttion, "You");
                        binding.msgEt.setText("");
                    }
                    return true;
                }
            });
            RxTextView.textChanges(binding.msgEt)
                    .debounce(200, TimeUnit.MILLISECONDS)
                    .subscribe(new Observer<CharSequence>() {
                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }

                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(CharSequence charSequence) {
                            if (charSequence.length() > 0) {
                                mNearConnect.send(STATUS_TYPING.getBytes(), mParticipant);
                            } else {
                                mNearConnect.send(STATUS_STOPPED_TYPING.getBytes(), mParticipant);
                            }
                        }
                    });
            mStatusDisposable = Observable.timer(0, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(@io.reactivex.annotations.NonNull Long aLong) throws Exception {
                            binding.statusTv.setVisibility(View.INVISIBLE);
                        }
                    });
        }
        else{
            requestPermission();
        }
    }
    public static double getFileSizeMegaByte(File file){
        return (double) file.length()/(1024*1024);
    }


    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mNearConnect.send(STATUS_EXIT_CHAT.getBytes(), mParticipant);
        mNearConnect.stopReceiving(true);
    }

    private String encrypt(String Data, String key) throws Exception{
        SecretKeySpec skey = generateKey(key);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE,skey);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
        return encryptedValue;
    }
    private String decrypt(String outputString, String password) throws Exception{
        SecretKeySpec skey = generateKey(password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE,skey);
        byte[] decodeValue = Base64.decode(outputString, Base64.DEFAULT);
        byte[] decValue = c.doFinal(decodeValue);
        String decryptValue = new String(decValue);
        return decryptValue;
    }

    private SecretKeySpec generateKey(String password) throws Exception{
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes,0,bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}