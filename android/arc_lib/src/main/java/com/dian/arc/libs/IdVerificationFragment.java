package com.dian.arc.libs;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dian.arc.libs.custom.FancyButton;
import com.dian.arc.libs.utilities.ArcManager;
import com.dian.arc.libs.utilities.SignatureManager;
import com.github.gcacace.signaturepad.views.SignaturePad;

public class IdVerificationFragment extends BaseFragment {

    public boolean firstSignature = true;
    FancyButton buttonNext;
    FancyButton buttonUndo;
    SignaturePad signaturePad;


    public static IdVerificationFragment create(boolean first){
        IdVerificationFragment fragment = new IdVerificationFragment();
        fragment.firstSignature = first;
        return fragment;
    }

    public IdVerificationFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_id_verfication, container, false);

        setupDebug(view);

        buttonNext = (FancyButton)view.findViewById(R.id.fancybuttonIdVerificationNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendingTask task = new SendingTask();
                task.execute();
            }
        });
        buttonNext.setEnabled(false);
        if (!firstSignature) {
            buttonNext.setText(getString(R.string.done));
            ((TextView)view.findViewById(R.id.textView9)).setText(R.string.idverification_body2);
        } else {
            ArcManager.getInstance().markTestStarted();
            ArcManager.getInstance().saveState();
        }

        buttonUndo = (FancyButton)view.findViewById(R.id.fancybuttonIdVerificationUndo);
        buttonUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signaturePad.clear();
            }
        });
        buttonUndo.setEnabled(false);

        signaturePad = (SignaturePad)view.findViewById(R.id.signaturePad);
        signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
            }

            @Override
            public void onSigned() {
                buttonNext.setEnabled(true);
                buttonUndo.setEnabled(true);
            }

            @Override
            public void onClear() {
                buttonNext.setEnabled(false);
                buttonUndo.setEnabled(false);
            }
        });

        return view;
    }

    private class SendingTask extends AsyncTask<Void, Void, Void> {
        LoadingDialog loadingDialog;
        Bitmap signature;
        @Override
        protected void onPreExecute() {
            signature = signaturePad.getSignatureBitmap();
            loadingDialog = new LoadingDialog();
            loadingDialog.show(getFragmentManager(),"LoadingDialog");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            if(ArcManager.getInstance().getLifecycleStatus()!= ArcManager.LIFECYCLE_INIT) {
                if (firstSignature) {
                    SignatureManager.getInstance().put(signature, true);
                } else {
                    SignatureManager.getInstance().put(signature, false);
                    ArcManager.getInstance().markTestFinished();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            loadingDialog.dismiss();
            ArcManager.getInstance().nextFragment();
            super.onPostExecute(result);
        }

    }


}
