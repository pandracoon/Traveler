package com.example.tripscheduler.Place;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.tripscheduler.R;
import com.example.tripscheduler.Server.BitmapArithmetic;
import com.example.tripscheduler.Server.IAppService;
import com.example.tripscheduler.Server.RetrofitClient;
import com.example.tripscheduler.Travel.Travel;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.app.Activity.RESULT_OK;

public class PlaceFragment extends Fragment {

    IAppService apiService;
    public static final int INFO_REQUEST = 1;
    private final String SERVER = "http://143.248.36.205:3000";
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private IAppService iAppService;
    PlaceAdapter adapter;

    RecyclerView mRecyclerView;
    ArrayList<TPlace> TPlaceList;

    String title;
    String email;

    String name;
    String strLatLng;
    String label;
    Bitmap image;
    Context mContext;

    Button uploadButton;

    int state;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public PlaceFragment(String title, String email) {

        state = 1;
        this.title = title;
        this.email = email;
    }

    public PlaceFragment(String title, String email, String name, String strLatLng, String label, Bitmap image) {

        state = 2;
        this.title = title;
        this.email = email;
        this.name = name;
        this.strLatLng = strLatLng;
        this.label = label;
        this.image = image;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_fragmentplace, container, false);

        uploadButton = (Button) rootView.findViewById(R.id.upload_image);

        Retrofit retrofitClient = RetrofitClient.getInstance();
        iAppService = retrofitClient.create(IAppService.class);

        initRetrofitClient();

