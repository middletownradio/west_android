package middletownmusic.org.midwestradio.audio;

import android.net.Uri;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultHttpDataSource;

public class AudioPlayer {

    private static final int RENDERER_COUNT = 1;
    private static final int MIN_BUFFER_MS = 1000;
    private static final int MIN_REBUFFER_MS = 5000;
    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    public static final int STATE_STOPPED = 5;
    private int currentState;
    private String currentStream = "";


    private ExoPlayer player = ExoPlayer.Factory.newInstance(RENDERER_COUNT, MIN_BUFFER_MS, MIN_REBUFFER_MS);
    private Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_COUNT);
    private DataSource dataSource;
    private ExtractorSampleSource sampleSource;
    private MediaCodecAudioTrackRenderer audioRenderer;
    private OnPreparedListener audioServiceReadyListener;

    MediaCodecAudioTrackRenderer getNewAudioRenderer(String source) {
        dataSource = new DefaultHttpDataSource("MidwestRadio/", null, null, 15000, 15000);
        sampleSource = new ExtractorSampleSource(Uri.parse(source), dataSource, allocator, BUFFER_SEGMENT_SIZE * BUFFER_SEGMENT_COUNT );
        audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource);
        currentState = STATE_PLAYING;
        return audioRenderer;
    }

    public void prepareAndPlay(String source) {
        if(!currentStream.equals(source)){
            currentState = STATE_PREPARING;
            player.setPlayWhenReady(true);
            player.prepare(getNewAudioRenderer(source));
            currentStream = source;
        }
    }

    public void stop(){
        player.stop();
        currentState = STATE_STOPPED;
        currentStream = "";
    }

    public void release(){
        player.release();
    }

    public void prepare(){
        player.prepare();
    }

    interface OnPreparedListener {
        void onPrepared(AudioPlayer audioPlayer);
    }

    private AudioPlayer audioPlayerThis = this;
    boolean preparedListenerCalled = false;
    private class Listener implements ExoPlayer.Listener {
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playbackState == ExoPlayer.STATE_READY && !preparedListenerCalled) {
                audioPlayerThis.onPrepared();
                preparedListenerCalled = true;
            } else if (playbackState == ExoPlayer.STATE_ENDED) {
                audioPlayerThis.onFinished();
            }
        }
        @Override
        public void onPlayWhenReadyCommitted() {}
        @Override
        public void onPlayerError(ExoPlaybackException error) {}
    }

    void onPrepared() {
        currentState = STATE_PREPARED;
        if(audioServiceReadyListener != null) {
            audioServiceReadyListener.onPrepared(this);
        }
        if(player.getPlayWhenReady()) {
            currentState = STATE_PLAYING;
        }
    }

    boolean isPlaying(){
        return currentState == 3;
    }

    void onFinished() {}
}
