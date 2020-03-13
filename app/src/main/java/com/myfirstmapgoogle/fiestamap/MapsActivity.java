package com.myfirstmapgoogle.fiestamap;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 현재위치를 보여주는 액티비티
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnInfoWindowLongClickListener {

    class ButtonMarker{
        Button button;
        Marker marker;
        public ButtonMarker(Button button,Marker marker){
        this.button = button;
        this.marker=marker;
        }
    }

    public static Context mContext;

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    // The entry point to the Places API.
    private PlacesClient mPlacesClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    //한국(서울)으로 디폴트값 설정
    // 권한 부여받지 못하면 서울로 ㄱㄱ
    private final LatLng mDefaultLocation = new LatLng(35.888, 128.65);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    //Fused Location Provider에의해 마지막으로 얻어진 장소
    private Location mLastKnownLocation;

    // 액티비티 상태를 알려주는 키
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // 현재 장소를위해 사용된 것들
    private static final int M_MAX_ENTRIES = 5;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private List[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;
    private ArrayList AList;
    private ArrayList<Marker> MarkerList;
    private ArrayList<ButtonMarker> ButtonMarkerList;
    int MarkCount;
    int count=0;
    int a;
    private LinearLayout Right_btn_layout ;
    private LinearLayout Center_btn_layout;
    private LinearLayout Left_btn_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);

        MarkCount = 0;
        mContext = this;

        //저장된 인스턴스 상태에 의해 얻어진 장소와 카메라 포지션
        if (savedInstanceState != null) {  //저장된 장소가 있으면
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // PlacesClient 구성하기
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));//구글 키를 가져오고
        mPlacesClient = Places.createClient(this);

        // CFusedLocationProviderClient 구성하기
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // 맵 만들기
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // 화면에 띄우기

        //날짜 출력
        /**
         * 현재시간을 구하기
         * */

        //추가 버튼누르면 버튼 내용 바뀌는거 임의로 적어둔거임
        MarkerList = new ArrayList();
        ButtonMarkerList = new ArrayList();

        //추가된 물건 버튼

        Button btn_add = findViewById(R.id.btn_add); // 물건추가 버튼


        //물건추가 버튼
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, InfoEnterActivity.class);
                intent.putExtra("Latitude", mLastKnownLocation.getLatitude());
                intent.putExtra("Longitude", mLastKnownLocation.getLongitude());
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {//물건추가하기 버튼눌렀을 떄의 화면 전환
            if (resultCode == RESULT_OK) //
            {
                String name = data.getStringExtra("name");
                String time = data.getStringExtra("time");
                String place = data.getStringExtra("place");
                String memo = data.getStringExtra("memo");
                myAddMarker(name, place, memo, time);
                 Right_btn_layout = findViewById(R.id.Right_btn_layout);
                 Center_btn_layout = findViewById(R.id.Center_btn_layout);
                 Left_btn_layout = findViewById(R.id.Left_btn_layout);
                 Button temptBtn = new Button(this);
                 temptBtn.setText(name);
                temptBtn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 150));
                if(count==0)Left_btn_layout.addView(temptBtn);
                else if(count == 1) Center_btn_layout.addView(temptBtn);
                else Right_btn_layout.addView(temptBtn);
                count ++;
                if(count>2)count = 0;

                Toast.makeText(MapsActivity.this, "good", Toast.LENGTH_LONG).show();
            } else Toast.makeText(MapsActivity.this, "bad", Toast.LENGTH_LONG).show();
        }
        else if (requestCode == 2) {//인포윈도우 롱 클릭 시의 화면 전환(정보화면 롱클릭)
        if(resultCode == RESULT_CANCELED)
            a = data.getIntExtra("ORDER",-1);
        if(a>=0) delMarker(MarkerList.get(a));

        }


    }


    /**
     * 액티비티가 pause 되었을 때 상태 저장하기
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * 옵션매뉴 설정
     *
     * @param menu The options menu.
     * @return Boolean.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.current_place_menu, menu);
        return true;
    }

    /**
     * 장소를 얻기위해 매뉴클릭 옵션 조정하기
     *
     * @param item The menu item to handle.
     * @return Boolean.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_get_place) {
            showCurrentPlace();
        }
        return true;
    }

    /**
     * 사용가능할때 맵 조종하기
     * 이 콜백은 맵이 사용될 준비가 됬을때 트리거됩니다.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnInfoWindowLongClickListener(this);


        //유저에게 permission을 촉발시키기
        getLocationPermission();

        //My Location layer를 불러오고, 관련된 컨트롤을 맵에 띄움
        updateLocationUI();

        //디바이스의 현위치를 얻어서 맵에 띄움
        getDeviceLocation();

        //커스텀 인포윈도우 설정
        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(MapsActivity.this);
        map.setInfoWindowAdapter(adapter);

        //마커 불러오기
        loadMarker();

    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     * 디바이스의 현위치를 얻어서 맵의 카메라를 옮김
     */
    private void getDeviceLocation() {
        /*
         * 디바이스의 가장 최신의, 정확한 위치를 얻음(장소가 접근 불능일 때 가끔씩 null임)
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // 맵의 카메라의 위치를 현재 디바이스의 위치로 옮김
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * 장소를 얻기위한 permission을 촉발함
     */
    public void getLocationPermission() {
        /*
         * 현 위치를 알수 있도록 location permission을 요구
         * permission의 결과는 onRequestPermissionsResult(콜백) 에 의해 다뤄짐
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * location permission 요구 결과를 다룸
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * 사용자에게 장소를 고르게 함(여러  근처 장소를 보여주는 듯)
     * 그리고 현위치를 지도에 띄움 - permission이 제공되어야함
     */
    private void showCurrentPlace() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            // Use fields to define the data types to return.
            //데이타를 return타입으로 지정하기 위해? field사용
            List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS,
                    Place.Field.LAT_LNG);

            //FindCurrentPlaceRequest를 만들기위해 빌더 사용
            FindCurrentPlaceRequest request =
                    FindCurrentPlaceRequest.newInstance(placeFields);

            //근처 장소(원문 likely place)얻기 - 즉, 비즈니스와 다른 명소들(디바이스의 현 위치와 가장 맞는 장소)
            @SuppressWarnings("MissingPermission") final Task<FindCurrentPlaceResponse> placeResult =
                    mPlacesClient.findCurrentPlace(request);
            placeResult.addOnCompleteListener(new OnCompleteListener<FindCurrentPlaceResponse>() {
                @Override
                public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        FindCurrentPlaceResponse likelyPlaces = task.getResult();

                        //카운트를 set해서 5entries보다 적은 경우가 반환될때를 다룸
                        int count;
                        if (likelyPlaces.getPlaceLikelihoods().size() < M_MAX_ENTRIES) {
                            count = likelyPlaces.getPlaceLikelihoods().size();
                        } else {
                            count = M_MAX_ENTRIES;
                        }

                        int i = 0;
                        mLikelyPlaceNames = new String[count];
                        mLikelyPlaceAddresses = new String[count];
                        mLikelyPlaceAttributions = new List[count];
                        mLikelyPlaceLatLngs = new LatLng[count];

                        for (PlaceLikelihood placeLikelihood : likelyPlaces.getPlaceLikelihoods()) {
                            //근처장소 (원문 likely place)를 유저에게 보여주기위해 빌드
                            //likely place가 아마 현위치와 가장 가까이있는 명소 말하는 듯
                            mLikelyPlaceNames[i] = placeLikelihood.getPlace().getName();
                            mLikelyPlaceAddresses[i] = placeLikelihood.getPlace().getAddress();
                            mLikelyPlaceAttributions[i] = placeLikelihood.getPlace()
                                    .getAttributions();
                            mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                            i++;
                            if (i > (count - 1)) {
                                break;
                            }
                        }
                        //유저에게 근처장소(likely place)를 제안하는 dialog를 보여주고, 지정된 장소에 marker 추거
                        MapsActivity.this.openPlacesDialog();
                    } else {
                        Log.e(TAG, "Exception: %s", task.getException());
                    }
                }
            });
        } else {
            //유저가 permission 허가를 안할 경우
            Log.i(TAG, "The user did not grant location permission.");

            // Add a default marker, because the user hasn't selected a place.
            //유저가 장소를 고르지 않았기 때문에 default marker를 추가함
            mMap.addMarker(new MarkerOptions()
                    .title("marker?")//요건그냥 내가 임의로 적은거임 오류나서 내용 바꿈
                    .position(mDefaultLocation)
            );
            //퍼미션 하라고 요구
            getLocationPermission();
        }
    }

    /**
     * Displays a form allowing the user to select a place from a list of likely places.
     * 유저에게 likely place리스트에서 장소를 고르도록 폼을 전시(display)
     */
    private void openPlacesDialog() {
        //유저가 어디있는지 장소를 고르도록 묻기
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //어떤 것이(원문 argument)선택된 아이템의 position을 가지고 있는지 고르기
                LatLng markerLatLng = mLikelyPlaceLatLngs[which];
                String markerSnippet = mLikelyPlaceAddresses[which];
                if (mLikelyPlaceAttributions[which] != null) {
                    markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[which];
                }

                //info window와 함께 선택된 장소에 마커를 추가

                //장소관련 정보 보여주기
                mMap.addMarker(new MarkerOptions()
                        .title(mLikelyPlaceNames[which])
                        .position(markerLatLng)
                        .snippet(markerSnippet));

                //마커 장소로 카메라 이동하기
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                        DEFAULT_ZOOM));
            }
        };

