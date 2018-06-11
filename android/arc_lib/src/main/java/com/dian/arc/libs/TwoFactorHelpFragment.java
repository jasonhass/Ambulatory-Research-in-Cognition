package com.dian.arc.libs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dian.arc.libs.custom.FancyButton;
import com.dian.arc.libs.rest.RestClient;
import com.dian.arc.libs.rest.models.VerificationCodeRequest;
import com.dian.arc.libs.utilities.ArcManager;
import com.dian.arc.libs.utilities.Config;
import com.dian.arc.libs.utilities.FontFactory;
import com.dian.arc.libs.utilities.NavigationManager;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class TwoFactorHelpFragment extends BaseFragment {

    FancyButton fancyButton;


    public TwoFactorHelpFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_twofactor_help, container, false);

        TextView help = (TextView)view.findViewById(R.id.textviewHeader);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationManager.getInstance().getFragmentManager().popBackStack();
            }
        });

        fancyButton = (FancyButton)view.findViewById(R.id.fancybutton);
        fancyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Config.REST_BLACKHOLE) {
                    NavigationManager.getInstance().getFragmentManager().popBackStack();
                    NavigationManager.getInstance().open(new TwoFactorHelpConfirmFragment());
                    return;
                }
                VerificationCodeRequest request = new VerificationCodeRequest();
                request.setArcId(ArcManager.getInstance().getArcId());
                Call<ResponseBody> call = RestClient.getService().requestVerificationCode(request);
                call.enqueue(verificationCodeRequestCallback);

            }
        });

        TextView subheader = (TextView)view.findViewById(R.id.textviewSubheader);
        subheader.setTypeface(FontFactory.georgiaItalic);




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

    private Callback verificationCodeRequestCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            if(response != null){
                int code = response.raw().code();
                switch (code){
                    case 200:
                        Log.i("verificationCodeRequest","200: ok");
                        NavigationManager.getInstance().getFragmentManager().popBackStack();
                        NavigationManager.getInstance().open(new TwoFactorHelpConfirmFragment());
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
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            Log.i("verificationCodeRequest","request failed: "+t.getMessage());
        }
    };

}
