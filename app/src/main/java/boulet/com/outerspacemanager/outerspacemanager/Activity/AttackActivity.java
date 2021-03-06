package boulet.com.outerspacemanager.outerspacemanager.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import boulet.com.outerspacemanager.outerspacemanager.Model.CodeResponse;
import boulet.com.outerspacemanager.outerspacemanager.Model.Api;
import boulet.com.outerspacemanager.outerspacemanager.R;
import boulet.com.outerspacemanager.outerspacemanager.Model.Ship;
import boulet.com.outerspacemanager.outerspacemanager.Model.Ships;
import boulet.com.outerspacemanager.outerspacemanager.Model.UserResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AttackActivity extends AppCompatActivity implements View.OnClickListener {
    private UserResponse user;
    private EditText edSmallShip;
    private EditText edHeavyShip;
    private EditText edSpyShip;
    private EditText edDestroyer;
    private EditText edDeathStar;
    private Button btnCancel;
    private Button btnAttack;
    private String token;
    public static final String PREFS_NAME = "TOKEN_FILE";
    private Activity thisActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attack);

        edSmallShip = findViewById(R.id.edLightShip);
        edHeavyShip = findViewById(R.id.edHeavyShip);
        edSpyShip = findViewById(R.id.edSpyShip);
        edDestroyer = findViewById(R.id.edDestroyer);
        edDeathStar = findViewById(R.id.edDeathStar);

        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);
        btnAttack = findViewById(R.id.btnAttack);
        btnAttack.setOnClickListener(this);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        token = settings.getString("token","");

        String userJson = getIntent().getStringExtra("USER_TO_ATTACK");
        Gson json = new Gson();
        user = json.fromJson(userJson, UserResponse.class);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnCancel.getId())
        {
            Intent galaxyIntent = new Intent(getApplicationContext(), GalaxyActivity.class);
            startActivity(galaxyIntent);
        }
        else
        {
            Ship ship1 = new Ship("0", edSmallShip.getText().toString());
            Ship ship2 = new Ship("1", edHeavyShip.getText().toString());
            Ship ship3 = new Ship("2", edSpyShip.getText().toString());
            Ship ship4 = new Ship("3", edDestroyer.getText().toString());
            Ship ship5 = new Ship("4", edDeathStar.getText().toString());
            Ships ships = new Ships();
            Ship[] listShips = new Ship[5];
            listShips[0] = ship1;
            listShips[1] = ship2;
            listShips[2] = ship3;
            listShips[3] = ship4;
            listShips[4] = ship5;
            ships.setShips(listShips);

            Retrofit retrofit= new Retrofit.Builder().baseUrl("https://outer-space-manager-staging.herokuapp.com").addConverterFactory(GsonConverterFactory.create()).build();
            Api service = retrofit.create(Api.class);
            Call<CodeResponse> request = service.AttackUser(token, ships, user.getUsername());

            request.enqueue(new Callback<CodeResponse>() {
                @Override
                public void onResponse(Call<CodeResponse> call, Response<CodeResponse> response) {
                    switch (response.code())
                    {
                        case 200:
                            Calendar currentDate = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

                            String dateNow = sdf.format(currentDate.getTime());
                            Date today =  new Date(dateNow);

                            String resourceAttackStarted = getResources().getString( getResources().getIdentifier("started_attack", "string", thisActivity.getPackageName()));
                            Toast.makeText(getApplicationContext(), resourceAttackStarted, Toast.LENGTH_LONG).show();
                            Date attackDate = new Date(Long.parseLong(response.body().getAttackTime()));

                            try {
                                attackDate = (Date) sdf.parse("MM/dd/yyyy HH:mm:ss");
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            long numberOfMiliSeconds=(long)((attackDate.getTime()-today.getTime()));

                            if (numberOfMiliSeconds <= 0)
                            {
                                String resourceSeeResultNow = getResources().getString( getResources().getIdentifier("see_result_now", "string", thisActivity.getPackageName()));
                                Toast.makeText(getApplicationContext(), resourceSeeResultNow, Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                String hours = new SimpleDateFormat("HH").format(new Date(numberOfMiliSeconds));
                                String minutes = new SimpleDateFormat("mm").format(new Date(numberOfMiliSeconds));
                                String seconds = new SimpleDateFormat("ss").format(new Date(numberOfMiliSeconds));
                                String toShow = getResources().getString( getResources().getIdentifier("see_result_in", "string", thisActivity.getPackageName()));
                                if (!hours.equals("00"))
                                {
                                    toShow += hours + " h ";
                                }
                                if (!minutes.equals("00") || !hours.equals("00"))
                                {
                                    toShow += minutes + " min ";
                                }
                                toShow += seconds + " sec";
                                Toast.makeText(getApplicationContext(),  toShow, Toast.LENGTH_SHORT).show();
                            }
                            Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(myIntent);
                            break;
                        case 401 :
                            String res = "";
                            try {
                                res = response.errorBody().string();
                            } catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                            String resourceInvalidRequest = getResources().getString( getResources().getIdentifier("not_enough_ships", "string", thisActivity.getPackageName()));
                            Toast.makeText(getApplicationContext(), resourceInvalidRequest, Toast.LENGTH_SHORT).show();
                            break;
                        case 404 :
                            Toast.makeText(getApplicationContext(), "Comment as tu fait pour changer mes valeurs ? Oo", Toast.LENGTH_LONG).show();
                            break;
                        case 500 :
                            String resourceApiProblem = getResources().getString( getResources().getIdentifier("api_problem", "string", thisActivity.getPackageName()));
                            Toast.makeText(getApplicationContext(), resourceApiProblem, Toast.LENGTH_LONG).show();
                            break;
                    }
                }

                @Override
                public void onFailure(Call<CodeResponse> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), call.toString(), Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
