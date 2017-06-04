package com.gymsic.kara.apps.pitchfile;

import android.Manifest;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventListener;

import static android.os.Environment.getExternalStorageDirectory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    Button start;
    Button stop;
    Button capture;
    MediaPlayer md;
    String[] values;
    TextView timePlay;
    private final Runnable updateUI= new Runnable()
    {
        public void run()
        {
            try
            {
                //update ur ui here
                timePlay.setText((md.getCurrentPosition()/md.getDuration())*100);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };

    private Handler mHandler = new Handler();




    final static int  MY_PERMISSIONS_REQUEST_READ_STORAGE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        md = new MediaPlayer();
        checkPermissionReadStorage(getApplicationContext(),MainActivity.this);
        showListView();




        //Log.d("lsit item : ",values.toString());
        start = (Button) findViewById(R.id.start);
        start.setOnClickListener(this);

        stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(this);


        timePlay = (TextView)findViewById(R.id.timePlay);


    }

    @Override
    public void onResume(){
        super.onResume();

            showListView();


    }

    public void checkPermissionReadStorage(Context context, Activity activity){
        if (ContextCompat.checkSelfPermission(context,      Manifest.permission.READ_EXTERNAL_STORAGE) !=     PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_STORAGE: {
                //premission to read storage
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {


                }
                return;
            }

        }
    }

    void runUpdate(){
        MainActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if(md != null && md.isPlaying()){
                    timePlay.setText(""+(md.getCurrentPosition()/100));
                    //Log.d("current postion : ", md.getCurrentPosition()+"");
                }
                mHandler.postDelayed(this, 100);
            }
        });
    }

    private void showListView(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) ==     PackageManager.PERMISSION_GRANTED)
        {

            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            values = dir.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".mp3");
                }
            });
            ListView listView = (ListView) findViewById(R.id.list_view);


            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    R.layout.listview, R.id.song, values);
//
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    try {
                        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + values[position];
                        md.reset();
                        md.setDataSource(path);
                        md.setVolume(1.5f, 1.5f);
                        md.prepare();
                        md.start();
                        runUpdate();


                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
//
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id){
            case R.id.start :
                try {
                    if(!md.isPlaying()) {
                        md.prepare();
                        md.start();
                        runUpdate();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            break;

            case R.id.stop :
                md.stop();
            break;
            case R.id.capture :


            break;
        }
    }
}
