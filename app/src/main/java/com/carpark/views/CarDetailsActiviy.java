package com.carpark.views;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.carpark.Api.ParkingApi;
import com.carpark.Api.Responses.BaseDataResponse;
import com.carpark.Api.RetrofitClient;
import com.carpark.Model.Vehicle;
import com.carpark.R;
import com.carpark.utils.SharePreference;
import com.carpark.views.homefragments.MyVehicleFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarDetailsActiviy extends BaseActivity {

    Button saveCarDetails;
    TextView carDetails;
    EditText plateNumber, carModel;
    Switch primaryRide;
    private String plateNum,makeModel;
    private boolean main_ride = true;
    private ProgressBar progressBar;
    private String  plate = "new", make;
    private int vehicle_id;
    private String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_details);
        token = getSharePref().getAccessToken();
        viewsInit();

        // To Edit or Delete Vehicle
        if(plate!=null){
            getCarDetails(vehicle_id,plate,make);
            saveCarDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressBar.setVisibility(View.VISIBLE);
                    UpdateInfo();
                    saveCarDetails.setClickable(false);
                    updateCarDetails(vehicle_id, plate, make);
                    saveCarDetails.setClickable(true);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });

            // To Add new Vehicle
        }else{
            saveCarDetails.setText("Save");
            getSupportActionBar().setTitle("Add New Vehicle");
            saveCarDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkInputBoxes();
                }
            });
        }
    }

    private void UpdateInfo() {
        plate = plateNumber.getText().toString();
        make = carModel.getText().toString();
    }

    private void createNewVehicle() {

    }

    private void getCarDetails(final int vehicle_id, final String plate, final String make) {
        plateNumber.setText(plate);
        carModel.setText(make);
        saveCarDetails.setText("Update");
        getSupportActionBar().setTitle(make);
    }

    private void updateCarDetails(int vehicleId, String plate, String make) {
        getParkingApi().editVehicle(token, vehicleId, plate, make).enqueue(new Callback<BaseDataResponse<Vehicle>>() {
            @Override
            public void onResponse(Call<BaseDataResponse<Vehicle>> call, Response<BaseDataResponse<Vehicle>> response) {
                // Vehicle will be updated In the API, Nothing to do with the response gotten
                showToast("Vehicle Updated");
                finish();
            }

            @Override
            public void onFailure(Call<BaseDataResponse<Vehicle>> call, Throwable t) {
                showToast("Failed to Update, Please try again");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.delete_menu,menu);
        if(plate!=null){
            menu.findItem(R.id.delete).setVisible(true);
        }else{
            menu.findItem(R.id.delete).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                deleteVehicle(vehicle_id);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteVehicle(int vehicle_id) {
        getParkingApi().deleteVehicle(token, vehicle_id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                showToast( "Vehicle Deleted");
                finish();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showToast( "Failed to delete Vehicle");
            }
        });

    }

    // initialising the views
    public void viewsInit(){
        getSupportActionBar().setTitle("Add Vehicle");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        saveCarDetails = findViewById(R.id.save_car_details);
        primaryRide = findViewById(R.id.primary_ride);
        plateNumber = findViewById(R.id.car_plate_number);
        carModel =  findViewById(R.id.car_model);
        progressBar = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.INVISIBLE);
        vehicle_id = getIntent().getIntExtra("Vehicle_Id", -1);
        plate = getIntent().getStringExtra("plate_number");
        make = getIntent().getStringExtra("make");
    }

    // checking if the input boxes were filled
    public void checkInputBoxes(){
        plateNum = plateNumber.getText().toString();
        makeModel = carModel.getText().toString();
        //checking the input boxes first
        if (plateNum.isEmpty()){
            this.plateNumber.setError("Please fill this field");
        }else if (makeModel.isEmpty()){
            this.carModel.setError("please fill this field");
        }else{
            primaryRide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        main_ride = true;
                    }else{
                        main_ride = false;
                    }
                }
            });
            progressBar.setVisibility(View.VISIBLE);
            saveCar(plateNum,makeModel,main_ride);
        }

    }

    public void saveCar(String plate_number, String make_model,boolean main_ride){
        token = SharePreference.getINSTANCE(getApplicationContext()).getAccessToken();
    }


}
