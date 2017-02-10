package nantes_sqli.rhchain.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sqli.blockchain.android_geth.EthereumService;

import java.util.List;

import ethereumjava.EthereumJava;
import ethereumjava.exception.EthereumJavaException;
import ethereumjava.module.objects.Peer;
import ethereumjava.net.provider.AndroidIpcProvider;
import nantes_sqli.rhchain.R;
import nantes_sqli.rhchain.RhchainApplication;
import nantes_sqli.rhchain.blockchain.GethManager;
import nantes_sqli.rhchain.data.Survey;
import nantes_sqli.rhchain.data.User;
import nantes_sqli.rhchain.utils.Bouchonnage;

import static nantes_sqli.rhchain.blockchain.GethConfigConstants.ACCOUNT_PASSWORD;

/**
 * Display 2 possibilities: answer survey or create a user's account At btn_connect get the survey
 * to be displayed Use Survey class User class
 */


public class MainActivity extends AppCompatActivity implements View.OnClickListener , EthereumService.EthereumServiceInterface{
    Button btn_connect, btn_account;
    Survey survey;
    User user ;
    public EthereumJava ethereumjava;

    Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RhchainApplication application = (RhchainApplication) getApplication();
        application.registerGethReady(this);

        this.savedInstanceState =savedInstanceState;
        setContentView(R.layout.activity_main);

        // Chargement questionnaire Bouchonné
        survey = Bouchonnage.setDemoSurvey();
        user = Bouchonnage.setDemoUser();
        //user.setIdentity("toto");

        // Initialisation bouton de connection
        btn_connect = (Button) findViewById(R.id.btn_connect);
        modifierEtatBouton(R.id.btn_connect, false);
        btn_connect.setOnClickListener(this);


        // Initialisation du bouton  de creation de compte
        btn_account = (Button) findViewById(R.id.btn_account);
        btn_account.setOnClickListener(this);
        modifierEtatBouton(R.id.btn_account, false);



    }

    /**
     * Gestion des actions de clic sur un bouton
     * @param v la vue cliquée
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_connect: {
                TextView passwordView = (TextView) findViewById(R.id.editTextPassword);
                GethManager gethManager = ((RhchainApplication) getApplication()).gethManager;
                EthereumJava eth = gethManager.getEthereumJava();
                // Tentative de débloquage du compte
                String password = passwordView.getText().toString();
                boolean isSucess = gethManager.unlockDefaultAccountSession(password);
                if(! isSucess){
                    // deblocage KO -> affichage message d'erreur
                    Toast.makeText(getApplicationContext(), "Ce n'est pas le bon mot de passe", Toast.LENGTH_SHORT).show();
                }
                else{
                    // le déblocage du compte a réussit -> transition vers
                    Intent intent = new Intent(getApplicationContext(), SurveyActivity.class);
                    intent.putExtra("survey",survey);
                    startActivity(intent);
                }


                break;
            }
            case R.id.btn_account: {
                Log.v("étape","choix de la création");
                Intent intent = new Intent(this, UserRegistrationActivity.class);
                startActivity(intent);
                break;
            }

            default:
                break;

        }
    }

    private void modifierEtatBouton(int buttonId, boolean enabled){

        Button btn_submButton = (Button) findViewById(buttonId);

        int color = ContextCompat.getColor(this, R.color.lessdarkgrey);
        if (enabled) {
            color = ContextCompat.getColor(this, R.color.colorSqli);
        }

        // MOdification du fond du bouton http://www.41post.com/5094/programming/android-change-color-of-the-standard-button-inside-activity
        btn_submButton.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);

        btn_submButton.setEnabled(enabled);
        btn_submButton.invalidate();
    }

    @Override
    public void onEthereumServiceReady() {
        Log.d("Main activity","etherum service started");
        EthereumJava eth = ((RhchainApplication)getApplication()).gethManager.getEthereumJava();
        List<String> comptes = eth.personal.listAccounts();
        //creation de compte
        if(comptes.isEmpty()){

            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    modifierEtatBouton(R.id.btn_connect, false);
                    modifierEtatBouton(R.id.btn_account,true);
                }
            });
        }
        else{
            Log.d("MainActivity","connection avec le compte "+ comptes.get(0));

            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    modifierEtatBouton(R.id.btn_account,false);
                    modifierEtatBouton(R.id.btn_connect,true);
                }
            });
        }
    }



}
