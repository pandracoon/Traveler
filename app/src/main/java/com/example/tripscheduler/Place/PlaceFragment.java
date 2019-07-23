package com.example.tripscheduler.Place;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.tripscheduler.R;
import com.example.tripscheduler.Server.BitmapArithmetic;
import com.example.tripscheduler.Server.IAppService;
import com.example.tripscheduler.Server.RetrofitClient;
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

public class PlaceFragment extends Fragment {

  IAppService apiService;
  private final String SERVER = "http://143.248.36.205:3000";
  private CompositeDisposable compositeDisposable = new CompositeDisposable();
  private IAppService iAppService;

  RecyclerView mRecyclerView;
  ArrayList<Place> placeList;

  String title;

  Button uploadButton;

  public PlaceFragment(String title) {
    this.title = title;
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

    placeList = new ArrayList<>();

    mRecyclerView = (RecyclerView) rootView.findViewById(R.id.masonryGrid);
    mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

    final PlaceAdapter adapter = new PlaceAdapter(getActivity(), placeList);
    mRecyclerView.setAdapter(adapter);
    PlaceItemDecoration decoration = new PlaceItemDecoration(16);
    mRecyclerView.addItemDecoration(decoration);

    compositeDisposable.add(iAppService.places_get_one("bob@gmail.com", "Trip to Seoul")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .retry()
            .subscribe(new Consumer<String>() {
              @Override
              public void accept(String data) throws Exception {
                Log.e("places_get_one", data);

                if (data.equals("0")) {
                  System.out.println("No data existed");
                }

                else {
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

                    placeList.add(new Place(object.get("name").toString(), object.get("location").toString(), object.get("label").toString(), url));
                    adapter.notifyDataSetChanged();

                  }
                }
              }
            }));

    // TODO dummy data -> should be removed later
//    Bitmap icon = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.image_test);
//    placeList.add(new Place("PLACE1", "LOCATION1", "LABEL1 intentionally long to make it more important to notice the difference of labelibg", icon));
//    placeList.add(new Place("PLACE2", "LOCATION2", "LABEL2", icon));
//    placeList.add(new Place("PLACE3", "LOCATION3", "LABEL3", icon));
//    placeList.add(new Place("PLACE4", "LOCATION4", "LABEL4", icon));
//
//    adapter.notifyDataSetChanged();


    uploadButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Bitmap icon1 = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.image_test);
        Bitmap icon2 = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.image_test2);
        Bitmap icon3 = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.image_test3);
        Bitmap icon4 = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.image_test4);
        Bitmap icon5 = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.image_test5);
        Bitmap icon6 = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.image_test6);
        Bitmap icon7 = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.image_test7);
        Bitmap icon8 = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.image_test8);
        Bitmap icon9 = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.image_test9);
        Bitmap icon10 = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.image_test10);

        BitmapArithmetic bitmapArithmetic = new BitmapArithmetic();
        icon1 = bitmapArithmetic.resizeBitmap(icon1);
        icon2 = bitmapArithmetic.resizeBitmap(icon2);
        icon3 = bitmapArithmetic.resizeBitmap(icon3);
        icon4 = bitmapArithmetic.resizeBitmap(icon4);
        icon5 = bitmapArithmetic.resizeBitmap(icon5);
        icon6 = bitmapArithmetic.resizeBitmap(icon6);
        icon7 = bitmapArithmetic.resizeBitmap(icon7);
        icon8 = bitmapArithmetic.resizeBitmap(icon8);
        icon9 = bitmapArithmetic.resizeBitmap(icon9);
        icon10 = bitmapArithmetic.resizeBitmap(icon10);

//        multipartImageUpload(icon1, "bob@gmail.com", "Trip to Seoul", "Place Name1", "14.1 112.3", "Label");
//        multipartImageUpload(icon2, "bob@gmail.com", "Trip to Seoul", "Place Name2", "14.12 112.", "Labe2");
//        multipartImageUpload(icon3, "bob@gmail.com", "Trip to Seoul", "Place Name3", "14.12 12.3", "Labe3");
//        multipartImageUpload(icon4, "bob@gmail.com", "Trip to Seoul", "Place Name4", "114.122 12.3", "this is labelsfsafase");
//        multipartImageUpload(icon5, "bob@gmail.com", "Trip to Seoul", "Place Name5", "12.1 6.3", "accommodation");
//        multipartImageUpload(icon6, "bob@gmail.com", "Trip to Seoul", "Place Name6", "14.12 7.3.", "transportation");
//        multipartImageUpload(icon7, "bob@gmail.com", "Trip to Seoul", "Place Name7", "9.12 5.3", "transportation");
//        multipartImageUpload(icon8, "bob@gmail.com", "Trip to Seoul", "Place Name8", "152.122 75.3", "attraction");
//        multipartImageUpload(icon9, "bob@gmail.com", "Trip to Seoul", "Place Name9", "14.1 90.3", "transportation");
        multipartImageUpload(icon10, "bob@gmail.com", "Trip to Seoul", "Place Name10", "1523.12 112.", "attraction");



      }


    });

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

  private void multipartImageUpload(Bitmap mBitmap, String email, String title, String name, String location, String label) {
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
      MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), reqFile);
      RequestBody imageName = RequestBody.create("upload", MediaType.parse("text/plain"));
      RequestBody send_email = RequestBody.create(email, MediaType.parse("text/plain"));
      RequestBody send_title = RequestBody.create(title, MediaType.parse("text/plain"));
      RequestBody send_name = RequestBody.create(name, MediaType.parse("text/plain"));
      RequestBody send_location = RequestBody.create(location, MediaType.parse("text/plain"));
      RequestBody send_label = RequestBody.create(label, MediaType.parse("text/plain"));

      Call<ResponseBody> req = apiService.place_insert_one(body, imageName, send_email, send_title, send_name, send_location, send_label);
      req.enqueue(new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
          if (response.code() == 200) {
            Toast.makeText(getContext(), "Upload Success!", Toast.LENGTH_SHORT).show();
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
}
