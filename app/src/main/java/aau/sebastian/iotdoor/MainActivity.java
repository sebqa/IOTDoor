package aau.sebastian.iotdoor;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
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
    MaterialDialog dialog;
    private boolean receivedResponse = false;
    final Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button unlockBtn = findViewById(R.id.unlockBtn);
        unlockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(lockStatus){
                    //ref.child("door").child("doorStatus").setValue(0);
                    ref.child("motorAngle").setValue(0);
                    createDialog("Locking");
                }else{
                    //ref.child("door").child("doorStatus").setValue(1);
                    ref.child("motorAngle").setValue(180);
                    createDialog("Unlocking");
                }
                receivedResponse = false;
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
                    receivedResponse = true;

                    if(dialog != null) {
                        dialog.dismiss();
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

    public void createDialog(final String status){
        dialog = new MaterialDialog.Builder(this)
                .title(status)
                .content("Please wait")
                .progress(true, 0)
                .show();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                if(!receivedResponse){
                    if(status.equals("Locking")) {
                        ref.child("motorAngle").setValue(180);
                    } else{
                        ref.child("motorAngle").setValue(0);
                    }
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "Lock could not be reached", Toast.LENGTH_SHORT).show();
                }

            }
        }, 5000);
    }
}