//        // Display the dialog.
//        AlertDialog dialog = new AlertDialog.Builder(this)
//                .setTitle(R.string.pick_place)
//                .setItems(mLikelyPlaceNames, listener)
//                .show();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     * 유저가 permission 허가했는지에 따라 맵UI세팅 업데이트 하기
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public boolean getpermission() {
        return mLocationPermissionGranted;
    }

    public void myAddMarker(final String name, final String place, final String memo, final String time) {
        FileOutputStream fos = null;
        String myLatitude;
        String myLongitude;
        if (mLastKnownLocation != null) {
            MarkerList.add(mMap.addMarker(new MarkerOptions().
                    position(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()))
                    .title(name)
                    .snippet(time + "\n" + place + "\n" + memo)));
            //좌표를 String 타입으로 변환
            myLatitude = Double.toString(mLastKnownLocation.getLatitude());
            myLongitude = Double.toString(mLastKnownLocation.getLongitude());
            try {
                String a = "\r\n";
                fos = openFileOutput("internal.txt", Context.MODE_APPEND);
                fos.write(myLatitude.getBytes());
                fos.write(a.getBytes());
                fos.write(myLongitude.getBytes());
                fos.write(a.getBytes());
                fos.write(name.getBytes());
                fos.write(a.getBytes());
                fos.write(place.getBytes());
                fos.write(a.getBytes());
                fos.write(memo.getBytes());
                fos.write(a.getBytes());
                fos.write(time.getBytes());
                fos.write(a.getBytes());
                fos.close();
                //latitude, longitude, name, place, memo, time 순으로 저장, 줄바꿈으로 칸 나누기
                Toast.makeText(MapsActivity.this, "저장완료", Toast.LENGTH_LONG).show();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            MarkCount++;
        } else Toast.makeText(MapsActivity.this, "위치를 알 수 없습니다.", Toast.LENGTH_LONG).show();
    }

    public void loadMarker() {
        String data = null;
//        StringBuffer Strbuffer = new StringBuffer();
        FileInputStream fis = null;
        String myLatitude = "";
        String myLongitude = "";
        String name = "";
        String place = "";
        String memo = "";
        String time = "";
        double Latitude;
        double Longitude;
        int i = 0;
        try {
            fis = openFileInput("internal.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis));
            data = bufferedReader.readLine();
            while (data != null) {
                if (i == 0) myLatitude = data;
                else if (i == 1) myLongitude = data;
                else if (i == 2) name = data;
                else if (i == 3) place = data;
                else if (i == 4) memo = data;
                else if (i == 5) time = data;
                i++;
                if (i > 5) {
                    Latitude = Double.parseDouble(myLatitude);
                    Longitude = Double.parseDouble(myLongitude);
                    MarkerList.add(mMap.addMarker(new MarkerOptions().
                            position(new LatLng(Latitude, Longitude))
                            .title(name)
                            .snippet(time + "\n" + place + "\n" + memo)));
                    i = 0;
                }
                data = bufferedReader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void delMarker(Marker marker) {
        int i = 0;
        while (MarkerList.get(i) != marker) {
            i++;
        }

        MarkerList.get(i).remove();
        File file = new File("/data/data/com.myfirstmapgoogle.fiestamap/files/internal.txt");
        String dummy = "";
        try {
            BufferedReader bufferedReader = new BufferedReader((new InputStreamReader(new FileInputStream(file))));
            String line;
            Toast.makeText(this, "지웠다 "+MarkerList.get(i).getId(), Toast.LENGTH_SHORT).show();

            for (int j = 0; j < i * 6; j++) {
                line = bufferedReader.readLine();
                dummy += (line + "\r\n");
            }
            for (int j = i * 6; j < (i + 1) * 6; j++) {
                String deline = bufferedReader.readLine();
            }
            while ((line = bufferedReader.readLine()) != null) {
                dummy += (line + "\r\n");
            }
            FileOutputStream fos = null;
            fos = openFileOutput("internal.txt", Context.MODE_PRIVATE);
            fos.write(dummy.getBytes());
            fos.close();
            bufferedReader.close();
            Toast.makeText(this, "지웠다 "+MarkerList.get(i).getId(), Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        marker.showInfoWindow();
    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {
        int i = 0;
        while(!MarkerList.get(i).getId().equals(marker.getId())){
            if(MarkerList.get(i)==null)break;
            i++;
        }
        if(MarkerList.get(i)==null) Toast.makeText(this,"null",Toast.LENGTH_LONG).show();
        else {
            Intent intent = new Intent(MapsActivity.this, PopUpActivity.class);
            intent.putExtra("ORDER", i);
            startActivityForResult(intent, 2);
        }


    }
}