        TPlaceList = new ArrayList<>();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.masonryGrid);
        mRecyclerView
                .setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        adapter = new PlaceAdapter(getActivity(), TPlaceList);
        mRecyclerView.setAdapter(adapter);
        PlaceItemDecoration decoration = new PlaceItemDecoration(16);
        mRecyclerView.addItemDecoration(decoration);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.e("hi", "hi");

                TPlace place = TPlaceList.get(position);

                Intent intent = new Intent(mContext, PlaceInfoActivity.class);
                intent.putExtra("place", place);
                startActivityForResult(intent, INFO_REQUEST);
            }

            @Override
            public void onLongItemClick(View view, final int position) {

            }
        }));

        compositeDisposable.add(iAppService.places_get_one(email, title)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry()
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String data) throws Exception {
                        Log.e("places_get_one", data);

                        if (data.equals("0")) {
                            System.out.println("No data existed");
                        } else {
                            JsonParser jsonParser = new JsonParser();
                            JsonArray jsonArray = (JsonArray) jsonParser.parse(data);

                            for (int i = 0; i < jsonArray.size(); i++) {
                                final JsonObject object = (JsonObject) jsonArray.get(i);
                                System.out.println(object.get("name"));
                                System.out.println(object.get("location"));
                                System.out.println(object.get("label"));
                                System.out.println(object.get("path"));

                                JsonArray location = (JsonArray) object.get("location");
                                System.out.println(location.get(0));
                                System.out.println(location.get(1));

                                System.out.println(SERVER + "/" + object.get("path").toString().replace("\"", ""));

                                String url = SERVER + "/" + object.get("path").toString().replace("\"", "");

                                TPlaceList.add(
                                        new TPlace(object.get("name").toString(), object.get("location").toString(),
                                                object.get("label").toString(), url));

                                adapter.notifyDataSetChanged();

                            }
                        }
                    }
                }));

        if (state == 2) {
            multipartImageUpload(image, email.replace("\"", ""), title, name, strLatLng, label);
        }

        return rootView;
    }

    private void initRetrofitClient() {
        OkHttpClient client = new OkHttpClient.Builder().build();
        apiService = new Retrofit.Builder()
                .baseUrl(SERVER + "/")
                .client(client)
                .build()
                .create(IAppService.class);
    }

    private void multipartImageUpload(Bitmap mBitmap, String email, String title, final String name,
                                      final String location, final String label) {
        try {
            File filesDir = getContext().getFilesDir();
            File file = new File(filesDir, "image" + ".png");

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
            byte[] bitmapData = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapData);
            fos.flush();
            fos.close();

            RequestBody reqFile = RequestBody.create(file, MediaType.parse("image/*"));
            MultipartBody.Part body = MultipartBody.Part
                    .createFormData("upload", file.getName(), reqFile);
            RequestBody imageName = RequestBody.create("upload", MediaType.parse("text/plain"));
            RequestBody send_email = RequestBody.create(email, MediaType.parse("text/plain"));
            RequestBody send_title = RequestBody.create(title, MediaType.parse("text/plain"));
            RequestBody send_name = RequestBody.create(name, MediaType.parse("text/plain"));
            RequestBody send_location = RequestBody.create(location, MediaType.parse("text/plain"));
            RequestBody send_label = RequestBody.create(label, MediaType.parse("text/plain"));

            Call<ResponseBody> req = apiService
                    .place_insert_one(body, imageName, send_email, send_title, send_name, send_location,
                            send_label);
            req.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == 200) {
                        Toast.makeText(getContext(), "Upload Success!", Toast.LENGTH_SHORT).show();


//            JsonObject object = new JsonObject().get(response.body().toString()).getAsJsonObject();
//            System.out.println(object);
                        System.out.println(response.toString());
                        System.out.println(response.message());
                        System.out.println(response.body());
                        System.out.println(response.body());
                        String url = response.toString().split("=")[4];
                        url = url.split("\\}")[0];
                        System.out.println(url);
                        System.out.println(call.toString());
                        System.out.println(name);
                        System.out.println(location);
                        System.out.println(label);

                        TPlaceList.add(new TPlace(name, location, label, url));
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Error : " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getContext(), "Request failed.", Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case INFO_REQUEST:
                if (resultCode == RESULT_OK) {

                    int state = data.getIntExtra("state", 0);

                    if (state == 0) {
                        Log.e("ERROR", "error");
                    } else if (state == 1) {
                        TPlace place = (TPlace) data.getSerializableExtra("place");
                        TPlace newPlace = (TPlace) data.getSerializableExtra("newPlace");

                        for(int i= TPlaceList.size()-1; i>=0; i--) {
                            System.out.println(TPlaceList.get(i).getData("name"));
                            System.out.println(place.getData("name"));
                            System.out.println(TPlaceList.get(i).getData("location"));
                            System.out.println(place.getData("location"));
                            System.out.println(TPlaceList.get(i).getData("label"));
                            System.out.println(place.getData("label"));

                            if(TPlaceList.get(i).getData("name").equals(place.getData("name")) &&
                                    TPlaceList.get(i).getData("location").equals(place.getData("location")) &&
                                    TPlaceList.get(i).getData("label").equals(place.getData("label"))) {
                                System.out.println("asd");

                                TPlaceList.remove(i);
                            }
                            else {
                            }
                        }

                        TPlaceList.add(newPlace);

                        compositeDisposable.add(iAppService.place_update_one(email.replace("\"", ""), title,
                                place.getData("name").replace("\"", ""),
                                place.getData("location").split("\"")[1] + " " + place.getData("location").split("\"")[3],
                                place.getData("label").replace("\"", ""),
                                newPlace.getData("name").replace("\"", ""),
                                newPlace.getData("location").split("\"")[1] + " " + newPlace.getData("location").split("\"")[3],
                                newPlace.getData("label").replace("\"", ""))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .retry()
                                .subscribe(new Consumer<String>() {
                                    @Override
                                    public void accept(String data) throws Exception {
                                        Log.e("travel_insert_one", data);

                                        adapter.notifyDataSetChanged();

                                    }
                                }));

                    } else if (state == 2) {
                        TPlace place = (TPlace) data.getSerializableExtra("place");

                        for(int i= TPlaceList.size()-1; i>=0; i--) {
                            System.out.println(TPlaceList.get(i).getData("name"));
                            System.out.println(place.getData("name"));
                            System.out.println(TPlaceList.get(i).getData("location"));
                            System.out.println(place.getData("location"));
                            System.out.println(TPlaceList.get(i).getData("label"));
                            System.out.println(place.getData("label"));

                            if(TPlaceList.get(i).getData("name").equals(place.getData("name")) &&
                                    TPlaceList.get(i).getData("location").equals(place.getData("location")) &&
                                    TPlaceList.get(i).getData("label").equals(place.getData("label"))) {
                                TPlaceList.remove(i);
                                System.out.println("asd");
                            }
                            else {
                            }
                        }

                        compositeDisposable.add(iAppService.place_delete_one(email.replace("\"", ""), title,
                                                                                place.getData("name").replace("\"", ""),
                                                                      place.getData("location").split("\"")[1] + " " + place.getData("location").split("\"")[3],
                                                                                place.getData("label").replace("\"", ""))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .retry()
                                .subscribe(new Consumer<String>() {
                                    @Override
                                    public void accept(String data) throws Exception {
                                        Log.e("place_delete_one", data);

                                        adapter.notifyDataSetChanged();
                                    }
                                }));
                    }

                }
                break;
        }
    }
}
