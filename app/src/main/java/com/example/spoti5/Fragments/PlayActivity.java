package com.example.spoti5.Fragments;

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
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
    TextView tvTitle, tvArtist, totalTime, currentTime;
    SeekBar seekBar;
    ImageButton btnPlay, btnNext, btnPrev;

    static MediaPlayer mediaPlayer;
    static ArrayList<SongModel> songList;
    static int position = 0;

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
        btnBack = findViewById(R.id.btnBack);
        totalTime = findViewById(R.id.totalTime);
        currentTime = findViewById(R.id.currentTime);

        // Lấy dữ liệu từ Intent
        position = getIntent().getIntExtra("position", 0);
        songList = (ArrayList<SongModel>) getIntent().getSerializableExtra("songList");

        System.out.println("vị trí: " + position);
        playSong(position);

//        btnPlay.setOnClickListener(v -> {
//            if (mediaPlayer.isPlaying()) {
//                mediaPlayer.pause();
//                btnPlay.setImageResource(R.drawable.play_icon);
//            } else {
//                mediaPlayer.start();
//                btnPlay.setImageResource(R.drawable.pause_icon);
//            }
//        });
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        btnPlay.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                try {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        btnPlay.setImageResource(R.drawable.play_icon);
                    } else {
                        mediaPlayer.start();
                        btnPlay.setImageResource(R.drawable.pause_icon);
                        startSeekBarUpdate();
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    resetMediaPlayer();
                    playSong(position); // Tự động phát lại nếu có lỗi
                }
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
                .load(song.getImage())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(imgAlbum);

        // Xử lý MediaPlayer an toàn
        try {
            // Giải phóng MediaPlayer cũ nếu tồn tại
            if (mediaPlayer != null) {
                try {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                    mediaPlayer.release();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
                mediaPlayer = null;
            }

            // Kiểm tra URL hợp lệ
            String url = song.getAudioUrl();
            if (url == null || url.isEmpty()) {
                Toast.makeText(this, "Link bài hát không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(url);

            // Sử dụng prepareAsync để tránh block UI thread
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(mp -> {
                // Cập nhật UI khi đã prepared
                int duration = mp.getDuration();
                updateSongDuration(duration);
                seekBar.setMax(duration);
                mp.start();
                btnPlay.setImageResource(R.drawable.pause_icon);
                startSeekBarUpdate();
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                runOnUiThread(() ->
                        Toast.makeText(this, "Lỗi phát nhạc", Toast.LENGTH_SHORT).show());
                resetMediaPlayer();
                return true;
            });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Không thể phát bài hát", Toast.LENGTH_SHORT).show();
            resetMediaPlayer();
        }
    }

    private void resetMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } finally {
                mediaPlayer = null;
            }
        }

        if (updateSeekBar != null) {
            updateSeekBar.interrupt();
            updateSeekBar = null;
        }
    }

    private void startSeekBarUpdate() {
        // Dừng thread cũ nếu tồn tại
        if (updateSeekBar != null) {
            updateSeekBar.interrupt();
        }

        updateSeekBar = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(200);
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        final int currentPosition = mediaPlayer.getCurrentPosition();
                        runOnUiThread(() -> {
                            seekBar.setProgress(currentPosition);
                            currentTime.setText(formatTime(currentPosition));
                        });

                    }
                } catch (InterruptedException | IllegalStateException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        updateSeekBar.start();
    }

    private void updateSongDuration(int durationMs) {
        runOnUiThread(() -> {
            if (durationMs <= 0) {
                totalTime.setText("00:00");
            } else {
                int minutes = (durationMs / 1000) / 60;
                int seconds = (durationMs / 1000) % 60;
                totalTime.setText(String.format("%02d:%02d", minutes, seconds));
            }
        });
    }

    private String formatTime(int millis) {
        int minutes = (millis / 1000) / 60;
        int seconds = (millis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        resetMediaPlayer();
        super.onDestroy();
    }
//    private void playSong(int pos) {
//        SongModel song = songList.get(pos);
//
//        tvTitle.setText(song.getName());
//        tvArtist.setText(song.getArtistName());
//
//        Glide.with(this)
//                .load(song.getImage())
//                .placeholder(R.drawable.placeholder_image)
//                .error(R.drawable.error_image)
//                .listener(new RequestListener<Drawable>() {
//                    @Override
//                    public boolean onLoadFailed(GlideException e, Object model,
//                                                Target<Drawable> target, boolean isFirstResource) {
//                        e.printStackTrace();
//                        return false; // allow error drawable to be set
//                    }
//                    @Override
//                    public boolean onResourceReady(Drawable resource, Object model,
//                                                   Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                        return false;
//                    }
//                })
//                .into(imgAlbum);
//
//
//        //Glide.with(this).load(song.getAlbumImage()).into(imgAlbum);
//
//        if (mediaPlayer != null) {
//            mediaPlayer.stop();
//            mediaPlayer.release();
//        }
//
//        mediaPlayer = new MediaPlayer();
//
//        // Lấy URL và kiểm tra điều kiện
//        String url = song.getAudioUrl();
//        android.util.Log.d("PlayActivity", "Audio URL: " + url);
//        if (url == null || url.isEmpty()) {
//            android.widget.Toast.makeText(this, "Link bài hát không hợp lệ", android.widget.Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        try {
//            mediaPlayer.setDataSource(url);
//            mediaPlayer.prepare(); // Có thể gây crash nếu URL không hợp lệ
//
//            // Lấy thời gian bài hát từ mediaPlayer (ms)
//            int duration = mediaPlayer.getDuration();
//
//            if (duration <= 0) {
//                totalTime.setText("00:00");
//            } else {
//                int minutes = (duration / 1000) / 60;
//                int seconds = (duration / 1000) % 60;
//                String timeFormatted = String.format("%02d:%02d", minutes, seconds);
//                totalTime.setText(timeFormatted);
//            }
//            mediaPlayer.start();
//            btnPlay.setImageResource(R.drawable.pause_icon);
//        } catch (IOException e) {
//            e.printStackTrace();
//            android.widget.Toast.makeText(this, "Không thể phát bài hát", android.widget.Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        seekBar.setMax(mediaPlayer.getDuration());
//
//        updateSeekBar = new Thread(() -> {
//            while (mediaPlayer != null && mediaPlayer.isPlaying()) {
//                seekBar.setProgress(mediaPlayer.getCurrentPosition());
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        updateSeekBar.start();
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        if (mediaPlayer != null) mediaPlayer.release();
//        super.onDestroy();
//    }

}
