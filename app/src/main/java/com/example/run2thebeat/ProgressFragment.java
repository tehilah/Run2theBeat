package com.example.run2thebeat;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class ProgressFragment extends Fragment {
    public static final String CONFIRM_DELETE = "Are you sure you want to delete?";
    private ProgressAdapter mAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference routeRef;
    private ExecutorService executor;
    private float sumDistance;
    private TextView tvTotalKm;
    private AppBarLayout appBar;
    private Toolbar toolbar;
    private SharedPreferences myPrefs;
    private CollapsingToolbarLayout c;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_progress, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initVariables(view);
        getSavedPreferences(); // upload saved value from shared pref.
        initializeCollectionRefs();
        updateSumKilometersFromFirestore();
        buildRecyclerView(view);

        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isVisible = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    c.setTitleEnabled(true);
                    toolbar.setTitle("Running History");
                    isVisible = true;
                } else if (isVisible) {
                    c.setTitleEnabled(false);
                    toolbar.setTitle("");
                    isVisible = false;
                }
            }
        });
    }

    private void initVariables(View view){
        executor = Executors.newCachedThreadPool();
        tvTotalKm = view.findViewById(R.id.total_km);
        appBar = view.findViewById(R.id.appBar);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        c = view.findViewById(R.id.collapsing_toolbar);
        c.setTitleEnabled(false);
    }

    private void getSavedPreferences(){
        myPrefs = getActivity().getSharedPreferences("MY_PREF", Context.MODE_PRIVATE);
        String dist = myPrefs.getString("TOTAL_DISTANCE","0.0");
        tvTotalKm.setText(String.valueOf(dist));

    }

    private void initializeCollectionRefs() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            routeRef = db.collection(currentUser.getUid())
                    .document("Document Routes").collection("Routes");

        }
    }

    private void buildRecyclerView(View view) {
        Query query = routeRef.orderBy("date", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<SavedRunItem> options = new FirestoreRecyclerOptions.Builder<SavedRunItem>()
                .setQuery(query, SavedRunItem.class)
                .build();
        mAdapter = new ProgressAdapter(options, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        RecyclerView mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                getDialog(viewHolder);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(getContext(), c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorRed))
                        .addSwipeRightActionIcon(R.drawable.delete_white)
                        .addSwipeRightLabel(getString(R.string.action_delete))
                        .setSwipeRightLabelColor(Color.WHITE)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            }
        }).attachToRecyclerView(mRecyclerView);
        mAdapter.setOnItemClickListener(new ProgressAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot ds, int position) {
                Route route = ds.toObject(Route.class);
                Intent i = new Intent(getContext(), RunDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("POINTS", route.getPoints());
                bundle.putString("DURATION", route.getDuration());
                bundle.putString("DISTANCE", route.getDistance());
                bundle.putInt("AVG_BPM", route.getAvgBPM());
                bundle.putString("AVG_PACE", route.getAvgPace());
                bundle.putString("DOC_REF", ds.getReference().getPath());
                DateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                df.setTimeZone(TimeZone.getTimeZone("GMT+3"));
                bundle.putString("DATE", df.format(route.getDate()));
                i.putExtras(bundle);
                startActivity(i);
            }
        });


//        mRecyclerView.post(new Runnable() {
//            @Override
//            public void run() {
//                if (!mRecyclerView.hasNestedScrollingParent(ViewCompat.TYPE_NON_TOUCH)) {
//                    mRecyclerView.startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_NON_TOUCH);
//                }
//                mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
//            }
//        });
    }


    public void getDialog(final RecyclerView.ViewHolder viewHolder) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom);
        alertDialogBuilder
                .setMessage(CONFIRM_DELETE)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, delete message
                        mAdapter.deleteItem(viewHolder.getAdapterPosition());
                        updateSumKilometersFromFirestore();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, just close the dialog box and do nothing
                dialog.cancel();
                mAdapter.notifyDataSetChanged();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    public void updateSumKilometersFromFirestore() {
        sumDistance = 0;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                routeRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Route route = documentSnapshot.toObject(Route.class);
                            sumDistance += Double.parseDouble(route.getDistance());
                        }
                        String total = String.format(Locale.getDefault(), "%.2f", sumDistance);
                        tvTotalKm.setText(total);
                        SharedPreferences.Editor editor = myPrefs.edit();
                        editor.putString("TOTAL_DISTANCE", total);
                        editor.apply();
                    }
                });
            }
        });
    }

}
