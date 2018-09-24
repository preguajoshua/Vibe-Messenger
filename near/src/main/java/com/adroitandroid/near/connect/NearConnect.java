package com.adroitandroid.near.connect;

import android.content.Context;
import android.os.Looper;

import com.adroitandroid.near.discovery.NearDiscovery;
import com.adroitandroid.near.model.Host;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;
public interface NearConnect {

    long send(byte[] bytes, Host peer);

    void startReceiving();

    void stopReceiving(boolean abortCurrentTransfers);

    Set<Host> getPeers();

    boolean isReceiving();

    class Builder {
        private Context mContext;
        private Listener mListener;
        private Looper mListenerLooper;
        private Set<Host> mPeers;

        public Builder setContext(Context context) {
            mContext = context;
            return this;
        }

        public Builder setListener(Listener listener, Looper listenerLooper) {
            mListener = listener;
            mListenerLooper = listenerLooper;
            return this;
        }

        public Builder fromDiscovery(NearDiscovery discovery) {
            if (discovery != null) {
                mPeers = discovery.getAllAvailablePeers();
            } else {
                mPeers = null;
            }
            return this;
        }

        public Builder forPeers(Set<Host> peers) {
            mPeers = peers;
            return this;
        }

        public NearConnect build() {
            return new NearConnectImpl(mContext, mListener, mListenerLooper, mPeers);
        }
    }

    interface Listener {
        void onReceive(byte[] bytes, Host sender) throws IOException;

        void onSendComplete(long jobId);

        void onSendFailure(Throwable e, long jobId);

        void onStartListenFailure(Throwable e);
    }
}
