package com.example.demoprojectmusic.View;



import static android.content.ContentValues.TAG;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import com.example.demoprojectmusic.Controler.PodcastAdapter;
import com.example.demoprojectmusic.Model.Podcast;
import com.example.demoprojectmusic.Model.UserDataManager;
import com.example.demoprojectmusic.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class List_Like extends AppCompatActivity {
    List<String> listPodCast = new ArrayList<>();;
    ListView listView;
    PodcastAdapter podcastAdapter;

    LinearLayout LLtitle,LLmusic,LL_btn;

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_like);
        setupView();
        addController();
        customActionbar();
        podcastAdapter = new PodcastAdapter(List_Like.this, listPodCast);
        listView.setAdapter(podcastAdapter);
        int userID = UserDataManager.getInstance().getUserID();
        Log.d(TAG,"IdUser: " + userID);
        loadPodcastsForUser(userID);

        Event();
    }
    private void addController(){
        LLtitle=findViewById(R.id.LL_title);
        LLmusic=findViewById(R.id.LL_music);
      //  LL_btn=findViewById(R.id.LL_btn);
        listView = findViewById(R.id.lsv_list_music);
    }
    private void Event(){
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showBottomSheetDialog(position);
                return true;
            }
        });
//        LL_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(List_Like.this, Detail_listPocast.class);
//                startActivity(intent);
//            }
//        });
    }
    private  void customActionbar(){
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Thư viện");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#282f32")));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_like, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.Add_list) {
            showCreateDialog();
            return true;
        } else if (itemId == R.id.Search_List) {
            Intent intent = new Intent(List_Like.this, Search_Library.class);
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void showCreateDialog() {
        // Tạo một AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Sử dụng LayoutInflater để tải layout custom_dialog.xml
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_album, null);
        // Ánh xạ các thành phần trong layout custom_dialog.xml
        EditText edtName = dialogView.findViewById(R.id.Ed_nameplaylist);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btnCreate = dialogView.findViewById(R.id.btnCreate);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btnClose = dialogView.findViewById(R.id.btnClose);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đóng AlertDialog khi nút "Đóng" được nhấn
                alertDialog.dismiss();
            }
        });
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playlistName = edtName.getText().toString();
                if (!TextUtils.isEmpty(playlistName)) {
                    int userID = UserDataManager.getInstance().getUserID();
                    Log.e("UserId", String.valueOf(userID));
                    addPodcastToFirebase(userID, playlistName);
                    alertDialog.dismiss();
                } else {
                    edtName.setError("Tên playlist không được trống");
                }
            }
        });
        alertDialog.show();
    }
    private void showBottomSheetDialog(final int position) {
        // Inflate the bottom sheet layout
        @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_dialog, null);
        // Create the bottom sheet dialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);
        String podcast = listPodCast.get(position);
        Button deleteButton = view.findViewById(R.id.action_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int userID = UserDataManager.getInstance().getUserID();
                Log.d(TAG, "showBottomSheetDialog: " + podcast + userID );
                //Toast.makeText(getApplication(), "showBottomSheetDialog : " + podcast + " / " + userID  , Toast.LENGTH_LONG).show();
                deleteDataFromFirestore(podcast,userID);
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.show();
    }
    private void deleteDataFromFirestore(String name, int userId) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        // Reference to your collection
        CollectionReference collectionReference = firebaseFirestore.collection("listPodcast");
        // Query to find the document to delete
        Query query = collectionReference
                .whereEqualTo("name", name)
                .whereEqualTo("idUser", String.valueOf(userId));

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Delete the document from Firestore
                        firebaseFirestore.collection("listPodcast").document(document.getId())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Notify the adapter that the data has changed
                                        podcastAdapter.notifyDataSetChanged();
                                        // Load podcasts for the user
                                        loadPodcastsForUser(userId);
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error deleting document", e);
                                    }
                                });
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void addPodcastToFirebase(int userID, String podcastName) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        // Kiểm tra xem podcast đã tồn tại hay chưa
        firebaseFirestore.collection("listPodcast")
                .whereEqualTo("idUser", String.valueOf(userID))
                .whereEqualTo("name", podcastName)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Nếu có ít nhất một tài liệu trùng khớp, hiển thị thông báo hoặc thực hiện hành động phù hợp
                        if (!queryDocumentSnapshots.isEmpty()) {
                            Log.d(TAG, "Podcast with idUser " + userID + " and name " + podcastName + " already exists");
                            Toast.makeText(getApplication(), "Playlist name already exists", Toast.LENGTH_LONG).show();
                            // Hiển thị thông báo hoặc thực hiện hành động phù hợp ở đây
                        } else {
                            // Nếu không có tài liệu trùng khớp, thêm mới podcast
                            Podcast podcast = new Podcast();
                            podcast.setIdUser(String.valueOf(userID));
                            podcast.setName(podcastName);
                            podcast.setSongIds(new ArrayList<>());
                            // Tạo một số nguyên ngẫu nhiên có 8 chữ số
                            Random random = new Random();
                            int min = 10000000; // Số nhỏ nhất có 8 chữ số
                            int max = 99999999; // Số lớn nhất có 8 chữ số
                            int randomPodcastID = random.nextInt(max - min + 1) + min;

                            // Thêm podcast vào Firestore
                            firebaseFirestore.collection("listPodcast").document(String.valueOf(randomPodcastID))
                                    .set(podcast)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            podcastAdapter.notifyDataSetChanged();
                                            // Load podcasts for the user
                                            loadPodcastsForUser(userID);
                                            Log.d(TAG, "Podcast added with ID: " + randomPodcastID);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Error adding podcast", e);
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error checking podcast existence", e);
                    }
                });

    }
    private void loadPodcastsForUser(int userID) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        listPodCast.clear(); // Clear the list before loading new data
        firebaseFirestore.collection("listPodcast")
                .whereEqualTo("idUser", String.valueOf(userID))
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Add the loaded podcasts to the list
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Podcast podcast = document.toObject(Podcast.class);
                            Log.d(TAG, "Podcast ID: " + document.getId() + ", Name: " + podcast.getName());
                            listPodCast.add(podcast.getName());
                        }
                        // Notify the adapter that the data has changed
                        podcastAdapter.notifyDataSetChanged();
                        // Update the view visibility
                        setupView();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error getting podcasts for user", e);
                    }
                });
    }

    public  void setupView(){
        if (listPodCast.isEmpty()) {
            // If the list is empty, show the LLtitle view
            findViewById(R.id.LL_title).setVisibility(View.VISIBLE);
            // Hide the LLmusic view
            findViewById(R.id.LL_music).setVisibility(View.GONE);
        }
        else {
            // If the list is not empty, hide the LLtitle view
            findViewById(R.id.LL_title).setVisibility(View.GONE);
            // Show the LLmusic view
            findViewById(R.id.LL_music).setVisibility(View.VISIBLE);
        }
    }
}