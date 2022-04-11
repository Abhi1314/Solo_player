package com.example.solo_player;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
ListView listView;
String[] items;
private int Requestcode=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=findViewById(R.id.list_item_song);
        //runtimepermission();
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)+ ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO)!=
         PackageManager.PERMISSION_GRANTED){
           if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)|| ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.RECORD_AUDIO)){
               new AlertDialog.Builder(this)
                       .setTitle("Grant Permission!")
                       .setMessage("Permisison Needed for Song list")
                       .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO},Requestcode);

                            }
                       })
                   .setNegativeButton("Cancle", new DialogInterface.OnClickListener()
                   {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                   }
               }).create().show();
           }
           else {
               ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO},Requestcode);
           }
        }
        else {
            displaysong();
        }

    }

     @Override
     public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
         if(requestCode==Requestcode){
             if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                 displaysong();
                 Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
             }
             else {
                 Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();

             }
         }
     }


    public ArrayList<File> Findsong(File file)
    {
     ArrayList<File> arrayList=new ArrayList<>();
     File[] files=file.listFiles();
     for(File singlefile:files){
         if(singlefile.isDirectory() && !singlefile.isHidden())
         {
             arrayList.addAll(Findsong(singlefile));
         }
         else
         {
             if(singlefile.getName().endsWith(".mp3")||singlefile.getName().endsWith(".wav"))
             {
                 arrayList.add(singlefile);
             }

         }

     }
     return arrayList;
    }
    public void displaysong()
    {
        final ArrayList<File> Mysongs=Findsong(Environment.getExternalStorageDirectory());
        items=new String[Mysongs.size()];
        for(int i=0; i<Mysongs.size();i++){
            items[i]=Mysongs.get(i).getName().toString().replace(".mp3","").replace(".wav","");
        }
        //ArrayAdapter<String> MyAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,items);
        //listView.setAdapter(MyAdapter);
        CustomAdapter customAdapter=new CustomAdapter();
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                String SongName=(String)listView.getItemAtPosition(i);
                startActivity(new Intent(getApplicationContext(),Player_Activity.class).putExtra("songs",Mysongs)
                .putExtra("songname",SongName).putExtra("position",i));
            }
        });
    }

    class CustomAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            View Myview=getLayoutInflater().inflate(R.layout.list_item,null);
            TextView textsong=Myview.findViewById(R.id.txtsong_name);
            textsong.setSelected(true);
            textsong.setText(items[i]);

            return Myview;
        }
    }
}