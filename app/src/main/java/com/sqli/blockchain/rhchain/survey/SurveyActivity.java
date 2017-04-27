package com.sqli.blockchain.rhchain.survey;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.sqli.blockchain.rhchain.R;
import com.sqli.blockchain.rhchain.RHChainAbstractActivity;
import com.sqli.blockchain.rhchain.Utils;
import com.sqli.blockchain.rhchain.model.Question;
import com.sqli.blockchain.rhchain.results.ResultActivity;

import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by gunicolas on 19/04/17.
 */

public class SurveyActivity extends RHChainAbstractActivity implements View.OnClickListener {

    ListView surveyListView;
    Button surveyButton;
    ProgressDialog progressDialog;

    SurveyAdapter adapter;
    boolean canSubmit;

    Subscription overSubscription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.survey_activity);

        List<Question> questions = application.questions;

        int[] submission = application.blockchainAPI.getSubmission(questions.size());

        surveyListView = (ListView) findViewById(R.id.survey_listview);
        surveyButton = (Button) findViewById(R.id.survey_button);
        adapter = new SurveyAdapter(this,questions,submission);

        setCanSubmit(application.blockchainAPI.canSubmit(submission));

        surveyListView.setAdapter(adapter);

        surveyButton.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Vote en cours");
        progressDialog.setMessage("Votre vote est en attente de prise en compte");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setProgressNumberFormat(null);
        progressDialog.setProgressPercentFormat(null);

        overSubscription = application.blockchainAPI.registerOverEvent()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(results -> showResultsActivity());
    }

    void showResultsActivity() {
        Intent intent = new Intent(this, ResultActivity.class);
        startActivity(intent);
    }

    void updateView(){
        surveyButton.setVisibility( canSubmit ? View.VISIBLE : View.GONE);
        adapter.lockUpdate(!canSubmit);
    }

    public void setCanSubmit(boolean canSubmit) {
        this.canSubmit = canSubmit;
        updateView();
    }

    @Override
    public void onClick(View v) {
        if( v.equals(surveyButton) ){
            progressDialog.show();
            int[] submission = adapter.submission;
            application.blockchainAPI.submit(submission)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( tx -> {
                                progressDialog.dismiss();
                                setCanSubmit(false);
                            },
                            error -> {
                                progressDialog.dismiss();
                                Utils.showAlertDialog(this,error.getMessage());
                            });
        }
    }

    @Override
    protected void onStop() {
        overSubscription.unsubscribe();
        super.onStop();
    }
}
