package com.example.spoti5.Activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.spoti5.Adapters.ViewpagerAdapter;
import com.example.spoti5.Fragments.MusicService;
import com.example.spoti5.Fragments.PlayActivity;
import com.example.spoti5.Models.SongModel;
import com.example.spoti5.R;
import com.example.spoti5.SQLite.PlaylistDAO;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private ViewPager2 mViewPager;
    private BottomNavigationView mBottomNavigationView;
    private FrameLayout fragmentHolder;
    private BroadcastReceiver miniPlayerReceiver;
    private ArrayList<SongModel> songList;
    private int position ;
    private MusicService musicService;
    LinearLayout miniPlayer;
    ImageView miniAlbumImage;
    TextView miniSongTitle, miniArtistName;
    ImageButton miniPlayPause, miniNext;
    public static SongModel currentSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int nightMode = prefs.getInt("night_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(nightMode);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mViewPager = findViewById(R.id.view_pager);
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        miniPlayer = findViewById(R.id.mini_player);
        miniAlbumImage = findViewById(R.id.mini_album_image);
        miniSongTitle = findViewById(R.id.mini_song_title);
        miniArtistName = findViewById(R.id.mini_artist_name);
        miniPlayPause = findViewById(R.id.mini_play_pause);
        miniNext = findViewById(R.id.mini_next);

        Intent serviceIntent = new Intent(this, MusicService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);


        ViewpagerAdapter viewpagerAdapter = new ViewpagerAdapter(this);
        mViewPager.setAdapter(viewpagerAdapter);

        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mBottomNavigationView.getMenu().getItem(position).setChecked(true);

                // Sửa lỗi setTitle
                if (getSupportActionBar() != null) {
                    switch (position) {
                        case 0:
                            getSupportActionBar().setTitle("Trang chủ");
                            break;
                        case 1:
                            getSupportActionBar().setTitle("Tìm kiếm");
                            break;
                        case 2:
                            getSupportActionBar().setTitle("Thư viện");
                            break;
                    }
                }
            }
        });

        mBottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    mViewPager.setCurrentItem(0);
                    break;
                case R.id.search:
                    mViewPager.setCurrentItem(1);
                    break;
                case R.id.library:
                    mViewPager.setCurrentItem(2);
                    break;
            }
            return true;
        });

        // Xử lý sự kiện chọn item trong BottomNavigationView
        mBottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        mViewPager.setCurrentItem(0);
                        //getSupportActionBar().setTitle("Trang chủ");
                        return true;
                    case R.id.search:
                        mViewPager.setCurrentItem(1);
                        //getSupportActionBar().setTitle("Tìm kiếm");
                        return true;
                    case R.id.library:
                        mViewPager.setCurrentItem(2);
                        //getSupportActionBar().setTitle("Thư viện");
                        return true;
                }
                return false;
            }
        });
        miniPlayer.setOnClickListener(v -> {
            if (currentSong != null){
                Intent intent = new Intent(MainActivity.this, PlayActivity.class);
                intent.putExtra("songList", songList); // truyền cả danh sách bài hát
                intent.putExtra("position", position); // vị trí bài hát đang phát
                startActivity(intent);
            }
        });

        miniPlayPause.setOnClickListener(v -> {
            if (MusicService.mediaPlayer != null) {
                if (MusicService.mediaPlayer.isPlaying()) {
                    MusicService.mediaPlayer.pause();
                    miniPlayPause.setImageResource(R.drawable.play_icon);
                } else {
                    MusicService.mediaPlayer.start();
                    miniPlayPause.setImageResource(R.drawable.pause_icon);
                }
            }
        });

        miniNext.setOnClickListener(v -> {
            if (songList != null && songList.size() > 0 && musicService != null) {
                position = (position + 1) % songList.size();
                SongModel nextSong = songList.get(position);

                musicService.playSong(nextSong.getAudioUrl());
                updateMiniPlayer(nextSong);
            } else {
                Log.e("MiniNext", "songList null hoặc rỗng!");
            }
        });


        miniPlayerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("UPDATE_MINI_PLAYER".equals(intent.getAction())) {
                    String title = intent.getStringExtra("title");
                    String artist = intent.getStringExtra("artist");
                    String imageUrl = intent.getStringExtra("imageUrl");
                    SongModel song = (SongModel) intent.getSerializableExtra("song");
                    if (song != null) {
                        updateMiniPlayer(song);
                    }
                    Log.d("MiniPlayer", "Đã nhận broadcast - title: " + title + ", artist: " + artist + ", imageUrl: " + imageUrl);

                    if (song != null) {
                        updateMiniPlayer(song);
                    }
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(miniPlayerReceiver, new IntentFilter("UPDATE_MINI_PLAYER"));


    }

    public void updateMiniPlayer(SongModel song) {
        currentSong = song;
        miniPlayer.setVisibility(View.VISIBLE);
        miniSongTitle.setText(song.getAlbumName());
        miniArtistName.setText(song.getArtistName());

        // Nếu dùng Glide
        Glide.with(this)
                .load(song.getImage())
                .into(miniAlbumImage);

        if (MusicService.mediaPlayer != null && MusicService.mediaPlayer.isPlaying()) {
            miniPlayPause.setImageResource(R.drawable.pause_icon);
        } else {
            miniPlayPause.setImageResource(R.drawable.play_icon);
        }

        if (this.songList == null) {
            this.songList = new ArrayList<>(); // tránh null
        }
        if (!this.songList.contains(song)) {
            this.songList.add(song);
        }
        // Cập nhật vị trí của bài hát hiện tại
        this.position = this.songList.indexOf(song);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(miniPlayerReceiver);

        if (musicService != null) {
            unbindService(serviceConnection);
            musicService = null;
        }
    }

    private void playSong(int pos) {
        SongModel song = songList.get(pos);
        miniSongTitle.setText(song.getName());
        miniArtistName.setText(song.getArtistName());

        if (musicService != null) {
            musicService.playSong(song.getAudioUrl());
            musicService.setOnPreparedListener(mp -> {
                mp.start();
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
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
        }
    };


}