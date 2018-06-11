package com.dian.arc.libs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.dian.arc.libs.custom.FancyButton;
import com.dian.arc.libs.rest.RestClient;
import com.dian.arc.libs.rest.models.Registration;
import com.dian.arc.libs.rest.models.VerificationCodeRequest;
import com.dian.arc.libs.utilities.ArcManager;
import com.dian.arc.libs.utilities.Config;
import com.dian.arc.libs.utilities.FontFactory;
import com.dian.arc.libs.utilities.NavigationManager;
import com.dian.arc.libs.utilities.PreferencesManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class SetupParticipantFragment extends BaseFragment {

    EditText arcId;
    EditText arcIdConfirm;
    EditText raterId;
    boolean existingUser;
    FancyButton button;
    int visitId = -1;

    boolean confirmStarted = false;
    boolean invalidRaterId = false;
    int bottom;
    int top;
    int right;
    int left;

    public SetupParticipantFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setup_participant, container, false);

        setupDebug(view);

        ((TextView)view.findViewById(R.id.textView)).setTypeface(FontFactory.tahomaBold);
        ((TextView)view.findViewById(R.id.textView2)).setTypeface(FontFactory.tahomaBold);
        ((TextView)view.findViewById(R.id.textView4)).setTypeface(FontFactory.tahomaBold);
        ((TextView)view.findViewById(R.id.textView5)).setTypeface(FontFactory.tahomaBold);

        arcId = (EditText)view.findViewById(R.id.editSetupArcId);
        arcIdConfirm = (EditText)view.findViewById(R.id.editSetupParticipantConfirm);
        raterId = (EditText)view.findViewById(R.id.editSetupParticipantRaterId);

        if(!Config.AUTHENTICATION_RATER_ID){
            raterId.setVisibility(View.GONE);
            view.findViewById(R.id.textView5).setVisibility(View.GONE);
        }

        if(Config.AUTHENTICATION_TWO_FACTOR){
            TextView copy = (TextView) view.findViewById(R.id.textViewCopy);
            copy.setText("Once you tap \"Next\", you should receive a 6-digit verification code via text to the phone number you provided when initially signing up.");
            copy.setVisibility(View.VISIBLE);
        }

        button = (FancyButton)view.findViewById(R.id.fancybuttonSetupParticipantNext);
        button.setEnabled(false);
        arcIdConfirm.addTextChangedListener(textWatcher);
        arcId.addTextChangedListener(textWatcher);
        raterId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(invalidRaterId) {
                    invalidRaterId = false;
                    raterId.setBackgroundResource(R.drawable.background_edittext);
                    raterId.setPadding(left, top, right, bottom);
                }
                checkValidation();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setEnabled(false);
                arcId.setEnabled(false);
                arcIdConfirm.setEnabled(false);
                raterId.setEnabled(false);

                if(Config.AUTHENTICATION_TWO_FACTOR){
                    if(Config.REST_BLACKHOLE) {
                        ArcManager.getInstance().setArcId(arcId.getText().toString());
                        NavigationManager.getInstance().getFragmentManager().popBackStack();
                        NavigationManager.getInstance().open(new TwoFactorVerficationFragment());
                        return;
                    }
                    VerificationCodeRequest request = new VerificationCodeRequest();
                    request.setArcId(arcId.getText().toString());
                    Call<ResponseBody> call = RestClient.getService().requestVerificationCode(request);
                    call.enqueue(verificationCodeRequestCallback);
                } else if(Config.AUTHENTICATION_RATER_ID){
                    if(Config.REST_BLACKHOLE) {
                        existingUser = visitId!=-1;
                        visitId++;
                        PreferencesManager.getInstance().putInt("visitId",visitId);
                        moveOn();
                        return;
                    }
                    Registration registration = new Registration();
                    registration.setArcId(arcId.getText().toString());
                    registration.setDeviceId(ArcManager.getInstance().getDeviceId());
                    registration.setRegistrarCode(Integer.valueOf(raterId.getText().toString()));
                    Call<ResponseBody> call = RestClient.getService().registerDevice(registration);
                    call.enqueue(registrationRequestCallback);
                }
            }
        });

        if(Config.REST_BLACKHOLE && Config.AUTHENTICATION_RATER_ID){
            visitId = PreferencesManager.getInstance().getInt("visitId",-1);
        }

        bottom = arcId.getPaddingBottom();
        top = arcId.getPaddingTop();
        right = arcId.getPaddingRight();
        left = arcId.getPaddingLeft();

        return view;
    }

    private void moveOn(){
        ArcManager.getInstance().setArcId(arcId.getText().toString());
        ArcManager.getInstance().setRaterId(raterId.getText().toString());
        if (existingUser) {
            ArcManager.getInstance().setPathSetupExisting(visitId);
        } else {
            ArcManager.getInstance().setPathSetupNew();
        }
        ArcManager.getInstance().nextFragment(true);
    }

    private Callback verificationCodeRequestCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            if(response != null){
                int code = response.raw().code();
                switch (code){
                    case 200:
                        Log.i("verificationCodeRequest","200: ok");
                        ArcManager.getInstance().setArcId(arcId.getText().toString());
                        NavigationManager.getInstance().getFragmentManager().popBackStack();
                        NavigationManager.getInstance().open(new TwoFactorVerficationFragment());
                        return;
                    case 400:
                        Log.i("verificationCodeRequest","400: bad request, malformed json body content");
                        break;
                    case 404:
                        Log.i("verificationCodeRequest","404: subject not found");
                        break;
                    case 429:
                        Log.i("verificationCodeRequest","429: too many requests");
                        break;
                    case 500:
                        Log.i("verificationCodeRequest","500: system failure on server side");
                        break;
                    default:
                        Log.i("verificationCodeRequest",String.valueOf(code));
                        break;
                }
            } else {
                Log.i("verificationCodeRequest","null: invalid response");
            }
            
            // if not going somewhere else set everything red and let the user can try again
            setFormInvalid();
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            Log.i("verificationCodeRequest","request failed: "+t.getMessage());
            setFormInvalid();
        }
    };

    private Callback registrationRequestCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            if(response != null){
                int code = response.raw().code();
                switch (code){
                    case 200:
                        Log.i("registrationRequest","200: ok");
                        JsonParser parser = new JsonParser();
                        String responseString = null;
                        try {
                            responseString = response.body().string();
                        } catch (IOException e) {
                            Log.i("registrationRequest","200: invalid body");
                            setFormInvalid();
                            e.printStackTrace();
                            return;
                        }
                        JsonObject jsonObject = parser.parse(responseString).getAsJsonObject();
                        visitId = jsonObject.get("visitId").getAsInt();
                        Log.i("visitId",String.valueOf(visitId));
                        existingUser = visitId!=-1;
                        visitId++;
                        moveOn();
                        return;
                    case 400:
                        Log.i("registrationRequest","400: bad request, malformed json body content");
                        break;
                    case 404:
                        Log.i("registrationRequest","404: subject not found");
                        break;
                    case 429:
                        Log.i("registrationRequest","429: too many requests");
                        break;
                    case 500:
                        Log.i("registrationRequest","500: system failure on server side");
                        break;
                    default:
                        Log.i("registrationRequest",String.valueOf(code));
                        break;
                }
            } else {
                Log.i("registrationRequest","null: invalid response");
            }
            
            // if not going somewhere else set everything red and let the user can try again
            setFormInvalid();
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            Log.i("registrationRequest","request failed");
            setFormInvalid();
        }
    };

    private void setFormInvalid(){
        invalidRaterId = true;
        arcId.setEnabled(true);
        arcIdConfirm.setEnabled(true);
        raterId.setEnabled(true);

        raterId.setBackgroundResource(R.drawable.background_edittext_red);
        arcId.setBackgroundResource(R.drawable.background_edittext_red);
        arcIdConfirm.setBackgroundResource(R.drawable.background_edittext_red);

        raterId.setPadding(left, top, right, bottom);
        arcId.setPadding(left, top, right, bottom);
        arcIdConfirm.setPadding(left, top, right, bottom);
    }

    private void checkValidation(){
        boolean empty = arcId.getText().toString().isEmpty();
        boolean emptyConfirm = arcIdConfirm.getText().toString().isEmpty();
        if(!confirmStarted && !emptyConfirm){
            confirmStarted = true;
        }
        boolean match = arcId.getText().toString().equals(arcIdConfirm.getText().toString());
        boolean rater = !raterId.getText().toString().isEmpty();

        if((match && !empty) || !confirmStarted){
            arcId.setBackgroundResource(R.drawable.background_edittext);
            arcIdConfirm.setBackgroundResource(R.drawable.background_edittext);
        } else {
            arcId.setBackgroundResource(R.drawable.background_edittext_red);
            arcIdConfirm.setBackgroundResource(R.drawable.background_edittext_red);
        }
        arcId.setPadding(left, top, right, bottom);
        arcIdConfirm.setPadding(left, top, right, bottom);

        boolean ready;
        if(Config.AUTHENTICATION_TWO_FACTOR) {
            ready = match && !empty;
        } else {
            ready = rater && match && !empty;
        }


        button.setEnabled(ready);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            checkValidation();
        }
    };

}
