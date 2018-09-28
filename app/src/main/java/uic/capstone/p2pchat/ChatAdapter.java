package uic.capstone.p2pchat;

import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import uic.capstone.p2pchat.databinding.RowMessagesBinding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageVH> {
    private final List<String> mMessages;
    private final List<String> mRecordings;
    private final List<String> mMessageSender;
    String outputString;
    String AES = "AES";
    String key = "VibeMessengerS1cUr!tyc0d3";
    String mDecrypt = "";

    ChatAdapter() {
        mMessages = new ArrayList<>();
        mMessageSender = new ArrayList<>();
        mRecordings = new ArrayList<>();
    }

    @Override
    public MessageVH onCreateViewHolder(ViewGroup parent, int viewType) {
        RowMessagesBinding binding = RowMessagesBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new MessageVH(binding);
    }


    @Override
    public void onBindViewHolder(MessageVH holder, int position) {
        String sender = mMessageSender.get(position);
        holder.binding.setSender(sender);
        holder.binding.setMessage(mMessages.get(position));
        holder.binding.setUserIsSender("you".equalsIgnoreCase(sender));
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    void addMessage(String message, String senderName) {
        //decrypt
        try{
            mDecrypt = decrypt(message,key);
        }catch(Exception e){
            e.printStackTrace();
        }
        mMessages.add(mDecrypt);
        mMessageSender.add(senderName);
        notifyItemInserted(mMessages.size() - 1);
    }

    class MessageVH extends RecyclerView.ViewHolder {
        private final RowMessagesBinding binding;
        MessageVH(RowMessagesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
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
}
