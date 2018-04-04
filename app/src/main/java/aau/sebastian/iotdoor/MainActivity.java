package aau.sebastian.iotdoor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    TextView tvLockStatus, tvWasKnocked;
    boolean lockStatus = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button unlockBtn = findViewById(R.id.unlockBtn);
        unlockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lockStatus){
                    ref.child("door").child("doorStatus").setValue(0);
                }else{
                    ref.child("door").child("doorStatus").setValue(1);
                }
            }
        });
        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");

        /*String instanceId = FirebaseInstanceId.getInstance().getToken();
        ref.child("userToken").setValue(instanceId);*/
        //  ref.child("userTokens").child(instanceId).setValue("Active");

        tvLockStatus = findViewById(R.id.doorStatus);
        tvWasKnocked = findViewById(R.id.tvWasKnocked);

        ref.child("door").child("doorKnock").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Long.class)== 1){
                    tvWasKnocked.setVisibility(View.VISIBLE);
                }
                else{
                    tvWasKnocked.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ref.child("door").child("doorStatus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    if(dataSnapshot.getValue(Long.class)== 0){
                        tvLockStatus.setText("Locked");
                        unlockBtn.setText("Unlock door");
                        lockStatus = false;
                    }
                    else{
                        tvLockStatus.setText("Unlocked");
                        lockStatus = true;
                        unlockBtn.setText("Lock door");

                    }
                } catch (DatabaseException e){
                    Log.d(TAG, "onDataChange: "+e);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
