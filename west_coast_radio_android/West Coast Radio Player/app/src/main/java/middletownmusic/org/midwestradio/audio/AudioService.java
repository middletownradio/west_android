package middletownmusic.org.midwestradio.audio;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;


public class AudioService extends Service{
    WifiManager.WifiLock wifiLock;
    AudioPlayer player = new AudioPlayer();
    AudioBinder binder = new AudioBinder();

    public class AudioBinder extends Binder {
        public AudioService getService(){
            return AudioService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        if(wifiLock.isHeld()){
            wifiLock.release();
        }
        player.release();
    }

    @Override
    public void onCreate() {
        System.out.println("making new wifi lock");
        wifiLock = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "audio_wifi_lock");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    public void release(){
        player.release();
    }

    public void startPlaying(String source){
        player.prepareAndPlay(source);
    }

    public void stopPlaying(){ player.stop(); }

    public boolean isPlaying() { return player.isPlaying(); }

}
