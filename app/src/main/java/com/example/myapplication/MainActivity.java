package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.dto.MovieSchedule;
import com.google.android.gms.common.util.MapUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private RecyclerView listview;
    private MyAdapter adapter;
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    //상영시간표
    private ListView myListView;
    private final List<MovieSchedule> myList = new ArrayList<>();

    private final DatabaseReference fireBaseRootRef = FirebaseDatabase
            .getInstance()
            .getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.menu).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        findViewById(R.id.buy).setOnClickListener(view -> {
            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.lottecinema.co.kr/NLCHS/Ticketing")
            );
            startActivity(intent);
        });

        //상영시간표 누르면 영화관 선택 페이지 나오게
        findViewById(R.id.time).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MyTimeActivity.class));
            }
        });

        listview = findViewById(R.id.list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        listview.setLayoutManager(layoutManager);

//        StorageReference storageRef = storage.getReference();
//        StorageReference folderRef = storageRef.child("movie");
//        StorageReference pathRef = folderRef.child("001.jpg");

        // https://www.geeksforgeeks.org/how-to-view-all-the-uploaded-images-in-firebase-storage/
//        StorageReference listRef = FirebaseStorage.getInstance().getReference(); //.child("images");
//        listRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
//            @Override
//            public void onSuccess(ListResult listResult) {
//                for(StorageReference file:listResult.getItems()){
//                    file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            imagelist.add(uri.toString());
//                            Log.e("Itemvalue", uri.toString());
//                        }
//                    }).addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            recyclerView.setAdapter(adapter);
//                            progressBar.setVisibility(View.GONE);
//                            Log.e("onSuccess", uri.toString());
//                        }
//                    });
//                }
//            }
//        });

        ArrayList<String> strList = new ArrayList<>();
        strList.add("https://firebasestorage.googleapis.com/v0/b/moviecredit-b50c2.appspot.com/o/Main-MovieImage%2F%EB%B2%A0%EB%86%882.jpg?alt=media&token=457911c5-60fd-4d73-8981-a787f333fec7");
        strList.add("https://firebasestorage.googleapis.com/v0/b/moviecredit-b50c2.appspot.com/o/Main-MovieImage%2F%EA%B8%B0%EC%A0%81.jpg?alt=media&token=9f28ab3b-db8a-4cb3-98b0-b298746f9e06");
        strList.add("https://firebasestorage.googleapis.com/v0/b/moviecredit-b50c2.appspot.com/o/Main-MovieImage%2F%EB%B3%B4%EC%9D%B4%EC%8A%A4.jpg?alt=media&token=ca1f39df-4921-46ff-ba42-b23690976b79");
        strList.add("https://firebasestorage.googleapis.com/v0/b/moviecredit-b50c2.appspot.com/o/Main-MovieImage%2F007%20%EB%85%B8%20%ED%83%80%EC%9E%84%20%ED%88%AC%20%EB%8B%A4%EC%9D%B4.jpg?alt=media&token=0863ee30-8e50-4388-b64c-e16e398d1cf5");


        adapter = new MyAdapter(this, strList, onClickItem);
        listview.setAdapter(adapter);

        MyListDecoration decoration = new MyListDecoration();
        listview.addItemDecoration(decoration);


        //상영 시간표
        myListView = findViewById(R.id.my_list);
        MyListAdapter myListAdapter = new MyListAdapter(this, myList);
        myListView.setAdapter(myListAdapter);
        //**가산 상영시간표 추가
        DatabaseReference gasanRef = fireBaseRootRef
                .child("Gasan");

        //가산 상영시간표 데이터 추가.
        gasanRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Object> gasanInfo = (Map<String, Object>) snapshot.getValue();

                if(gasanInfo != null) {
                    for (String key : gasanInfo.keySet()) {
                        if(key.contains("join")) {
                            Integer deadLine = Integer.valueOf(key.substring(key.length() - 1));
                            String movieName = String.valueOf(gasanInfo.get(key));
                            myList.add(new MovieSchedule(deadLine, movieName));

                        }
                    }

                    myListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private OnClickListener onClickItem = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String str = (String) v.getTag();
            Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
        }
    };

}