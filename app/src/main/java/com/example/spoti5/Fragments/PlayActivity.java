package com.example.spoti5.Fragments;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.example.spoti5.Activities.MainActivity;
import com.example.spoti5.R;
import com.example.spoti5.Models.SongModel;

import java.util.ArrayList;

public class PlayActivity extends AppCompatActivity {

    private ImageView imgAlbum;
    private TextView tvTitle, tvArtist, totalTime, currentTime;
    private ImageView btnPlay, btnNext, btnPrev, btnBack;
    private SeekBar seekBar;
    private MusicService musicService;
    private boolean isBound = false;

    private Thread updateSeekBar;


    private ArrayList<SongModel> songList;
    private int position;

    private Handler handler = new Handler();
    private Runnable updateRunnable;



    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_play);

        imgAlbum = findViewById(R.id.albumImage);
        tvTitle = findViewById(R.id.songTitle);
        tvArtist = findViewById(R.id.artistName);
        seekBar = findViewById(R.id.seekBar);
        btnPlay = findViewById(R.id.btnPlayPause);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrevious);
        btnBack = findViewById(R.id.btnBack);
        totalTime = findViewById(R.id.totalTime);
        currentTime = findViewById(R.id.currentTime);

        position = getIntent().getIntExtra("position", 0);
        songList = (ArrayList<SongModel>) getIntent().getSerializableExtra("songList");

        if (songList == null || songList.isEmpty()) {
            Log.e("PlayActivity", "Dữ liệu truyền vào bị null hoặc rỗng");
            finish(); // hoặc hiển thị dialog lỗi
            return;
        }
        if (position < 0 || position >= songList.size()) {
            Log.e("PlayActivity", "Vị trí không hợp lệ: " + position);
            finish();
            return;
        }
        playSong(position);

        Intent serviceIntent = new Intent(this, MusicService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);


        btnPlay.setOnClickListener(v -> {
            if (musicService != null) {
                if (musicService.isPlaying()) {
                    musicService.pause();
                    btnPlay.setImageResource(R.drawable.play_icon);
                } else {
                    musicService.play();
                    btnPlay.setImageResource(R.drawable.pause_icon);
                    startSeekBarUpdate();
                }
            }
        });

        btnNext.setOnClickListener(v -> {
            position = (position + 1) % songList.size();
            playSong(position);
        });
        btnPrev.setOnClickListener(v -> {
            position = (position - 1 + songList.size()) % songList.size();
            playSong(position);
        });
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && MusicService.mediaPlayer != null) {
                    MusicService.mediaPlayer.seekTo(progress);
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        updateRunnable = new Runnable() {
            @Override
            public void run() {
                if (MusicService.mediaPlayer != null && MusicService.mediaPlayer.isPlaying()) {
                    int currentPos = MusicService.mediaPlayer.getCurrentPosition();
                    int duration = MusicService.mediaPlayer.getDuration();

                    seekBar.setMax(duration);
                    seekBar.setProgress(currentPos);

                    currentTime.setText(formatTime(currentPos));
                    totalTime.setText(formatTime(duration));
                }
                handler.postDelayed(this, 500); // cập nhật mỗi 0.5 giây
            }
        };
        handler.post(updateRunnable);


    }

    private void playSong(int pos) {
        SongModel song = songList.get(pos);
        tvTitle.setText(song.getName());
        tvArtist.setText(song.getArtistName());
        Glide.with(this).load(song.getImage()).into(imgAlbum);

        if (musicService != null) {
            musicService.playSong(song.getAudioUrl());
            musicService.setOnPreparedListener(mp -> {
                seekBar.setMax(mp.getDuration());
                mp.start();
                startSeekBarUpdate();
                btnPlay.setImageResource(R.drawable.pause_icon);
                MainActivity.currentSong = song;
                Intent intent = new Intent("UPDATE_MINI_PLAYER");
                intent.putExtra("title", song.getName());
                intent.putExtra("artist", song.getArtistName());
                intent.putExtra("imageUrl", song.getImage());
                intent.putExtra("song", song); // rất quan trọng để MainActivity nhận đúng

                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

                Log.d("MiniPlayer", "Broadcast đã gửi: " + song.getName() + " - " + song.getArtistName() + " - " + song.getImage());

            });
        }


    }

    private void startSeekBarUpdate() {
        if (updateSeekBar != null && updateSeekBar.isAlive()) {
            updateSeekBar.interrupt();  // Dừng thread cũ nếu có
        }

        updateSeekBar = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(500);  // Cập nhật mỗi 0.5s
                    if (musicService != null && musicService.isPlaying()) {
                        int currentPosition = musicService.getCurrentPosition();
                        runOnUiThread(() -> {
                            seekBar.setProgress(currentPosition);
                            currentTime.setText(formatTime(currentPosition));
                        });
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        updateSeekBar.start();
    }


    private String formatTime(int millis) {
        int minutes = (millis / 1000) / 60;
        int seconds = (millis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }


    private void sendActionToService(String action) {
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(action);
        ContextCompat.startForegroundService(this, intent);
    }
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
            isBound = true;
            playSong(position); // bắt đầu phát luôn sau khi kết nối
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };
    @Override
    protected void onDestroy() {
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
        if (updateSeekBar != null) updateSeekBar.interrupt();
        super.onDestroy();

        if (updateSeekBar != null && updateSeekBar.isAlive()) {
            updateSeekBar.interrupt();
        }
        if (handler != null && updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
        }


    }


}
