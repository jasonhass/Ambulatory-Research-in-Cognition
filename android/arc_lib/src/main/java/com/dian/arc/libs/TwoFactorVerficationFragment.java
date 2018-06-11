package com.dian.arc.libs;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dian.arc.libs.custom.FancyButton;
import com.dian.arc.libs.custom.ToggledButton;
import com.dian.arc.libs.rest.RestClient;
import com.dian.arc.libs.rest.models.PricesTest;
import com.dian.arc.libs.rest.models.PricesTestSection;
import com.dian.arc.libs.rest.models.Registration;
import com.dian.arc.libs.rest.models.RegistrationTwoFactor;
import com.dian.arc.libs.utilities.ArcManager;
import com.dian.arc.libs.utilities.Config;
import com.dian.arc.libs.utilities.FontFactory;
import com.dian.arc.libs.utilities.JodaUtil;
import com.dian.arc.libs.utilities.NavigationManager;
import com.dian.arc.libs.utilities.PreferencesManager;
import com.dian.arc.libs.utilities.PriceManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import static com.dian.arc.libs.Application.isActivityVisible;

public class TwoFactorVerficationFragment extends BaseFragment {

    FancyButton fancyButton;
    EditText editText;
    boolean existingUser;
    int visitId = -1;
    int bottom;
    int top;
    int right;
    int left;

    public TwoFactorVerficationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_twofactor_verification, container, false);

        TextView help = (TextView)view.findViewById(R.id.textviewHeader);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationManager.getInstance().open(new TwoFactorHelpFragment());
            }
        });

        fancyButton = (FancyButton)view.findViewById(R.id.fancybutton);
        fancyButton.setEnabled(false);
        fancyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fancyButton.setEnabled(false);
                if(Config.REST_BLACKHOLE) {
                    if(Config.SKIP_BASELINE){
                        visitId++;
                    }
                    existingUser = visitId!=-1;
                    visitId++;
                    PreferencesManager.getInstance().putInt("visitId",visitId);
                    moveOn();
                    return;
                }
                RegistrationTwoFactor registration = new RegistrationTwoFactor();
                registration.setArcId(ArcManager.getInstance().getArcId());
                registration.setDeviceId(ArcManager.getInstance().getDeviceId());
                registration.setVerificationCode(editText.getText().toString());
                Call<ResponseBody> call = RestClient.getService().registerDevice(registration);
                call.enqueue(registrationRequestCallback);
            }
        });

        TextView subheader = (TextView)view.findViewById(R.id.textviewSubheader);
        subheader.setTypeface(FontFactory.georgiaItalic);

        editText = (EditText)view.findViewById(R.id.editText);
        editText.addTextChangedListener(textWatcher);

        bottom = editText.getPaddingBottom();
        top = editText.getPaddingTop();
        right = editText.getPaddingRight();
        left = editText.getPaddingLeft();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

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

                        if(Config.SKIP_BASELINE && visitId==-1){
                            visitId++;
                        }
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
            editText.setBackgroundResource(R.drawable.background_edittext_red);
            editText.setPadding(left, top, right, bottom);
    }

    private void moveOn(){
        if (existingUser) {
            ArcManager.getInstance().setPathSetupExisting(visitId);
        } else {
            ArcManager.getInstance().setPathSetupNew();
        }
        ArcManager.getInstance().nextFragment(true);
    }

    private void checkValidation(){
        boolean empty = editText.getText().toString().length() < 6;
        if(!empty){
            editText.setBackgroundResource(R.drawable.background_edittext);
        } else {
            editText.setBackgroundResource(R.drawable.background_edittext_red);
        }
        editText.setPadding(left, top, right, bottom);
        fancyButton.setEnabled(!empty);
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
