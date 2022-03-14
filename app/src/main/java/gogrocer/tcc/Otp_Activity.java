package gogrocer.tcc;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import util.CountryPhoneCodesUtils;

public class Otp_Activity extends AppCompatActivity implements View.OnClickListener{
    Button sendBtn;
    final Context context = this;
    private EditText otpview;
    TextView resend;
    FirebaseAuth mAuth;
    String CountryID,mVerificationId,smsCode,countryCode;
    // RegisterBean registerBean;
    FrameLayout fm;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_);
        phone = getIntent().getStringExtra("phoneno");
     //   Log.e("@@",phone);
        init();
    initFirebaseOtp();
    }
    private void init() {



        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        countryCode= CountryPhoneCodesUtils.getPhone(manager.getSimCountryIso());
        CountryID= manager.getSimCountryIso().toUpperCase();
        resend = (TextView) findViewById(R.id.resend);
        otpview = findViewById(R.id.otpnum);
        sendBtn = findViewById(R.id.submit_bt);
        sendBtn.setOnClickListener(this);
        resend.setOnClickListener(this);

    }

    private void initFirebaseOtp(){
        if (mAuth==null)
            mAuth= FirebaseAuth.getInstance();
        // mAuth.setLanguageCode(CountryID);

        PhoneAuthProvider auth= PhoneAuthProvider.getInstance();
//        Log.e("@@",phone);
        auth.verifyPhoneNumber(
                "+91"+phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        smsCode = phoneAuthCredential.getSmsCode();
                        Log.e("fb complete","done"+smsCode);


                    }
                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Log.e("FireBase EX :",e.getMessage());
                        Toast.makeText(Otp_Activity.this, "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        mVerificationId = s;

                        Toast.makeText(Otp_Activity.this,"Your OTP Has Been Sent", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                        super.onCodeAutoRetrievalTimeOut(s);
                        Log.e("firebase timeout",s);
                    }
                });
    }


    @Override
    public void onClick(View view) {
        if (view == sendBtn){
            initFirebaseOtp();
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);

        }

        if (view == resend) {
            initFirebaseOtp();
        }
    }
}
