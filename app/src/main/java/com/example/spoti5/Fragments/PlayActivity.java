package com.example.spoti5.Fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.spoti5.Models.SongModel;
import com.example.spoti5.R;

import java.io.IOException;
import java.util.ArrayList;

public class PlayActivity extends AppCompatActivity {

    ImageView imgAlbum, btnBack;
    TextView tvTitle, tvArtist, totalTime;
    SeekBar seekBar;
    ImageButton btnPlay, btnNext, btnPrev;

    static MediaPlayer mediaPlayer;
    static ArrayList<SongModel> songList;
    static int position = 0;
    private float x1, x2;
    private static final int MIN_DISTANCE = 150;


    Thread updateSeekBar;

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
        totalTime = findViewById(R.id.totalTime);
        btnBack = findViewById(R.id.btnBack);

        // Lấy dữ liệu từ Intent
        position = getIntent().getIntExtra("position", 0);
        songList = (ArrayList<SongModel>) getIntent().getSerializableExtra("songList");

        playSong(position);

        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnPlay.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                btnPlay.setImageResource(R.drawable.play_icon);
            } else {
                mediaPlayer.start();
                btnPlay.setImageResource(R.drawable.pause_icon);
            }
        });

        btnNext.setOnClickListener(v -> {
            position = (position + 1) % songList.size();
            playSong(position);
        });

        btnPrev.setOnClickListener(v -> {
            position = (position - 1 < 0) ? songList.size() - 1 : position - 1;
            playSong(position);
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) mediaPlayer.seekTo(progress);
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void playSong(int pos) {
        SongModel song = songList.get(pos);

        tvTitle.setText(song.getName());
        tvArtist.setText(song.getArtistName());

        Glide.with(this)
                .load(song.getAlbumImage())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        e.printStackTrace();
                        return false; // allow error drawable to be set
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(imgAlbum);


        //Glide.with(this).load(song.getAlbumImage()).into(imgAlbum);

        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mediaPlayer = null;
        }


        mediaPlayer = new MediaPlayer();

        // Lấy URL và kiểm tra điều kiện
        String url = song.getAudioUrl();
        android.util.Log.d("PlayActivity", "Audio URL: " + url);

        if (url == null || url.isEmpty()) {
            Toast.makeText(this, "Link bài hát không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare(); // Có thể gây crash nếu URL không hợp lệ

            // Lấy thời gian bài hát từ mediaPlayer (ms)
            int duration = mediaPlayer.getDuration();

            if (duration <= 0) {
                totalTime.setText("00:00");
            } else {
                int minutes = (duration / 1000) / 60;
                int seconds = (duration / 1000) % 60;
                String timeFormatted = String.format("%02d:%02d", minutes, seconds);
                totalTime.setText(timeFormatted);
            }
            mediaPlayer.start();
            btnPlay.setImageResource(R.drawable.pause_icon);
        } catch (IOException e) {
            e.printStackTrace();
            android.widget.Toast.makeText(this, "Không thể phát bài hát", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        seekBar.setMax(mediaPlayer.getDuration());

        updateSeekBar = new Thread(() -> {
            try {
                while (mediaPlayer != null) {
                    try {
                        if (mediaPlayer.isPlaying()) {
                            seekBar.setProgress(mediaPlayer.getCurrentPosition());
                        }
                    } catch (IllegalStateException e) {
                        e.printStackTrace(); // MediaPlayer không ở trạng thái hợp lệ
                        break; // Thoát khỏi vòng lặp để tránh crash
                    }
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        updateSeekBar.start();
    }


    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) mediaPlayer.release();
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    if (deltaX < 0) {
                        // Vuốt trái → mở LyricsActivity
                        Intent intent = new Intent(PlayActivity.this, LyricsActivity.class);
                        intent.putExtra("song", songList.get(position)); // hoặc truyền lyrics
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }


}